package com.ctrip.datasource.configure;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureCollection;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

public class AllInOneConfigureReader {
    private static final Logger logger = LoggerFactory.getLogger(AllInOneConfigureReader.class);
    private static final String CONFIG_FILE = "Database.Config";
    private static final String LINUX_DB_CONFIG_FILE = "/opt/ctrip/AppData/" + CONFIG_FILE;
    private static final String WIN_DB_CONFIG_FILE = "/D:/WebSites/CtripAppData/" + CONFIG_FILE;

    private static String DATABASE_ENTRY = "add";
    private static String DATABASE_ENTRY_NAME = "name";
    private static String DATABASE_ENTRY_CONNECTIONSTRING = "connectionString";
    private static String VERSION = "Version";
    private static String DEV_FLAG = "dev";

    private static final String CLASSPATH_LOCATION = "$classpath";

    public Map<String, DataSourceConfigureCollection> getDataSourceConfigures(Set<String> dbNames, boolean useLocal,
            String databaseConfigLocation) {
        String location = getAllInOneConfigLocation(databaseConfigLocation);
        Map<String, DataSourceConfigure> config = parseDBAllInOneConfig(location, dbNames, useLocal);
        validate(dbNames, config);
        Map<String, DataSourceConfigureCollection> map = wrapDataSourceConfigure(config);
        return map;
    }

    private void validate(Set<String> dbNames, Map<String, DataSourceConfigure> config) {
        if (config == null)
            throw new RuntimeException("Cannot load config");

        Set<String> dbConfigNames = config.keySet();
        if (dbConfigNames.containsAll(dbNames))
            return;

        dbNames.removeAll(dbConfigNames);

        logger.error("Cannot load config for the following DB: " + dbNames.toString());
        throw new RuntimeException("Cannot load config for the following DB: " + dbNames.toString());
    }

    private String getAllInOneConfigLocation(String databaseConfigLocation) {
        String location = getUserDefinedLocation(databaseConfigLocation);
        if (location != null && location.length() > 0) {
            if (CLASSPATH_LOCATION.equalsIgnoreCase(location)) {
                logger.info("Looking up Database.Config in classpath...");

                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader == null) {
                    classLoader = AllInOneConfigureReader.class.getClassLoader();
                }

                URL url = classLoader.getResource(CONFIG_FILE);
                if (url == null)
                    throw new RuntimeException("Can not locate " + CONFIG_FILE + " from classpath");
                location = url.getFile();
            }
        } else {
            String osName = null;
            try {
                osName = System.getProperty("os.name");
            } catch (SecurityException ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex.getMessage(), ex);
            }
            location = osName != null && osName.startsWith("Windows") ? WIN_DB_CONFIG_FILE : LINUX_DB_CONFIG_FILE;
        }

        return location;
    }

    private String getUserDefinedLocation(String databaseConfigLocation) {
        String location = DataSourceConfigureParser.getInstance().getDatabaseConfigLocation();
        if (location == null || location.length() == 0) {
            location = databaseConfigLocation;
        }
        return location;
    }

    private Map<String, DataSourceConfigure> parseDBAllInOneConfig(String absolutePath, Set<String> dbNames,
            boolean useLocal) {
        Map<String, DataSourceConfigure> dataSourceConfigures = new HashMap<>();

        FileInputStream in = null;
        try {
            logger.info("Allinone: using db config: " + absolutePath);
            File conFile = new File(absolutePath);
            in = new FileInputStream(conFile);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            Element root = doc.getDocumentElement();

            /**
             * If the version is not set or forceLocal is not true, we assume it is not dev environment. In this case we
             * should use titan service
             */
            if (!(isDevVersion(root) || useLocal)) {
                logger.info("No Version found in Databse.config or useLocal is not set in Dal.config.");
                return null;
            }

            List<Node> databaseEntryList = getChildNodes(root, DATABASE_ENTRY);

            for (int i = 0; i < databaseEntryList.size(); i++) {
                Node databaseEntry = databaseEntryList.get(i);

                if (!hasAttribute(databaseEntry, DATABASE_ENTRY_NAME)
                        || !hasAttribute(databaseEntry, DATABASE_ENTRY_CONNECTIONSTRING)) {
                    continue;
                }

                String name = getAttribute(databaseEntry, DATABASE_ENTRY_NAME);
                if (!dbNames.contains(name))
                    continue;

                String connectionString = getAttribute(databaseEntry, DATABASE_ENTRY_CONNECTIONSTRING);

                logger.info("Try to read config for " + name);
                DataSourceConfigure config = ConnectionStringParser.getInstance().parse(name, connectionString);
                dataSourceConfigures.put(name, config);
            }
            in.close();

            return dataSourceConfigures;
        } catch (Throwable e) {
            String msg = String.format("Read %s file error, msg: %s", absolutePath, e.getMessage());
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable e1) {
                    logger.warn(e1.toString());
                }
            }
        }
    }

    private Map<String, DataSourceConfigureCollection> wrapDataSourceConfigure(
            Map<String, DataSourceConfigure> config) {
        if (config == null || config.isEmpty())
            return null;

        Map<String, DataSourceConfigureCollection> map = new HashMap<>();
        for (Map.Entry<String, DataSourceConfigure> entry : config.entrySet()) {
            DataSourceConfigureCollection collection =
                    new DataSourceConfigureCollection(entry.getValue(), entry.getValue());
            map.put(entry.getKey(), collection);
        }

        return map;
    }

    private List<Node> getChildNodes(Node node, String name) {
        List<Node> nodes = new ArrayList<Node>();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (!children.item(i).getNodeName().equalsIgnoreCase(name))
                continue;
            nodes.add(children.item(i));
        }
        return nodes;
    }

    private String getAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    private boolean hasAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName) != null;
    }

    private boolean isDevVersion(Node node) {
        if (!hasAttribute(node, VERSION))
            return false;

        String version = getAttribute(node, VERSION);
        return DEV_FLAG.equalsIgnoreCase(version);
    }
}

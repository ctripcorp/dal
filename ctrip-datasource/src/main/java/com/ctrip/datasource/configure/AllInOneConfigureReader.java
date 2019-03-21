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

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AllInOneConfigureReader {
    private static final ILogger logger = DalElementFactory.DEFAULT.getILogger();
    private static final String CONFIG_FILE = "Database.Config";
    private static final String LINUX_DB_CONFIG_FILE = "/opt/ctrip/AppData/" + CONFIG_FILE;
    private static final String WIN_DB_CONFIG_FILE = "/D:/WebSites/CtripAppData/" + CONFIG_FILE;

    private static String DATABASE_ENTRY = "add";
    private static String DATABASE_ENTRY_NAME = "name";
    private static String DATABASE_ENTRY_CONNECTIONSTRING = "connectionString";
    private static String VERSION = "Version";
    private static String DEV_FLAG = "dev";

    private static final String CLASSPATH_LOCATION = "$classpath";

    public Map<String, DalConnectionString> getConnectionStrings(Set<String> names, boolean useLocal,
                                                                 String databaseConfigLocation) {
        String location = getAllInOneConfigLocation(databaseConfigLocation);
        Map<String, DalConnectionString> config = parseDBAllInOneConfig(location, names, useLocal);
        validate(names, config);
        return config;
    }

    private void validate(Set<String> names, Map<String, DalConnectionString> config) {
        if (config == null)
            throw new RuntimeException("Cannot load config");

        Set<String> dbConfigNames = config.keySet();
        if (dbConfigNames.containsAll(names))
            return;

        names.removeAll(dbConfigNames);
        RuntimeException exception = new DalRuntimeException("Cannot load config for the following DB: " + names.toString());
        logger.error("Cannot load config for the following DB: " + names.toString(), exception);
        throw exception;
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
                logger.error(ex.getMessage(), ex);
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

    private Map<String, DalConnectionString> parseDBAllInOneConfig(String absolutePath, Set<String> names,
            boolean useLocal) {
        Map<String, DalConnectionString> connectionStrings = new HashMap<>();
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
                String keyName = ConnectionStringKeyHelper.getKeyName(name);
                if (!names.contains(keyName))
                    continue;

                String cs = getAttribute(databaseEntry, DATABASE_ENTRY_CONNECTIONSTRING);
                DalConnectionString connectionString = new ConnectionString(keyName, cs, cs);
                connectionStrings.put(keyName, connectionString);
            }

            in.close();
            return connectionStrings;
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

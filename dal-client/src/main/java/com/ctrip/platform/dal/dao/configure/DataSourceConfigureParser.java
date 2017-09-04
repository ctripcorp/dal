package com.ctrip.platform.dal.dao.configure;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataSourceConfigureParser implements DataSourceConfigureConstants {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfigureParser.class);
    private static DataSourceConfigureParser dataSourceConfigureParser = null;
    private static final String DBPOOL_CONFIG = "datasource.xml";
    private static final String LOCATION = "location";
    private static final String RESOURCE_NODE = "Datasource";
    private static final String NAME = "name";

    private static final String DAL = "DAL";
    private static final String DAL_DATASOURCE_XML = "datasource.xml::";
    private static final String DAL_DATASOURCE_XML_LOCAL = "readLocal";

    public static final boolean DEFAULT_TESTWHILEIDLE = false;
    public static final boolean DEFAULT_TESTONBORROW = true;
    public static final boolean DEFAULT_TESTONRETURN = false;
    public static final String DEFAULT_VALIDATIONQUERY = "SELECT 1";
    public static final int DEFAULT_VALIDATIONQUERYTIMEOUT = 5;
    public static final long DEFAULT_VALIDATIONINTERVAL = 30000L;
    public static final String DEFAULT_VALIDATORCLASSNAME = "com.ctrip.platform.dal.dao.datasource.DataSourceValidator";
    public static final int DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS = 5000;
    public static final int DEFAULT_MAXAGE = 0;
    public static final int DEFAULT_MAXACTIVE = 100;
    public static final int DEFAULT_MINIDLE = 0;
    public static final int DEFAULT_MAXWAIT = 10000;
    public static final int DEFAULT_INITIALSIZE = 1;
    public static final int DEFAULT_REMOVEABANDONEDTIMEOUT = 60;
    public static final boolean DEFAULT_REMOVEABANDONED = true;
    public static final boolean DEFAULT_LOGABANDONED = false;
    public static final int DEFAULT_MINEVICTABLEIDLETIMEMILLIS = 30000;
    public static final String DEFAULT_CONNECTIONPROPERTIES = null;
    public static final boolean DEFAULT_JMXENABLED = true;
    public static final String DEFAULT_JDBCINTERCEPTORS = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
            + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

    private Map<String, DataSourceConfigure> dataSourceConfigures = new ConcurrentHashMap<>();

    // DataSourceConfigure change listener
    private Map<String, DataSourceConfigureChangeListener> listeners = new ConcurrentHashMap<>();

    private String databaseConfigLocation = null;

    private String dataSourceXmlLocation = null;
    private boolean dataSourceXmlExist = false;

    private DataSourceConfigureParser() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = DataSourceConfigureParser.class.getClassLoader();
            }
            URL url = classLoader.getResource(DBPOOL_CONFIG);
            if (url != null) {
                dataSourceXmlExist = true;
                dataSourceXmlLocation = url.getFile();
                logger.info("datasource property will use file :" + url.getFile());
                parse(url.openStream());
            }


        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public synchronized static DataSourceConfigureParser getInstance() {
        if (dataSourceConfigureParser == null) {
            dataSourceConfigureParser = new DataSourceConfigureParser();
        }

        return dataSourceConfigureParser;
    }

    public DataSourceConfigure getDataSourceConfigure(String name) {
        return dataSourceConfigures.get(name);
    }

    public Map<String, DataSourceConfigure> getDataSourceConfigures() {
        return dataSourceConfigures;
    }

    public boolean contains(String name) {
        return dataSourceConfigures.containsKey(name);
    }

    public void addDataSourceConfigure(String name, DataSourceConfigure configure) {
        dataSourceConfigures.put(name, configure);
    }

    public void copyDataSourceConfigure(String sampleName, String newName) {
        DataSourceConfigure oldConfig = dataSourceConfigures.get(sampleName);

        DataSourceConfigure newConfig = new DataSourceConfigure();
        newConfig.setName(newName);
        newConfig.setProperties(oldConfig.getProperties());
        newConfig.setMap(new HashMap<>(oldConfig.getMap()));
        dataSourceConfigures.put(newName, newConfig);
    }

    public void addChangeListener(String dbName, DataSourceConfigureChangeListener listener) {
        listeners.put(dbName, listener);
    }

    public Map<String, DataSourceConfigureChangeListener> getChangeListeners() {
        return listeners;
    }

    public String getDatabaseConfigLocation() {
        return databaseConfigLocation;
    }

    private void parse(InputStream in) throws Exception {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            parseDocument(doc);
            in.close();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable e1) {
                    logger.warn(e1.getMessage(), e1);
                }
            }
        }
    }

    private void parseDocument(Document doc) throws Exception {
        Element root = doc.getDocumentElement();
        if (hasAttribute(root, LOCATION)) {
            databaseConfigLocation = getAttribute(root, LOCATION);
        }
        List<Node> resourceList = getChildNodes(root, RESOURCE_NODE);
        for (Node resource : resourceList) {
            DataSourceConfigure dataSourceConfigure = parseResource(resource);
            dataSourceConfigures.put(dataSourceConfigure.getName(), dataSourceConfigure);
        }
    }

    private DataSourceConfigure parseResource(Node resource) {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setName(getAttribute(resource, NAME));
        Map<String, String> map = new HashMap<>();
        dataSourceConfigure.setMap(map);

        // The following are key connection parameters, developer do not need to provide them in case the configure
        // provider is set
        if (hasAttribute(resource, USER_NAME)) {
            dataSourceConfigure.setUserName(getAttribute(resource, USER_NAME));
        }
        if (hasAttribute(resource, PASSWORD)) {
            dataSourceConfigure.setPassword(getAttribute(resource, PASSWORD));
        }
        if (hasAttribute(resource, CONNECTION_URL)) {
            dataSourceConfigure.setConnectionUrl(getAttribute(resource, CONNECTION_URL));
        }
        if (hasAttribute(resource, DRIVER_CLASS_NAME)) {
            dataSourceConfigure.setDriverClass(getAttribute(resource, DRIVER_CLASS_NAME));
        }

        // The following are common options
        List<String> properties = new ArrayList<>();
        properties.add(TESTWHILEIDLE);
        properties.add(TESTONBORROW);
        properties.add(TESTONRETURN);

        properties.add(VALIDATIONQUERY);
        properties.add(VALIDATIONQUERYTIMEOUT);
        properties.add(VALIDATIONINTERVAL);

        properties.add(TIMEBETWEENEVICTIONRUNSMILLIS);
        properties.add(MINEVICTABLEIDLETIMEMILLIS);

        properties.add(MAX_AGE);
        properties.add(MAXACTIVE);
        properties.add(MINIDLE);
        properties.add(MAXWAIT);
        properties.add(INITIALSIZE);

        properties.add(REMOVEABANDONEDTIMEOUT);
        properties.add(REMOVEABANDONED);
        properties.add(LOGABANDONED);

        properties.add(VALIDATORCLASSNAME);
        properties.add(INIT_SQL);
        properties.add(INIT_SQL2);
        processProperties(dataSourceConfigure, map, resource, properties);

        /**
         * Special handing for connectionProperties and option. If connectionProperties is not set, we will use option's
         * value if connectionProperties is set, we will ignore option's value
         */
        if (hasAttribute(resource, CONNECTIONPROPERTIES)) {
            String value = getAttribute(resource, CONNECTIONPROPERTIES);
            dataSourceConfigure.setProperty(CONNECTIONPROPERTIES, value);
            map.put(CONNECTIONPROPERTIES, value);
        } else {
            if (hasAttribute(resource, OPTION)) {
                String value = getAttribute(resource, OPTION);
                dataSourceConfigure.setProperty(OPTION, value);
                map.put(CONNECTIONPROPERTIES, value);
            }
        }

        return dataSourceConfigure;
    }

    private void processProperties(DataSourceConfigure dataSourceConfigure, Map<String, String> map, Node resource,
            List<String> properties) {
        for (String property : properties) {
            if (hasAttribute(resource, property)) {
                String value = getAttribute(resource, property);
                dataSourceConfigure.setProperty(property, value);
                map.put(property, value);
            }
        }
    }

    private List<Node> getChildNodes(Node node, String name) {
        List<Node> nodes = new ArrayList<>();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (!children.item(i).getNodeName().equalsIgnoreCase(name))
                continue;
            nodes.add(children.item(i));
        }
        return nodes;
    }

    private boolean hasAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName) != null;
    }

    private String getAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    public boolean isDataSourceXmlExist() {
        return dataSourceXmlExist;
    }

    public String getDataSourceXmlLocation() {
        return dataSourceXmlLocation;
    }

}

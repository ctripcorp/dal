package com.ctrip.platform.dal.dao.configure;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DatabasePoolConfigParser implements DatabasePoolConfigConstants {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePoolConfigParser.class);
    private static DatabasePoolConfigParser poolConfigParser = new DatabasePoolConfigParser();
    private static final String DBPOOL_CONFIG = "datasource.xml";

    private static final String LOCATION = "location";

    private static final String RESOURCE_NODE = "Datasource";
    private static final String NAME = "name";
    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";
    private static final String CONNECTION_URL = "connectionUrl";
    private static final String DRIVER_CLASS_NAME = "driverClassName";

    public static final boolean DEFAULT_TESTWHILEIDLE = false;
    public static final boolean DEFAULT_TESTONBORROW = false;
    public static final boolean DEFAULT_TESTONRETURN = false;
    public static final String DEFAULT_VALIDATIONQUERY = "SELECT 1";
    public static final long DEFAULT_VALIDATIONINTERVAL = 30000L;
    public static final int DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS = 30000;
    public static final int DEFAULT_MAXACTIVE = 100;
    public static final int DEFAULT_MINIDLE = 0;
    public static final int DEFAULT_MAXWAIT = 10000;
    public static final int DEFAULT_MAXAGE = 30000;
    public static final int DEFAULT_INITIALSIZE = 10;
    public static final int DEFAULT_REMOVEABANDONEDTIMEOUT = 60;
    public static final boolean DEFAULT_REMOVEABANDONED = true;
    public static final boolean DEFAULT_LOGABANDONED = true;
    public static final int DEFAULT_MINEVICTABLEIDLETIMEMILLIS = 30000;
    public static final String DEFAULT_CONNECTIONPROPERTIES = null;
    public static final boolean DEFAULT_JMXENABLED = true;
    public static final String DEFAULT_JDBCINTERCEPTORS = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
            + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";
    public static final String DEFAULT_VALIDATORCLASSNAME = "com.ctrip.platform.dal.dao.datasource.DataSourceValidator";

    private Map<String, DatabasePoolConfig> poolConfigs = new ConcurrentHashMap<String, DatabasePoolConfig>();

    private String databaseConfigLocation = null;

    private boolean datasourceXmlExist = false;

    private DatabasePoolConfigParser() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = DatabasePoolConfigParser.class.getClassLoader();
            }
            URL url = classLoader.getResource(DBPOOL_CONFIG);
            if (url != null) {
                datasourceXmlExist = true;
                parse(url.openStream());
                logger.info("datasource property will use file :" + url.getFile());
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static DatabasePoolConfigParser getInstance() {
        return poolConfigParser;
    }

    public DatabasePoolConfig getDatabasePoolConifg(String name) {
        return poolConfigs.get(name);
    }

    public boolean contains(String name) {
        return poolConfigs.containsKey(name);
    }

    public void addDatabasePoolConifg(String name, DatabasePoolConfig config) {
        poolConfigs.put(name, config);
    }

    public void copyDatabasePoolConifg(String sampleName, String newName) {
        DatabasePoolConfig oldConfig = poolConfigs.get(sampleName);

        DatabasePoolConfig newConfig = new DatabasePoolConfig();
        newConfig.setName(newName);
        newConfig.setPoolProperties(oldConfig.getPoolProperties());
        newConfig.setMap(new HashMap<>(oldConfig.getMap()));
        poolConfigs.put(newName, newConfig);
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
            DatabasePoolConfig poolConfig = parseResource(resource);
            poolConfigs.put(poolConfig.getName(), poolConfig);
        }
    }

    private DatabasePoolConfig parseResource(Node resource) {
        DatabasePoolConfig poolConfig = new DatabasePoolConfig();
        poolConfig.setName(getAttribute(resource, NAME));
        Map<String, String> map = new HashMap<>();
        poolConfig.setMap(map);
        PoolProperties prop = poolConfig.getPoolProperties();
        // The following are key connection parameters, developer do not need to provide them in case the configure
        // provider is set
        if (hasAttribute(resource, USER_NAME)) {
            prop.setUsername(getAttribute(resource, USER_NAME));
        }
        if (hasAttribute(resource, PASSWORD)) {
            prop.setPassword(getAttribute(resource, PASSWORD));
        }
        if (hasAttribute(resource, CONNECTION_URL)) {
            prop.setUrl(getAttribute(resource, CONNECTION_URL));
        }
        if (hasAttribute(resource, DRIVER_CLASS_NAME)) {
            prop.setDriverClassName(getAttribute(resource, DRIVER_CLASS_NAME));
        }
        // The following are common options
        if (hasAttribute(resource, TESTWHILEIDLE)) {
            String value = getAttribute(resource, TESTWHILEIDLE);
            boolean testWhileIdle = Boolean.parseBoolean(value);
            prop.setTestWhileIdle(testWhileIdle);
            map.put(TESTWHILEIDLE, value);
        }
        if (hasAttribute(resource, TESTONBORROW)) {
            String value = getAttribute(resource, TESTONBORROW);
            boolean testOnBorrow = Boolean.parseBoolean(value);
            prop.setTestOnBorrow(testOnBorrow);
            map.put(TESTONBORROW, value);
        }
        if (hasAttribute(resource, TESTONRETURN)) {
            String value = getAttribute(resource, TESTONRETURN);
            boolean testOnReturn = Boolean.parseBoolean(value);
            prop.setTestOnReturn(testOnReturn);
            map.put(TESTONRETURN, value);
        }
        if (hasAttribute(resource, VALIDATIONQUERY)) {
            String validationQuery = getAttribute(resource, VALIDATIONQUERY);
            prop.setValidationQuery(validationQuery);
            map.put(VALIDATIONQUERY, validationQuery);
        }
        if (hasAttribute(resource, VALIDATIONINTERVAL)) {
            String value = getAttribute(resource, VALIDATIONINTERVAL);
            long validationInterval = Long.parseLong(value);
            prop.setValidationInterval(validationInterval);
            map.put(VALIDATIONINTERVAL, value);
        }
        if (hasAttribute(resource, TIMEBETWEENEVICTIONRUNSMILLIS)) {
            String value = getAttribute(resource, TIMEBETWEENEVICTIONRUNSMILLIS);
            int timeBetweenEvictionRunsMillis = Integer.parseInt(value);
            prop.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            map.put(TIMEBETWEENEVICTIONRUNSMILLIS, value);
        }
        if (hasAttribute(resource, MAX_AGE)) {
            String value = getAttribute(resource, MAX_AGE);
            int maxAge = Integer.parseInt(value);
            prop.setMaxAge(maxAge);
            map.put(MAX_AGE, value);
        }
        if (hasAttribute(resource, MAXACTIVE)) {
            String value = getAttribute(resource, MAXACTIVE);
            int maxActive = Integer.parseInt(value);
            prop.setMaxActive(maxActive);
            map.put(MAXACTIVE, value);
        }
        if (hasAttribute(resource, MINIDLE)) {
            String value = getAttribute(resource, MINIDLE);
            int minIdle = Integer.parseInt(value);
            prop.setMinIdle(minIdle);
            map.put(MINIDLE, value);
        }
        if (hasAttribute(resource, MAXWAIT)) {
            String value = getAttribute(resource, MAXWAIT);
            int maxWait = Integer.parseInt(value);
            prop.setMaxWait(maxWait);
            map.put(MAXWAIT, value);
        }
        if (hasAttribute(resource, INITIALSIZE)) {
            String value = getAttribute(resource, INITIALSIZE);
            int initialSize = Integer.parseInt(value);
            prop.setInitialSize(initialSize);
            map.put(INITIALSIZE, value);
        }
        if (hasAttribute(resource, REMOVEABANDONEDTIMEOUT)) {
            String value = getAttribute(resource, REMOVEABANDONEDTIMEOUT);
            int removeAbandonedTimeout = Integer.parseInt(value);
            prop.setRemoveAbandonedTimeout(removeAbandonedTimeout);
            map.put(REMOVEABANDONEDTIMEOUT, value);
        }
        if (hasAttribute(resource, REMOVEABANDONED)) {
            String value = getAttribute(resource, REMOVEABANDONED);
            boolean removeAbandoned = Boolean.parseBoolean(value);
            prop.setRemoveAbandoned(removeAbandoned);
            map.put(REMOVEABANDONED, value);
        }
        if (hasAttribute(resource, LOGABANDONED)) {
            String value = getAttribute(resource, LOGABANDONED);
            boolean logAbandoned = Boolean.parseBoolean(value);
            prop.setLogAbandoned(logAbandoned);
            map.put(LOGABANDONED, value);
        }
        if (hasAttribute(resource, MINEVICTABLEIDLETIMEMILLIS)) {
            String value = getAttribute(resource, MINEVICTABLEIDLETIMEMILLIS);
            int minEvictableIdleTimeMillis = Integer.parseInt(value);
            prop.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
            map.put(MINEVICTABLEIDLETIMEMILLIS, value);
        }
        if (hasAttribute(resource, INIT_SQL)) {
            String value = getAttribute(resource, INIT_SQL);
            prop.setInitSQL(value);
            map.put(INIT_SQL, value);
        }
        if (hasAttribute(resource, INIT_SQL2)) {
            String value = getAttribute(resource, INIT_SQL2);
            prop.setInitSQL(value);
            map.put(INIT_SQL2, value);
        }

        /**
         * Special handing for connectionProperties and option. If connectionProperties is not set, we will use option's
         * value if connectionProperties is set, we will ignore option's value
         */
        if (hasAttribute(resource, CONNECTIONPROPERTIES)) {
            String value = getAttribute(resource, CONNECTIONPROPERTIES);
            prop.setConnectionProperties(value);
            map.put(CONNECTIONPROPERTIES, value);
        } else {
            if (hasAttribute(resource, OPTION)) {
                String value = getAttribute(resource, OPTION);
                prop.setConnectionProperties(value);
                map.put(CONNECTIONPROPERTIES, value);
            }
        }

        if (hasAttribute(resource, VALIDATORCLASSNAME)) {
            String value = getAttribute(resource, VALIDATORCLASSNAME);
            prop.setValidatorClassName(value);
            map.put(VALIDATORCLASSNAME, value);
        }

        return poolConfig;
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

    private boolean hasAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName) != null;
    }

    private String getAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    public boolean isDatasourceXmlExist() {
        return datasourceXmlExist;
    }
}

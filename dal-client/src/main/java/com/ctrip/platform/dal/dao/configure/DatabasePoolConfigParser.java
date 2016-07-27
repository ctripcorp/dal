package com.ctrip.platform.dal.dao.configure;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;

public class DatabasePoolConfigParser {
	
	private static final Logger logger = LoggerFactory.getLogger(DataSourceLocator.class);
	private static DatabasePoolConfigParser poolConfigParser = new DatabasePoolConfigParser();
	private static final String DBPOOL_CONFIG = "datasource.xml";
	
	private static final String LOCATION = "location";
	
	private static final String RESOURCE_NODE = "Datasource";
	private static final String NAME = "name";
	private static final String USER_NAME = "userName";
	private static final String PASSWORD = "password";
	private static final String CONNECTION_URL = "connectionUrl";
	private static final String DRIVER_CLASS_NAME = "driverClassName";
	private static final String TESTWHILEIDLE = "testWhileIdle";
	private static final String TESTONBORROW = "testOnBorrow";
	private static final String TESTONRETURN = "testOnReturn";
	private static final String VALIDATIONQUERY = "validationQuery";
	private static final String VALIDATIONINTERVAL = "validationInterval";
	private static final String TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";
	private static final String MAX_AGE = "maxAge";
	private static final String MAXACTIVE = "maxActive";
	private static final String MINIDLE = "minIdle";
	private static final String MAXWAIT = "maxWait";
	private static final String INITIALSIZE = "initialSize";
	private static final String REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
	private static final String REMOVEABANDONED = "removeAbandoned";
	private static final String LOGABANDONED = "logAbandoned";
	private static final String MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
	private static final String CONNECTIONPROPERTIES = "connectionProperties";
	private static final String INIT_SQL = "initSql";
	private static final String OPTION = "option";
	
	public static final boolean DEFAULT_TESTWHILEIDLE = true;
	public static final boolean DEFAULT_TESTONBORROW = false;
	public static final boolean DEFAULT_TESTONRETURN = false;
	public static final String DEFAULT_VALIDATIONQUERY = "SELECT 1";
	public static final long DEFAULT_VALIDATIONINTERVAL = 30000L;
	public static final int DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS = 30000;
	public static final int DEFAULT_MAXACTIVE = 100;
	public static final int DEFAULT_MINIDLE = 10;
	public static final int DEFAULT_MAXWAIT = 10000;
	public static final int DEFAULT_INITIALSIZE = 10;
	public static final int DEFAULT_REMOVEABANDONEDTIMEOUT = 60;
	public static final boolean DEFAULT_REMOVEABANDONED = true;
	public static final boolean DEFAULT_LOGABANDONED = true;
	public static final int DEFAULT_MINEVICTABLEIDLETIMEMILLIS = 30000;
	public static final String DEFAULT_CONNECTIONPROPERTIES = null;
	public static final boolean DEFAULT_JMXENABLED = true;
	public static final String DEFAULT_JDBCINTERCEPTORS = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
	          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";
	
	private Map<String, DatabasePoolConifg> poolConfigs = new HashMap<String, DatabasePoolConifg>();
	
	private String databaseConfigLocation = null;
	
	private DatabasePoolConfigParser() {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null) {
				classLoader = DatabasePoolConfigParser.class.getClassLoader();
			}
			URL url = classLoader.getResource(DBPOOL_CONFIG);
			if (url == null) {
				logger.warn(DBPOOL_CONFIG + " is not exist in the root directory of classpath.");
			} else {
				parse(url.openStream());
				logger.info("datasource property will use file :" + url.getFile());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static DatabasePoolConfigParser getInstance() {
		return poolConfigParser;
	}
	
	public DatabasePoolConifg getDatabasePoolConifg(String name) {
		return poolConfigs.get(name);
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
			DatabasePoolConifg poolConfig = parseResource(resource);
			poolConfigs.put(poolConfig.getName(), poolConfig);
		}
	}
	
	private DatabasePoolConifg parseResource(Node resource) {
		DatabasePoolConifg poolConfig = new DatabasePoolConifg();
		poolConfig.setName(getAttribute(resource, NAME));
		PoolProperties prop = poolConfig.getPoolProperties();
		// The following are key connection parameters, developer do not need to provide them in case the configure provider is set
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
			boolean testWhileIdle = Boolean.parseBoolean(getAttribute(resource, TESTWHILEIDLE));
			prop.setTestWhileIdle(testWhileIdle);
		}
		if (hasAttribute(resource, TESTONBORROW)) {
			boolean testOnBorrow = Boolean.parseBoolean(getAttribute(resource, TESTONBORROW));
			prop.setTestOnBorrow(testOnBorrow);
		}
		if (hasAttribute(resource, TESTONRETURN)) {
			boolean testOnReturn = Boolean.parseBoolean(getAttribute(resource, TESTONRETURN));
			prop.setTestOnReturn(testOnReturn);
		}
		if (hasAttribute(resource, VALIDATIONQUERY)) {
			String validationQuery = getAttribute(resource, VALIDATIONQUERY);
			prop.setValidationQuery(validationQuery);
		}
		if (hasAttribute(resource, VALIDATIONINTERVAL)) {
			long validationInterval = Long.parseLong(getAttribute(resource, VALIDATIONINTERVAL));
			prop.setValidationInterval(validationInterval);
		}
		if (hasAttribute(resource, TIMEBETWEENEVICTIONRUNSMILLIS)) {
			int timeBetweenEvictionRunsMillis = Integer.parseInt(getAttribute(resource, TIMEBETWEENEVICTIONRUNSMILLIS));
			prop.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		}
		if (hasAttribute(resource, MAX_AGE)) {
			int maxAge = Integer.parseInt(getAttribute(resource, MAX_AGE));
			prop.setMaxAge(maxAge);
		}
		if (hasAttribute(resource, MAXACTIVE)) {
			int maxActive = Integer.parseInt(getAttribute(resource, MAXACTIVE));
			prop.setMaxActive(maxActive);
		}
		if (hasAttribute(resource, MINIDLE)) {
			int minIdle = Integer.parseInt(getAttribute(resource, MINIDLE));
			prop.setMinIdle(minIdle);
		}
		if (hasAttribute(resource, MAXWAIT)) {
			int maxWait = Integer.parseInt(getAttribute(resource, MAXWAIT));
			prop.setMaxWait(maxWait);
		}
		if (hasAttribute(resource, INITIALSIZE)) {
			int initialSize = Integer.parseInt(getAttribute(resource, INITIALSIZE));
			prop.setInitialSize(initialSize);
		}
		if (hasAttribute(resource, REMOVEABANDONEDTIMEOUT)) {
			int removeAbandonedTimeout = Integer.parseInt(getAttribute(resource, REMOVEABANDONEDTIMEOUT));
			prop.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		}
		if (hasAttribute(resource, REMOVEABANDONED)) {
			boolean removeAbandoned = Boolean.parseBoolean(getAttribute(resource, REMOVEABANDONED));
			prop.setRemoveAbandoned(removeAbandoned);
		}
		if (hasAttribute(resource, LOGABANDONED)) {
			boolean logAbandoned = Boolean.parseBoolean(getAttribute(resource, LOGABANDONED));
			prop.setLogAbandoned(logAbandoned);
		}
		if (hasAttribute(resource, MINEVICTABLEIDLETIMEMILLIS)) {
			int minEvictableIdleTimeMillis = Integer.parseInt(getAttribute(resource, MINEVICTABLEIDLETIMEMILLIS));
			prop.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		}
		if (hasAttribute(resource, CONNECTIONPROPERTIES)) {
			prop.setConnectionProperties(getAttribute(resource, CONNECTIONPROPERTIES));
		}
		if (hasAttribute(resource, INIT_SQL)) {
			prop.setInitSQL(getAttribute(resource, INIT_SQL));
		}		
		if (hasAttribute(resource, OPTION)) {
			poolConfig.setOption(getAttribute(resource, OPTION));
		}
		return poolConfig;
	}
	
	private List<Node> getChildNodes(Node node, String name) {
		List<Node> nodes = new ArrayList<Node>();
		NodeList children = node.getChildNodes();
		for(int i = 0; i < children.getLength(); i++){
			if(!children.item(i).getNodeName().equalsIgnoreCase(name))
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
	
	
}

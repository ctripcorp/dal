package com.ctrip.datasource.configure;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DatabasePoolConfigParser {
	
	private static final Log log = LogFactory.getLog(DatabasePoolConfigParser.class);
	private static DatabasePoolConfigParser poolConfigParser = new DatabasePoolConfigParser();
	private static final String DBPOOL_CONFIG = "context.xml";
	
	private static final String LOCATION = "location";
	
	private static final String RESOURCE_NODE = "Resource";
	private static final String NAME = "name";
	private static final String TESTWHILEIDLE = "testWhileIdle";
	private static final String TESTONBORROW = "testOnBorrow";
	private static final String TESTONRETURN = "testOnReturn";
	private static final String VALIDATIONQUERY = "validationQuery";
	private static final String VALIDATIONINTERVAL = "validationInterval";
	private static final String TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";
	private static final String MAXACTIVE = "maxActive";
	private static final String MINIDLE = "minIdle";
	private static final String MAXWAIT = "maxWait";
	private static final String INITIALSIZE = "initialSize";
	private static final String REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
	private static final String REMOVEABANDONED = "removeAbandoned";
	private static final String LOGABANDONED = "logAbandoned";
	private static final String MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
	private static final String CONNECTIONPROPERTIES = "connectionProperties";
	private static final String OPTION = "option";
	
	private Map<String, DatabasePoolConifg> poolConfigs = new HashMap<String, DatabasePoolConifg>();
	
	private String databaseConfigLocation = null;
	
	private DatabasePoolConfigParser() {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null) {
				classLoader = DatabasePoolConfigParser.class.getClassLoader();
			}
			parse(classLoader.getResource(DBPOOL_CONFIG).openStream());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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
					log.warn(e1.getMessage(), e1);
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
		if (hasAttribute(resource, TESTWHILEIDLE)) {
			boolean testWhileIdle = Boolean.parseBoolean(getAttribute(resource, TESTWHILEIDLE));
			poolConfig.setTestWhileIdle(testWhileIdle);
		}
		if (hasAttribute(resource, TESTONBORROW)) {
			boolean testOnBorrow = Boolean.parseBoolean(getAttribute(resource, TESTONBORROW));
			poolConfig.setTestOnBorrow(testOnBorrow);
		}
		if (hasAttribute(resource, TESTONRETURN)) {
			boolean testOnReturn = Boolean.parseBoolean(getAttribute(resource, TESTONRETURN));
			poolConfig.setTestOnReturn(testOnReturn);
		}
		if (hasAttribute(resource, VALIDATIONQUERY)) {
			String validationQuery = getAttribute(resource, VALIDATIONQUERY);
			poolConfig.setValidationQuery(validationQuery);
		}
		if (hasAttribute(resource, VALIDATIONINTERVAL)) {
			long validationInterval = Long.parseLong(getAttribute(resource, VALIDATIONINTERVAL));
			poolConfig.setValidationInterval(validationInterval);
		}
		if (hasAttribute(resource, TIMEBETWEENEVICTIONRUNSMILLIS)) {
			int timeBetweenEvictionRunsMillis = Integer.parseInt(getAttribute(resource, TIMEBETWEENEVICTIONRUNSMILLIS));
			poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		}
		if (hasAttribute(resource, MAXACTIVE)) {
			int maxActive = Integer.parseInt(getAttribute(resource, MAXACTIVE));
			poolConfig.setMaxActive(maxActive);
		}
		if (hasAttribute(resource, MINIDLE)) {
			int minIdle = Integer.parseInt(getAttribute(resource, MINIDLE));
			poolConfig.setMinIdle(minIdle);
		}
		if (hasAttribute(resource, MAXWAIT)) {
			int maxWait = Integer.parseInt(getAttribute(resource, MAXWAIT));
			poolConfig.setMaxWait(maxWait);
		}
		if (hasAttribute(resource, INITIALSIZE)) {
			int initialSize = Integer.parseInt(getAttribute(resource, INITIALSIZE));
			poolConfig.setInitialSize(initialSize);
		}
		if (hasAttribute(resource, REMOVEABANDONEDTIMEOUT)) {
			int removeAbandonedTimeout = Integer.parseInt(getAttribute(resource, REMOVEABANDONEDTIMEOUT));
			poolConfig.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		}
		if (hasAttribute(resource, REMOVEABANDONED)) {
			boolean removeAbandoned = Boolean.parseBoolean(getAttribute(resource, REMOVEABANDONED));
			poolConfig.setRemoveAbandoned(removeAbandoned);
		}
		if (hasAttribute(resource, LOGABANDONED)) {
			boolean logAbandoned = Boolean.parseBoolean(getAttribute(resource, LOGABANDONED));
			poolConfig.setLogAbandoned(logAbandoned);
		}
		if (hasAttribute(resource, MINEVICTABLEIDLETIMEMILLIS)) {
			int minEvictableIdleTimeMillis = Integer.parseInt(getAttribute(resource, MINEVICTABLEIDLETIMEMILLIS));
			poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		}
		if (hasAttribute(resource, CONNECTIONPROPERTIES)) {
			poolConfig.setConnectionProperties(getAttribute(resource, CONNECTIONPROPERTIES));
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

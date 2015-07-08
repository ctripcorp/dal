package com.ctrip.platform.dal.dao.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.NullLogger;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.dao.task.DefaultTaskFactory;

/*
<dal name="dal.prize.test">
  <databaseSets>
    <!--这里需要注意，mod的取值，如果city_id余2，则要么是1，要么是0，那么在下面配置的时候，sharding字段的取值就需要覆盖所有情况-->
    <databaseSet name="shardingtestMaster" provider="sqlProvider"  shardStrategy="class=Arch.Data.DbEngine.Shard.ShardModStrategy;columns=city_id;mod=2" >
      <add name="shardingtestMaster_M1" databaseType="Master" sharding="0" connectionString="shardingtestMaster"/>
      <add name="shardingtestMaster_M2" databaseType="Master" sharding="1" connectionString="shardingtestMaster"/>
      <add name="shardingtestMaster_S1" databaseType="Slave" sharding="0" connectionString="shardingtestSlave"/>
      <add name="shardingtestMaster_S2" databaseType="Slave" sharding="1" connectionString="shardingtestSlave"/>
    </databaseSet>
  </databaseSets>
  <databaseProviders>
    <add name="sqlProvider" type="Arch.Data.DbEngine.Providers.SqlDatabaseProvider,Arch.Data"/>
    <add name="mySqlProvider" type="Arch.Data.MySqlProvider.MySqlDatabaseProvider,Arch.Data.MySqlProvider"/>
  </databaseProviders>
   
  <logListener enabled="true/false">
	<logger>com.xxx.xxx.xxx</logger>
    <settings>
	  <encrypt>true</encrypt>
	  <secretKey>dalctripcn</secretKey>
	  <sampling>false</sampling>
	  <samplingLow>60</samplingLow>
	  <samplingHigh>5</samplingHigh>
	  <sampleMaxNum>5000</sampleMaxNum>
	  <sampleClearInterval>30</sampleClearInterval>
	  <simplified>true</simplified>
	  <asyncLogging>true</asyncLogging>
	  <capacity>10000</capacity>
	<settings>
  </logListener>
  <ConnectionLocator>
    <locator>com.xxx.xxx.xxx</locator>
    <settings>
	  <dc>{$DBDataCenter}</dc>
	<settings>
  </ConnectionLocator>
</dal>
 */
// For java we only process databaseSets. log and providers are covered elsewhere.

public class DalConfigureFactory {
	private static DalConfigureFactory factory = new DalConfigureFactory();
	private static final String DAL_CONFIG = "Dal.config";

	private static String NAME = "name";
	private static String DATABASE_SETS = "databaseSets";
	private static String DATABASE_SET = "databaseSet";
	private static String ADD = "add";
	private static String PROVIDER = "provider";
	private static String SHARD_STRATEGY = "shardStrategy";
	private static String SHARDING_STRATEGY = "shardingStrategy";
	private static String DATABASE_TYPE = "databaseType";
	private static String SHARDING = "sharding";
	private static String CONNECTION_STRING = "connectionString";
	private static String MASTER = "Master";
	private static String LOG_LISTENER = "LogListener";
	private static String TASK_FACTORY = "TaskFactory";
	private static String ENABLED = "enabled";
	private static String FACTORY = "factory";
	private static String LOGGER = "logger";
	private static String SETTINGS = "settings";
	private static String CONNECTION_LOCATOR = "ConnectionLocator";
	private static String LOCATOR = "locator";

	/**
	 * Load frmo classpath
	 * @return
	 * @throws Exception
	 */
	public static DalConfigure load() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = DalClientFactory.class.getClassLoader();
		}

		return load(classLoader.getResource(DAL_CONFIG));
	}
		
	public static DalConfigure load(URL url) throws Exception {
		return load(url.openStream());
	}
	
	public static DalConfigure load(String path) throws Exception {
		return load(new File(path));
	}
	
	public static DalConfigure load(File model) throws Exception {
		return load(new FileInputStream(model));
	}
	
	public static DalConfigure load(InputStream in) throws Exception {
		
		try{
			Document doc= DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			DalConfigure def = factory.getFromDocument(doc);
			in.close();
			return def;
		} finally {
			if(in != null)
				try{
					in.close();
				}catch(Throwable e1){
					
				}
		}
	}

	public DalConfigure getFromDocument(Document doc) throws Exception{
		Element root = doc.getDocumentElement();

		String name = getAttribute(root, NAME);

		DalLogger logger = readLogListener(getChildNode(root, LOG_LISTENER));
		
		DalTaskFactory factory = readTaskFactory(getChildNode(root, TASK_FACTORY));
		
		DalConnectionLocator locator = readConnectionLocator(getChildNode(root, CONNECTION_LOCATOR));
		
		Map<String, DatabaseSet> databaseSets = readDatabaseSets(getChildNode(root, DATABASE_SETS), logger);
		
		return new DalConfigure(name, databaseSets, logger, locator, factory);
	}
	
	private Map<String, DatabaseSet> readDatabaseSets(Node databaseSetsNode, DalLogger logger) throws Exception {
		List<Node> databaseSetList = getChildNodes(databaseSetsNode, DATABASE_SET);
		Map<String, DatabaseSet> databaseSets = new HashMap<String, DatabaseSet>();
		for(int i = 0;i < databaseSetList.size(); i++) {
			DatabaseSet databaseSet = readDatabaseSet(databaseSetList.get(i), logger);
			databaseSets.put(databaseSet.getName(), databaseSet);
		}
		return databaseSets;
	}
	
	private DatabaseSet readDatabaseSet(Node databaseSetNode, DalLogger logger) throws Exception {
		List<Node> databaseList = getChildNodes(databaseSetNode, ADD);
		Map<String, DataBase> databases = new HashMap<String, DataBase>();
		for(int i = 0;i < databaseList.size(); i++) {
			DataBase database = readDataBase(databaseList.get(i));
			databases.put(database.getName(), database);
		}
		
		if(hasAttribute(databaseSetNode, SHARD_STRATEGY))
			return new DatabaseSet(
					getAttribute(databaseSetNode, NAME),
					getAttribute(databaseSetNode, PROVIDER),
					getAttribute(databaseSetNode, SHARD_STRATEGY),
					databases, logger);
		else if(hasAttribute(databaseSetNode, SHARDING_STRATEGY))
			return new DatabaseSet(
					getAttribute(databaseSetNode, NAME),
					getAttribute(databaseSetNode, PROVIDER),
					getAttribute(databaseSetNode, SHARDING_STRATEGY),
					databases, logger);
		else
			return new DatabaseSet(
					getAttribute(databaseSetNode, NAME),
					getAttribute(databaseSetNode, PROVIDER),
					databases, logger);
	}
	
	private DataBase readDataBase(Node dataBaseNode) {
		return new DataBase(
				getAttribute(dataBaseNode, NAME),
				getAttribute(dataBaseNode, DATABASE_TYPE).equals(MASTER),
				getAttribute(dataBaseNode, SHARDING),
				getAttribute(dataBaseNode, CONNECTION_STRING));
	}
	
	private DalConnectionLocator readConnectionLocator(Node connLocatorNode) throws Exception {
		if(connLocatorNode == null)
			throw new NullPointerException("There is no ConnectionLocator node found. Please check manul to setup dal config properly.");
		
		Node locatorNode = getChildNode(connLocatorNode, LOCATOR);
		if(locatorNode == null)
			throw new NullPointerException("There is no locator node found. Please check manul to setup dal config properly.");

		DalConnectionLocator locator = (DalConnectionLocator)Class.forName(locatorNode.getTextContent()).newInstance();
		
		Node settingsNode = getChildNode(connLocatorNode, SETTINGS);
		Map<String, String> settings = new HashMap<>();

		if(settingsNode != null) {
			NodeList children = settingsNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++) {
				if(children.item(i).getNodeType() == Node.ELEMENT_NODE)
					settings.put(children.item(i).getNodeName(), children.item(i).getTextContent());
			}
		}
		
		locator.initLocator(settings);
		return locator;
	}
	
	private DalLogger readLogListener(Node logListenerNode) throws Exception {
		DalLogger logger = new NullLogger();
		if(logListenerNode == null)
			return logger;
		
		if(hasAttribute(logListenerNode, ENABLED)){
			boolean enabled = Boolean.parseBoolean(getAttribute(logListenerNode, ENABLED));
			if(enabled == false)
				return logger;
		}
		
		Node loggerNode = getChildNode(logListenerNode, LOGGER);
		if(loggerNode == null)
			return logger;

		logger = (DalLogger)Class.forName(loggerNode.getTextContent()).newInstance();
		
		Node settingsNode = getChildNode(logListenerNode, SETTINGS);
		Map<String, String> settings = new HashMap<>();

		if(settingsNode != null) {
			NodeList children = settingsNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++) {
				if(children.item(i).getNodeType() == Node.ELEMENT_NODE)
					settings.put(children.item(i).getNodeName(), children.item(i).getTextContent());
			}
		}
		
		logger.initLogger(settings);
		return  logger;
	}
	
	private DalTaskFactory readTaskFactory(Node taskFactoryNode) throws Exception {
		DalTaskFactory factory = new DefaultTaskFactory();
		if(taskFactoryNode == null)
			return factory;
		
		Node factoryNode = getChildNode(taskFactoryNode, FACTORY);
		if(factoryNode == null)
			return factory;

		factory = (DalTaskFactory)Class.forName(factoryNode.getTextContent()).newInstance();
		
		Node settingsNode = getChildNode(taskFactoryNode, SETTINGS);
		Map<String, String> settings = new HashMap<>();

		if(settingsNode != null) {
			NodeList children = settingsNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++) {
				if(children.item(i).getNodeType() == Node.ELEMENT_NODE)
					settings.put(children.item(i).getNodeName(), children.item(i).getTextContent());
			}
		}
		
		factory.initialize(settings);
		return  factory;
	}
	
	private Node getChildNode(Node node, String name) {
		NodeList children = node.getChildNodes();
		Node found = null;
		for(int i = 0; i < children.getLength(); i++){
			if(!children.item(i).getNodeName().equalsIgnoreCase(name))
				continue;
			found = children.item(i);
			break;
		}
		return found;
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

	private boolean hasAttribute(Node node, String attributeName){
		return node.getAttributes().getNamedItem(attributeName) != null;		
	}
	
	private String getAttribute(Node node, String attributeName){
		return node.getAttributes().getNamedItem(attributeName).getNodeValue();
	}
}
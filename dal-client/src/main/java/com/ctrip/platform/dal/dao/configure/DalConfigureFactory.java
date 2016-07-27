package com.ctrip.platform.dal.dao.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.DefaultLogger;
import com.ctrip.platform.dal.dao.datasource.DefaultDalConnectionLocator;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.dao.task.DefaultTaskFactory;

/* 
 * The following are sample dal.config
<dal name="dal.prize.test">
  <databaseSets>
    <databaseSet name="SimpleShard" provider="sqlProvider" shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=index,tableIndex;mod=2;">
      <add  name="dao_test_sqlsvr_dbShard_0" databaseType="Master" sharding="0" connectionString="SimpleShard_0"/>
      <add  name="dao_test_sqlsvr_dbShard_1" databaseType="Master" sharding="1" connectionString="SimpleShard_1"/>
    </databaseSet>
    <databaseSet name="HotelPubDB" provider="sqlProvider">
      <add  name="HotelPubDB_M" databaseType="Master" sharding="" connectionString="dao_test_sqlsvr"/>
      <add  name="HotelPubDB_S1" databaseType="Slave" sharding="" connectionString="dao_test_sqlsvr"/>
      <add  name="HotelPubDB_S2" databaseType="Slave" sharding="" connectionString="dao_test_sqlsvr"/>
      <add  name="HotelPubDB_S3" databaseType="Slave" sharding="" connectionString="dao_test_sqlsvr"/>
    </databaseSet>
  </databaseSets>
  <logListener enabled="true/false">
	<logger>com.xxx.xxx.xxx</logger>
    <settings>
	  <encrypt>true</encrypt>
	  <sampling>false</sampling>
	<settings>
  </logListener>
  <ConnectionLocator>
    <locator>com.xxx.xxx.xxx</locator>
    <settings>
	  <dataSourceConfigureProvider>com.xxx.xxx.xxx</dataSourceConfigureProvider>
	  <serviceAddress>http:...</serviceAddress>
	  <appid>123456</appid>
	  <forceLocalConfig>true/false</forceLocalConfig>
	<settings>
  </ConnectionLocator>
  <TaskFactory>
      <factory>com.xxx.xxx.xxx</factory>
  </TaskFactory>
</dal>
 */
// For java we only process databaseSets. log and providers are covered elsewhere.

public class DalConfigureFactory {
	private static DalConfigureFactory factory = new DalConfigureFactory();
	private static final String DAL_XML = "dal.xml";
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
	private static String FACTORY = "factory";
	private static String LOGGER = "logger";
	private static String SETTINGS = "settings";
	private static String CONNECTION_LOCATOR = "ConnectionLocator";
	private static String LOCATOR = "locator";

	/**
	 * Load from classpath. For historic reason, we support both dal.xml and Dal.config
	 * for configure name. 
	 * @return
	 * @throws Exception
	 */
	public static DalConfigure load() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = DalClientFactory.class.getClassLoader();
		}
		
		URL dalconfigUrl = classLoader.getResource(DAL_XML);
		if(dalconfigUrl == null )
			dalconfigUrl = classLoader.getResource(DAL_CONFIG);
		
		if(dalconfigUrl == null)
			throw new IllegalStateException(
					"Can not find " + DAL_XML + " or " + DAL_CONFIG + " to initilize dal configure");
		
		return load(dalconfigUrl);
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

		DalLogger logger = readComponent(root, LOG_LISTENER, new DefaultLogger(), LOGGER);
		
		DalTaskFactory factory = readComponent(root, TASK_FACTORY, new DefaultTaskFactory(), FACTORY);
		
		DalConnectionLocator locator = readComponent(root, CONNECTION_LOCATOR, new DefaultDalConnectionLocator(), LOCATOR);

		Map<String, DatabaseSet> databaseSets = readDatabaseSets(getChildNode(root, DATABASE_SETS), logger);
		
		locator.setup(getAllDbNames(databaseSets));
		
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
	
	private Set<String> getAllDbNames(Map<String, DatabaseSet> databaseSets) {
		Set<String> dbNames = new HashSet<>();
		for(DatabaseSet dbSet: databaseSets.values()){
			for(DataBase db: dbSet.getDatabases().values()) {
				dbNames.add(db.getConnectionString());
			}
		}
		return dbNames;
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
	
	private <T extends DalComponent> T readComponent(Node root, String componentName, T defaultImpl, String implNodeName) throws Exception {
		Node node = getChildNode(root, componentName);
		T component = defaultImpl;
		
		if(node != null){
			Node implNode = getChildNode(node, implNodeName);
			if(implNode != null)
				component = (T)Class.forName(implNode.getTextContent()).newInstance();
		}
		
		component.initialize(getSettings(node));
		return  component;
	}
	
	private Map<String, String> getSettings(Node pNode) {
		Map<String, String> settings = new HashMap<>();
		
		if(pNode == null)
			return settings;
					
		Node settingsNode = getChildNode(pNode, SETTINGS);

		if(settingsNode != null) {
			NodeList children = settingsNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++) {
				if(children.item(i).getNodeType() == Node.ELEMENT_NODE)
					settings.put(children.item(i).getNodeName(), children.item(i).getTextContent());
			}
		}
		return settings;
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
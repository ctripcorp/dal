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
   
  <logListeners>
    <add name="clog" type="Arch.Data.Common.Logging.Listeners.CentralLoggingListener,Arch.Data" level="Information" setting=""/>
    <add name="textfile" type="Arch.Data.Common.Logging.Listeners.TextFileListener,Arch.Data" level="Information" setting="FileSize=4;FilePath=D:\log;FileName={0:yyyy_MM_dd_HH_mm_ss}.log;"/>
  </logListeners>
   
  <metrics name="centrallogging"/>
</dal>
 */
// For java we only process databaseSets. log and providers are covered elsewhere.

public class DalConfigureFactory {
	private static DalConfigureFactory factory = new DalConfigureFactory();
	
	private static String CONFIGURE_NT_LOCATION = "";
	private static String CONFIGURE_LINUX_LOCATION = "";
	private static String NAME = "name";
	private static String DATABASE_SETS = "databaseSets";
	private static String DATABASE_SET = "databaseSet";
	private static String ADD = "add";
	private static String PROVIDER = "provider";
	private static String SHARD_STRATEGY = "shardStrategy";
	private static String DATABASE_TYPE = "databaseType";
	private static String SHARDING = "sharding";
	private static String CONNECTION_STRING = "connectionString";
	private static String MASTER = "Master";

	public static DalConfigure load() throws Exception {
		if(System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1&& System.getProperty("opath.separator").equals("\\"))
			return load(CONFIGURE_NT_LOCATION);
		else
			return load(CONFIGURE_LINUX_LOCATION);
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
		Map<String, DatabaseSet> databaseSets = readDatabaseSets(getChildNode(root, DATABASE_SETS));
		
		return new DalConfigure(name, databaseSets);
	}
	
	private Map<String, DatabaseSet> readDatabaseSets(Node databaseSetsNode) throws Exception {
		List<Node> databaseSetList = getChildNodes(databaseSetsNode, DATABASE_SET);
		Map<String, DatabaseSet> databaseSets = new HashMap<String, DatabaseSet>();
		for(int i = 0;i < databaseSetList.size(); i++) {
			DatabaseSet databaseSet = readDatabaseSet(databaseSetList.get(i));
			databaseSets.put(databaseSet.getName(), databaseSet);
		}
		return databaseSets;
	}
	
	private DatabaseSet readDatabaseSet(Node databaseSetNode) throws Exception {
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
					databases );
		else
			return new DatabaseSet(
					getAttribute(databaseSetNode, NAME),
					getAttribute(databaseSetNode, PROVIDER),
					databases );
	}
	
	private DataBase readDataBase(Node dataBaseNode) {
		return new DataBase(
				getAttribute(dataBaseNode, NAME),
				getAttribute(dataBaseNode, DATABASE_TYPE).equals(MASTER),
				getAttribute(dataBaseNode, SHARDING),
				getAttribute(dataBaseNode, CONNECTION_STRING));
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
//		NamedNodeMap map = node.getAttributes();
//		for(int i = 0; i < map.getLength(); i++)
//			if(attributeName.equals(map.item(i).getNodeName()))
//				return map.item(i).getNodeValue();
//
//		return null;
	}
}
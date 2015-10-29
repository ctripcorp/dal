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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigParser;
import com.ctrip.security.encryption.Crypto;

public class AllInOneConfigureReader {

	private static final Log log = LogFactory.getLog(AllInOneConfigureReader.class);
	private static final String CLASSPATH_CONFIG_FILE = "/Database.Config";
	private static final String LINUX_DB_CONFIG_FILE = "/opt/ctrip/AppData/Database.Config";
	private static final String WIN_DB_CONFIG_FILE = "/D:/WebSites/CtripAppData/Database.Config";
	
	private static String DATABASE_ENTRY = "add";
	private static String DATABASE_ENTRY_NAME = "name";
	private static String DATABASE_ENTRY_CONNECTIONSTRING = "connectionString";
	private static String DEV_FLAG = "Version";
	
	private static final String DATABASE_CONFIG_LOCATION = "$classpath";
	private ConnectionStringParser parser = new ConnectionStringParser();
	
	public Map<String, DataSourceConfigure> getDataSourceConfigures(Set<String> dbNames, boolean forceLocal) {
		String location = getAllInOneConfigLocation();
		if (location == null) 
			return null;

		return parseDBAllInOneConfig(location, dbNames, forceLocal);
	}
	
	private String getAllInOneConfigLocation() {
		String location = DatabasePoolConfigParser.getInstance().getDatabaseConfigLocation();
		if (location != null && location.length() > 0) {
			if (DATABASE_CONFIG_LOCATION.equalsIgnoreCase(location)) {
				URL url = super.getClass().getResource(CLASSPATH_CONFIG_FILE);
				location = url == null ? null: url.getFile();
			}
		} else {
			String osName = null;
			try{
				osName=System.getProperty("os.name");
			} catch(SecurityException ex) {
				log.error(ex.getMessage());
				throw new RuntimeException(ex.getMessage(), ex);
			}
			location = osName!=null && osName.startsWith("Windows") ? WIN_DB_CONFIG_FILE : LINUX_DB_CONFIG_FILE;
		}
		
		return location;
	}
	
	private Map<String, DataSourceConfigure> parseDBAllInOneConfig(String absolutePath, Set<String> dbNames, boolean forceLocal) {
		Map<String, DataSourceConfigure> dataSourceConfigures = new HashMap<>();
		
		FileInputStream in = null;
		try {
			log.info("Allinone: using db config: " + absolutePath);
			File conFile = new File(absolutePath);
			in = new FileInputStream(conFile);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			Element root = doc.getDocumentElement();

			/**
			 * If the version is not set or forceLocal is not true, we assume it is not dev environment. In this case
			 * we should use titan service
			 */
			if(!(hasAttribute(root, DEV_FLAG) || forceLocal)){
				return null;
			}
			
			List<Node> databaseEntryList = getChildNodes(root, DATABASE_ENTRY);
			
			for (int i=0; i<databaseEntryList.size(); i++) {
				Node databaseEntry = databaseEntryList.get(i);
				
				if(!hasAttribute(databaseEntry, DATABASE_ENTRY_NAME) || !hasAttribute(databaseEntry, DATABASE_ENTRY_CONNECTIONSTRING)) {
					continue;
				}
				
				String name = getAttribute(databaseEntry, DATABASE_ENTRY_NAME);
				if(!dbNames.contains(name))
					continue;
				
				String connectionString = getAttribute(databaseEntry, DATABASE_ENTRY_CONNECTIONSTRING);
				try {
					DataSourceConfigure config = parser.parse(name, decrypt(name, connectionString));
					dataSourceConfigures.put(name, config);
				} catch(Throwable e) {
					String msg = String.format("Read %s file error, msg: %s", absolutePath, e.getMessage());
					log.error(msg, e);
				}
			}
			in.close();
			
			return dataSourceConfigures;
		} catch (Throwable e) {
			String msg = String.format("Read %s file error, msg: %s", absolutePath, e.getMessage());
			log.error(msg, e);
			throw new RuntimeException(msg, e);
		} finally {
			if (in != null) {
				try{
					in.close();
				} catch(Throwable e1) {
					log.warn(e1);
				}
			}
		}
	}
	
	private String decrypt(String dbname, String connStr) {
		if (connStr!=null && -1==connStr.indexOf(';')) { // connStr was encrypted
			try {
				return Crypto.getInstance().decrypt(connStr);
			} catch(Exception e) {
				log.error("decode " + dbname + " connectionString exception, msg:" + e.getMessage(), e);
				throw new RuntimeException("decode " + dbname + " connectionString exception, msg:" + e.getMessage(), e);
			}
		} else {
			return connStr;
		}
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
	
	private String getAttribute(Node node, String attributeName) {
		return node.getAttributes().getNamedItem(attributeName).getNodeValue();
	}
	
	private boolean hasAttribute(Node node, String attributeName) {
		return node.getAttributes().getNamedItem(attributeName) != null;		
	}	
}

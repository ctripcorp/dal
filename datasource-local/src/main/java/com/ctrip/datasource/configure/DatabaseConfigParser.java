package com.ctrip.datasource.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ctrip.security.encryption.Crypto;

public class DatabaseConfigParser {

	private static final Log log = LogFactory.getLog(DatabaseConfigParser.class);
	private static final String CLASSPATH_CONFIG_FILE = "/Database.Config";
	private static final String LINUX_DB_CONFIG_FILE = "/opt/ctrip/AppData/Database.Config";
	private static final String WIN_DB_CONFIG_FILE = "/D:/WebSites/CtripAppData/Database.Config";
	private static final Pattern dburlPattern = Pattern
			.compile("(data\\ssource|server|address|addr|network)=([^;]+)",Pattern.CASE_INSENSITIVE);
	private static final Pattern dbuserPattern = Pattern
			.compile("(uid|user\\sid)=([^;]+)",Pattern.CASE_INSENSITIVE);
	private static final Pattern dbpasswdPattern = Pattern
			.compile("(password|pwd)=([^;]+)",Pattern.CASE_INSENSITIVE);
	private static final Pattern dbnamePattern = Pattern
			.compile("(database|initial\\scatalog)=([^;]+)",Pattern.CASE_INSENSITIVE);
	private static final Pattern dbcharsetPattern = Pattern
			.compile("(charset)=([^;]+)",Pattern.CASE_INSENSITIVE);
	private static final Pattern dbportPattern = Pattern
			.compile("(port)=([^;]+)",Pattern.CASE_INSENSITIVE);
	private static final String PORT_SPLIT = ",";
	private static final String DBURL_SQLSERVER = "jdbc:sqlserver://%s:%s;DatabaseName=%s";
	private static final String DBURL_MYSQL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s";
//	private static final String DBURL_MYSQL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s"
//			+"&rewriteBatchedStatements=true&allowMultiQueries=true";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String DEFAULT_PORT = "3306";
	private static final String DRIVER_MYSQL ="com.mysql.jdbc.Driver";
	private static final String DRIVER_SQLSERVRE ="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	private static String DATABASE_ENTRY = "add";
	private static String DATABASE_ENTRY_NAME = "name";
	private static String DATABASE_ENTRY_CONNECTIONSTRING = "connectionString";
	
	private static DatabaseConfigParser allInOneConfigParser = new DatabaseConfigParser();
	
	private Map<String, String[]> props = new HashMap<String, String[]>();
	
	private static final String DATABASE_CONFIG_LOCATION = "$classpath";
	
	private DatabaseConfigParser() {
		initDBAllInOneConfig();
	}
	
	public static DatabaseConfigParser newInstance() {
		return allInOneConfigParser;
	}
	
	private void initDBAllInOneConfig() {
		InputStream in = null;
		String fileName = null;
		String location = DatabasePoolConfigParser.getInstance().getDatabaseConfigLocation();
		if (location != null && location.length() > 0) {
			if (DATABASE_CONFIG_LOCATION.equalsIgnoreCase(location)) {
				URL url = super.getClass().getResource(CLASSPATH_CONFIG_FILE);
				fileName = url.getFile();
				try {
					in = url.openStream();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			} else {
				File conFile = new File(location);
				if (!conFile.exists()) {
					log.error(location + " is not exist.");
					throw new RuntimeException(location + " is not exist.");
				} else {
					fileName = location;
					try {
						in = new FileInputStream(conFile);
					} catch (FileNotFoundException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		} else {
			String osName = null;
			try{
				osName=System.getProperty("os.name");
			} catch(SecurityException ex) {
				log.error(ex.getMessage());
				throw new RuntimeException(ex.getMessage(), ex);
			}
			if (osName!=null && osName.startsWith("Windows")) {
				File conFile = new File(WIN_DB_CONFIG_FILE);
				if (!conFile.exists()) {
					log.error(WIN_DB_CONFIG_FILE + " is not exist.");
					throw new RuntimeException(WIN_DB_CONFIG_FILE + " is not exist.");
				} else {
					fileName = WIN_DB_CONFIG_FILE;
					try {
						in = new FileInputStream(conFile);
					} catch (FileNotFoundException e) {
						log.error(e.getMessage(), e);
					}
				}
			} else {
				File conFile = new File(LINUX_DB_CONFIG_FILE);
				if(!conFile.exists()) {
					log.error(LINUX_DB_CONFIG_FILE + " is not exist.");
					throw new RuntimeException(LINUX_DB_CONFIG_FILE + " is not exist.");
				} else {
					fileName = LINUX_DB_CONFIG_FILE;
					try {
						in = new FileInputStream(conFile);
					} catch (FileNotFoundException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		
		if (in != null) {
			parseDBAllInOneConfig(in);
			log.info("Allinone: using db config: "+fileName);
		}
	}
	
	private void parseDBAllInOneConfig(InputStream in) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			Element root = doc.getDocumentElement();
			List<Node> databaseEntryList = getChildNodes(root, DATABASE_ENTRY);
			
			for (int i=0; i<databaseEntryList.size(); i++) {
				Node databaseEntry = databaseEntryList.get(i);
				if(!hasAttribute(databaseEntry, DATABASE_ENTRY_NAME)) {
					throw new RuntimeException("can not find attribute 'name' of 'add' node. add index is " + i);
				}
				if(!hasAttribute(databaseEntry, DATABASE_ENTRY_CONNECTIONSTRING)) {
					throw new RuntimeException("can not find attribute 'connectionString' of 'add' node. add index is " + i);
				}
				String name = getAttribute(databaseEntry, DATABASE_ENTRY_NAME);
				String connectionString = getAttribute(databaseEntry, DATABASE_ENTRY_CONNECTIONSTRING);
				DatabasePoolConifg poolConfig = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg(name);
				String[] prop = parseDBConnString(connectionString, poolConfig);
				props.put(name, prop);
			}
			in.close();
		} catch (Throwable e) {
			log.error("Read db config file error, msg: "+e.getMessage(), e);
			throw new RuntimeException("Read db config file error, msg: "+e.getMessage(), e);
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
	

	/**
	 * get and parse allinone config file
	 * 
	 * @return Hashtable<String name,String[]{dbname,port,url}>
	 */
	public Map<String, String[]> getDBAllInOneConfig() {
		return props;
	}
	
	/**
	 * reload all in one config
	 */
	public void reloadAllInOneConfig(){
		props.clear();
		initDBAllInOneConfig();
	}
	
	/**
	 * clear all in one config, be careful if using this method
	 */
	public void clear(){
		props.clear();
	}

	/**
	 * parse
	 * "Data Source=devdb.dev.sh.ctriptravel.com,28747;UID=uws_AllInOneKey_dev;password=!QAZ@WSX1qaz2wsx; database=AbacusDB;"
	 * 
	 * @return new String[]{url,username,passwd,driver}
	 */
	private String[] parseDBConnString(String connStr, DatabasePoolConifg poolConfig) {
		String[] dbInfos = new String[] { "", "", "","" };
		if (connStr!=null && -1==connStr.indexOf(';')) { // connStr was encrypted
			try {
				connStr = Crypto.getInstance().decrypt(connStr);
			} catch(Exception e) {
				log.error("decode connectionString exception, msg:" + e.getMessage(), e);
				throw new RuntimeException("decode connectionString exception, msg:" + e.getMessage(), e);
			}
		}
		try {
			String dbname = null, charset = null, dbhost = null;
			Matcher matcher = dbnamePattern.matcher(connStr);
			if (matcher.find()) {
				dbname = matcher.group(2);
			}

			matcher = dburlPattern.matcher(connStr);

			if (matcher.find()) {
				String[] dburls = matcher.group(2).split(PORT_SPLIT);
				dbhost = dburls[0];
				if (dburls.length == 2) {// is sqlserver
					dbInfos[0] = String.format(DBURL_SQLSERVER, dbhost, dburls[1], dbname);
					if (poolConfig!=null && poolConfig.getOption()!=null && poolConfig.getOption().length()>0) {
						dbInfos[0] = dbInfos[0] + ";" + poolConfig.getOption();
					}
					dbInfos[3] = DRIVER_SQLSERVRE;
				} else {// should be mysql
					matcher = dbcharsetPattern.matcher(connStr);
					if (matcher.find()) {
						charset = matcher.group(2);
					} else {
						charset = DEFAULT_ENCODING;
					}
					matcher = dbportPattern.matcher(connStr);
					if (matcher.find()) {
						dbInfos[0] = String.format(DBURL_MYSQL, dbhost, matcher.group(2), dbname, charset);
					} else {
						dbInfos[0] = String.format(DBURL_MYSQL, dbhost, DEFAULT_PORT, dbname, charset);
					}
					if (poolConfig!=null && poolConfig.getOption()!=null && poolConfig.getOption().length()>0) {
						dbInfos[0] = dbInfos[0] + "&" + poolConfig.getOption().replaceAll(";", "&");
					}
					dbInfos[3] = DRIVER_MYSQL;
				}
			}

			matcher = dbuserPattern.matcher(connStr);
			if (matcher.find()) {
				dbInfos[1]= matcher.group(2);
			}
			
			matcher = dbpasswdPattern.matcher(connStr);
			if (matcher.find()) {
				dbInfos[2]= matcher.group(2);
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		return dbInfos;
	}
}

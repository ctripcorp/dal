package com.ctrip.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.ctrip.security.encryption.AESCrypto;
import com.ctrip.security.encryption.Crypto;

public class AllInOneConfigParser {

	private static final Log log = LogFactory.getLog(AllInOneConfigParser.class);
	private static final String DB_CONFIG_FILE = "/opt/ctrip/appdata/allinone.config";
	private static final String DB_CONFIG_FILE_DES = "/opt/ctrip/appdata/database.config";
	private static final String DB_CONFIG_FILE_DES_Old = "/opt/ctrip/AppData/Database.Config";
	private static final String WIN_DB_CONFIG_FILE = "/D:/website/ctripappdata/allinone.config";
	private static final String WIN_DB_CONFIG_FILE_DES = "/D:/website/ctripappdata/database.config";
	//private static final String DB_CONFIG_FILE_DEV = "/Database-dev.config";
	private static final Pattern dbLinePattern = Pattern
			.compile(" name=\"([^\"]+)\" connectionString=\"([^\"]+)\"");
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
	//private static final String DBURL_SQLSERVER = "jdbc:jtds:sqlserver://%s:%s;DatabaseName=%s";
	private static final String DBURL_MYSQL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s"
			+"&rewriteBatchedStatements=true&allowMultiQueries=true";
	//private static final String DBURL_ORACLE = "jdbc:oracle:thin:@%s:%s:%s";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String DEFAULT_PORT = "3306";
	private static final String DRIVER_MYSQL ="com.mysql.jdbc.Driver";
	private static final String DRIVER_SQLSERVRE ="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	//private static final String DRIVER_SQLSERVRE ="net.sourceforge.jtds.jdbc.Driver";
	//Current Set isDES = true (always)
	private boolean isDES=true;
	
	private static AllInOneConfigParser allInOneConfigParser = new AllInOneConfigParser();
	
	private Map<String, String[]> props = new HashMap<String, String[]>();
	
	private AllInOneConfigParser(){
		initDBAllInOneConfig();
	}
	
	public static AllInOneConfigParser newInstance(){
		/*if(allInOneConfigParser==null){
			synchronized(AllInOneConfigParser.class){
				if(allInOneConfigParser==null){
					allInOneConfigParser=new AllInOneConfigParser();
				}
			}
		}*/
		return allInOneConfigParser;
	}
	
	private void initDBAllInOneConfig(){
		try{
			String fileName=DB_CONFIG_FILE;
			try{
				String osName=System.getProperty("os.name");
				if(osName!=null && osName.startsWith("Windows")){
					File conFile=new File(WIN_DB_CONFIG_FILE);
					if(!conFile.exists()){
						fileName=WIN_DB_CONFIG_FILE_DES;
						isDES=true;
					}else{
						fileName=WIN_DB_CONFIG_FILE;
					}
				}else{
					File conFile=new File(DB_CONFIG_FILE);
					if(!conFile.exists()){
						conFile=new File(DB_CONFIG_FILE_DES_Old);
						if(!conFile.exists()){
							fileName=DB_CONFIG_FILE_DES;
						}else{
							fileName=DB_CONFIG_FILE_DES_Old;
						}
						isDES=true;
					}
				}
			}catch(SecurityException ex){
				log.info(ex.getMessage());
			}
			parseDBAllInOneConfig(new FileReader(fileName));
			log.info("Allinone: using db config:"+fileName+", isDES:"+isDES);
		}catch(Exception e){
			log.info("No database config file");
		}
	}
	
	private void parseDBAllInOneConfig(Reader reader){
		//Map<String, String[]> props = new HashMap<String, String[]>();
		BufferedReader br = null;
		try {
			//br = new BufferedReader(new FileReader(DB_CONFIG_FILE));
			br = new BufferedReader(reader);
			// StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			Matcher matcher;
			while (line != null) {
				// sb.append(line);
				matcher = dbLinePattern.matcher(line);
				if (matcher.find()) {

					props.put(matcher.group(1),parseDotNetDBConnString(matcher.group(2)));
				}

				line = br.readLine();
			}
			// String everything = sb.toString();
			// props.put("product_select", new
			// String[]{"jdbc:mysql://192.168.80.60/product?useUnicode=true&characterEncoding=utf-8","product","123456"});
			//return props;
		} catch (IOException e) {
			log.error("Read db config file error, msg:"+e.getMessage(), e);
			//throw e;
		} catch (Exception e) {
			log.error("Init db config props error, msg:"+e.getMessage(), e);
			//throw e;
		} finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error("close DB config file IO error", e);
				}
			}
		}
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
	private String[] parseDotNetDBConnString(String connStr) {
		String[] dbInfos = new String[] { "", "", "","" };
		try {
			if(isDES){
				connStr = Crypto.getInstance().decrypt(connStr);
			}else{
				connStr = AESCrypto.getInstance().decrypt(connStr);
			}
		}catch(Exception e){
			log.debug("decode exception");
			//connStr is the same;
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
					dbInfos[0] = String.format(DBURL_SQLSERVER, dbhost,
							dburls[1], dbname);
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
						dbInfos[0] = String.format(DBURL_MYSQL, dbhost,
								matcher.group(2), dbname, charset);
					} else {
						dbInfos[0] = String.format(DBURL_MYSQL, dbhost,
								DEFAULT_PORT, dbname, charset);
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
		}
		return dbInfos;
	}

}

package com.ctrip.platform.dal.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


public class AllInOneConfigParser {

//	private static final Log log = LogFactory
//			.getLog(AllInOneConfigParser.class);
	private static final Logger log = Logger.getLogger(AllInOneConfigParser.class);
	private static final String DB_CONFIG_FILE = "/Database.config";
	private static final Pattern dbLinePattern = Pattern
			.compile(" name=\"([^\"]+)\" connectionString=\"([^\"]+)\"");
	private static final Pattern dburlPattern = Pattern.compile(
			"(data\\ssource|server|address|addr|network)=([^;]+)", 2);
	private static final Pattern dbuserPattern = Pattern.compile(
			"(uid|user\\sid)=([^;]+)", 2);
	private static final Pattern dbpasswdPattern = Pattern.compile(
			"(password|pwd)=([^;]+)", 2);
	private static final Pattern dbnamePattern = Pattern.compile(
			"(database|initial\\scatalog)=([^;]+)", 2);
	private static final Pattern dbcharsetPattern = Pattern.compile(
			"(charset)=([^;]+)", 2);
	private static final Pattern dbportPattern = Pattern.compile(
			"(port)=([^;]+)", 2);
	private static final String PORT_SPLIT = ",";
	private static final String DBURL_SQLSERVER = "jdbc:sqlserver://%s:%s;DatabaseName=%s";
	private static final String DBURL_MYSQL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String DEFAULT_PORT = "3306";
	private static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	private static final String DRIVER_SQLSERVRE = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static AllInOneConfigParser allInOneConfigParser = new AllInOneConfigParser();
	private ConcurrentHashMap<String, String[]> props = new ConcurrentHashMap();

	private AllInOneConfigParser() {
		//initDBAllInOneConfig();
	}

	public static AllInOneConfigParser newInstance() {
		return allInOneConfigParser;
	}

	public void initialize(String configFile) {
		initDBAllInOneConfig(configFile);
	}

	public String refresh(String line) {
		Matcher matcher = dbLinePattern.matcher(line);
		if (matcher.find()) {
			String key = matcher.group(1);
			String connStr = matcher.group(2);
			String[] previous = this.props.putIfAbsent(key,
					parseDotNetDBConnString(connStr));
			return previous == null ? key : null;
		}

		return null;
	}

	public boolean remove(String key) {
		return this.props.remove(key) != null;
	}

	private void initDBAllInOneConfig(String configFilePath) {
		BufferedReader br = null;
		try {
			if (null != configFilePath && new File(configFilePath).exists()) {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(configFilePath)));
			} else {
				br = new BufferedReader(new InputStreamReader(getClass()
						.getResourceAsStream("/Database.config")));
			}

			String line = br.readLine();
			while (line != null) {
				try{
					Matcher matcher = dbLinePattern.matcher(line);
					if (matcher.find()) {
						this.props.put(matcher.group(1),
								parseDotNetDBConnString(matcher.group(2)));
					}
					line = br.readLine();
				}catch(Exception ex){
					log.error("parse all in one error: " + line, ex);
				}
			}
			return;
		} catch (IOException e) {
			log.error("Read db config file error, msg:" + e.getMessage(), e);
		} catch (Exception e) {
			log.error("Init db config props error, msg:" + e.getMessage(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("close DB config file IO error", e);
				}
			}
		}
	}

	public Map<String, String[]> getDBAllInOneConfig() {
		return this.props;
	}

	public void reloadAllInOneConfig(String configFile) {
		this.props.clear();
		initDBAllInOneConfig(configFile);
	}

	private static String[] parseDotNetDBConnString(String connStr) {
		String[] dbInfos = { "", "", "", "" };
		try {
			String dbname = null;
			String charset = null;
			String dbhost = null;
			Matcher matcher = dbnamePattern.matcher(connStr);
			if (matcher.find()) {
				dbname = matcher.group(2);
			}
			matcher = dburlPattern.matcher(connStr);
			if (matcher.find()) {
				String[] dburls = matcher.group(2).split(",");
				dbhost = dburls[0];
				if (dburls.length == 2) {
					dbInfos[0] = String.format(
							"jdbc:sqlserver://%s:%s;DatabaseName=%s",
							new Object[] { dbhost, dburls[1], dbname });

					dbInfos[3] = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				} else {
					matcher = dbcharsetPattern.matcher(connStr);
					if (matcher.find()) {
						charset = matcher.group(2);
					} else {
						charset = "UTF-8";
					}
					matcher = dbportPattern.matcher(connStr);
					if (matcher.find()) {
						dbInfos[0] = String
								.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s",
										new Object[] { dbhost,
												matcher.group(2), dbname,
												charset });
					} else {
						dbInfos[0] = String
								.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s",
										new Object[] { dbhost, "3306", dbname,
												charset });
					}
					dbInfos[3] = "com.mysql.jdbc.Driver";
				}
			}
			matcher = dbuserPattern.matcher(connStr);
			if (matcher.find()) {
				dbInfos[1] = matcher.group(2);
			}
			matcher = dbpasswdPattern.matcher(connStr);
			if (matcher.find()) {
				dbInfos[2] = matcher.group(2);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return dbInfos;
	}

}

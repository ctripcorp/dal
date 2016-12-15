package com.ctrip.datasource.configure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigParser;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConifg;

public class ConnectionStringParser {
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
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String DEFAULT_PORT = "3306";
	private static final String DRIVER_MYSQL ="com.mysql.jdbc.Driver";
	private static final String DRIVER_SQLSERVRE ="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	/**
	 * parse "Data Source=devdb.dev.sh.ctriptravel.com,28747;UID=uws_AllInOneKey_dev;password=!QAZ@WSX1qaz2wsx; database=AbacusDB;"
	 * 
	 * @return DataSourceConfigure
	 */
	public DataSourceConfigure parse(String name, String connStr) {
		DataSourceConfigure config = new DataSourceConfigure();

		String url = null;
		String userName = null;
		String password = null;
		String driverClass = null;
		
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
				url = String.format(DBURL_SQLSERVER, dbhost, dburls[1], dbname);
				driverClass = DRIVER_SQLSERVRE;
			} else {// should be mysql
				matcher = dbcharsetPattern.matcher(connStr);
				if (matcher.find()) {
					charset = matcher.group(2);
				} else {
					charset = DEFAULT_ENCODING;
				}
				matcher = dbportPattern.matcher(connStr);
				if (matcher.find()) {
					url = String.format(DBURL_MYSQL, dbhost, matcher.group(2), dbname, charset);
				} else {
					url = String.format(DBURL_MYSQL, dbhost, DEFAULT_PORT, dbname, charset);
				}
				driverClass = DRIVER_MYSQL;
			}
		}

		matcher = dbuserPattern.matcher(connStr);
		if (matcher.find()) {
			userName = matcher.group(2);
		}
		
		matcher = dbpasswdPattern.matcher(connStr);
		if (matcher.find()) {
			password = matcher.group(2);
		}
			
		config.setConnectionUrl(url);
		config.setUserName(userName);
		config.setPassword(password);
		config.setDriverClass(driverClass);
		config = applyOptions(name, config);
		
		return config;
	}

	private DataSourceConfigure applyOptions(String name, DataSourceConfigure config) {
		DatabasePoolConifg poolConfig = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg(name);
		if(poolConfig == null)
			return config;
		
		String option = poolConfig.getOption();
		
		if(option == null || option.length() == 0)
			return config;
		
		String url = config.getConnectionUrl();
		if(config.getDriverClass().equals(DRIVER_SQLSERVRE)){
			url = url + ";" + option;
		}else{
			url = url + "&" + option.replaceAll(";", "&");
		}
		config.setConnectionUrl(url);
		
		return config;
	}
}
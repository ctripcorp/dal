package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class DbMeta {
    private static Pattern hostRegxPattern = null;

	private static ConcurrentHashMap<String, DbMeta> metaMap = new ConcurrentHashMap<String, DbMeta>();
	
	private String databaseName;
	private DatabaseCategory dbCategory;
	private String dataBaseKeyName;
	private String userName;
	private String shardId;
	private boolean isMaster;
	private String url;
	private String host;

	static {
		String regEx = "(?<=://)[\\w\\-_]+(\\.[\\w\\-_]+)+(?=[,|:|;])";
		hostRegxPattern = Pattern.compile(regEx);
	}
	
	private DbMeta(Connection conn, String realDbName, DatabaseCategory dbCategory, String shardId, boolean master) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();

		databaseName = conn.getCatalog();
		url = meta.getURL();
		host = parseHostFromDBURL(url);
		userName = meta.getUserName();
		
		dataBaseKeyName = realDbName;
		this.dbCategory = dbCategory;
		isMaster = master;
		this.shardId = shardId;
	}
	
	public void populate(LogEntry entry) {
		entry.setDatabaseName(databaseName);
		entry.setServerAddress(host);
		entry.setDbUrl(url);
		entry.setUserName(userName);
		entry.setMaster(isMaster);
		entry.setShardId(shardId);
		entry.setDataBaseKeyName(dataBaseKeyName);
	}
	

	public static DbMeta createIfAbsent(String realDbName, DatabaseCategory dbCategory, String shardId, boolean isMaster, Connection conn) throws SQLException {
		DbMeta meta = metaMap.get(realDbName);
		if(meta == null) {
			meta = new DbMeta(conn, realDbName, dbCategory, shardId, isMaster);
			DbMeta oldMeta = metaMap.putIfAbsent(realDbName, meta);
			meta = oldMeta == null ? meta : oldMeta;
		}
		return meta;
	}
	
	public static DbMeta getDbMeta(String realDbName) throws SQLException {
		return metaMap.get(realDbName);
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getDataBaseKeyName() {
		return dataBaseKeyName;
	}
	
	public DatabaseCategory getDatabaseCategory() {
		return dbCategory;
	}

	public String getShardId() {
		return shardId;
	}
	
	private String parseHostFromDBURL(String url) {
		Matcher m = hostRegxPattern.matcher(url);
		String host = "NA";
		while (m.find()) {
			host = m.group();
			break;
		}
		return host;
	}
}

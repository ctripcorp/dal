package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.helper.CommonUtil;

public class DbMeta {
	private static ConcurrentHashMap<String, DbMeta> metaMap = new ConcurrentHashMap<String, DbMeta>();
	
	private String databaseName;
	private DatabaseCategory dbCategory;
	private String allInOneKey;
	private String userName;
	private String shardId;
	private boolean isMaster;
	private String url;
	private String host;

	private DbMeta(Connection conn, String realDbName, DatabaseCategory dbCategory, String shardId, boolean master) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();

		databaseName = conn.getCatalog();
		url = meta.getURL();
		host = CommonUtil.parseHostFromDBURL(url);
		userName = meta.getUserName();
		
		allInOneKey = realDbName;
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
		entry.setAllInOneKey(allInOneKey);
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

	public String getAllInOneKey() {
		return allInOneKey;
	}
	
	public DatabaseCategory getDatabaseCategory() {
		return dbCategory;
	}

	public String getShardId() {
		return shardId;
	}
}

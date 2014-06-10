package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.logging.CommonUtil;
import com.ctrip.platform.dal.dao.logging.LogEntry;

public class DbMeta {
	private static ConcurrentHashMap<String, DbMeta> metaMap = new ConcurrentHashMap<String, DbMeta>();
	
	private String databaseName;
	private String userName;
	private String url;

	private DbMeta(Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();

		databaseName = conn.getCatalog();
		url = CommonUtil.parseHostFromDBURL(meta.getURL());
		userName = meta.getUserName();
	}
	
	public void populate(LogEntry entry) {
		entry.setDatabaseName(databaseName);
		entry.setServerAddress(url);
		entry.setUserName(userName);
	}
	

	public static DbMeta getDbMeta(String realDbName, Connection conn) throws SQLException {
		DbMeta meta = metaMap.get(realDbName);
		if(meta == null) {
			meta = new DbMeta(conn);
			metaMap.putIfAbsent(realDbName, meta);
		}

		return meta;
	}

	public String getDatabaseName() {
		return databaseName;
	}
}

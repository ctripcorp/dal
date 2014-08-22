package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.helper.CommonUtil;
import com.ctrip.platform.dal.sql.logging.LogEntry;

public class DbMeta {
	private static ConcurrentHashMap<String, DbMeta> metaMap = new ConcurrentHashMap<String, DbMeta>();
	
	private String databaseName;
	private String allInOneKey;
	private String userName;
	private boolean isMaster;
	private boolean isSlave;
	private String url;

	private DbMeta(Connection conn, String realDbName, boolean master, boolean isSelect) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();

		databaseName = conn.getCatalog();
		url = CommonUtil.parseHostFromDBURL(meta.getURL());
		userName = meta.getUserName();
		
		allInOneKey = realDbName;
		isMaster = master;
		isSlave = isSelect;
	}
	
	public void populate(LogEntry entry) {
		entry.setDatabaseName(databaseName);
		entry.setServerAddress(url);
		entry.setUserName(userName);
		entry.setMaster(isMaster);
		entry.setSlave(isSlave);
		entry.setAllInOneKey(allInOneKey);
	}
	

	public static DbMeta getDbMeta(String realDbName, boolean isMaster, boolean isSelect, Connection conn) throws SQLException {
		DbMeta meta = metaMap.get(realDbName);
		if(meta == null) {
			meta = new DbMeta(conn, realDbName, isMaster, isSelect);
			metaMap.putIfAbsent(realDbName, meta);
		}
		return meta;
	}
	
	public static DbMeta getDbMeta(String realDbName, Connection conn) throws SQLException {
		return getDbMeta(realDbName, false, false, conn);
	}
	
	public static DbMeta getDbMeta(String realDbName, boolean isMaster, Connection conn) throws SQLException {
		return getDbMeta(realDbName, isMaster, false, conn);
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getAllInOneKey() {
		return allInOneKey;
	}
}

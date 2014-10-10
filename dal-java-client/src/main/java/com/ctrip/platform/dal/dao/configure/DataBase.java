package com.ctrip.platform.dal.dao.configure;

import java.sql.Connection;
import java.sql.SQLException;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class DataBase {
	private String name;
	private boolean master;
	private String sharding;
	private String connectionString;
	
	public DataBase(String name, 
			boolean master, 
			String sharding, 
			String connectionString) {
		this.name = name;
		this.master = master;
		this.sharding = sharding;
		this.connectionString = connectionString;
	}
	
	public static DatabaseCategory getDatabaseCategory(String connectionString) throws SQLException {
		Connection conn = null;
		DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
		Throwable ex = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(connectionString).getConnection();
			String dbType = conn.getMetaData().getDatabaseProductName();
			if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
				dbCategory = DatabaseCategory.MySql;
			}
		} catch (Throwable e) {
			ex = e;
		}finally {
			if(conn != null)
				try {
					conn.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			if(ex != null)
				throw new SQLException(ex);
		}
		return dbCategory;
	}
	
	public String getName() {
		return name;
	}

	public boolean isMaster() {
		return master;
	}

	public String getSharding() {
		return sharding;
	}

	public String getConnectionString() {
		return connectionString;
	}
}

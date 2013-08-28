package com.ctrip.sysdev.das.worker;

import java.sql.Connection;

public class ConnectionPool {
	private String logicDbName;
	private int size;
	
	/**
	 * TODO We should move this out to configure file
	 * @param logicDbName
	 */
	public ConnectionPool(String logicDbName) {
		this.logicDbName = logicDbName;
		this.size = 100;
	}
	
	public Connection getConnection() {
		return null;
	}
	
	/**
	 * We should wrapp connection and when it is closed, it should be returned to pool
	 */
	public void returnConnection(Connection conn) {
		
	}
}

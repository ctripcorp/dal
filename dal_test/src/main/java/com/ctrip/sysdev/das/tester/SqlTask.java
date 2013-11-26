package com.ctrip.sysdev.das.tester;


public class SqlTask implements Runnable {
	private String sql;
	private boolean isSelect;
	private DalClient dal;
	
	public SqlTask(String sql, DalClient dal) {
		this.sql = sql.trim();;
		this.isSelect = sql.startsWith("SELECT") || sql.startsWith("select");
		this.dal = dal;
	}
	
	public void run() {
		if(isSelect)
			dal.executeQuery(sql);
		else
			dal.executeUpdate(sql);
	}
}

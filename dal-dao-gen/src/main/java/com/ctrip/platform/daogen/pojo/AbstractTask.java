package com.ctrip.platform.daogen.pojo;

public class AbstractTask implements Task {
	
	protected String db_name;
	
	protected String table_name;

	@Override
	public String getDb_name() {
		return db_name;
	}

	@Override
	public String getTable_name() {
		return table_name;
	}

	@Override
	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}

	@Override
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

}

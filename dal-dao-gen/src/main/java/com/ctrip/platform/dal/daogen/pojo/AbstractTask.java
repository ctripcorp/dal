package com.ctrip.platform.dal.daogen.pojo;

public class AbstractTask implements Task {
	
	protected String db_name;
	
	protected String table_name;
	
	protected int server_id;

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

	@Override
	public int getServer_id() {
		// TODO Auto-generated method stub
		return this.server_id;
	}

	@Override
	public void setServer_id(int server_id) {
		this.server_id = server_id;
	}

}

package com.ctrip.sysdev.das.common.zk;


public class LogicDbAccessor extends DasZkAccessor {
	public void addLogicDB(String name, String driver, String jdbcUrl) {
	}
	
	public void removeLogicDB(String name) {
	}

	public void modifyLogicDB(String oldName, String newName, String driver, String jdbcUrl) {
	}

	@Override
	public void initialize() {
		createPath(DB);
		createPath(DB_NODE);
	}
}

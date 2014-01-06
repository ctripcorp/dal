package com.ctrip.sysdev.das.common.zk;

import org.apache.zookeeper.ZooKeeper;


public class LogicDbAccessor extends DasZkAccessor {
	public LogicDbAccessor(ZooKeeper zk) {
		super(zk);
	}
	
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

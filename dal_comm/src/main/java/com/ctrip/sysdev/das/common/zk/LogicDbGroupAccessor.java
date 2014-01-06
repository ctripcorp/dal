package com.ctrip.sysdev.das.common.zk;

import org.apache.zookeeper.ZooKeeper;

import com.ctrip.sysdev.das.common.zk.to.LogicDbGroup;

public class LogicDbGroupAccessor extends DasZkAccessor {
	public LogicDbGroupAccessor(ZooKeeper zk) {
		super(zk);
	}
	
	public String[] listGroups() {
		String[] logicDBs = null;
		return logicDBs;
	}
	
	public LogicDbGroup getGroup(String name) {
		return null;
	}
	
	public void createGroup(String name, String[] logicDBs) {
		
	}

	public void modifyGroup(String oldName, String newName, String[] logicDBs) {
		
	}

	public void removeGroup(String name) {
		
	}

	@Override
	public void initialize() {
		createPath(DB_GROUP);
		createPath(DB_GROUP_NODE);
	}
}

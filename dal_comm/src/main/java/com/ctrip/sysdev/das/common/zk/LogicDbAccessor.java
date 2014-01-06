package com.ctrip.sysdev.das.common.zk;

import java.util.List;

import org.apache.zookeeper.ZooKeeper;

import com.ctrip.sysdev.das.common.zk.to.LogicDbSetting;


public class LogicDbAccessor extends DasZkAccessor {
	public LogicDbAccessor(ZooKeeper zk) {
		super(zk);
	}
	
	public List<String> list() throws Exception {
		return getChildren(DB_NODE);
	}
	
	public LogicDbSetting getSetting(String name) throws Exception {
		LogicDbSetting setting = new LogicDbSetting();
		String path = pathOf(DB_NODE, name);
		setting.setDriver(getStringValue(path, DRIVER));
		setting.setDriver(getStringValue(path, JDBC_URL));
		return setting;
	}
	
	public void addLogicDB(String name, String driver, String jdbcUrl) throws Exception {
		String dbNodePath = pathOf(DB_NODE, name);
		
		create(dbNodePath);		
		create(pathOf(dbNodePath, DRIVER), driver);
		create(pathOf(dbNodePath, JDBC_URL), jdbcUrl);
	}
	
	public void removeLogicDB(String name) throws Exception {
		deleteNodeNested(pathOf(DB_NODE, name));
	}

	public void modifyLogicDB(String name, String driver, String jdbcUrl) throws Exception {
		String dbNodePath = pathOf(DB_NODE, name);
		
		setValue(pathOf(dbNodePath, DRIVER), driver);
		setValue(pathOf(dbNodePath, JDBC_URL), jdbcUrl);
	}

	public List<String> listSlave(String masterName) throws Exception {
		return getChildren(pathOf(DB_NODE,masterName));
	}
	
	public LogicDbSetting getSalveSetting(String masterName, String name) throws Exception {
		LogicDbSetting setting = new LogicDbSetting();
		String path = pathOf(pathOf(DB_NODE,masterName), name);
		setting.setDriver(getStringValue(path, DRIVER));
		setting.setDriver(getStringValue(path, JDBC_URL));
		return setting;
	}
	
	public void addSlaveLogicDB(String masterName, String name, String driver, String jdbcUrl) throws Exception {
		String dbNodePath = pathOf(pathOf(DB_NODE,masterName), name);
		
		create(dbNodePath);		
		create(pathOf(dbNodePath, DRIVER), driver);
		create(pathOf(dbNodePath, JDBC_URL), jdbcUrl);
	}
	
	public void removeSlaveLogicDB(String masterName, String name) throws Exception {
		deleteNodeNested(pathOf(pathOf(DB_NODE,masterName), name));
	}

	public void modifySlaveLogicDB(String masterName, String name, String driver, String jdbcUrl) throws Exception {
		String dbNodePath = pathOf(pathOf(DB_NODE,masterName), name);
		
		setValue(pathOf(dbNodePath, DRIVER), driver);
		setValue(pathOf(dbNodePath, JDBC_URL), jdbcUrl);
	}

	@Override
	public void initialize() {
		createPath(DB);
		createPath(DB_NODE);
	}
}

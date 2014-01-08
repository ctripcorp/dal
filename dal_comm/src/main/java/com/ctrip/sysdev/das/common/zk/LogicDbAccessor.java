package com.ctrip.sysdev.das.common.zk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.ZooKeeper;

import com.ctrip.sysdev.das.common.to.LogicDB;
import com.ctrip.sysdev.das.common.to.LogicDbSetting;
import com.ctrip.sysdev.das.common.to.MasterLogicDB;


public class LogicDbAccessor extends DasZkAccessor {
	public LogicDbAccessor(ZooKeeper zk) {
		super(zk);
	}
	
	public List<String> listName() throws Exception {
		return getChildren(DB);
	}
	
	public List<MasterLogicDB> list() throws Exception {
		List<String> names = listName();
		List<MasterLogicDB> dbs = new ArrayList<MasterLogicDB>();
		for(String name: names) {
			MasterLogicDB db = new MasterLogicDB();
			db.setName(name);
			db.setSetting(getSetting(name));
			db.setSlave(listSlave(name));
			dbs.add(db);
		}
		return dbs;
	}
	
	public LogicDbSetting getSetting(String name) throws Exception {
		LogicDbSetting setting = new LogicDbSetting();
		String path = pathOf(DB, name);
		setting.setDriver(getStringValue(path, DRIVER));
		setting.setDriver(getStringValue(path, JDBC_URL));
		return setting;
	}
	
	public void addLogicDB(String name, String driver, String jdbcUrl) throws Exception {
		String dbNodePath = pathOf(DB, name);
		
		create(dbNodePath);		
		create(pathOf(dbNodePath, DRIVER), driver);
		create(pathOf(dbNodePath, JDBC_URL), jdbcUrl);
	}
	
	public void removeLogicDB(String name) throws Exception {
		deleteNodeNested(pathOf(DB, name));
	}

	public void modifyLogicDB(String name, String driver, String jdbcUrl) throws Exception {
		String dbNodePath = pathOf(DB, name);
		
		setValue(pathOf(dbNodePath, DRIVER), driver);
		setValue(pathOf(dbNodePath, JDBC_URL), jdbcUrl);
	}

	public List<String> listSlaveName(String masterName) throws Exception {
		try {
			return getChildren(pathOf(DB, masterName, SLAVE));
		} catch (NoNodeException e) {
			return Collections.emptyList();
		}
	}
	
	public List<LogicDB> listSlave(String masterName) throws Exception {
		List<String> names = listSlaveName(masterName);
		List<LogicDB> dbs = new ArrayList<LogicDB>();
		for(String name: names) {
			LogicDB db = new LogicDB();
			db.setName(name);
			db.setSetting(getSalveSetting(masterName, name));
			dbs.add(db);
		}
		return dbs;
	}
	
	public LogicDbSetting getSalveSetting(String masterName, String name) throws Exception {
		LogicDbSetting setting = new LogicDbSetting();
		String path = pathOf(DB, masterName, SLAVE, name);
		setting.setDriver(getStringValue(path, DRIVER));
		setting.setDriver(getStringValue(path, JDBC_URL));
		return setting;
	}
	
	public void addSlaveLogicDB(String masterName, String name, String driver, String jdbcUrl) throws Exception {
		String dbNodePath = pathOf(DB, masterName, SLAVE, name);
		
		create(dbNodePath);		
		create(pathOf(dbNodePath, DRIVER), driver);
		create(pathOf(dbNodePath, JDBC_URL), jdbcUrl);
	}
	
	public void removeSlaveLogicDB(String masterName, String name) throws Exception {
		deleteNodeNested(pathOf(DB, masterName, SLAVE, name));
	}

	public void modifySlaveLogicDB(String masterName, String name, String driver, String jdbcUrl) throws Exception {
		String dbNodePath = pathOf(DB, masterName, SLAVE, name);
		
		setValue(pathOf(dbNodePath, DRIVER), driver);
		setValue(pathOf(dbNodePath, JDBC_URL), jdbcUrl);
	}

	@Override
	public void initialize() {
		createPath(DB);
		createPath(DB_NODE);
	}
}

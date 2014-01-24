package com.ctrip.platform.dasconsole.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.ctrip.platform.dasconsole.common.Status;
import com.ctrip.platform.dasconsole.domain.DB;
import com.ctrip.platform.dasconsole.domain.DbSetting;

@Resource
public class DbResource extends DalBaseResource {
	public static final String DB_ZK_PATH = "/dal/das/configure/db";
	
	@Context
	protected ServletContext sContext;
	
	public List<DB> getDBs(String root) throws Exception {
		List<DB> dbList = new ArrayList<DB>();
		ZooKeeper zk = getZk();
		if(zk.exists(root, false) == null)
			return dbList;
		
		List<String> dbNameList = zk.getChildren(root, false);
		for(String dbName: dbNameList) {
			dbList.add(getDB(root, dbName));
		}				
		return dbList;
	}
	
	public DB getDB(String root, String name) throws Exception {
		String dbNodePath = getPath(root, name);
		DB db = new DB();
		db.setName(name);
		db.setSetting(readSetting(dbNodePath));
		return db;
	}

	public Status addDB(String root, String name, String driver, String jdbcUrl) {
		ZooKeeper zk = getZk();
		String dbNodePath = getPath(root, name);
		
		try {
			zk.create(dbNodePath, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
			zk.create(getDriverPath(dbNodePath), driver.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(getJdbcUrlPath(dbNodePath), jdbcUrl.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
	}

	public Status updateDB(String root, String name, String driver, String jdbcUrl) {
		ZooKeeper zk = getZk();
		String dbNodePath = getPath(root, name);
		
		try {
			zk.setData(getDriverPath(dbNodePath), driver.getBytes(), -1);
			zk.setData(getJdbcUrlPath(dbNodePath), jdbcUrl.getBytes(), -1);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
	}
	
	public Status deleteDB(String root, String name) {
		String dbNodePath = getPath(root, name);
		
		try {
			deleteNodeNested(dbNodePath);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
	}

	private DbSetting readSetting(String path) throws Exception {
		ZooKeeper zk = getZk();
		DbSetting setting = new DbSetting();
		
		setting.setDriver(new String(zk.getData(getDriverPath(path), false, null)));
		setting.setJdbcUrl(new String(zk.getData(getJdbcUrlPath(path), false, null)));
		
		return setting;
	}

	protected String getPath(String root, String name) {
		return root + "/" + name;
	}
	
	private String getDriverPath(String root) {
		return root + "/driver";
	}

	private String getJdbcUrlPath(String root) {
		return root + "/jdbcUrl";
	}
}


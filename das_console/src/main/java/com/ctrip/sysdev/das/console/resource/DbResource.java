package com.ctrip.sysdev.das.console.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.ctrip.sysdev.das.console.domain.DB;
import com.ctrip.sysdev.das.console.domain.DbSetting;
import com.ctrip.sysdev.das.console.domain.MasterDB;
import com.ctrip.sysdev.das.console.domain.Status;

@Resource
@Path("configure/db")
@Singleton
public class DbResource extends DalBaseResource {
	@Context
	private ServletContext sContext;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DB> getDb() {
		List<DB> dbList = new ArrayList<DB>();
		ZooKeeper zk = getZk();
		try {
			List<String> dbNameList = zk.getChildren("/dal/das/configure/db", false);
			for(String dbName: dbNameList) {
				String dbNodePath = "/dal/das/configure/db" + "/" + dbName;
				MasterDB master = new MasterDB();
				master.setName(dbName);
				master.setSetting(readSetting(dbNodePath));
				master.setSlaves(readSlaves(dbNodePath));
				dbList.add(master);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbList;
	}
	
	private List<DB> readSlaves(String masterPath) {
		List<DB> dbList = new ArrayList<DB>();
		ZooKeeper zk = getZk();
		try {
			List<String> dbNameList = zk.getChildren(masterPath, false);
			for(String dbName: dbNameList) {
				String dbNodePath = masterPath + "/" + dbName;
				DB slave = new DB();
				slave.setName(dbName);
				slave.setSetting(readSetting(dbNodePath));
				dbList.add(slave);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbList;
	}
	
	@GET
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public DbSetting getDbSetting(@PathParam("name") String name) {
		DbSetting setting = new DbSetting();
		try {
			String dbNodePath = "/dal/das/configure/db" + "/" + name;
			setting = readSetting(dbNodePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setting;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addDb(@FormParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		System.out.printf("Add DB: " +name);
		ZooKeeper zk = getZk();
		String dbNodePath = "/dal/das/configure/db" + "/" + name;
		
		try {
			zk.create(dbNodePath, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
			zk.create(dbNodePath + "/driver", driver.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(dbNodePath + "/jdbcUrl", jdbcUrl.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
	}
	
	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Status updateDB(@PathParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		System.out.printf("Update DB: " +name);
		ZooKeeper zk = getZk();
		String dbNodePath = "/dal/das/configure/db" + "/" + name;
		
		try {
			zk.setData(dbNodePath + "/driver", driver.getBytes(), -1);
			zk.setData(dbNodePath + "/jdbcUrl", jdbcUrl.getBytes(), -1);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
	}
	
	@DELETE
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteDb(@PathParam("name") String name) {
		System.out.printf("Delete DB: " +name);
		String dbNodePath = "/dal/das/configure/db" + "/" + name;
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
		
		setting.setDriver(new String(zk.getData(path + "/driver", false, null)));
		setting.setJdbcUrl(new String(zk.getData(path + "/jdbcUrl", false, null)));
		
		return setting;
	}	
}

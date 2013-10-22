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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.zookeeper.ZooKeeper;
import org.glassfish.jersey.server.JSONP;

import com.ctrip.sysdev.das.console.domain.DB;
import com.ctrip.sysdev.das.console.domain.DbSetting;
import com.ctrip.sysdev.das.console.domain.Node;

@Resource
@Path("configure/db")
@Singleton
public class DbResource {
	@Context
	private ServletContext sContext;
	
	@GET
	@JSONP(queryParam = "jsonpCallback")
	@Produces("application/x-javascript")
	public List<DB> getDb(@QueryParam("jsonpCallback") String callback) {
		List<DB> dbList = new ArrayList<DB>();
		ZooKeeper zk = (ZooKeeper)sContext.getAttribute("com.ctrip.sysdev.das.console.zk");
		try {
			List<String> dbNameList = zk.getChildren("/dal/das/configure/db", false);
			for(String dbName: dbNameList) {
				String dbNodeName = "/dal/das/configure/db" + "/" + dbName;
				DB db = new DB();
				db.setName(dbName);
				DbSetting setting = new DbSetting();
				setting.setDriver(new String(zk.getData(dbNodeName + "/driver", false, null)));
				setting.setJdbcUrl(new String(zk.getData(dbNodeName + "/jdbcUrl", false, null)));
				db.setSetting(setting);
				dbList.add(db);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbList;
	}
	
	@GET
	@Path("{name}")
	@JSONP(queryParam = "jsonpCallback")
	@Produces("application/x-javascript")
	public DbSetting getDbSetting(@QueryParam("jsonpCallback") String callback) {
		DbSetting setting = new DbSetting();
		setting.setDriver("aaa");
		setting.setJdbcUrl("mmm");
		return setting;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void addDb(@FormParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		System.out.printf("add DB: " +name);
	}
	
	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateDB(@PathParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		System.out.printf("Update DB: " +name);
	}
	
	@DELETE
	@Path("{name}")
	public void deleteDb(@PathParam("name") String name) {
		System.out.printf("Delete DB: " +name);
	}
}

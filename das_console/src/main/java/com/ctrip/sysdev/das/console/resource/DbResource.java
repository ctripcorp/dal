package com.ctrip.sysdev.das.console.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ctrip.sysdev.das.console.domain.DB;
import com.ctrip.sysdev.das.console.domain.DbSetting;

@Resource
@Path("dal/das/configure/db")
@Singleton
public class DbResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DB> getDb() {
		List<DB> dbList = new ArrayList<DB>();
		DB db = new DB();
		db.setName("aaa");
		DbSetting setting = new DbSetting();
		setting.setDriver("aaa");
		setting.setJdbcUrl("mmm");
		db.setSetting(setting);
		dbList.add(db);
		return dbList;
	}
	
	@GET
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public DbSetting getDbSetting() {
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

package com.ctrip.platform.dasconsole.resource;

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

import com.ctrip.platform.dasconsole.common.Status;
import com.ctrip.platform.dasconsole.domain.DB;

@Resource
//@Path("configure/db")
@Singleton
public class MasterDbResource extends DbResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DB> getMasters() throws Exception {
		return getDBs(DB_ZK_PATH);
	}
	
	@GET
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public DB getMaster(@PathParam("name") String name) throws Exception {
		return getDB(DB_ZK_PATH, name);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addMaster(@FormParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		return addDB(DB_ZK_PATH, name, driver, jdbcUrl);
	}
	
	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Status updateMaster(@PathParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		return updateDB(DB_ZK_PATH, name, driver, jdbcUrl);
	}
	
	@DELETE
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteMaster(@PathParam("name") String name) {
		return deleteDB(DB_ZK_PATH, name);
	}
}

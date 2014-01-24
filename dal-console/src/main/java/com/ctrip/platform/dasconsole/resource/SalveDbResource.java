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
@Path("configure/db/{master}/slave")
@Singleton
public class SalveDbResource extends DbResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DB> getSalves(@PathParam("master") String master) throws Exception {
		return getDBs(getRoot(master));
	}
	
	@GET
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public DB getSalve(@PathParam("master") String master, @PathParam("name") String name) throws Exception {
		return getDB(getRoot(master), name);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addSalve(@PathParam("master") String master, @FormParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		return addDB(getRoot(master), name, driver, jdbcUrl);
	}
	
	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Status updateSalve(@PathParam("master") String master, @PathParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		return updateDB(getRoot(master), name, driver, jdbcUrl);
	}
	
	@DELETE
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteSalve(@PathParam("master") String master, @PathParam("name") String name) {
		return deleteDB(getRoot(master), name);
	}

	private String getRoot(String master) {
		return getPath(DB_ZK_PATH, master + "/slave");
	}	
}

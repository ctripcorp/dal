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
import com.ctrip.sysdev.das.common.to.LogicDbSetting;

@Resource
@Path("configure/db")
@Singleton
public class TestDbResource extends DalBaseResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> list() throws Exception {
		return getFactory().getLogicDbAccessor().listName();
	}
	
	@GET
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public LogicDbSetting getSetting(@PathParam("name") String name) throws Exception {
		return getFactory().getLogicDbAccessor().getSetting(name);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addMaster(@FormParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		try {
			getFactory().getLogicDbAccessor().addLogicDB(name, driver, jdbcUrl);
			return Status.OK;
		} catch (Exception e) {
			logger.error("Error during add master logic DB", e);
			return Status.ERROR;
		}
	}
	
	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Status modifyLogicDB(@PathParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		try {
			getFactory().getLogicDbAccessor().modifyLogicDB(name, driver, jdbcUrl);
			return Status.OK;
		} catch (Exception e) {
			logger.error("Error during modify master logic DB", e);
			return Status.ERROR;
		}
	}
	
	@DELETE
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status removeLogicDB(@PathParam("name") String name) {
		try {
			getFactory().getLogicDbAccessor().removeLogicDB(name);
			return Status.OK;
		} catch (Exception e) {
			logger.error("Error during delete master logic DB", e);
			return Status.ERROR;
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getSalves(@PathParam("master") String master) throws Exception {
		return getFactory().getLogicDbAccessor().listSlaveName(master);
	}
	
	@GET
	@Path("configure/db/{master}/slave/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public LogicDbSetting getSalve(@PathParam("master") String master, @PathParam("name") String name) throws Exception {
		return getFactory().getLogicDbAccessor().getSalveSetting(master, name);
	}

	@POST
	@Path("configure/db/{master}/slave")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addSalve(@PathParam("master") String master, @FormParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		try {
			getFactory().getLogicDbAccessor().addSlaveLogicDB(master, name, driver, jdbcUrl);
			return Status.OK;
		} catch (Exception e) {
			logger.error("Error during add slave logic DB", e);
			return Status.ERROR;
		}
	}
	
	@PUT
	@Path("configure/db/{master}/slave/{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Status updateSalve(@PathParam("master") String master, @PathParam("name") String name, @FormParam("driver") String driver, @FormParam("jdbcUrl") String jdbcUrl) {
		try {
			getFactory().getLogicDbAccessor().modifySlaveLogicDB(master, name, driver, jdbcUrl);
			return Status.OK;
		} catch (Exception e) {
			logger.error("Error during modify slave logic DB", e);
			return Status.ERROR;
		}
	}
	
	@DELETE
	@Path("configure/db/{master}/slave/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteSalve(@PathParam("master") String master, @PathParam("name") String name) {
		try {
			getFactory().getLogicDbAccessor().removeSlaveLogicDB(master, name);
			return Status.OK;
		} catch (Exception e) {
			logger.error("Error during delete slave logic DB", e);
			return Status.ERROR;
		}
	}
}
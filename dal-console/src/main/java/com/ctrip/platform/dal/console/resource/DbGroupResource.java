package com.ctrip.platform.dal.console.resource;

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

import com.ctrip.platform.dal.console.common.Status;

@Resource
@Path("configure/dbGroup")
@Singleton
public class DbGroupResource extends DalBaseResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> list() throws Exception {
		return getFactory().getLogicDbGroupAccessor().listName();
	}
	
	@GET
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public String[] getGroup(@PathParam("name") String name) throws Exception {
		return getFactory().getLogicDbGroupAccessor().getGroup(name);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status createGroup(@FormParam("name") String name, @FormParam("logicDBs") String logicDBs) {
		try {
			getFactory().getLogicDbGroupAccessor().createGroup(name, logicDBs.split(","));
			return Status.OK;
		} catch (Exception e) {
			logger.error("Error during add logic DB group", e);
			return Status.ERROR;
		}
	}
	
	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Status modifyLogicDbGroup(@PathParam("name") String name, @FormParam("logicDBs") String logicDBs) {
		try {
			getFactory().getLogicDbGroupAccessor().modifyGroup(name, logicDBs.split(","));
			return Status.OK;
		} catch (Exception e) {
			logger.error("Error during modify logic DB group", e);
			return Status.ERROR;
		}
	}
	
	@DELETE
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status removeLogicDbGroup(@PathParam("name") String name) {
		try {
			getFactory().getLogicDbGroupAccessor().removeGroup(name);
			return Status.OK;
		} catch (Exception e) {
			logger.error("Error during delete logic DB group", e);
			return Status.ERROR;
		}
	}
}

package com.ctrip.platform.dasconsole.resource;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ctrip.sysdev.das.common.to.DasConfigure;

@Resource
@Path("configure/snapshot")
@Singleton
public class ConfigureResource extends DalBaseResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DasConfigure get() throws Exception {
		DasConfigure config = new DasConfigure();
		
		config.setPort(getFactory().getPortAccessor().list());
		config.setNode(getFactory().getDasNodeAccessor().list());
		config.setDb(getFactory().getLogicDbAccessor().list());
		config.setDbGroup(getFactory().getLogicDbGroupAccessor().list());
		config.setDeployment(getFactory().getDeploymentAccessor().list());
		
		return config;
	}
}

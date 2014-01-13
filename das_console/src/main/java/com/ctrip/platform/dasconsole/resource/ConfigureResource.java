package com.ctrip.platform.dasconsole.resource;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.ctrip.sysdev.das.common.to.DasConfigure;
import com.fasterxml.jackson.databind.ObjectMapper;

@Resource
@Path("configure/snapshot")
@Singleton
public class ConfigureResource extends DalBaseResource {
	private ObjectMapper mapper = new ObjectMapper();
	private String latestConfig;
	
	@GET
	public String get() {
		if(latestConfig == null)
			buildLatest();
		return latestConfig;
	}
	
	private void buildLatest() {
		DasConfigure config = new DasConfigure();
		
		try {
			config.setPort(getFactory().getPortAccessor().list());
			config.setNode(getFactory().getDasNodeAccessor().list());
			config.setDb(getFactory().getLogicDbAccessor().list());
			config.setDbGroup(getFactory().getLogicDbGroupAccessor().list());
			config.setDeployment(getFactory().getDeploymentAccessor().list());
			latestConfig = mapper.writeValueAsString(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

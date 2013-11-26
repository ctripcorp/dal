package com.ctrip.sysdev.das.daogen;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.ctrip.sysdev.das.daogen.resource.ProjectResource;

@ApplicationPath("/daogen")
public class DaoGenApplication extends ResourceConfig {

	public DaoGenApplication() {
		packages(ProjectResource.class.getPackage().getName());
		this.register(EntityFilteringFeature.class);
	}

}

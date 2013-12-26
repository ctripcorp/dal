package com.ctrip.platform.daogen;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.ctrip.platform.daogen.resource.ProjectResource;

@ApplicationPath("/rest")
public class DaoGenApplication extends ResourceConfig {

	public DaoGenApplication() {
		packages(ProjectResource.class.getPackage().getName());
		this.register(EntityFilteringFeature.class);
	}

}

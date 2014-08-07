package com.ctrip.platform.dal.console;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.ctrip.platform.dal.console.resource.DbResource;

@ApplicationPath("/rest")
public class DasConsoleApplication extends ResourceConfig {
	public DasConsoleApplication() {
		packages(DbResource.class.getPackage().getName());
		this.register(EntityFilteringFeature.class);
	}
}

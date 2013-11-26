package com.ctrip.sysdev.das.console;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.ctrip.sysdev.das.console.resource.DbResource;

@ApplicationPath("/console")
public class DasConsoleApplication extends ResourceConfig {
	public DasConsoleApplication() {
		packages(DbResource.class.getPackage().getName());
		this.register(EntityFilteringFeature.class);
	}
}

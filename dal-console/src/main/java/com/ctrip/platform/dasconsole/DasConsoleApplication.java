package com.ctrip.platform.dasconsole;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.ctrip.platform.dasconsole.resource.DbResource;

@ApplicationPath("/rest")
public class DasConsoleApplication extends ResourceConfig {
	public DasConsoleApplication() {
		packages(DbResource.class.getPackage().getName());
		this.register(EntityFilteringFeature.class);
	}
}

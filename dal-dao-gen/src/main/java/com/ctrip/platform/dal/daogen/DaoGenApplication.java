package com.ctrip.platform.dal.daogen;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.resource.ProjectResource;
import com.ctrip.platform.dal.datasource.LocalDataSourceLocator;

@ApplicationPath("/rest")
public class DaoGenApplication extends ResourceConfig {

	public DaoGenApplication() {
		//将与ProjectResource同Package的所有Class均注册为Jersey的Resource
		packages(ProjectResource.class.getPackage().getName());
		this.register(EntityFilteringFeature.class);
		
		Configuration.addResource("conf.properties");
		
		LocalDataSourceLocator.newInstance().initialize(Configuration.get("all_in_one"));
	}
	
	public static void main(String[] args) {
	}

}

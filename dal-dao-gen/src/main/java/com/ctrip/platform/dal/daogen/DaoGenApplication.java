package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.resource.ProjectResource;
import com.ctrip.platform.dal.daogen.utils.Configuration;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/rest")
public class DaoGenApplication extends ResourceConfig {
    public DaoGenApplication() throws Exception {
        // 将与ProjectResource同Package的所有Class均注册为Jersey的Resource
        packages(ProjectResource.class.getPackage().getName());
        this.register(EntityFilteringFeature.class);

        Configuration.addResource("conf.properties");
    }
}

package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.hickwall.HickwallMetrics;
import com.ctrip.platform.dal.daogen.resource.DalDynamicDSResource;
import com.ctrip.platform.dal.daogen.resource.DalReportResource;
import com.ctrip.platform.dal.daogen.utils.Configuration;

import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/rest")
public class DaoGenApplication extends ResourceConfig {
    public DaoGenApplication() throws Exception {
        // 将与ProjectResource同Package的所有Class均注册为Jersey的Resource
        packages(DalReportResource.class.getPackage().getName());
        this.register(EntityFilteringFeature.class);

        Configuration.addResource("conf.properties");

        // init hickwall metrics
        HickwallMetrics.initHickwallMetrics();

        // init dal version report
        DalReportResource.initReportData();

        // init dal dynamic datasource data
        DalDynamicDSResource.initDalDynamicDS();
    }
}

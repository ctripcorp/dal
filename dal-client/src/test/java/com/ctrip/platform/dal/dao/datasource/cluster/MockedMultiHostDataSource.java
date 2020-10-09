package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

import java.util.List;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class MockedMultiHostDataSource extends MultiHostDataSource {

    public MockedMultiHostDataSource(Map<HostSpec, DataSourceConfigure> dataSourceConfigs,
                                     MultiHostClusterOptions clusterOptions) {
        super(dataSourceConfigs, clusterOptions);
    }

    @Override
    protected RouteStrategy buildRouteStrategy() {
        return (factory, context, options) -> {
            List<HostSpec> primaryHost = options.orderedMasters(context.clientZone());
            return factory.getConnectionForHost(primaryHost.get(0));
        };
    }

}

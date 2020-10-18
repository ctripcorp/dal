package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

import java.util.List;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class MockMultiHostDataSource extends MultiHostDataSource {

    private final String mockClientZone;

    public MockMultiHostDataSource(Map<HostSpec, DataSourceConfigure> dataSourceConfigs,
                                   MultiHostClusterProperties clusterProperties,
                                   String mockClientZone) {
        super(dataSourceConfigs, clusterProperties);
        this.mockClientZone = mockClientZone;
    }

    @Override
    protected RequestContext buildRequestContext() {
        return mockClientZone != null ? new DefaultRequestContext(mockClientZone) : super.buildRequestContext();
    }

}

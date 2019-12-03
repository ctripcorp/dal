package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.ClusterInfo;

public class CtripLocalClusterInfoProvider implements ClusterInfoProvider {

    @Override
    public ClusterInfo getClusterInfo(String titanKey) {
        return null;
    }

}

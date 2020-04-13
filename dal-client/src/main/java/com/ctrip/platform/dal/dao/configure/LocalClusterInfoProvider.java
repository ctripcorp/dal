package com.ctrip.platform.dal.dao.configure;

public class LocalClusterInfoProvider implements ClusterInfoProvider {

    @Override
    public ClusterInfo getClusterInfo(String databaseKey) {
        return new NullClusterInfo();
    }

}

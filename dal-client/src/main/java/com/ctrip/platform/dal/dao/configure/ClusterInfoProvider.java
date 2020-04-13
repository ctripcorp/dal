package com.ctrip.platform.dal.dao.configure;

public interface ClusterInfoProvider {

    ClusterInfo getClusterInfo(String databaseKey);

}

package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.ClusterInfo;

public interface ClusterInfoProvider {

    ClusterInfo getClusterInfo(String titanKey);

}

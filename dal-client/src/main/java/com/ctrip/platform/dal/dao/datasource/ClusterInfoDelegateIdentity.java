package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.ClusterInfo;

/**
 * @author c7ch23en
 */
public interface ClusterInfoDelegateIdentity extends DataSourceIdentity {

    ClusterInfo getClusterInfo();

}

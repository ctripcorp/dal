package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

/**
 * @author c7ch23en
 */
public interface DalExtendedPoolConfiguration {

    int getSessionWaitTimeout();

    DataSourceIdentity getDataSourceId();

    HostSpec getHost();

}

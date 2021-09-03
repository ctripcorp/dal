package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.base.Listenable;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;

public interface ConnectionStringConfigureProvider extends Listenable<DalConnectionStringConfigure> {

    String getDbName();

    DalConnectionStringConfigure getConnectionString() throws Exception;

}

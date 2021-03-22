package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.configure.DalConfigure;

public interface DalClientExtension {

    /**
     * Init the necessary variables for custom DalClient.
     * @param configure
     * @param logicDbName
     */
    void init(DalConfigure configure, String logicDbName);

    /**
     * Release resources whenever the instance need.
     */
    void destroy();
}

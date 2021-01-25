package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.configure.DalConfigure;

public interface DalClientExtension {

    void init(DalConfigure configure, String logicDbName);

    void destroy();
}

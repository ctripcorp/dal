package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.common.enums.DBModel;

import java.util.Properties;

public interface DalPoolPropertiesConfigure extends PoolPropertiesConfigure {
    Properties getProperties();
    String getOption();

    // mgr datasource need
    String getDBToken();
    Integer getCallMysqlApiPeriod();
    DBModel getDBModel();
}

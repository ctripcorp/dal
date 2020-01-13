package com.ctrip.platform.dal.dao.configure;

import java.util.Properties;

public interface DalPoolPropertiesConfigure extends PoolPropertiesConfigure {
    Properties getProperties();
    String getOption();
}

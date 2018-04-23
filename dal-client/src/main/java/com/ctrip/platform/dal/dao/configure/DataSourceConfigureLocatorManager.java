package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;

public class DataSourceConfigureLocatorManager {
    private volatile static DataSourceConfigureLocator locator = null;

    public synchronized static DataSourceConfigureLocator getInstance() {
        if (locator == null) {
            locator = ServiceLoaderHelper.getInstance(DataSourceConfigureLocator.class);
        }
        return locator;
    }

}

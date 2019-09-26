package com.ctrip.datasource.datasource.MockQConfigProvider;

import com.ctrip.platform.dal.dao.configure.DalPoolPropertiesConfigure;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;
import com.ctrip.platform.dal.exceptions.DalException;

/**
 * Created by taochen on 2019/8/28.
 */
public class FailedQConfigPoolPropertiesProvider implements PoolPropertiesProvider {
    public static final String EXCEPTION_MESSAGE = "An error occured while getting datasource.properties from QConfig.";

    @Override
    public DalPoolPropertiesConfigure getPoolProperties() {
        try {
            throw new DalException(EXCEPTION_MESSAGE);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addPoolPropertiesChangedListener(PoolPropertiesChanged callback) {

    }
}

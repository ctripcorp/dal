package com.ctrip.datasource.datasource.MockQConfigProvider;

import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;
import com.ctrip.platform.dal.exceptions.DalException;

public class ExceptionQConfigPoolPropertiesProvider implements PoolPropertiesProvider {
    public static final String EXCEPTION_MESSAGE = "An error occured while getting datasource.properties from QConfig.";

    @Override
    public PoolPropertiesConfigure getPoolProperties() {
        try {
            throw new DalException(EXCEPTION_MESSAGE);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addPoolPropertiesChangedListener(PoolPropertiesChanged callback) {
        throw new UnsupportedOperationException();
    }

}

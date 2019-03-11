package com.ctrip.datasource.datasource.MockQConfigProvider;

import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.exceptions.DalException;

import java.util.Map;
import java.util.Set;

public class ExceptionQConfigConnectionStringProvider implements ConnectionStringProvider {
    @Override
    public Map<String, DalConnectionString> getConnectionStrings(Set<String> names) throws Exception {

        throw new DalException("ExceptionQConfigConnectionStringProvider");

    }

    @Override
    public void addConnectionStringChangedListener(final String name, final ConnectionStringChanged callback) {

    }
}

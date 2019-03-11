package com.ctrip.datasource.datasource.MockQConfigProvider;

import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.InvalidConnectionString;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.exceptions.DalException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InvalidQConfigConnectionStringProvider implements ConnectionStringProvider {
    @Override
    public Map<String, DalConnectionString> getConnectionStrings(Set<String> names) throws Exception {
        Map<String, DalConnectionString> configures = new HashMap<>();
        for (String name : names)
            configures.put(name, new InvalidConnectionString(name, new DalException("ExceptionQConfigConnectionStringProvider")));
        return configures;
    }

    @Override
    public void addConnectionStringChangedListener(final String name, final ConnectionStringChanged callback) {

    }
}

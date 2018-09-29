package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DalConnectionString;

import java.util.Map;
import java.util.Set;

public interface ConnectionStringProvider {
    Map<String, DalConnectionString> getConnectionStrings(Set<String> names) throws Exception;

    void addConnectionStringChangedListener(final String name, final ConnectionStringChanged callback);

}

package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.ConnectionString;

import java.util.Map;
import java.util.Set;

public interface ConnectionStringProvider {
    Map<String, ConnectionString> getConnectionStrings(Set<String> names) throws Exception;

    void addConnectionStringChangedListener(final String name, final ConnectionStringChanged callback);

}

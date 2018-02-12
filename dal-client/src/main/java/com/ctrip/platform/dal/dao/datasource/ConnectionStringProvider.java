package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

import java.util.Map;
import java.util.Set;

public interface ConnectionStringProvider {
    Map<String, DataSourceConfigure> getConnectionStrings(Set<String> dbNames) throws Exception;

    void addConnectionStringChangedListener(final String name, final ConnectionStringChanged callback);
}

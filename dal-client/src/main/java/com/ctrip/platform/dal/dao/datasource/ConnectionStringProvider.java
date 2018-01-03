package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.common.enums.SourceType;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.exceptions.DalException;

import java.util.Map;
import java.util.Set;

public interface ConnectionStringProvider {
    void initialize(Map<String, String> settings) throws DalException;

    Map<String, DataSourceConfigure> initializeConnectionStrings(Set<String> dbNames, SourceType sourceType);

    Map<String, DataSourceConfigure> getConnectionStrings(Set<String> dbNames) throws Exception;

    void addConnectionStringChangedListener(final String name, final ConnectionStringChanged callback);

    void clear();
}

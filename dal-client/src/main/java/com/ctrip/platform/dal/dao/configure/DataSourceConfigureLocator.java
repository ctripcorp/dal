package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.helper.Ordered;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public interface DataSourceConfigureLocator extends Ordered {
    void addUserPoolPropertiesConfigure(String name, DalPoolPropertiesConfigure configure);

    DalPoolPropertiesConfigure getUserPoolPropertiesConfigure(String name);

    DataSourceConfigure getDataSourceConfigure(String name);

    DataSourceConfigure getDataSourceConfigure(DataSourceIdentity id);

    void removeDataSourceConfigure(DataSourceIdentity id);

    Set<String> getDataSourceConfigureKeySet();

    Map<String, DalConnectionString> getAllConnectionStrings();

    Map<String, DalConnectionString> getSuccessfulConnectionStrings();

    Map<String, DalConnectionString> getFailedConnectionStrings();

    Map<String, DalConnectionStringConfigure> getFailedVariableConnectionStrings();

    void setIPDomainStatus(IPDomainStatus status);

    IPDomainStatus getIPDomainStatus();

    void setConnectionStrings(Map<String, DalConnectionString> map);

    DalConnectionString setConnectionString(String name, DalConnectionString connectionString);

    void setVariableConnectionStringConfigs(Map<String, DalConnectionStringConfigure> map);

    Properties setPoolProperties(DalPoolPropertiesConfigure configure);

    PropertiesWrapper getPoolProperties();

    DataSourceConfigure mergeDataSourceConfigure(DalConnectionString connectionString);

    DataSourceConfigure mergeDataSourceConfigure(DalConnectionStringConfigure configure);
}

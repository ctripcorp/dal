package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.dao.helper.Ordered;

import java.util.Map;
import java.util.Set;

public interface DataSourceConfigureLocator extends Ordered {
    void addUserPoolPropertiesConfigure(String name, PoolPropertiesConfigure configure);

    PoolPropertiesConfigure getUserPoolPropertiesConfigure(String name);

    DataSourceConfigure getDataSourceConfigure(String name);

    void addDataSourceConfigureKeySet(Set<String> names);

    Set<String> getDataSourceConfigureKeySet();

    void setIPDomainStatus(IPDomainStatus status);

    IPDomainStatus getIPDomainStatus();

    void setConnectionStrings(Map<String, ConnectionString> map);

    void setPoolProperties(PoolPropertiesConfigure configure);

    DataSourceConfigure mergeDataSourceConfigure(ConnectionString connectionString);

}

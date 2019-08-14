package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

public class SingleDataSourceConfigureProvider implements IDataSourceConfigureProvider {

    private DataSourceIdentity identity;
    private DataSourceConfigureProvider provider;

    public SingleDataSourceConfigureProvider(DataSourceIdentity identity, DataSourceConfigureProvider provider){
        this.identity = identity;
        this.provider = provider;
    }

    public IDataSourceConfigure getDataSourceConfigure(){
        return provider.getDataSourceConfigure(identity);
    }

    public IDataSourceConfigure forceLoadDataSourceConfigure(){
        return provider.forceLoadDataSourceConfigure(identity);
    }

}

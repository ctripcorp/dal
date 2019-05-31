package com.ctrip.platform.dal.dao.configure;


public class SingleDataSourceConfigureProvider implements IDataSourceConfigureProvider {
    private String name;
    private DataSourceConfigureProvider provider;

    public SingleDataSourceConfigureProvider(String name, DataSourceConfigureProvider provider){
        this.name=name;
        this.provider=provider;
    }

    public IDataSourceConfigure getDataSourceConfigure(){
        return provider.getDataSourceConfigure(name);
    }

    public IDataSourceConfigure forceLoadDataSourceConfigure(){
        return provider.forceLoadDataSourceConfigure(name);
    }
}

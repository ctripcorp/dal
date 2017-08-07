package com.ctrip.platform.dal.dao.configure;

import java.util.Map;
import java.util.Set;

public class DefaultDataSourceConfigureProvider implements DataSourceConfigureProvider {

	@Override
	public void initialize(Map<String, String> settings) throws Exception {
	}

	@Override
	public DataSourceConfigure getDataSourceConfigure(String dbName) {
	    DatabasePoolConfig dpc = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg(dbName);
	    if(dpc == null)
	        return new DataSourceConfigure(dbName);
	    
	    return new DataSourceConfigure(dbName, dpc.getMap());
	}

	@Override
	public void setup(Set<String> dbNames) {
	}

    @Override
    public void register(String dbName, DataSourceConfigureChangeListener listener) {
    }
}

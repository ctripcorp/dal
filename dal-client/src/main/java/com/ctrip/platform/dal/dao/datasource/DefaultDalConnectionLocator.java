package com.ctrip.platform.dal.dao.datasource;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureProvider;

public class DefaultDalConnectionLocator implements DalConnectionLocator {
	public static final String DATASOURCE_CONFIG_PROVIDER = "dataSourceConfigureProvider";

	private DataSourceLocator locator;
	private DataSourceConfigureProvider provider;
	
	@Override
	public void initialize(Map<String, String> settings) throws Exception {
		provider = new DefaultDataSourceConfigureProvider();
		if(settings.containsKey(DATASOURCE_CONFIG_PROVIDER)){
			provider = (DataSourceConfigureProvider) Class.forName(settings.get(DATASOURCE_CONFIG_PROVIDER)).newInstance();
		}
		
		provider.initialize(settings);
		
		locator = new DataSourceLocator(provider);
	}

	@Override
	public void setup(Set<String> dbNames) {
		provider.setup(dbNames);
	}
	
	@Override
	public Connection getConnection(String name) throws Exception {
		return locator.getDataSource(name).getConnection();
	}
}

package com.ctrip.platform.dal.dao.datasource;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.DefaultConnectionStringParser;

public class DefaultDalConnectionLocator implements DalConnectionLocator {
	
	private DataSourceLocator locator;
	
	private String dc;
	
	@Override
	public void initialize(Map<String, String> settings) throws Exception {
		String tmpDc = settings.get("dc");
		dc = tmpDc == null ? "" : tmpDc;
		
		ConnectionStringParser parser = new DefaultConnectionStringParser();
		if(settings.containsKey("connectionStringParser")){
			parser = (ConnectionStringParser) Class.forName(settings.get("connectionStringParser")).newInstance();
		}
		
		locator = DataSourceLocator.newInstance(parser);
	}

	@Override
	public Set<String> getDBNames() {
		return locator.getDBNames();
	}

	@Override
	public Connection getConnection(String name) throws Exception {
		return locator.getDataSource(name + dc).getConnection();
	}

}

package com.ctrip.platform.dal.dao.datasource;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;

public class DefaultDalConnectionLocator implements DalConnectionLocator {
	
	private DataSourceLocator locator;
	
	private String dc;
	
	@Override
	public void initLocator(Map<String, String> settings) throws Exception {
		String tmpDc = settings.get("dc");
		dc = tmpDc == null ? "" : tmpDc;
		ConnectionStringParser parser = (ConnectionStringParser) Class.forName(settings.get("connectionStringParser")).newInstance();
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

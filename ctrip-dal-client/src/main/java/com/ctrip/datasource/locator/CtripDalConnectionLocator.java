package com.ctrip.datasource.locator;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.client.DalConnectionLocator;

public class CtripDalConnectionLocator implements DalConnectionLocator {
	private DataSourceLocator locator;
	private String dc;
	@Override
	public void initLocator(Map<String, String> settings) {
		String tmpDc = settings.get("dc");
		dc = tmpDc == null ? "" : tmpDc;
		locator = DataSourceLocator.newInstance();
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
package com.ctrip.platform.dal.dao.configure;

import java.util.Map;
import java.util.Set;

public class DefaultDataSourceConfigureProvider implements DataSourceConfigureProvider {

	@Override
	public void initialize(Map<String, String> settings) throws Exception {
	}

	@Override
	public DataSourceConfigure getDataSourceConfigure(String dbName) {
		return null;
	}

	@Override
	public void setup(Set<String> dbNames) {
	}
}

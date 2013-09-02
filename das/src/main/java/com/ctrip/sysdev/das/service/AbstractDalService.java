package com.ctrip.sysdev.das.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.ctrip.sysdev.das.commons.DataSourceWrapper;
import com.google.inject.Inject;

public abstract class AbstractDalService {

	@Inject
	private DataSourceWrapper dataSourceWrapper;

	public Connection getConnection() throws SQLException {
		return dataSourceWrapper.getConnection();
	}

	public DataSourceWrapper getDataSourceWrapper() {
		return dataSourceWrapper;
	}

	public void setDataSourceWrapper(DataSourceWrapper dataSourceWrapper) {
		this.dataSourceWrapper = dataSourceWrapper;
	}

}

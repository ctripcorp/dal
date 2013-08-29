package com.ctrip.sysdev.das.commons;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSourceWrapper {

	public void init() throws SQLException;

	public void close();

	public Connection getConnection() throws SQLException;
}

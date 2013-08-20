package com.ctrip.sysdev.das.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.ctrip.sysdev.das.utils.Consts;

public class BaseHandler {

	protected Connection connection;
	protected Statement statement;
	
	public void init() throws Exception {
		// Register the driver of mysql
		// Class.forName("com.mysql.jdbc.Driver");

		// Register the driver of sql server
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		// connection = DriverManager.getConnection(Consts.connectionString,
		// Consts.user, Consts.password);
		connection = DriverManager.getConnection(Consts.connectionString);

		statement = connection.createStatement();

	}

	public void close() throws Exception {

		if (!statement.isClosed()) {
			statement.close();
		}

		if (!connection.isClosed()) {
			connection.close();
		}

	}

}

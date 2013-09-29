package com.ctrip.sysdev.pack;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.utils.Consts;

public class BatchTest {

	private Connection connection;
	private Statement statement;

	/**
	 * 
	 * @param tnxCtxt
	 * @param statement
	 * @param params
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public ResultSet fetch(String tnxCtxt, int flag, String statement,
			Parameter... params) throws Exception {

		PreparedStatement ps = connection.prepareStatement(statement);

		int currentParameterIndex = 1;
		Arrays.sort(params);
		for (int i = 0; i < params.length; i++) {
			params[i].setParameterIndex(currentParameterIndex);
			params[i].setPreparedStatement(ps);
			currentParameterIndex = params[i].getParameterIndex() + 1;
		}

		ResultSet rs = ps.executeQuery();

		return rs;
	}

	public int execute(String tnxCtxt, int flag, String statement,
			Parameter... params) throws Exception {

		PreparedStatement ps = connection.prepareStatement(statement);

		ps.setString(1, "gawu0");
		ps.setString(2, "shanghai0");

		ps.addBatch();

		ps.setString(1, "gawu1");
		ps.setString(2, "shanghai1");

		ps.addBatch();
		
		 ps.executeBatch();
		 
		 connection.commit();
		
		return 0;
	}

	public void init() throws Exception {
		// Register the driver of mysql
		// Class.forName("com.mysql.jdbc.Driver");

		// Register the driver of sql server
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		// connection = DriverManager.getConnection(Consts.connectionString,
		// Consts.user, Consts.password);
		connection = DriverManager.getConnection(Consts.connectionString);
		
		connection.setAutoCommit(false);

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

	public static void main(String[] args) throws Exception {
		BatchTest bt = new BatchTest();
		bt.init();

		int row = bt.execute(null, 0,
				"Insert into Person (Address, Name) Values (?,?)", null);
		
		System.out.println(row);

		bt.close();
	}

}

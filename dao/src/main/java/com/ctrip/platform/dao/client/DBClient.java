package com.ctrip.platform.dao.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import com.ctrip.platform.dao.enums.ParameterType;
import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.utils.Consts;

public class DBClient {

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

		boolean batchOperation = false;

		for (Parameter p : params) {
			if (p.getParameterType() == ParameterType.PARAMARRAY) {
				batchOperation = true;
				break;
			}
		}

		PreparedStatement ps = connection.prepareStatement(statement);

		int currentParameterIndex = 1;
		Arrays.sort(params);
		for (int i = 0; i < params.length; i++) {
			params[i].setParameterIndex(currentParameterIndex);
			ps = params[i].setPreparedStatement(ps);
			currentParameterIndex = params[i].getParameterIndex() + 1;
		}
		
		int count= 0;
		
		if(batchOperation){
			int[] counts = ps.executeBatch();
			for(int c : counts){
				count += c;
			}
		}else{
			count = ps.executeUpdate();
		}

		connection.commit();

		return count;
	}

	public ResultSet fetchBySp(String tnxCtxt, int flag, String sp,
			Parameter... params) throws Exception {

		StringBuffer occupy = new StringBuffer();

		for (int i = 0; i < params.length; i++) {
			occupy.append("?");
			occupy.append(",");
		}
		occupy.deleteCharAt(occupy.length() - 1);

		CallableStatement callableStmt = connection.prepareCall(String.format(
				"{call dbo.%s(%s)}", sp, occupy.toString()));

		for (int i = 0; i < params.length; i++) {
			params[i].setPreparedStatement(callableStmt);
		}

		return callableStmt.executeQuery();
	}

	/**
	 * 
	 * @param tnxCtxt
	 * @param sp
	 * @param flag
	 * @param params
	 * @return
	 */
	public int executeSp(String tnxCtxt, int flag, String sp,
			Parameter... params) throws Exception {

		StringBuffer occupy = new StringBuffer();

		for (int i = 0; i < params.length; i++) {
			occupy.append("?");
			occupy.append(",");
		}
		occupy.deleteCharAt(occupy.length() - 1);

		CallableStatement callableStmt = connection.prepareCall(String.format(
				"{call dbo.%s(%s)}", sp, occupy.toString()));

		for (int i = 0; i < params.length; i++) {
			params[i].setPreparedStatement(callableStmt);
		}

		return callableStmt.executeUpdate();
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

}

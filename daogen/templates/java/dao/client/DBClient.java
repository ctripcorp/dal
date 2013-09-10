package {{product_line}}.{{domain}}.{{app_name}}.dao.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import {{product_line}}.{{domain}}.{{app_name}}.dao.param.Parameter;
import {{product_line}}.{{domain}}.{{app_name}}.dao.utils.Consts;

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
	public ResultSet fetch(String tnxCtxt, String statement, int flag,
			Parameter... params) throws Exception {

		PreparedStatement ps = connection.prepareStatement(statement);

		int currentParameterIndex = 1;
		Arrays.sort(params);
		for (int i = 0; i < params.length; i++) {
			params[i].setParameterIndex(currentParameterIndex);
			params[i].setPreparedStatement(ps);
			currentParameterIndex= params[i].getParameterIndex() + 1;
		}

		ResultSet rs = ps.executeQuery();

		return rs;
	}

	public int bulkInsert(String tnxCtxt, String statement,
			List<Parameter> params, int flag) {

		return 0;
	}

	public int execute(String tnxCtxt, String statement, int flag,
			Parameter... params) throws Exception{

		PreparedStatement ps = connection.prepareStatement(statement);

		for (int i = 0; i < params.length; i++) {
			params[i].setPreparedStatement(ps);
		}

		return ps.executeUpdate();
	}

	public ResultSet fetchBySp(String tnxCtxt, String sp, int flag,
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
	public int executeSp(String tnxCtxt, String sp, int flag,
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
		// DBClient db = new DBClient();
		// db.init();
		// db.fetchBySp(null, null, null, 0);
		// db.close();
	}

}

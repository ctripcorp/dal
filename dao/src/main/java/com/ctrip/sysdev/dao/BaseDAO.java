package com.ctrip.sysdev.dao;

import java.sql.ResultSet;
import java.util.List;

import com.ctrip.sysdev.client.DBClient;
import com.ctrip.sysdev.msg.AvailableType;

public class BaseDAO{ //implements IDAO {

	/*
	 * Free Form SQL Example:
	 * 
	 * SELECT Address, Telephone FROM Person
	 * 
	 * WHERE Name = {string} AND Age BETWEEN {int} AND {int}
	 * 
	 * AND Gender IN {int[]}
	 */

	private boolean useDBClient;
	private DBClient dbClient;

	public BaseDAO() {

	}

	public boolean getDbClient() {
		return this.useDBClient;
	}

	public void setDbClient(boolean dbClient) {
		this.useDBClient = dbClient;
	}

	public ResultSet fetch(String tnxCtxt,
			DAOFunction statement, List<AvailableType> params, int flag)
			throws Exception {

//		for (int i = 0; i < params.size(); i++) {
//			if (statement.getRequiredParams().get(i) != params.get(i)
//					.getCurrentClass()) {
//				throw new ParametersInvalidException(String.format(
//						"Expect type of %s but got %s!", statement
//								.getRequiredParams().get(i), params.get(i)
//								.getCurrentClass()));
//			}
//		}

		// all the parameters required are now provided
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.fetch(null, statement, params, 0);
		} else {

		}

		return null;
	}

	public <T> List<T> fetchByORM(String tnxCtxt, DAOFunction statement,
			List<AvailableType> params, int flag) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet fetchBySp(String tnxCtxt, DAOFunction sp,
			List<AvailableType> params, int flag) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> List<T> fetchBySpByORM(String tnxCtxt, DAOFunction sp,
			List<AvailableType> params, int flag) {
		// TODO Auto-generated method stub
		return null;
	}

	public int execute(String tnxCtxt, DAOFunction statement,
			List<AvailableType> params, int flag) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int executeSp(String tnxCtxt, DAOFunction sp,
			List<AvailableType> params, int flag) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int bulkInsert(String tnxCtxt, DAOFunction statement,
			List<AvailableType> params, int flag) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) throws Exception {

		// new BaseDAO().fetch(null, null, null, (Flags.TEST.getIntVal() |
		// Flags.COMMIT.getIntVal()));
		// new BaseDAO().fetch(null, null, null,Flags.COMMIT.getIntVal());
		// new BaseDAO().fetch(null, null, null,Flags.TEST.getIntVal());

		// Statement stmt;
		//
		// Class.forName("com.mysql.jdbc.Driver");
		//
		// String url = "jdbc:mysql://192.168.83.132:3306/dao_test";
		//
		// Connection conn = DriverManager.getConnection(url, "root", "123456");
		//
		// stmt = conn.createStatement();
		//
		// //PreparedStatement ps =
		// conn.prepareStatement("SELECT * FROM Person WHERE Gender = ?");
		//
		// PreparedStatement ps =
		// conn.prepareStatement("SELECT * FROM Person WHERE Gender in ?");
		//
		// //ps.setInt(1, 1);
		// Date d = new Date();
		//
		// System.out.println(d.toString());
		//
		// ResultSet rs = ps.executeQuery();
		//
		// //ResultSet rs = stmt.executeQuery("SELECT * FROM Person");
		//
		// ResultSetMetaData rsmd = rs.getMetaData();
		//
		// int totalColumns = rsmd.getColumnCount();
		//
		// int[] colTypes = new int[totalColumns];
		//
		// for (int i = 1; i <= totalColumns; i++) {
		// int currentColType = rsmd.getColumnType(i);
		// colTypes[i - 1] = currentColType;
		// }
		//
		// while (rs.next()) {
		// for (int i = 1; i <= totalColumns; i++) {
		// switch (colTypes[i-1]) {
		// case java.sql.Types.INTEGER:
		// System.out.println(rs.getInt(i));
		// break;
		// case java.sql.Types.VARCHAR:
		// System.out.println(rs.getString(i));
		// break;
		// default:
		// System.out.println("---------begin default----------");
		// System.out.println(rs.getObject(i));
		// System.out.println("----------end default----------");
		// break;
		// }
		// }
		// }

	}

}

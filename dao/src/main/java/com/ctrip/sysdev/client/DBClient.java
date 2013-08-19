package com.ctrip.sysdev.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.ctrip.sysdev.dao.DAOFunction;
import com.ctrip.sysdev.msg.AvailableType;
import com.ctrip.sysdev.utils.Consts;
import com.ctrip.sysdev.utils.DAOResultSet;

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
	public ResultSet fetch(String tnxCtxt, DAOFunction statement, 
			List<AvailableType> params, int flag) throws Exception{
		
		PreparedStatement ps = connection.prepareStatement(statement.getSql());
		
		for(int i=1;i<=params.size();i++){
			AvailableType at = params.get(i-1);
			at.setPreparedStatement(ps, i);
		}
		
		ResultSet rs = ps.executeQuery();
		
		return rs;
		
//		List<List<AvailableType>> results = new LinkedList<List<AvailableType>>();
//		
//		while(rs.next()){
//			List<AvailableType> result = new LinkedList<AvailableType>();
//			for(int key : statement.getResultFields().keySet()){
//				result.add(AvailableType.getResultSet(
//						rs, statement.getResultFields().get(key), key)
//						);
//			}
//			results.add(result);
//		}
//		
//		return new DAOResultSet(results);
	}
	
	public int bulkInsert(String tnxCtxt, String statement,
			List<AvailableType> params, int flag){
		
		return 0;
	}
	
	public int execute(String tnxCtxt, String statement, 
			List<AvailableType> params, int flag){
		
		return 0;
	}
	
	public ResultSet fetchBySp(String tnxCtxt, String sp, 
			List<AvailableType> params, int flag) throws Exception{
		
//		CallableStatement callableStmt = connection.prepareCall("{call demoSp(?)}");
//		
//		callableStmt.setInt("inputParam", 1);
//		
//		ResultSet rs = callableStmt.executeQuery();
//		
//		while(rs.next()){
//			System.out.println(rs.getString(1));
//		}
		
		CallableStatement callableStmt = connection.prepareCall("{call dbo.demoSp(?)}");
		
		callableStmt.setInt("inputParam", 1);
		
//		callableStmt.setInt("outputParam", 1);
		
		ResultSet rs = callableStmt.executeQuery();
		
		while(rs.next()){
			System.out.println(rs.getString(1));
		}
	
		return null;
	}
	
	public int executeSp(String tnxCtxt, String sp, 
			List<AvailableType> params, int flag){
		
		return 0;
	}
	
	public void init() throws Exception{
		//Register the driver of mysql
		//Class.forName("com.mysql.jdbc.Driver");
		
		//Register the driver of sql server
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		
		//connection = DriverManager.getConnection(Consts.connectionString, Consts.user, Consts.password);
		connection = DriverManager.getConnection(Consts.connectionString);
		
		statement = connection.createStatement();

	}
	
	public void close() throws Exception{
		
		if(!statement.isClosed()){
			statement.close();
		}
		
		if(!connection.isClosed()){
			connection.close();
		}
		
	}
	
	public static void main(String[] args) throws Exception{
//		DBClient db = new DBClient();
//		db.init();
//		db.fetchBySp(null, null, null, 0);
//		db.close();
	}

}


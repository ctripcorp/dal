package com.ctrip.platform.dal.daogen.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.daogen.Consts;

public class SQLValidation {
	private static final String MYSQLPLAINPATTER = "|%1$-5s|%2$-10s|%3$-15s|%4$-10s|%5$-5s|%6$-32s\n";
	
	public static void main(String[] args){
		System.out.println(queryValidate("dao_test", "Select * from Person"));
		String sql = "Select * from [Hotel] WHERE [HotelID] = ? and [HotelName] = ?";
		int[] parms = new int[] {Types.INTEGER, Types.NVARCHAR};
		for (int i = 0; i < parms.length; i++) {
			Object mockValue = mockSQLValue(parms[i]);
			String replacement = mockValue instanceof String ? "'" + mockValue.toString() + "'" : mockValue.toString();
			sql = sql.replaceFirst("\\?", replacement);
		}
		System.out.println(queryValidate("HotelPubDB_test1", sql, parms));
	}
	
	public static String queryValidate(String dbName, String sql, int... paramsTyps){
		if(!StringUtils.startsWithIgnoreCase(sql, "SELECT"))
			return "";
		ResultSet rs = null;
		Connection connection = null;
		StringBuilder result = new StringBuilder();
		try{
			connection = DataSourceUtil.getConnection(dbName);
			String dbType = getDBType(connection, dbName);
			if(dbType == "MySQL"){
			String sql_content = "EXPLAIN " + sql;
				PreparedStatement stat = connection.prepareStatement(sql_content);
				if(paramsTyps.length > 0){
					for (int i = 1; i <= paramsTyps.length; i++) {
						stat.setObject(i, mockSQLValue(paramsTyps[i-1]));
					}
				}
				rs = stat.executeQuery();
				
				result.append(String.format(MYSQLPLAINPATTER, 
						"id", "type", "possible_keys","key", "rows","Extra"));
				while(rs.next()){
					result.append(String.format(MYSQLPLAINPATTER, rs.getObject("id"),
							rs.getObject("select_type"),
							rs.getObject("possible_keys"),
							rs.getObject("key"),
							rs.getObject("rows"),
							rs.getObject("Extra")));
				}
			}
			else if(dbType.equals("Microsoft SQL Server")){
				try{
					connection.setAutoCommit(false);
					Statement profile = connection.createStatement();
					profile.execute("SET SHOWPLAN_ALL ON");
					for (int i = 0; i < paramsTyps.length; i++) {
						Object mockValue = mockSQLValue(paramsTyps[i]);
						String replacement = mockValue instanceof String ? "'" + mockValue.toString() + "'" : mockValue.toString();
						sql = sql.replaceFirst("\\?", replacement);
					}				
					rs = profile.executeQuery(sql);
				
					while(rs.next()){
						for (int i = 0; i < rs.getRow(); i++) {
							result.append(rs.getObject(i+1)).append(System.lineSeparator());
						}
					}
					profile.execute("SET SHOWPLAN_ALL OFF");
				}catch(SQLException e){
					e.printStackTrace();
					try{
						connection.rollback();
					}catch(SQLException excep){
						excep.printStackTrace();
					}
				}
				finally{
					connection.setAutoCommit(false);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result.toString();
	}
	
	private static String getDBType(Connection conn, String dbName) throws SQLException{
		String dbType = null;
		if (Consts.databaseType.containsKey(dbName)) {
			dbType = Consts.databaseType.get(dbName);
		} else {
			dbType = conn.getMetaData().getDatabaseProductName();
			Consts.databaseType.put(dbName, dbType);
		}
		return dbType;
	}

	
	public static Object mockSQLValue(int javaSqlTypes) {
		switch (javaSqlTypes) {
			case java.sql.Types.BIT:
				return true;
			case java.sql.Types.TINYINT:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.BIGINT:
				return 1;
			case java.sql.Types.REAL:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.DECIMAL:
				return 1.0;
			case java.sql.Types.CHAR:
				return 't';
			case java.sql.Types.DATE:
				return "2012-01-01";
			case java.sql.Types.TIME:
				return "10:00:00";
			case java.sql.Types.TIMESTAMP:
				return "2012-01-01 10:00:00";
			default:
				return "test";
		}
	}
	
	
}

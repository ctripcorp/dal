package com.ctrip.platform.dal.daogen.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.Consts;

/**
 * The SQL Validate Utils
 * @author wcyuan
 * @version 2014-08-01
 */
public class SQLValidation {
	
	/**
	 * Common Logger instance.
	 */
	private static Logger log = Logger.getLogger(SQLValidation.class);
	
	/**
	 * The MYSQL explain output format pattern.
	 */
	private static final String MYSQLPLAINPATTER = "|%1$-5s|%2$-10s|%3$-15s|%4$-10s|%5$-5s|%6$-32s\n";
	
	/**
	 * Validate the SQL is correct or not
	 * @param dbName
	 * 		The database name
	 * @param sql
	 * 		The validated SQL
	 * @param paramsTypes
	 * 		The parameter type list, which will be mocked to some default values
	 * @return
	 * 		The SQL is correct, return true, otherwise return false.
	 * @throws Exception 
	 */
	public static Validation validate(String dbName, String sql, int... paramsTypes) throws Exception{
		if(StringUtils.startsWithIgnoreCase(sql, "SELECT"))
			return queryValidate(dbName, sql, paramsTypes);
		else{
			return updateValidate(dbName, sql, paramsTypes);
		}
	}
	
	/**
	 * Validate the Select SQL is correct or not
	 * @param dbName
	 * 		The database name
	 * @param sql
	 * 		The validated SQL
	 * @param paramsTypes
	 * 		The parameter type list, which will be mocked to some default values
	 * @return
	 * 		The SQL is correct, return true, otherwise return false.
	 */
	private static Validation queryValidate(String dbName, String sql, int... paramsTypes){
		Validation status = new Validation(sql);
		Connection connection = null;
		try{
			connection = DataSourceUtil.getConnection(dbName);
			String dbType = getDBType(connection, dbName);
			if(dbType == "MySQL"){
				mysqlQuery(connection, sql, status, paramsTypes);
			}
			else if(dbType.equals("Microsoft SQL Server")){
				sqlserverQuery(connection, sql, status, paramsTypes);
			}
			
		}catch(Exception e){
			status.clearAppend(e.getMessage());
			log.error("Validate query failed", e);
		}
		return status;
	}
	
	private static void sqlserverQuery(Connection conn, String sql, Validation status, int... paramsTypes) throws Exception{
		ResultSet rs = null;
		Statement profile = null;
		try{
			conn.setAutoCommit(false);
			profile = conn.createStatement();
			profile.execute("SET SHOWPLAN_ALL ON");
			for (int i = 0; i < paramsTypes.length; i++) {
				Object mockValue = mockSQLValue(paramsTypes[i]);
				String replacement = mockValue instanceof String ? "'" + mockValue.toString() + "'" : mockValue.toString();
				sql = sql.replaceFirst("\\?", replacement);
			}				
			rs = profile.executeQuery(sql);
		
			while(rs.next()){
				for (int i = 0; i < rs.getRow(); i++) {
					status.append(rs.getObject(i+1).toString())
						.append(System.lineSeparator());
				}
			}
			profile.execute("SET SHOWPLAN_ALL OFF");
		}catch(Exception e){
			status.append(getExceptionStack(e));
			log.error("Validate sql server query failed", e);
		}finally{
			conn.setAutoCommit(true);
			rs.close();
			profile.close();
		}
	}
	
	private static void mysqlQuery(Connection conn, String sql, Validation status, int... paramsTypes) throws Exception {
		ResultSet rs = null;
		PreparedStatement stat = null;
		try{
			String sql_content = "EXPLAIN " + sql;
			stat = conn.prepareStatement(sql_content);
			if(paramsTypes.length > 0){
				for (int i = 1; i <= paramsTypes.length; i++) {
					stat.setObject(i, mockSQLValue(paramsTypes[i-1]));
				}
			}
			rs = stat.executeQuery();
			
			status.appendLineFormat(MYSQLPLAINPATTER, "id", "type", "possible_keys","key", "rows","Extra");
			while(rs.next()){
				status.appendLineFormat(MYSQLPLAINPATTER, 
						rs.getObject("id"),
						rs.getObject("select_type"),
						rs.getObject("possible_keys"),
						rs.getObject("key"),
						rs.getObject("rows"),
						rs.getObject("Extra"));
			}
			status.setPassed(true);
		}catch(Exception e){
			status.append(getExceptionStack(e));
			log.error("Validate mysql query failed", e);
		}finally{
			rs.close();
			stat.close();
		}
	}
	
	/**
	 * Validate the SQL accept for Select statement is correct or not.
	 * @param dbName
	 * 		The database name
	 * @param sql
	 * 		The validated SQL
	 * @param paramsTypes
	 * 		The parameter type list, which will be mocked to some default values
	 * @return
	 * 		The SQL is correct, return true, otherwise return false.
	 */
	private static Validation updateValidate(String dbName, String sql, int... paramsTypes) throws Exception{
		Validation status = new Validation(sql);
		Connection connection = null;
		try{
			connection = DataSourceUtil.getConnection(dbName);
			connection.setAutoCommit(false);
			PreparedStatement stat = connection.prepareStatement(sql);
			if(paramsTypes.length > 0){
				for (int i = 1; i <= paramsTypes.length; i++) {
					stat.setObject(i, mockSQLValue(paramsTypes[i-1]));
				}
			}
			stat.execute();
			status.setPassed(true).append("Validate Successfully");
		}catch(Exception e){
			status.append(getExceptionStack(e));
			log.error("Validate update failed", e);
		}
		finally{
			connection.rollback();
			connection.setAutoCommit(true);
		}
		
		return status;		
	}
	
	private static String getExceptionStack(Throwable e)
	{
		String msg = e.getMessage();
		try {  
            StringWriter sw = new StringWriter();  
            PrintWriter pw = new PrintWriter(sw);  
            e.printStackTrace(pw);  
            msg = sw.toString();  
        } catch (Throwable e2) {  
        	msg = "bad getErrorInfoFromException";  
        }
		
		return msg;
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

	private static Object mockSQLValue(int javaSqlTypes) {
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
	
	public static class Validation{
		private boolean passed;
		private String sql;
		private StringBuffer msg = new StringBuffer();
		
		public Validation(String sql){
			this.sql = sql;
		}
		
		public boolean isPassed() {
			return passed;
		}
		public Validation setPassed(boolean passed) {
			this.passed = passed;
			return this;
		}
		public String getMessage() {
			return msg.toString();
		}
		
		public String getSQL(){
			return this.sql;
		}
		
		public Validation append(String msg) {
			this.msg.append(msg);
			return this;
		}
		
		public Validation appendFormat(String format, Object... args) {
			this.msg.append(String.format(format, args));
			return this;
		}
		
		public Validation appendLineFormat(String format, Object... args){
			this.msg.append(String.format(format, args)).append(System.lineSeparator());
			return this;
		}
		
		public Validation clearAppend(String msg){
			this.msg = new StringBuffer();
			this.msg.append(msg);
			return this;
		}
		
		@Override
		public String toString() {
			return String.format("[Passed: %s, Message: %s]",  this.passed, this.msg.toString());
		}
	}
}

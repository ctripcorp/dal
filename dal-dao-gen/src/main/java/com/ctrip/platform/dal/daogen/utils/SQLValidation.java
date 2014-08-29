package com.ctrip.platform.dal.daogen.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.Consts;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	private static ObjectMapper objectMapper =  new ObjectMapper();
	
	public static void main(String[] args) throws Exception{
		String sql = "INSERT INTO Person(Address, Telephone, Name, Age, Gender, PartmentID, space) VALUES(?, ?, ?, ?, ?, ?, ?)";
		int[] params = new int[]{Types.NVARCHAR, Types.NVARCHAR, Types.NVARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.NVARCHAR};
		String dbName = "dao_test";
		
		Validation v = validate(dbName, sql, params);
		System.out.println(v.toString());
		
		sql = "insert into Person(Address, Telephone, Name, Age, Gender, PartmentID, space) select Address, Telephone, Name, Age, Gender, PartmentID, space from Person where id = 2141839676";
		v = validate(dbName, sql);
		System.out.println(v.toString());
		
		sql = "select * from Person as p join Partment as pp on p.PartmentID = pp.Id where p.space=?";
		v = validate(dbName, sql, Types.NVARCHAR);
		
		System.out.println(v.toString());
		
		dbName = "HotelPubDB_test1";
		sql = "Select * from TestPerson where id = ?";
		v = validate(dbName, sql, Types.VARCHAR);
		
		System.out.println(v.toString());
		
		sql = "insert into TestPerson(name,age,address) values(?,?,?)";
		v = validate(dbName, sql, Types.VARCHAR, Types.INTEGER, Types.VARCHAR);
		
		System.out.println(v.toString());
		
	}
	
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
	public static Validation queryValidate(String dbName, String sql, int... paramsTypes){
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
			List<SqlServerExplain> explains = new ArrayList<SqlServerExplain>();
			while(rs.next()){	
				explains.add(ORMUtils.map(rs, SqlServerExplain.class));
			}
			status.append(objectMapper.writeValueAsString(explains));
			status.setPassed(true);
			profile.execute("SET SHOWPLAN_ALL OFF");
			conn.setAutoCommit(true);
		}catch(Exception e){
			status.append(e.getMessage());
			conn.rollback();
			log.error("Validate sql server query failed", e);
		}finally{
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
			List<MySQLExplain> explains = new ArrayList<MySQLExplain>();
			while(rs.next()){
				explains.add(ORMUtils.map(rs, MySQLExplain.class));
			}
			status.append(objectMapper.writeValueAsString(explains));
			status.setPassed(true);
		}catch(Exception e){
			status.append(e.getMessage());
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
	public static Validation updateValidate(String dbName, String sql, int... paramsTypes) throws Exception{
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
			status.append(e.getMessage());
			log.error("Validate update failed", e);
		}
		finally{
			connection.rollback();
			connection.setAutoCommit(true);
		}
		
		return status;		
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
				return 101;
			case java.sql.Types.REAL:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.DECIMAL:
				return 101.0;
			case java.sql.Types.CHAR:
				return "t";
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
	
	public static class MySQLExplain{
		private Integer id;
		private String select_type;
		private String possible_keys;
		private String key;
		private Integer rows;
		private String extra;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
		
		public String getSelect_type() {
			return select_type;
		}
		
		public void setSelect_type(String select_type) {
			this.select_type = select_type;
		}

		public String getPossible_keys() {
			return possible_keys;
		}

		public void setPossible_keys(String possible_keys) {
			this.possible_keys = possible_keys;
		}


		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Integer getRows() {
			return rows;
		}

		public void setRows(Integer rows) {
			this.rows = rows;
		}

		public String getExtra() {
			return extra;
		}

		public void setExtra(String extra) {
			this.extra = extra;
		}
	}
	
	public static class SqlServerExplain{
		private String StmtText; 
		private Integer StmtId;
		private Integer NodeId;
		private Integer Parent;
		private String PhysicalOp;
		private String LogicalOp;
		private String Argument;
		private String DefinedValues;
		private Integer EstimateRows;
		private Double EstimateIO;
		private Double EstimateCPU;
		private Integer AvgRowSize;
		private Double TotalSubtreeCost;
		private String OutputList;
		private String Type;
		private Boolean Parallel;
		private Integer EstimateExecutions;
		public String getStmtText() {
			return StmtText;
		}
		public void setStmtText(String stmtText) {
			StmtText = stmtText;
		}
		public Integer getStmtId() {
			return StmtId;
		}
		public void setStmtId(Integer stmtId) {
			StmtId = stmtId;
		}
		public Integer getNodeId() {
			return NodeId;
		}
		public void setNodeId(Integer nodeId) {
			NodeId = nodeId;
		}
		public Integer getParent() {
			return Parent;
		}
		public void setParent(Integer parent) {
			Parent = parent;
		}
		public String getPhysicalOp() {
			return PhysicalOp;
		}
		public void setPhysicalOp(String physicalOp) {
			PhysicalOp = physicalOp;
		}
		public String getLogicalOp() {
			return LogicalOp;
		}
		public void setLogicalOp(String logicalOp) {
			LogicalOp = logicalOp;
		}
		public String getArgument() {
			return Argument;
		}
		public void setArgument(String argument) {
			Argument = argument;
		}
		public String getDefinedValues() {
			return DefinedValues;
		}
		public void setDefinedValues(String definedValues) {
			DefinedValues = definedValues;
		}
		public Integer getEstimateRows() {
			return EstimateRows;
		}
		public void setEstimateRows(Integer estimateRows) {
			EstimateRows = estimateRows;
		}
		public Double getEstimateIO() {
			return EstimateIO;
		}
		public void setEstimateIO(Double estimateIO) {
			EstimateIO = estimateIO;
		}
		public Double getEstimateCPU() {
			return EstimateCPU;
		}
		public void setEstimateCPU(Double estimateCPU) {
			EstimateCPU = estimateCPU;
		}
		public Integer getAvgRowSize() {
			return AvgRowSize;
		}
		public void setAvgRowSize(Integer avgRowSize) {
			AvgRowSize = avgRowSize;
		}
		public Double getTotalSubtreeCost() {
			return TotalSubtreeCost;
		}
		public void setTotalSubtreeCost(Double totalSubtreeCost) {
			TotalSubtreeCost = totalSubtreeCost;
		}
		public String getOutputList() {
			return OutputList;
		}
		public void setOutputList(String outputList) {
			OutputList = outputList;
		}
		public String getType() {
			return Type;
		}
		public void setType(String type) {
			Type = type;
		}
		public Boolean getParallel() {
			return Parallel;
		}
		public void setParallel(Boolean parallel) {
			Parallel = parallel;
		}
		public Integer getEstimateExecutions() {
			return EstimateExecutions;
		}
		public void setEstimateExecutions(Integer estimateExecutions) {
			EstimateExecutions = estimateExecutions;
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

package com.ctrip.platform.dal.daogen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Consts {
	
	/**
	 * Key：数字
	 * Value：对应的数据库表达式，如等于表达式为'='
	 */
	public static Map<String, String> WhereConditionMap;
	
	public static Map<Integer, Class> jdbcSqlTypeToJavaClass;
	
	public static List<String> CSharpValueTypes;
	
	static{
		CSharpValueTypes = new ArrayList<String>();
		WhereConditionMap = new HashMap<String, String>();
		jdbcSqlTypeToJavaClass= new HashMap<Integer, Class>();
		
		CSharpValueTypes.add("int");
		CSharpValueTypes.add("DateTime");
		
		WhereConditionMap.put("0", "=");
		WhereConditionMap.put("1", "!=");
		WhereConditionMap.put("2", ">");
		WhereConditionMap.put("3", "<");
		WhereConditionMap.put("4", ">=");
		WhereConditionMap.put("5", "<=");
		WhereConditionMap.put("6", "Between");
		WhereConditionMap.put("7", "Like");
		WhereConditionMap.put("8", "In");
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.BIT, Boolean.class);
		
		// Recommended using Short for Byte
		jdbcSqlTypeToJavaClass.put(java.sql.Types.TINYINT, Integer.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.SMALLINT, Integer.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.INTEGER, Integer.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.BIGINT, Long.class);
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.FLOAT, Double.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.REAL, Float.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.DOUBLE, Double.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.NUMERIC, BigDecimal.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.DECIMAL, BigDecimal.class);
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.CHAR, String.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.VARCHAR, String.class);
		//getAsciiStream  getUnicodeStream
		jdbcSqlTypeToJavaClass.put(java.sql.Types.LONGVARCHAR, String.class);
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.DATE, java.sql.Date.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.TIME, java.sql.Time.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.TIMESTAMP, java.sql.Timestamp.class);
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.BINARY, byte[].class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.VARBINARY, byte[].class);
		// getBinaryStream 
		jdbcSqlTypeToJavaClass.put(java.sql.Types.LONGVARBINARY, byte[].class);
		
		// Used as an argument to CallableStatement.registerOutParameter
		//jdbcSqlTypeToJavaClass.put(java.sql.Types.OTHER, "Object");
		
//		jdbcSqlTypeToJavaClass.put(java.sql.Types.JAVA_OBJECT, Object.class);
//		jdbcSqlTypeToJavaClass.put(java.sql.Types.DISTINCT, Object.class);
//		
//		jdbcSqlTypeToJavaClass.put(java.sql.Types.STRUCT, Struct.class);
//		jdbcSqlTypeToJavaClass.put(java.sql.Types.ARRAY, Array.class);
//		jdbcSqlTypeToJavaClass.put(java.sql.Types.BLOB, Blob.class);
//		jdbcSqlTypeToJavaClass.put(java.sql.Types.CLOB, Clob.class);
//		jdbcSqlTypeToJavaClass.put(java.sql.Types.REF, Ref.class);
//		// should it be URL?
//		jdbcSqlTypeToJavaClass.put(java.sql.Types.DATALINK, Object.class);
//		
//		jdbcSqlTypeToJavaClass.put(java.sql.Types.BOOLEAN, Boolean.class);
		/*
		jdbcSqlTypeToJavaClass.put(java.sql.Types.ROWID, "Integer");
		jdbcSqlTypeToJavaClass.put(java.sql.Types.NCHAR, "Integer");
		jdbcSqlTypeToJavaClass.put(java.sql.Types.NVARCHAR, "Integer");
		jdbcSqlTypeToJavaClass.put(java.sql.Types.LONGNVARCHAR, "Integer");
		jdbcSqlTypeToJavaClass.put(java.sql.Types.NCLOB, "Integer");
		jdbcSqlTypeToJavaClass.put(java.sql.Types.SQLXML, "Integer");
		*/
		
	}
	

}


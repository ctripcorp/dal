package com.ctrip.platform.dal.daogen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Consts {
	
	/**
	 * Key：数字
	 * Value：对应的数据库表达式，如等于表达式为'='
	 */
	public static Map<String, String> WhereConditionMap;
	
	/**
	 * 系统数据库，如master, tempdb等
	 */
	public static List<String> SystemDatabases;
	
	public static Map<String, String> databaseType;
	
	public static Map<Integer, Class<?>> jdbcSqlTypeToJavaClass;
	public static Map<Integer, String> jdbcSqlTypeDisplay;
	
	public static List<String> CSharpValueTypes;
	
	static{
		CSharpValueTypes = new ArrayList<String>();
		WhereConditionMap = new HashMap<String, String>();
		jdbcSqlTypeToJavaClass= new HashMap<Integer, Class<?>>();
		SystemDatabases = new ArrayList<String>();
		databaseType = new ConcurrentHashMap<String, String>();
		
		CSharpValueTypes.add("sbyte");
		CSharpValueTypes.add("byte");
		CSharpValueTypes.add("char");
		CSharpValueTypes.add("short");
		CSharpValueTypes.add("ushort");
		CSharpValueTypes.add("int");
		CSharpValueTypes.add("uint");
		CSharpValueTypes.add("long");
		CSharpValueTypes.add("ulong");
		CSharpValueTypes.add("float");
		CSharpValueTypes.add("double");
		CSharpValueTypes.add("DateTime");
		CSharpValueTypes.add("TimeSpan");
		CSharpValueTypes.add("decimal");
		CSharpValueTypes.add("bool");
		CSharpValueTypes.add("Guid");
		CSharpValueTypes.add("DateTimeOffset");
		
		WhereConditionMap.put("0", "=");
		WhereConditionMap.put("1", "!=");
		WhereConditionMap.put("2", ">");
		WhereConditionMap.put("3", "<");
		WhereConditionMap.put("4", ">=");
		WhereConditionMap.put("5", "<=");
		WhereConditionMap.put("6", "Between");
		WhereConditionMap.put("7", "Like");
		WhereConditionMap.put("8", "In");
		
		SystemDatabases.add("master");
		SystemDatabases.add("model");
		SystemDatabases.add("msdb");
		SystemDatabases.add("Resource");
		SystemDatabases.add("tempdb");
		SystemDatabases.add("distribution");
		
		SystemDatabases.add("information_schema");
		SystemDatabases.add("performance_schema");
		SystemDatabases.add("mysql");
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.BIT, Boolean.class);
		
		// Recommended using Short for Byte
		jdbcSqlTypeToJavaClass.put(java.sql.Types.TINYINT, Integer.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.SMALLINT, Short.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.INTEGER, Integer.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.BIGINT, Long.class);
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.FLOAT, Double.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.REAL, Float.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.DOUBLE, Double.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.NUMERIC, BigDecimal.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.DECIMAL, BigDecimal.class);
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.CHAR, String.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.VARCHAR, String.class);
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.NVARCHAR, String.class);
		//getAsciiStream  getUnicodeStream
		jdbcSqlTypeToJavaClass.put(java.sql.Types.LONGVARCHAR, String.class);
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.DATE, java.sql.Date.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.TIME, java.sql.Time.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.TIMESTAMP, java.sql.Timestamp.class);
		
		jdbcSqlTypeToJavaClass.put(java.sql.Types.BINARY, byte[].class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.VARBINARY, byte[].class);
		// getBinaryStream 
		jdbcSqlTypeToJavaClass.put(java.sql.Types.LONGVARBINARY, byte[].class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.NCHAR, String.class);
		jdbcSqlTypeToJavaClass.put(java.sql.Types.LONGNVARCHAR, String.class);
		
		//uniqueidentifier
		jdbcSqlTypeToJavaClass.put(10001, byte[].class);
		
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
		
		jdbcSqlTypeDisplay = new HashMap<Integer, String>();
		jdbcSqlTypeDisplay.put(java.sql.Types.BIT, "Types.BIT");
		
		// Recommended using Short for Byte
		jdbcSqlTypeDisplay.put(java.sql.Types.TINYINT, "Types.TINYINT");
		jdbcSqlTypeDisplay.put(java.sql.Types.SMALLINT, "Types.SMALLINT");
		jdbcSqlTypeDisplay.put(java.sql.Types.INTEGER, "Types.INTEGER");
		jdbcSqlTypeDisplay.put(java.sql.Types.BIGINT, "Types.BIGINT");
		
		jdbcSqlTypeDisplay.put(java.sql.Types.NVARCHAR, "Types.NVARCHAR");
		
		jdbcSqlTypeDisplay.put(java.sql.Types.FLOAT, "Types.FLOAT");
		jdbcSqlTypeDisplay.put(java.sql.Types.REAL, "Types.REAL");
		jdbcSqlTypeDisplay.put(java.sql.Types.DOUBLE, "Types.DOUBLE");
		jdbcSqlTypeDisplay.put(java.sql.Types.NUMERIC, "Types.NUMERIC");
		jdbcSqlTypeDisplay.put(java.sql.Types.DECIMAL, "Types.DECIMAL");
		
		jdbcSqlTypeDisplay.put(java.sql.Types.CHAR, "Types.CHAR");
		jdbcSqlTypeDisplay.put(java.sql.Types.VARCHAR, "Types.VARCHAR");
		//getAsciiStream  getUnicodeStream
		jdbcSqlTypeDisplay.put(java.sql.Types.LONGVARCHAR, "Types.LONGVARCHAR");
		
		jdbcSqlTypeDisplay.put(java.sql.Types.DATE, "Types.DATE");
		jdbcSqlTypeDisplay.put(java.sql.Types.TIME, "Types.TIME");
		jdbcSqlTypeDisplay.put(java.sql.Types.TIMESTAMP, "Types.TIMESTAMP");
		
		jdbcSqlTypeDisplay.put(java.sql.Types.BINARY, "Types.BINARY");
		jdbcSqlTypeDisplay.put(java.sql.Types.VARBINARY, "Types.VARBINARY");
		// getBinaryStream 
		jdbcSqlTypeDisplay.put(java.sql.Types.LONGVARBINARY, "Types.LONGVARBINARY");
		jdbcSqlTypeDisplay.put(java.sql.Types.NCHAR, "Types.NCHAR");
		jdbcSqlTypeDisplay.put(java.sql.Types.LONGNVARCHAR, "Types.LONGNVARCHAR");
		jdbcSqlTypeDisplay.put(microsoft.sql.Types.DATETIMEOFFSET, "microsoft.sql.Types.DATETIMEOFFSET");
		
		//uniqueidentifier
		jdbcSqlTypeDisplay.put(10001, "Types.BINARY");
	}
	

}
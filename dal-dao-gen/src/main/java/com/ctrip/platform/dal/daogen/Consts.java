package com.ctrip.platform.dal.daogen;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Consts {
	
	/**
	 * Key: 数据库类型
	 * Value: 对应Java数据类型
	 */
	public static Map<Integer, Class> JavaSqlTypeMap;
	
	/**
	 * Key: Java数据类型
	 * Value: 对应的的DbType，DbType参照C#
	 */
	public static Map<String, String> JavaDbTypeMap;
	
	public static Map<String, String> CSharpSqlTypeMap;
	public static Map<String, String> CSharpDbTypeMap;
	
	/**
	 * Key：数字
	 * Value：对应的数据库表达式，如等于表达式为'='
	 */
	public static Map<String, String> WhereConditionMap;
	
	public static List<String> CSharpValueTypes;
	
	static{
		JavaSqlTypeMap = new HashMap<Integer, Class>();
		JavaDbTypeMap = new HashMap<String, String>();
		CSharpSqlTypeMap = new HashMap<String, String>();
		CSharpDbTypeMap = new HashMap<String, String>();
		CSharpValueTypes = new ArrayList<String>();
		
		WhereConditionMap = new HashMap<String, String>();
		
		// Initialize java SQL type to class mapping
		{
			JavaSqlTypeMap.put(java.sql.Types.BIT, Boolean.class);
			
			// Recommended using Short for Byte
			JavaSqlTypeMap.put(java.sql.Types.TINYINT, Byte.class);
			JavaSqlTypeMap.put(java.sql.Types.SMALLINT, Short.class);
			JavaSqlTypeMap.put(java.sql.Types.INTEGER, Integer.class);
			JavaSqlTypeMap.put(java.sql.Types.BIGINT, Long.class);
			
			JavaSqlTypeMap.put(java.sql.Types.FLOAT, Double.class);
			JavaSqlTypeMap.put(java.sql.Types.REAL, Float.class);
			JavaSqlTypeMap.put(java.sql.Types.DOUBLE, Double.class);
			JavaSqlTypeMap.put(java.sql.Types.NUMERIC, BigDecimal.class);
			JavaSqlTypeMap.put(java.sql.Types.DECIMAL, BigDecimal.class);
			
			JavaSqlTypeMap.put(java.sql.Types.CHAR, String.class);
			JavaSqlTypeMap.put(java.sql.Types.VARCHAR, String.class);
			//getAsciiStream  getUnicodeStream
			JavaSqlTypeMap.put(java.sql.Types.LONGVARCHAR, String.class);
			
			JavaSqlTypeMap.put(java.sql.Types.DATE, java.sql.Date.class);
			JavaSqlTypeMap.put(java.sql.Types.TIME, java.sql.Time.class);
			JavaSqlTypeMap.put(java.sql.Types.TIMESTAMP, java.sql.Timestamp.class);
			
			JavaSqlTypeMap.put(java.sql.Types.BINARY, byte[].class);
			JavaSqlTypeMap.put(java.sql.Types.VARBINARY, byte[].class);
			// getBinaryStream 
			JavaSqlTypeMap.put(java.sql.Types.LONGVARBINARY, byte[].class);
			
			// Used as an argument to CallableStatement.registerOutParameter
			//JavaSqlTypeMap.put(java.sql.Types.OTHER, "Object");
			
			JavaSqlTypeMap.put(java.sql.Types.JAVA_OBJECT, Object.class);
			JavaSqlTypeMap.put(java.sql.Types.DISTINCT, Object.class);
			
			JavaSqlTypeMap.put(java.sql.Types.STRUCT, Struct.class);
			JavaSqlTypeMap.put(java.sql.Types.ARRAY, Array.class);
			JavaSqlTypeMap.put(java.sql.Types.BLOB, Blob.class);
			JavaSqlTypeMap.put(java.sql.Types.CLOB, Clob.class);
			JavaSqlTypeMap.put(java.sql.Types.REF, Ref.class);
			// should it be URL?
			JavaSqlTypeMap.put(java.sql.Types.DATALINK, Object.class);
			
			JavaSqlTypeMap.put(java.sql.Types.BOOLEAN, Boolean.class);
			/*
			JavaSqlTypeMap.put(java.sql.Types.ROWID, "Integer");
			JavaSqlTypeMap.put(java.sql.Types.NCHAR, "Integer");
			JavaSqlTypeMap.put(java.sql.Types.NVARCHAR, "Integer");
			JavaSqlTypeMap.put(java.sql.Types.LONGNVARCHAR, "Integer");
			JavaSqlTypeMap.put(java.sql.Types.NCLOB, "Integer");
			JavaSqlTypeMap.put(java.sql.Types.SQLXML, "Integer");
			*/
		}
		
		JavaDbTypeMap.put("int", "Int32");
		JavaDbTypeMap.put("String", "String");
		JavaDbTypeMap.put("Timestamp", "DateTime");
		
		CSharpSqlTypeMap.put("int", "int");
		CSharpSqlTypeMap.put("varchar", "string");
		CSharpSqlTypeMap.put("datetime", "DateTime");
		CSharpSqlTypeMap.put("nvarchar", "string");
		
		CSharpDbTypeMap.put("int", "Int32");
		CSharpDbTypeMap.put("string", "String");
		CSharpDbTypeMap.put("DateTime", "DateTime");
		
		//CSharpValueTypes.add("int");
		//CSharpValueTypes.add("DateTime");
		CSharpValueTypes.add("int");
		CSharpValueTypes.add("datetime");
		
		WhereConditionMap.put("0", "=");
		WhereConditionMap.put("1", "!=");
		WhereConditionMap.put("2", ">");
		WhereConditionMap.put("3", "<");
		WhereConditionMap.put("4", ">=");
		WhereConditionMap.put("5", "<=");
		WhereConditionMap.put("6", "Between");
		WhereConditionMap.put("7", "Like");
		WhereConditionMap.put("8", "In");
		
	}
	

}

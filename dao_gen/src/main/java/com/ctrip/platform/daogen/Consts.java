package com.ctrip.platform.daogen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Consts {
	
	public static Map<String, String> JavaSqlTypeMap;
	public static Map<String, String> JavaDbTypeMap;
	
	public static Map<String, String> CSharpSqlTypeMap;
	public static Map<String, String> CSharpDbTypeMap;
	
	public static Map<String, String> WhereConditionMap;
	
	public static List<String> CSharpValueTypes;
	
	static{
		JavaSqlTypeMap = new HashMap<String, String>();
		JavaDbTypeMap = new HashMap<String, String>();
		CSharpSqlTypeMap = new HashMap<String, String>();
		CSharpDbTypeMap = new HashMap<String, String>();
		CSharpValueTypes = new ArrayList<String>();
		
		WhereConditionMap = new HashMap<String, String>();
		
		JavaSqlTypeMap.put("int", "int");
		JavaSqlTypeMap.put("varchar", "String");
		JavaSqlTypeMap.put("datetime", "Timestamp");
		JavaSqlTypeMap.put("nvarchar", "String");
		
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

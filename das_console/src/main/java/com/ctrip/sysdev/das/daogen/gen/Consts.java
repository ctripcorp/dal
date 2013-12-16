package com.ctrip.sysdev.das.daogen.gen;

import java.util.HashMap;
import java.util.Map;

public class Consts {
	
	public static Map<String, String> JavaSqlTypeMap;
	public static Map<String, String> JavaDbTypeMap;
	
	public static Map<String, String> CSharpSqlTypeMap;
	public static Map<String, String> CSharpDbTypeMap;
	
	static{
		JavaSqlTypeMap = new HashMap<String, String>();
		JavaDbTypeMap = new HashMap<String, String>();
		CSharpSqlTypeMap = new HashMap<String, String>();
		CSharpDbTypeMap = new HashMap<String, String>();
		
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
	}
	

}

package com.ctrip.sysdev.das.daogen.gen;

import java.util.HashMap;
import java.util.Map;

public class Consts {
	
	public static Map<String, String> JavaSqlTypeMap;
	public static Map<String, String> JavaDbTypeMap;
	
	static{
		JavaSqlTypeMap = new HashMap<String, String>();
		JavaDbTypeMap = new HashMap<String, String>();
		
		JavaSqlTypeMap.put("int", "int");
		JavaSqlTypeMap.put("varchar", "String");
		JavaSqlTypeMap.put("datetime", "Timestamp");
		JavaSqlTypeMap.put("nvarchar", "String");
		
		JavaDbTypeMap.put("int", "Int32");
		JavaDbTypeMap.put("String", "String");
		JavaDbTypeMap.put("Timestamp", "DateTime");
	}
	

}

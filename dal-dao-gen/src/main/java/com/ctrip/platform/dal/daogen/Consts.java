package com.ctrip.platform.dal.daogen;

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
	
	public static List<String> CSharpValueTypes;
	
	static{
		CSharpValueTypes = new ArrayList<String>();
		WhereConditionMap = new HashMap<String, String>();

		
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
		
	}
	

}

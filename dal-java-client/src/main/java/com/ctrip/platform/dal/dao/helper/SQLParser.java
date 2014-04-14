package com.ctrip.platform.dal.dao.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class SQLParser {
	private static String regEx = null;
	private static Pattern inRegxPattern = null;
	static{
		 regEx="In \\?";
		 inRegxPattern = Pattern.compile(regEx);
	}
	
	public static String parse(String original, List... parms)
	{
		if(null == parms)
			return original;
		String temp = new String(original);
		Matcher m = inRegxPattern.matcher(original);
		int index = 0;
		while(m.find())
    	{
			List<String> qus = new ArrayList<String>(parms[index].size());
			for(int i = 0; i < parms[index].size(); i ++)
				qus.add("?");
			temp = temp.replaceFirst(regEx, String.format("In (%s)", StringUtils.join(qus, ",")));
			index ++;
    	}
		
		return temp;
	}
	
	public static void main(String[] args)
	{
		String sql = "SELECT * FROM Person WHERE  ID In ?  AND Age In ? And Age BETWEEN ? AND ? ";
		List<String> param = new ArrayList<String>();
		param.add("1");
		param.add("2");
		
		String parseSql = SQLParser.parse(sql, param, param);
		
		System.out.println(parseSql);
		
		System.exit(1);
	}
}

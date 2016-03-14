package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class SQLParser {
	private static String regEx = null;
	private static Pattern inRegxPattern = null;
	static{
		 regEx="(?i)In *\\(? *\\? *\\)?";
		 inRegxPattern = Pattern.compile(regEx);
	}
	
	/**
	 * To disable the original expand logic before shard by in parameters
	 * @deprecated The new code gen will not include generation of this invocation
	 * @return just the original sql
	 * @throws SQLException
	 */
	public static String parse(String original, List... parms) throws SQLException
	{
		DalClientFactory.getDalLogger().warn(
				"In case you see this message, you are using old generated code. Please regenerate code or just remove the invocation of this method from the generated code.");

		return original;
	}
	
	/**
	 * Combine the parameters into SQL according to the IN Keyword
	 * TODO: Deal with the situation of null/empty of IN parameter specified;
	 * @param original
	 * 		The original SQL Statement.
	 * @param parms
	 * 		The IN Parameter specified.
	 * @return
	 * 		Combined SQL
	 * @throws SQLException
	 */
	public static String compile(String original, List<List<?>> parms) throws SQLException
	{
		if(null == parms || parms.size() == 0)
			return original;
		StringBuffer temp = new StringBuffer();
		Matcher m = inRegxPattern.matcher(original);
		List<String> plains = new ArrayList<String>();
		int start = 0;
		while(m.find(start))
    	{
			plains.add(original.substring(start, m.start()));
			start = m.end();		
    	}
		plains.add(original.substring(start, original.length()));
		
		if(plains.size() != parms.size() + 1){
			throw new SQLException(String.format("SQL Parser failed. The count of in parameters[%s] not match parameter count[%s]", 
					plains.size() - 1, parms.size()));
		}
		
		int index = 0;
		for (; index < parms.size(); index++) {
			List<String> qus = new ArrayList<String>(parms.get(index).size());
			for(int j = 0; j < parms.get(index).size(); j ++)
				qus.add("?");
			temp.append(plains.get(index))
				.append(String.format("In (%s)", StringUtils.join(qus, ",")))
				.append(" ");
		}
		temp.append(plains.get(index));
		
		return temp.toString();
	}
}
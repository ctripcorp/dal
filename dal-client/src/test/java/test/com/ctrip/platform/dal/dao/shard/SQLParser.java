package test.com.ctrip.platform.dal.dao.shard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class SQLParser {
	private static String regEx = null;
	private static Pattern inRegxPattern = null;
	private static String[] UPDATES = new String[]{
		"update ", "insert ", "delete "
	};
	
	static{
		 regEx="(?i)In *\\(? *\\? *\\)?";
		 inRegxPattern = Pattern.compile(regEx);
	}
	
	/**
	 * To work with new DalQueryDao. we don't need to parse sql before invoke DalQueryDao.
	 * If you see this get called, please make sure to regenerated code with code gen
	 * @param original
	 * @param parms
	 * @return Combined SQL
	 * @deprecated if you see this, please regenerated your code with code gen
	 * @throws SQLException
	 */
	public static String parse(String original, List... parms) throws SQLException
	{
		String test = original.toLowerCase();
		for(String pattern: UPDATES)
			if(test.contains(pattern))
				return parseBakup(original, parms);
		
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
	private static String parseBakup(String original, List... parms) throws SQLException
	{
		if(null == parms || parms.length == 0)
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
		
		if(plains.size() != parms.length + 1){
			throw new SQLException(String.format("SQL Parser failed. The count of in parameters[%s] not match parameter count[%s]", 
					plains.size() - 1, parms.length));
		}
		
		int index = 0;
		for (; index < parms.length; index++) {
			List<String> qus = new ArrayList<String>(parms[index].size());
			for(int j = 0; j < parms[index].size(); j ++)
				qus.add("?");
			temp.append(plains.get(index))
				.append(String.format("In (%s)", StringUtils.join(qus, ",")))
				.append(" ");
		}
		temp.append(plains.get(index));
		
		return temp.toString();
	}
}
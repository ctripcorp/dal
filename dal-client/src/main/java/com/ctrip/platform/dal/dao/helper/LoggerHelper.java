package com.ctrip.platform.dal.dao.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

public class LoggerHelper {

	public static final String SQLHIDDENString = "*";
	private static final String MYSQL_URL_PREFIX = "jdbc:mysql://";
	private static final String SQLSERVER_URL_PREFIX = "jdbc:sqlserver://";
	public static final String NULL_SET = "NullSet";
	public static final String EMPTY_SET = "EmptySet";

	private static class ObjectMapperHolder{
		private static ObjectMapper objectMapperInstance=new ObjectMapper();
		static {
			objectMapperInstance.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
			objectMapperInstance.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		}
	}

	public static ObjectMapper getObjectMapperInstance(){
		return ObjectMapperHolder.objectMapperInstance;
	}

	public static int getHashCode(String str) {
		str = getCompactSql(str);
		int hash, i;
		char[] arr = str.toCharArray();
		for (hash = i = 0; i < arr.length; ++i) {
			hash += arr[i];
			hash += (hash << 12);
			hash ^= (hash >> 4);
		}
		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);
		return hash;
	}

	public static String getCompactSql(String sql) {
		StringBuffer sqlnew=new StringBuffer();
		StringBuffer word=new StringBuffer();
		char[] chrs = sql.toCharArray(); // sql字符拆到数组里
		int len = chrs.length;

		// 遍历sql字符数组
		for (int i = 0; i < len; i++) {
			char chr = chrs[i];

			// 对单引号的处理（对 单引号 和 操作符 是分两个逻辑处理）
			if (chr == '\'') {
				for (i++; i < len; i++) {
					// 如果不等于'，continue
					if (chrs[i] != '\'')
						continue;

					// 循环到结尾，break
					if (i == len - 1)
						break;

					// 如果下一字符是'，继续
					if (chrs[i + 1] == '\'')
						i++;
					else
						break;
				}
				continue;
			}

			// 过滤字符
			if ((chr >= 97 && chr <= 122)) {
				// 普通字符，加入word流
				word.append(chr);
				continue;
			} else {
				// 对断词符号的处理 right(a.name,8)
				if (chr == ' ' || chr == ',' || chr == '(' || chr == ')'
						|| chr == '[' || chr == ']' || chr == '=' || chr == '<'
						|| chr == '>' || chr == '\t' || chr == '\r'
						|| chr == '\n' || chr == '`' || chr == '!'
						|| chr == '+' || chr == '-' || chr == '*' || chr == '/'
						|| chr == '%' || chr == '&' || chr == '|' || chr == '^') {
					// 如果保留该符号，则进行处理
					if (chr == '=' || chr == '<' || chr == '>' || chr == '!') {
						word.append(chr);
						sqlnew.append(checkWord(word.toString()));
						word.setLength(0);
					} else {
						// 断词符号不在保留范围内，则丢弃，并将之前的word流加入sqlnew
						if (word.length() > 0) {
							sqlnew.append(checkWord(word.toString()));
							word.setLength(0);
						}
					}

					continue;
				}

				// 普通字符，加入word流
				word.append(chr);
			}
		}

		if (word.length() > 0) {
			sqlnew.append(checkWord(word.toString()));
		}

		return sqlnew.toString();
	}

	public static String checkWord(String word) {
		char[] chrs = word.toCharArray();
		char chr = chrs[0];
		if (chr == '@' || chr == '$') {
			return "";
		}
		if (chr >= '0' && chr <= '9') {
			return "";
		}
		return word;
	}

	public static String getSqlTpl(LogEntry entry) {
		if ( entry.isSensitive() )
			return SQLHIDDENString;
		DalEventEnum event = entry.getEvent();

		if (entry.getSqls() == null || entry.getSqls().length == 0)
			return "";

		if(event == DalEventEnum.QUERY ||  event == DalEventEnum.UPDATE_SIMPLE ||
				event == DalEventEnum.UPDATE_KH || event == DalEventEnum.BATCH_UPDATE_PARAM){
			return entry.getSqls()[0];
		}
		if(event == DalEventEnum.BATCH_UPDATE){
			return join(entry.getSqls(), ";");
		}
		if(event == DalEventEnum.CALL || event == DalEventEnum.BATCH_CALL){
			return entry.getCallString();
		}

		return "";
	}

	public static String getParams(LogEntry entry) {
		DalEventEnum event = entry.getEvent();
		String[] pramemters = entry.getPramemters();

		StringBuilder sbout = new StringBuilder();
		if(pramemters == null || pramemters.length <= 0){
			return sbout.toString();
		}
		if(event == DalEventEnum.QUERY ||
				event == DalEventEnum.UPDATE_SIMPLE ||
				event == DalEventEnum.UPDATE_KH ||
				event == DalEventEnum.CALL){
			return null != pramemters && pramemters.length > 0 ? pramemters[0] : "";
		}
		if(event == DalEventEnum.BATCH_UPDATE_PARAM ||
				event == DalEventEnum.BATCH_CALL){
			for(String param : pramemters){
				sbout.append(param + ";");
			}
			return sbout.substring(0, sbout.length() - 1);
		}
		return "";
	}

	public static String join(Object[] array, String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	public static String join(Object[] array, String separator, int startIndex, int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = "";
		}

		int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return "";
		}

		StringBuilder buf = new StringBuilder(noOfItems * 16);

		for (int i = startIndex; i < endIndex; ++i) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	public static String getExceptionStack(Throwable e) {
		String msg = e.getMessage();
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			msg = sw.toString();
		} catch (Throwable e2) {
			msg = "bad getErrorInfoFromException";
		}

		return msg;
	}

	public static String toJson(Object object) {
		try {
			return getObjectMapperInstance().writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Error convert log value to json string!";
	}

	public static String getSimplifiedDBUrl(String url) {
		if(StringUtils.isBlank(url))
			return "blank url";

		if (url.startsWith(MYSQL_URL_PREFIX)) {
			String[] mySqlUrlStrings = url.split("[?]");
			return mySqlUrlStrings[0];
		}

		if (url.startsWith(SQLSERVER_URL_PREFIX)) {
			String[] sqlServerUrlStrings = url.split(";");
			String databaseName = "";
			for (String urlPart : sqlServerUrlStrings) {
				if ((urlPart.trim().toLowerCase()).startsWith("databaseName".toLowerCase())) {
					String[] databaseNamePart = urlPart.split("=");
					databaseName = databaseNamePart.length == 2 ? databaseNamePart[1] : "blank database name";
					break;
				}
			}
			if (databaseName.length() == 0)
				databaseName = "blank database name";
			return sqlServerUrlStrings[0].concat("/").concat(databaseName);
		}

		return url;

	}

	public static String setToOrderedString(Set<String> origin) {
		if (origin == null)
			return NULL_SET;

		if (origin.size()==0)
			return EMPTY_SET;

		Set<String> treeSet = new TreeSet<>();
		for (String item : origin) {
			if (item != null)
				treeSet.add(item);
		}

		return StringUtils.join(treeSet, ",");
	}
}
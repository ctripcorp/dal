package com.ctrip.sysdev.das.utils;

public class StringKit {
	public final static String COMMA_STR = ",";
	public final static String[] emptyStringArray = {};

	public static String[] getTrimmedStrings(String str) {
		if (null == str || "".equals(str.trim())) {
			return emptyStringArray;
		}
		return str.trim().split("\\s*,\\s*");
	}
}

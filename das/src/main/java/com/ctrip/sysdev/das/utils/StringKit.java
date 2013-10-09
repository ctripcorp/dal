package com.ctrip.sysdev.das.utils;

import com.ctrip.sysdev.das.controller.DasControllerConstants;

public class StringKit implements DasControllerConstants {
	public final static String COMMA_STR = ",";
	public final static String KEY_SEPARATOR = ".";
	public final static String[] emptyStringArray = {};

	public static String[] getTrimmedStrings(String str) {
		if (null == str || "".equals(str.trim())) {
			return emptyStringArray;
		}
		return str.trim().split("\\s*,\\s*");
	}
	
	public static String buildPath(String parent, String child) {
		return new StringBuilder(parent).append(SEPARATOR).append(child).toString();
	}

	public static String buildKey(String parent, String key) {
		return new StringBuilder(parent).append(KEY_SEPARATOR).append(key).toString();
	}
}

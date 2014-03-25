package com.ctrip.platform.dal.daogen.utils;

public class CommonUtils {
	
	public static String normalizeVariable(String variable){
		return variable.replaceAll("[^A-Za-z0-9()\\[\\]]", "");
	}

}

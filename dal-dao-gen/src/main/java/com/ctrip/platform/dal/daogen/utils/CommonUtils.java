package com.ctrip.platform.dal.daogen.utils;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {
	private static ObjectMapper objectMap;
	private static Logger log;
	
	static{
		objectMap = new ObjectMapper();
		log = Logger.getLogger(CommonUtils.class);
	}
	public static String normalizeVariable(String variable){
		return variable.replaceAll("[^A-Za-z0-9()\\[\\]]", "");
	}

	public static int tryParse(String val, int defaultValue)
	{
		int v = defaultValue;
		try{
			v = Integer.parseInt(val);
		}
		catch(Exception e) { }
		
		return v;
	}
	
	public static String toJson(Object o)
	{
		String res = "";
		try {
			res = objectMap.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			log.error(e);
		}
		return res;
	}
}

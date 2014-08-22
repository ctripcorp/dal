package com.ctrip.platform.dal.daogen.utils;

import java.sql.Timestamp;

import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;

public class VelocityHelper {
	public String getMockValForUnitTest(JavaParameterHost host, String seed){
		if(host.getJavaClass().equals(int.class) ||
				host.getJavaClass().equals(Integer.class) ||
				host.getJavaClass().equals(long.class) ||
				host.getJavaClass().equals(Long.class)||
				host.getJavaClass().equals(float.class) ||
				host.getJavaClass().equals(Float.class) ||
				host.getJavaClass().equals(Double.class)||
				host.getJavaClass().equals(double.class))
			return  host.isPrimary() ? seed : "-1";
		else if(host.getJavaClass().equals(boolean.class) ||
				host.getJavaClass().equals(Boolean.class))
			return "false";
		else if(host.getJavaClass().equals(String.class))
			return "\"test\"";
		else if(host.getJavaClass().equals(Timestamp.class))
			return "new Timestamp(System.currentTimeMillis())";
		else return null;
	}
}

package com.ctrip.platform.dal.dao.logging;

import com.ctrip.platform.dal.dao.StatementParameters;


public class Logger {
	public static final int DAL_APP_ID = 930201;
	
	public static final String TAG_APPID = "APPID";
	public static final String TAG_HOST = "Host";
	public static final String TAG_DAO = "DAO";
	public static final String TAG_METHOD = "Method";
	public static final String TAG_SIZE = "Size";
	public static final String TAG_STATUS = "Status";
	
	public static boolean encryptIn = true;
	public static boolean encryptOut = true;
	
	public static int getAppId() {
		return DAL_APP_ID;
	}
	
	void log(String className, String sql, StatementParameters parameters) {
		
	}
	
}

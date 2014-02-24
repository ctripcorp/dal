package com.ctrip.platform.dal.dao.logging;

import com.ctrip.platform.dal.dao.StatementParameters;


public class CentralLoggingManager {
	public static final String TAG_APPID = "APPID";
	public static final String TAG_HOST = "Host";
	public static final String TAG_DAO = "DAO";
	public static final String TAG_METHOD = "Method";
	public static final String TAG_SIZE = "Size";
	public static final String TAG_STATUS = "Status";
	
	void log(String className, String sql, StatementParameters parameters) {
		
	}
}

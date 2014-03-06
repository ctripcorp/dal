package com.ctrip.platform.dal.dao.logging;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

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
	
	private static Map<Integer, Date> sqlLogCache = Collections.synchronizedMap(new WeakHashMap<Integer, Date>());
	
	// Timeout threshold for low frequency SQL. In minutes
	private static int low;
	// Timeout threshold for high frequency SQL. In minutes 
	private static int high;
	// Cache size
	private static final int CACHE_SIZE_LIMIT = 5000;
	
	public static int getAppId() {
		return DAL_APP_ID;
	}
	
	public static void log(String sql, StatementParameters parameters) {
		if(!validate(sql, parameters)) 
			return;
		
		StackTraceElement [] trace = Thread.currentThread().getStackTrace();
	}
	
	/**
	 * Check if this entry need to be logged
	 * @param entry
	 * @return
	 */
	public static boolean validate(String sql, StatementParameters parameters) {
		Date now  = new Date();
		clearCache(now, low);
//		if(sqlLogCache)
		return true;
	}
	
	/**
	 * @param now
	 * @param interval in minutes
	 */
	private static void clearCache(Date now, int interval) {
		//no clear if the number in control
        if (CACHE_SIZE_LIMIT > sqlLogCache.size()) return;
        Set<Integer> keys = sqlLogCache.keySet();
        for(Integer key: keys) {
        	Date value = sqlLogCache.get(key);
        	if((now.getTime() - value.getTime())/1000 > interval * 60)
        		sqlLogCache.remove(key);
        }
    }
}

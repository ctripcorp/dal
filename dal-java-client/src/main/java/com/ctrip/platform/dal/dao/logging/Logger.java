package com.ctrip.platform.dal.dao.logging;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.ctrip.freeway.gen.v2.LogLevel;
import com.ctrip.freeway.logging.ILog;
import com.ctrip.freeway.logging.LogManager;
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
	
	private static long MINUTE = 60 * 1000;
	
	public static int getAppId() {
		return DAL_APP_ID;
	}
	
	private static ILog logger = LogManager.getLogger("DAL Java Client");
	
	public static LogEntry create(String sql, StatementParameters parameters, String logicDbName, String realDbName, int eId, String message) {
		return new LogEntry(sql, parameters, logicDbName, realDbName, eId, message);
	}
	
	public static void log(LogEntry log, long duration) {

		if(log == null) 
			return;
		// Don't log
		if(!validate(log.getSql(), log.getInputParamStr()))
			return;
		
		log.setDuration(duration);
		
		Map<String, String> tag = new LinkedHashMap<String, String>();
		tag.put("InTransaction", log.isTransactional() ? "True" : "False");
		tag.put("DurationTime", Long.toString(log.getDuration()) + "ms");
		tag.put("DatabaseName", CommonUtil.null2NA(log.getDatabaseName()));
		tag.put("ServerAddress", CommonUtil.null2NA(log.getServerAddress()));
		tag.put("CommandType", CommonUtil.null2NA(log.getCommandType()));
		tag.put("UserName", CommonUtil.null2NA(log.getUserName()));
		tag.put("RecordCount", Long.toString(log.getResultCount()));
		
		logger.info(CommonUtil.null2NA(log.getTitle()), log.toBrief(), tag);
	}
	
	public static void logGetConnectionSuccess(String realDbName)
	{
		Logger.log("Get connection", DalEventEnum.CONNECTION_SUCCESS, LogLevel.INFO, 
				String.format("Connection %s database successfully", realDbName));
	}
	
	public static void logGetConnectionFailed(String realDbName, Throwable e)
	{
		String logMsg = "Connection " + realDbName + " database failed." +
				System.lineSeparator() + System.lineSeparator() +
				"********** Exception Info **********" + System.lineSeparator() +
				e.getMessage();
		Logger.log("Get connection", DalEventEnum.CONNECTION_FAILED, LogLevel.ERROR, logMsg);	
	}
	
	public static void log(String name, DalEventEnum event, LogLevel level, String msg)
	{
		StringBuffer sbuffer = new StringBuffer();
		sbuffer.append(String.format("Log Name: %s" + System.lineSeparator(), name));
		sbuffer.append(String.format("Event: %s" + System.lineSeparator(), event.getEventId()));
		sbuffer.append(String.format("Message: %s " + System.lineSeparator(), msg));
		
		switch (level) {
			case DEBUG:
				logger.debug(sbuffer.toString());
			case INFO: 
				logger.info(sbuffer.toString());
				break;
			case ERROR:
				logger.error(sbuffer.toString());
				break;
			case FATAL:
				logger.fatal(sbuffer.toString());
				break;
		default:
			break;
		}
	}
	
	public static void log(LogEntry log, Throwable e) {
		logger.error(log.toBrief(), e);
	}
	
	/**
	 * Check if this entry need to be logged. For level abve information, we always log
	 * @param entry
	 * @return
	 */
	private static boolean validate(String sql, String inputParamStr) {
		Date now  = new Date();
		clearCache(now, low);
        if (sql == null) {
            return true;
        }
        
        Integer key = CommonUtil.getSqlHashCodeForCache(sql);

        Date value = sqlLogCache.get(key);
        if (value != null) { //含'@'情况，用low
            if (inputParamStr != null && inputParamStr.trim().length() > 0) {
                if ((now.getTime() - value.getTime())/MINUTE < low) {//时间不够
                    return false;
                }
            }
            else { //不包含参数，用high
                if ((now.getTime() - value.getTime())/MINUTE < high)//时间不够
                {
                    return false;
                }
            }
        }
        sqlLogCache.put(key, now);
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

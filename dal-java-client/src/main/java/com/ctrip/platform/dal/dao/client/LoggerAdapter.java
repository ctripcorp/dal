package com.ctrip.platform.dal.dao.client;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ctrip.platform.dal.dao.helper.LoggerHelper;

public abstract class LoggerAdapter implements DalLogger {
	
	private static final String SAMPLING = "sampling";
	private static final String ENCRYPT = "encrypt";
	private static final String SIMPLIFIED = "simplified";
	private static final String ASYNCLOGGING = "asyncLogging";
	private static final String CAPACITY = "capacity";
	
	private static final String  SAMPLINGLOW = "samplingLow";
	private static final String  SAMPLINGHIGH = "samplingHigh";
	
	protected static boolean simplifyLogging = false;
	protected static boolean encryptLogging = true;
	protected static boolean samplingLogging = false;
	protected static long samplingLow = 60 * 60 * 1000;//milliseconds
	protected static long samplingHigh = 5 * 60 * 1000;//milliseconds
	//key is sql hash code
	protected static final ConcurrentHashMap<Integer, Long> logEntryCache = new ConcurrentHashMap<Integer, Long>();
	protected static int CacheSizeLimit = 5000;
	
	protected static boolean asyncLogging = true;
	
	protected static ExecutorService executor = null;

	@Override
	public void initLogger(Map<String, String> settings) {
		if(settings == null)
			return;
		
		if(settings.containsKey(SAMPLING))
			samplingLogging = Boolean.parseBoolean(settings.get(SAMPLING));
		
		if(settings.containsKey(SAMPLINGLOW))
			samplingLow = Integer.parseInt(settings.get(SAMPLINGLOW)) * 60 * 1000;
		
		if(settings.containsKey(SAMPLINGHIGH))
			samplingHigh = Integer.parseInt(settings.get(SAMPLINGHIGH)) * 60 * 1000;

		if(settings.containsKey(SIMPLIFIED))
			simplifyLogging = Boolean.parseBoolean(settings.get(SIMPLIFIED));
		
		if(settings.containsKey(ENCRYPT))
			encryptLogging = Boolean.parseBoolean(settings.get(ENCRYPT));
		
		if(settings.containsKey(ASYNCLOGGING))
			asyncLogging = Boolean.parseBoolean(settings.get(ASYNCLOGGING));
		
		if (settings.containsKey(CAPACITY)) {
			executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
					new ArrayBlockingQueue<Runnable>(Integer.parseInt(settings.get(CAPACITY)), true),
					new RejectedExecutionHandler() {
						@Override
						public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
							//do nothing
						}
					});
		} else {
			executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>());
		}
	}
	
	@Override
	public void shutdown() {
		executor.shutdown();
	}
	

    /**
     * 1. 如果是 Warning/Error/Fatal, 直接写入
     * 2. 如果是Info及以下，进行如下操作
     * 	  a. 算出不带Where条件的Hash
     *    b. 如果此Sql有参数，查找此Hash一个小时之前是否有过执行，如果有，发送，并将此hash的最后执行时间修改为当前时间
     *    c. 如果此Sql没有参数，查找此Hash在5分钟之前是否有过执行，如果有，发送，并将此hash的最后执行时间修改为当前时间
     * @param entry
     * @return true表可以发送, false 表不可以发送
     */
	protected boolean validate(LogEntry entry) {
		String sqlTpl = LoggerHelper.getSqlTpl(entry);
		if (LoggerHelper.SQLHIDDENString.equals(sqlTpl) || "".equals(sqlTpl))
			return true;
		int hashCode = LoggerHelper.getHashCode(sqlTpl);
		long now = System.currentTimeMillis();
		Long old = logEntryCache.putIfAbsent(hashCode, now);
		if (old == null) {
			return true;
		}
		boolean userLow = useLow(entry);
		if ( (now - old) < (userLow ? samplingLow : samplingHigh) ) {
			return false;
		} else { 
			//将原来的日期更新为当前时间
			logEntryCache.replace(hashCode, System.currentTimeMillis());
			return true;
		}
	}
	
	private boolean useLow(LogEntry entry) {
		String[] pramemters = entry.getPramemters();
		//有参数时，用low
		if (pramemters == null || pramemters.length <= 0)
			return false;
		return true;
	}
	
	private void clearCache(long now, long interval) {
		int currentCount = logEntryCache.size();
		if (CacheSizeLimit > currentCount) return;
	}
}

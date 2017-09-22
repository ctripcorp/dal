package com.ctrip.platform.dal.dao.client;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ctrip.platform.dal.dao.helper.LoggerHelper;

/**
 * 
 * Implements the common logic.
 * 
 * @author gzxia
 *
 */
public abstract class LoggerAdapter implements DalLogger {
	public static final String DEFAULT_SECERET_KEY = "dalctripcn";

	private static final String SAMPLING = "sampling";
	private static final String ENCRYPT = "encrypt";
	private static final String SECRETKEY = "secretKey";
	private static final String SIMPLIFIED = "simplified";
	private static final String ASYNCLOGGING = "asyncLogging";

	private static final String CAPACITY = "capacity";
	private static final String  SAMPLINGLOW = "samplingLow";
	private static final String  SAMPLINGHIGH = "samplingHigh";
	private static final String  SAMPLEMAXNUM = "sampleMaxNum";

	private static final String  SAMPLECLEARINTERVAL = "sampleClearInterval";
	protected static boolean simplifyLogging = false;
	protected static boolean encryptLogging = true;
	public static String secretKey = DEFAULT_SECERET_KEY;
	protected static boolean samplingLogging = false;
	protected static long samplingLow = 60 * 60 * 1000;//milliseconds
	protected static long samplingHigh = 5 * 60 * 1000;//milliseconds
	//key is the sql hash code
	private static final ConcurrentHashMap<Integer, Long> logEntryCache = new ConcurrentHashMap<Integer, Long>();
	protected static int sampleMaxNum = 5000;
	protected static int sampleClearInterval = 30;
	
	private static ScheduledExecutorService scheduler = null;
	private static final AtomicBoolean isClearingCache = new AtomicBoolean(false);
	
	protected static boolean asyncLogging = false;
	
	protected static ExecutorService executor = null;

	@Override
	public void initialize(Map<String, String> settings) {
		if(settings == null)
			return;
		
		initSampling(settings);

		if(settings.containsKey(SIMPLIFIED))
			simplifyLogging = Boolean.parseBoolean(settings.get(SIMPLIFIED));
		
		if(settings.containsKey(ENCRYPT))
			encryptLogging = Boolean.parseBoolean(settings.get(ENCRYPT));
		
		if(settings.containsKey(SECRETKEY))
			secretKey = settings.get(SECRETKEY);
		
		initAsyncLogging(settings);
	}
	
	private void initSampling(Map<String, String> settings) {
		if(settings.containsKey(SAMPLING))
			samplingLogging = Boolean.parseBoolean(settings.get(SAMPLING));
		
		if(settings.containsKey(SAMPLEMAXNUM))
			sampleMaxNum = Integer.parseInt(settings.get(SAMPLEMAXNUM));
		
		if(settings.containsKey(SAMPLECLEARINTERVAL))
			sampleClearInterval = Integer.parseInt(settings.get(SAMPLECLEARINTERVAL));
			
		if(settings.containsKey(SAMPLINGLOW))
			samplingLow = Integer.parseInt(settings.get(SAMPLINGLOW)) * 60 * 1000;
		
		if(settings.containsKey(SAMPLINGHIGH))
			samplingHigh = Integer.parseInt(settings.get(SAMPLINGHIGH)) * 60 * 1000;
		
		if (samplingLogging) {
			scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					clearCache();
				}
			}, 5, sampleClearInterval, TimeUnit.SECONDS);
		}
	}
	
	private void clearCache() {
		int currentCount = logEntryCache.size();
		if (sampleMaxNum > currentCount) 
			return;
		isClearingCache.set(true);
		for(Map.Entry<Integer, Long> entry : logEntryCache.entrySet()) {
			Integer key = entry.getKey();
			long old = entry.getValue();
			long now = System.currentTimeMillis();
			if ( (now - old) > samplingLow ) {
				logEntryCache.remove(key);
			} 
		}
		isClearingCache.set(false);
	}
	
	private void initAsyncLogging(Map<String, String> settings) {
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
			executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		}
	}
	
	@Override
	public void shutdown() {
		if (executor != null)
			executor.shutdown();
		if (scheduler != null)
			scheduler.shutdown();
	}
	

    /**
     * 1. If log level is Warning/Error/Fatal, then immediately write with out validate.
     * 2. If log level is Info or less-than Info, validate according the below
     *    a. If the SQL have parameters, the log will send only once in the low interval minutes default is sixty minutes.
     *    b. If the SQL do not have any parameters, the log will send only once in the high interval minutes default is five minutes.
     * @param entry
     * @return  The log can be sent only when returning value is true
     */
	protected boolean validate(LogEntry entry) {
		if ( isClearingCache.get() )
			return false;
		String sqlTpl = LoggerHelper.getSqlTpl(entry);
		if (LoggerHelper.SQLHIDDENString.equals(sqlTpl) || "".equals(sqlTpl) )
			return true;
		int hashCode = LoggerHelper.getHashCode(sqlTpl);
		long now = System.currentTimeMillis();
		Long old = logEntryCache.putIfAbsent(hashCode, now);
		if (old == null) {
			if (logEntryCache.size() > sampleMaxNum)
				logEntryCache.remove(hashCode);
			return true;
		}
		boolean userLow = useLow(entry);
		if ( (now - old) < (userLow ? samplingLow : samplingHigh) ) {
			return false;
		} else { 
			//update the old timestamp associated the sqlTpl to now
			logEntryCache.put(hashCode, System.currentTimeMillis());
			return true;
		}
	}
	
	private boolean useLow(LogEntry entry) {
		String[] pramemters = entry.getPramemters();
		//use low when have parameters, otherwise use high.
		if (pramemters == null || pramemters.length <= 0)
			return false;
		return true;
	}
	
}

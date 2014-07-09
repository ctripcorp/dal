package com.ctrip.platform.dal.sql.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ctrip.freeway.metrics.IMetric;
import com.ctrip.freeway.metrics.MetricManager;

public class MetricsLogger {
	public static final String COUNT = "arch.dal.sql.count";
	public static final String COST = "arch.dal.sql.cost";
    public static String SUCCESS = "success";
    public static String FAIL = "fail";
    
    public static long ticksPerMillisecond = 10000;

	private static final String DAO = "DAO";
	private static final String METHOD = "Method";
	private static final String SIZE = "Size";
	private static final String STATUS = "Status";
	private static Queue<MetricsData> statusQueue = new ConcurrentLinkedQueue<MetricsData>();
	private static Map<String, MetricsData> metrixCache = new HashMap<String, MetricsData>();
	
	private static ScheduledExecutorService sender;

	private static IMetric metricLogger = MetricManager.getMetricer();

	static {
		sender = Executors.newSingleThreadScheduledExecutor();
		sender.scheduleAtFixedRate(new MetrixReporter(), 1, 1, TimeUnit.MINUTES);
	}
	
	public static void success(LogEntry entry, long duration) {
		report(entry.getDao(), entry.getMethod(), entry.getSqlSize(), SUCCESS, duration);
	}
	
	public static void fail(LogEntry entry, long duration) {
		report(entry.getDao(), entry.getMethod(), entry.getSqlSize(), FAIL, duration);
	}
	
	public static void shutdown(){
		sender.shutdown();
	}
	
	private static void report(String dao, String method, int size, String status, long duration) {
		long cost = duration;
        if (size < 200)
        {
            size = 200;
        }
        else if (size < 1000)
        {
            size = 1000;
        }
        else if (size < 5000)
        {
            size = 5000;
        }
        else
        {
            size = 99999;
        }

        MetricsData md = new MetricsData();
        md.method = method;
        md.dao = dao;
        md.cost = cost * ticksPerMillisecond;
        md.count = 1;
        md.size = size;
        md.status = status;
        statusQueue.offer(md);
    }
    
    private static void sendLog() {
		if (statusQueue.isEmpty())
			return;

		MetricsData md;
		// 1. Aggregate
		while((md = statusQueue.poll())!= null) {
	        String key = new StringBuilder().append(md.dao).
	                        append(md.method).
	                        append(md.status).
	                        append(md.size).
	                        toString();
	        
	        MetricsData cachedMd = metrixCache.get(key);
	        
	        if(cachedMd == null) {
	        	cachedMd = md;
	        	
	        	Map<String, String> tags = new HashMap<String, String>();
	            tags.put(DAO, cachedMd.dao);
	            tags.put(METHOD, cachedMd.method);
	            tags.put(STATUS, cachedMd.status);
	            tags.put(SIZE, String.valueOf(cachedMd.size));
	            cachedMd.tags = tags;
	            
	        	metrixCache.put(key, cachedMd);
	        } else {
                //cachedMd.cost = stmt.Duration.Ticks;
	        	cachedMd.count++;
	        }
		}
		
		// 2. Send
		for(MetricsData cachedMd: metrixCache.values()) {
			if(cachedMd.count > 0) {
				metricLogger.log(COUNT, cachedMd.count, cachedMd.tags);
				cachedMd.count = 0;
			}
			
			if(cachedMd.cost > 0) {
				metricLogger.log(COST, cachedMd.cost, cachedMd.tags);
				cachedMd.cost = 0;
			}
		}
    }
    
	private static class MetricsData {
        String dao;
    	String method;
        int size;
        String status;
        long cost;
        long count;
        Map<String, String> tags;
    }
    
    private static class MetrixReporter implements Runnable {
		@Override
		public void run() {
			sendLog();
		}
    }
}

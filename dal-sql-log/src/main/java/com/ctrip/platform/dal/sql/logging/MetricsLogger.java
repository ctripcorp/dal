package com.ctrip.platform.dal.sql.logging;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;

public class MetricsLogger {
	private static final String COUNT = "arch.dal.sql.count";
	private static final String COST = "arch.dal.sql.cost";
	private static final String MASTER_SLAVE_COUNT = "arch.dal.rw.count";
	private static String SUCCESS = "success";
	private static String FAIL = "fail";
    
    public static long ticksPerMillisecond = 10000;

	private static final String DAO = "DAO";
	private static final String METHOD = "Method";
	private static final String SIZE = "Size";
	private static final String STATUS = "Status";
	
	//private static Queue<MetricsData> statusQueue = new ConcurrentLinkedQueue<MetricsData>();
	//private static Map<String, MetricsData> metrixCache = new HashMap<String, MetricsData>();
	
	
	private static final String DBTYPE = "DBType";
    private static final String OPTTYPE = "OperationType";
    private static final String DB = "DB";
    
    public static final String CLIENT = "Client";
	private static final String CLIENT_NAME = "Java " + DalClientVersion.version;
    
	//private static Queue<MasterSlaveMetrics> msQueue = new ConcurrentLinkedQueue<MasterSlaveMetrics>();
	//private static Map<String, MasterSlaveMetrics> msCache = new HashMap<String, MasterSlaveMetrics>();
	
	//private static ScheduledExecutorService sender;

	private static IMetric metricLogger = MetricManager.getMetricer();

	static {
		//sender = Executors.newSingleThreadScheduledExecutor();
		//sender.scheduleAtFixedRate(new MetrixReporter(), 1, 1, TimeUnit.MINUTES);
	}
	
	public static void success(LogEntry entry, long duration) {
		report(entry.getDao(), entry.getMethod(), entry.getSqlSize(), SUCCESS, duration);
		report(entry.getDatabaseName(), entry.isMaster() ? "Master" : "Slave", entry.getEvent().name());
	}
	
	public static void fail(LogEntry entry, long duration) {
		report(entry.getDao(), entry.getMethod(), entry.getSqlSize(), FAIL, duration);
	}
	
	public static void shutdown(){
		//sender.shutdown();
	}
	
	public static void report(String databaseSet, String databaseType,String operationType){
		MasterSlaveMetrics data = new MasterSlaveMetrics();
		data.count = 1;
		data.databaseSet = databaseSet;
		data.databaseType = databaseType;
		data.operationType = operationType;
		
		data.tags = new HashMap<String, String>();
		data.tags.put(DB, data.databaseSet);
		data.tags.put(DBTYPE, data.databaseType);
		data.tags.put(OPTTYPE, data.operationType);
		data.tags.put(CLIENT, CLIENT_NAME);
		
		metricLogger.log(MASTER_SLAVE_COUNT, data.count, data.tags);
	}
	public static void report(String dao, String method, int size, String status, long duration) {
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
        
        Map<String, String> tags = new HashMap<String, String>();
        tags.put(DAO, md.dao);
        tags.put(METHOD, md.method);
        tags.put(STATUS, md.status);
        tags.put(SIZE, String.valueOf(md.size));
        tags.put(CLIENT, CLIENT_NAME);
        md.tags = tags;
        
        metricLogger.log(COUNT, md.count, md.tags);    
        metricLogger.log(COST, md.cost, md.tags);
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
	
	 private static class MasterSlaveMetrics
     {
         String databaseSet;
         String databaseType;
         String operationType;
         long count;
         Map<String, String> tags;
     }
}

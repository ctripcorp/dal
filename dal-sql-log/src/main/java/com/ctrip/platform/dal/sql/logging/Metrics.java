package com.ctrip.platform.dal.sql.logging;

import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;
import com.ctrip.platform.dal.logging.markdown.MarkDownInfo;
import com.ctrip.platform.dal.logging.markdup.MarkupInfo;
import com.ctrip.platform.dal.logging.sql.OptInfo;
import com.ctrip.platform.dal.logging.sql.SQLInfo;

public class Metrics {
	private static IMetric metric = MetricManager.getMetricer();
	public static long ticksPerMillisecond = 10000;
	private static String SUCCESS = "success";
	private static String FAIL = "fail";
	
	public static void report(MarkDownInfo info, long value){
		metric.log(MarkDownInfo.KEY, value, info.toTag());
	}
	
	public static void report(MarkupInfo info, int value){
		metric.log(MarkupInfo.KEY, value, info.toTag());
	}
	
	public static void success(LogEntry entry, long duration) {
		report(entry.getDatabaseName(), entry.getClientVersion(), entry.isMaster() ? "Master" : "Slave", entry.getEvent().name());
		SQLInfo info = new SQLInfo(entry.getDao(), entry.getClientVersion(), entry.getMethod(), entry.getSqlSize(), SUCCESS);
		metric.log(SQLInfo.COST, duration * ticksPerMillisecond, info.toTag());
		metric.log(SQLInfo.COUNT, 1, info.toTag());
	}
	
	public static void fail(LogEntry entry, long duration) {
		SQLInfo info = new SQLInfo(entry.getDao(), entry.getClientVersion(), entry.getMethod(), entry.getSqlSize(), FAIL);
		metric.log(SQLInfo.COST, duration * ticksPerMillisecond, info.toTag());
		metric.log(SQLInfo.COUNT, 1, info.toTag());
	}
	
	private static void report(String databaseSet, String version, String databaseType,String operationType){
		OptInfo info = new OptInfo(databaseSet,version, databaseType, operationType);
		metric.log(OptInfo.KEY, 1, info.toTag());
	}
}

package com.ctrip.platform.dal.sql.logging;

import java.util.*;


import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;


public class Metrics {
	private static final String AllInOneKey = "AllInOneKey";
	private static final String CLIENT = "Client";

	private static final String MarkDownInfo_KEY = "arch.dal.markdown.info";
	private static final String MarkDown_MarkDownPolicy = "MarkDownPolicy";
	private static final String MarkDown_Status = "Status";
	private static final String MarkDown_SamplingDuration = "SamplingDuration";
	private static final String MarkDown_MarkDownReason = "Reason";

	private static final String MarkupInfo_KEY = "arch.dal.markup.info";

	private static IMetric metric = MetricManager.getMetricer();
	public static long ticksPerMillisecond = 10000;
	private static String SUCCESS = "success";
	private static String FAIL = "fail";
	private static final String NULL_TABLES="NullTables";
	private static final String EMPTY_TABLES="EmptyTables";
	private static final String NUll_SHARDCATEGORY="NullShardCategory";

	public static void report(MarkDownInfo info){

		info.setStatus("Total");
		metric.log(MarkDownInfo_KEY, info.getTotal(), toTag(info));

		info.setStatus("Fail");
		metric.log(MarkDownInfo_KEY, info.getFail(), toTag(info));
	}

	private static Map<String, String> toTag(MarkDownInfo info){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(AllInOneKey, info.getDbKey());
		tag.put(MarkDown_MarkDownPolicy, info.getPolicy().toString().toLowerCase());
		tag.put(MarkDown_Status, info.getStatus());
		tag.put(MarkDown_SamplingDuration, info.getDuration().toString());
		tag.put(MarkDown_MarkDownReason, info.getReason().toString().toLowerCase());
		tag.put(CLIENT, info.getVersion());
		return tag;
	}


	public static void report(MarkupInfo info){
		metric.log(MarkupInfo_KEY, info.getQualifies(), toTag(info));
	}

	private static Map<String, String> toTag(MarkupInfo info){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(AllInOneKey, info.getDbKey());
		tag.put(CLIENT, info.getVersion());
		return tag;
	}

	public static void success(CtripLogEntry entry, long duration) {
		reportAll(entry,duration,SUCCESS);
	}

	public static void fail(CtripLogEntry entry, long duration) {
		reportAll(entry,duration,FAIL);
	}

	private static void reportAll(CtripLogEntry entry, long duration,String status){
		String database = entry.getDataBaseKeyName();
		String tableString = getTableString(entry.getTables());
		String optType = entry.getEvent().name();
		String version = entry.getClientVersion();
		String shardCategory = entry.getShardingCategory() == null ? NUll_SHARDCATEGORY : entry.getShardingCategory().toString();
//		arch.dal.rw.count
		report(database, version, entry.isMaster() ? "Master" : "Slave", optType, tableString, shardCategory);

//		arch.dal.sql.count & arch.dal.sql.cost
		SQLInfo info = new SQLInfo(entry.getDao(), version, entry.getMethod(), entry.getSqlSize(), status, database, tableString, optType);
		metric.log(SQLInfo.COST, duration * ticksPerMillisecond, info.toTag());
		metric.log(SQLInfo.COUNT, 1, info.toTag());
	}

	private static void report(String databaseSet, String version, String databaseType, String operationType, String tableString ,String shardingCategory) {
		OptInfo info = new OptInfo(databaseSet, version, databaseType, operationType, tableString, shardingCategory);
		metric.log(OptInfo.KEY, 1, info.toTag());
	}

	public static void reportDALCost(LogContext logContext, final Throwable e) {
		List<LogEntry> logEntries = logContext.getEntries();
		if (logEntries != null && logEntries.size() > 0) {
			LogEntry entry = logEntries.get(0);
			long duration = logContext.getDaoExecuteTime() - logContext.getStatementExecuteTime();
			String database = getDataBaseString(logEntries);
			String tableString = getTableString(logEntries);
			String optType = entry.getEvent().name();
			String version = entry.getClientVersion();
			String status = e == null ? SUCCESS : FAIL;
			SQLInfo info = new SQLInfo(entry.getDao(), version, entry.getMethod(), status, database, tableString, optType);
			metric.log(SQLInfo.DAL_COST, duration * ticksPerMillisecond, info.toTag());
		}
	}

	private static String getTableString(Set<String> tables) {
		String tableString = LoggerHelper.setToOrderedString(tables);
		if (tableString.equalsIgnoreCase(LoggerHelper.EMPTY_SET))
			return EMPTY_TABLES;
		if (tableString.equalsIgnoreCase(LoggerHelper.NULL_SET))
			return NULL_TABLES;
		return tableString;
	}

	private static String getTableString(List<LogEntry> entries) {
		Set<String> tableSet = new HashSet<>();
		for (LogEntry entry : entries) {
			tableSet.addAll(entry.getTables());
		}
		return getTableString(tableSet);
	}

	private static String getDataBaseString(List<LogEntry> entries) {
		Set<String> dataBaseSet = new HashSet<>();
		for (LogEntry entry : entries) {
			dataBaseSet.add(entry.getDataBaseKeyName());
		}
		return LoggerHelper.setToOrderedString(dataBaseSet);
	}
}

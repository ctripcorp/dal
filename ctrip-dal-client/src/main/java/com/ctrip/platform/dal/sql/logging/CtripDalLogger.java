package com.ctrip.platform.dal.sql.logging;

import java.util.Map;

import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;

public class CtripDalLogger implements DalLogger {
	private static final String SAMPLING = "sampling";
	private static final String ENCRYPT = "encrypt";
	private static final String SIMPLIFIED = "simplified";
	
	@Override
	public void initLogger(Map<String, String> settings) {
		if(settings == null)
			return;
		
		if(settings.containsKey(SAMPLING))
			DalCLogger.setSamplingLogging(Boolean.parseBoolean(settings.get(SAMPLING)));

		if(settings.containsKey(SIMPLIFIED))
			DalCLogger.setSimplifyLogging(Boolean.parseBoolean(settings.get(SIMPLIFIED)));
		
		if(settings.containsKey(ENCRYPT))
			DalCLogger.setEncryptLogging(Boolean.parseBoolean(settings.get(ENCRYPT)));

	}

	@Override
	public void info(String msg) {
		DalCLogger.log(LogLevel.INFO, msg);
	}

	@Override
	public void warn(String msg) {
		DalCLogger.log(LogLevel.WARN, msg);
	}

	@Override
	public void error(String msg, Throwable e) {
		DalCLogger.error(msg, e);	
	}

	@Override
	public void getConnectionFailed(String dbName, Throwable e) {
		DalCLogger.getConnectionFailed(dbName, e);
	}

	@Override
	public LogEntry createLogEntry() {
		return new CtripLogEntry();
	}

	@Override
	public void start(LogEntry entry) {
		DalCatLogger.start((CtripLogEntry)entry);
	}
	
	@Override
	public void success(LogEntry entry, int count) {
		try {
			DalCLogger.success((CtripLogEntry)entry, count);
			MetricsLogger.success((CtripLogEntry)entry, entry.getDuration());
			DalCatLogger.catTransactionSuccess((CtripLogEntry)entry);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fail(LogEntry entry, Throwable e) {
		try {
			DalCLogger.fail((CtripLogEntry)entry, e);
			MetricsLogger.fail((CtripLogEntry)entry, entry.getDuration());
			DalCatLogger.catTransactionFailed((CtripLogEntry)entry, e);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void markdown(MarkDownInfo markdown) {
		Metrics.report(markdown);
		
	}

	@Override
	public void markup(MarkupInfo markup) {
		Metrics.report(markup);
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
	}
}

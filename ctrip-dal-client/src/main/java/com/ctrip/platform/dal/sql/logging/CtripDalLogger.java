package com.ctrip.platform.dal.sql.logging;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;

public class CtripDalLogger implements DalLogger {
	private static final String SAMPLING = "sampling";
	private static final String ENCRYPT = "encrypt";
	private static final String SIMPLIFIED = "simplified";
	
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
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
	public void info(final String msg) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				DalCLogger.log(LogLevel.INFO, msg);
			}
		});
	}
	
	@Override
	public void warn(final String msg) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				DalCLogger.log(LogLevel.WARN, msg);
			}
		});
	}

	@Override
	public void error(final String msg, final Throwable e) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				DalCLogger.error(msg, e);	
			}
		});
	}

	@Override
	public void getConnectionFailed(final String dbName, final Throwable e) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				DalCLogger.getConnectionFailed(dbName, e);
			}
		});
	}

	@Override
	public LogEntry createLogEntry() {
		return new CtripLogEntry();
	}

	@Override
	public void start(final LogEntry entry) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				DalCatLogger.start((CtripLogEntry)entry);
			}
		});
	}
	
	@Override
	public void success(final LogEntry entry, final int count) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					DalCLogger.success((CtripLogEntry)entry, count);
					Metrics.success((CtripLogEntry)entry, entry.getDuration());
					DalCatLogger.catTransactionSuccess((CtripLogEntry)entry);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void fail(final LogEntry entry, final Throwable e) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					DalCLogger.fail((CtripLogEntry)entry, e);
					Metrics.fail((CtripLogEntry)entry, entry.getDuration());
					DalCatLogger.catTransactionFailed((CtripLogEntry)entry, e);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void markdown(final MarkDownInfo markdown) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				Metrics.report(markdown);
			}
		});
	}

	@Override
	public void markup(final MarkupInfo markup) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				Metrics.report(markup);
			}
		});
	}

	@Override
	public void shutdown() {
		executor.shutdown();
	}
}

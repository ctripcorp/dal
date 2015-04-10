package com.ctrip.platform.dal.sql.logging;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;

public class CtripDalLogger implements DalLogger {
	
	private static final String SAMPLING = "sampling";
	private static final String ENCRYPT = "encrypt";
	private static final String SIMPLIFIED = "simplified";
	private static final String ASYNCLOGGING = "asyncLogging";
	private static final String CAPACITY = "capacity";
	
	private static boolean asyncLogging = true;
	
	private static ExecutorService executor = null;
	
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
	public void info(final String msg) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					DalCLogger.log(LogLevel.INFO, msg);
				}
			});
		} else {
			DalCLogger.log(LogLevel.INFO, msg);
		}
	}
	
	@Override
	public void warn(final String msg) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					DalCLogger.log(LogLevel.WARN, msg);
				}
			});
		} else {
			DalCLogger.log(LogLevel.WARN, msg);
		}
	}

	@Override
	public void error(final String msg, final Throwable e) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					DalCLogger.error(msg, e);	
				}
			});
		} else {
			DalCLogger.error(msg, e);	
		}
	}

	@Override
	public void getConnectionFailed(final String dbName, final Throwable e) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					DalCLogger.getConnectionFailed(dbName, e);
				}
			});
		} else {
			DalCLogger.getConnectionFailed(dbName, e);
		}
	}

	@Override
	public LogEntry createLogEntry() {
		return new CtripLogEntry();
	}

	@Override
	public void start(final LogEntry entry) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					DalCatLogger.start((CtripLogEntry)entry);
				}
			});
		} else {
			DalCatLogger.start((CtripLogEntry)entry);
		}
	}
	
	@Override
	public void success(final LogEntry entry, final int count) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					recordSuccess(entry, count);
				}
			});
		} else {
			recordSuccess(entry, count);
		}
	}
	
	private void recordSuccess(final LogEntry entry, final int count) {
		try {
			DalCLogger.success((CtripLogEntry)entry, count);
			Metrics.success((CtripLogEntry)entry, entry.getDuration());
			DalCatLogger.catTransactionSuccess((CtripLogEntry)entry);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fail(final LogEntry entry, final Throwable e) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					recordFail(entry, e);
				}
			});
		} else {
			recordFail(entry, e);
		}
	}
	
	private void recordFail(final LogEntry entry, final Throwable e) {
		try {
			DalCLogger.fail((CtripLogEntry)entry, e);
			Metrics.fail((CtripLogEntry)entry, entry.getDuration());
			DalCatLogger.catTransactionFailed((CtripLogEntry)entry, e);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void markdown(final MarkDownInfo markdown) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					Metrics.report(markdown);
				}
			});
		} else {
			Metrics.report(markdown);
		}
	}

	@Override
	public void markup(final MarkupInfo markup) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					Metrics.report(markup);
				}
			});
		} else {
			Metrics.report(markup);
		}
	}

	@Override
	public void shutdown() {
		executor.shutdown();
	}
}

package com.ctrip.platform.dal.sql.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import com.ctrip.framework.clogging.agent.trace.ITrace;
import com.ctrip.framework.clogging.agent.trace.TraceManager;
import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.framework.clogging.domain.thrift.LogType;

public class DalLogger {
	public static final String TITLE = "Dal Fx";
	public static final String LOG_NAME = "DAL Java Client " + DalClientVersion.version;
	public static AtomicBoolean simplifyLogging = new AtomicBoolean(false);

	public static ThreadLocal<DalWatcher> watcher = new ThreadLocal<DalWatcher>();

	private static ILog logger = LogManager.getLogger(LOG_NAME);
	private static ITrace trace = TraceManager.getTracer(LOG_NAME);

	public static boolean isSimplifyLogging() {
		return simplifyLogging.get();
	}

	public static void setSimplifyLogging(boolean simplify) {
		simplifyLogging.set(simplify);
	}

	public static void success(LogEntry entry, long duration, int count) {
		entry.setDuration(duration);
		entry.setSuccess(true);
		entry.setResultCount(count);
		log(entry);
		entry.catTransactionSuccess();
	}

	public static void fail(LogEntry entry, long duration, Throwable e) {
		entry.setDuration(duration);
		entry.setSuccess(false);
		entry.setErrorMsg(e.getMessage());
		entry.setException(e);
		log(entry);
		entry.catTransactionFailed(e);
	}

	public static void log(LogEntry entry) {
		if (isSimplifyLogging()) {
			if (entry.getException() == null) {
				logger.info(TITLE, entry.toJson(), entry.getTag());
			} else {
				logger.error(TITLE, entry.toJson(), entry.getTag());
			}
		} else {
			if (entry.getException() == null)
				trace.log(LogType.SQL, LogLevel.ERROR, TITLE, entry.toJson(),
						entry.getTag());
			else
				trace.log(LogType.SQL, LogLevel.ERROR, TITLE, entry.toJson(),
						entry.getTag());
		}
	}

	public static void logGetConnectionFailed(String realDbName, Throwable e) {
		try {
			String msg = getExceptionStack(e);

			String logMsg = "Connectiing to " + realDbName
					+ " database failed." + System.lineSeparator()
					+ System.lineSeparator()
					+ "********** Exception Info **********"
					+ System.lineSeparator() + msg;
			log("Get connection", DalEventEnum.CONNECTION_FAILED,
					LogLevel.ERROR, logMsg);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}

	public static void error(String desc, Throwable e) {
		try {
			String msg = getExceptionStack(e);

			String logMsg = desc + System.lineSeparator()
					+ System.lineSeparator()
					+ "********** Exception Info **********"
					+ System.lineSeparator() + msg;
			logger.error(TITLE, logMsg);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}

	public static void log(String name, DalEventEnum event, LogLevel level,
			String msg) {
		StringBuffer sbuffer = new StringBuffer();
		sbuffer.append(String.format("Log Name: %s" + System.lineSeparator(),
				name));
		sbuffer.append(String.format("Event: %s" + System.lineSeparator(),
				event.getEventId()));
		sbuffer.append(String.format("Message: %s " + System.lineSeparator(),
				msg));

		log(level, sbuffer.toString());
	}
	
	public static void log(LogLevel level, String pattern, Object... args){
		log(level, String.format(pattern, args));
	}
	
	public static void log(LogLevel level, String msg) {
		switch (level) {
		case DEBUG:
			logger.debug(TITLE, msg);
		case INFO:
			logger.info(TITLE, msg);
			break;
		case ERROR:
			logger.error(TITLE, msg);
			break;
		case FATAL:
			logger.fatal(TITLE, msg);
			break;
		default:
			break;
		}
	}

	public static String getExceptionStack(Throwable e) {
		String msg = e.getMessage();
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			msg = sw.toString();
		} catch (Throwable e2) {
			msg = "bad getErrorInfoFromException";
		}

		return msg;
	}
}

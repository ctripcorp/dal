package com.ctrip.platform.dal.sql.logging;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import com.ctrip.framework.clogging.agent.trace.ISpan;
import com.ctrip.framework.clogging.agent.trace.ITrace;
import com.ctrip.framework.clogging.agent.trace.TraceManager;
import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.framework.clogging.domain.thrift.LogType;
import com.ctrip.framework.clogging.domain.thrift.SpanType;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.client.DalWatcher;
import com.ctrip.platform.dal.dao.client.ILogSamplingStrategy;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;

public class DalCLogger {
	public static final String TITLE = "Dal Fx";
	public static AtomicBoolean simplifyLogging = new AtomicBoolean(false);
	public static AtomicBoolean samplingLogging = new AtomicBoolean(true);
	public static AtomicBoolean encryptLogging = new AtomicBoolean(true);
    public static AtomicReference<ILogSamplingStrategy> samplingStrategyAtomicReference=new AtomicReference<>();
	public static ThreadLocal<DalWatcher> watcher = new ThreadLocal<DalWatcher>();

	private static ILog logger;
	private static ITrace trace;

	static {
		logger = LogManager.getLogger(Version.getLoggerName());
		trace = TraceManager.getTracer(Version.getLoggerName());
	}

	public static boolean isSimplifyLogging() {
		return simplifyLogging.get();
	}

	public static void setSimplifyLogging(boolean simplify) {
		simplifyLogging.set(simplify);
	}

	public static void setEncryptLogging(boolean encrypt) {
		encryptLogging.set(encrypt);
	}
	
	public static boolean isEncryptLogging() {
		return encryptLogging.get();
	}

	public static boolean isSamplingLogging() {
		return samplingLogging.get();
	}

	public static void setSamplingLogging(boolean sampling) {
		samplingLogging.set(sampling);
	}

	public static ILogSamplingStrategy getSamplingStrategy() {
		return samplingStrategyAtomicReference.get();
	}

	public static void setSamplingStrategy(ILogSamplingStrategy samplingStrategy) {
		samplingStrategyAtomicReference.set(samplingStrategy);
	}

	public static void start(CtripLogEntry entry) {
		try {
			if (isSimplifyLogging())
				return;
			
//			 Trace is no longer work according to clog team
			ISpan urlSpan = trace.startSpan("DAL", "DAL", SpanType.SQL);
			entry.setUrlSpan(urlSpan);
		} catch (Throwable e) {
			logger.error(e);
		}
	}

	public static void success(CtripLogEntry entry, int count) {
		try {
			entry.setSuccess(true);
			entry.setResultCount(count);
			if (isSamplingLogging() && !getSamplingStrategy().validate(entry))
				return;
			log(entry);
		} catch (Throwable e) {
			logger.error(e);
		}
	}

	public static void fail(CtripLogEntry entry, Throwable e) {
		try {
			entry.setSuccess(false);
			entry.setErrorMsg(e.getMessage());
			entry.setException(e);
			log(entry);
		} catch (Throwable e1) {
			logger.error(e);
		}
	}

	private static void log(CtripLogEntry entry) {
		if (isSimplifyLogging()) {
			if (entry.getException() == null) {
				logger.info(TITLE, entry.toJson(isEncryptLogging(), entry), entry.getTag());
			} else {
				logger.error(TITLE, entry.toJson(isEncryptLogging(), entry), entry.getTag());
			}
		} else {
			// Trace is no longer work according to clog team
			LogLevel level = entry.getException() == null ? LogLevel.INFO : LogLevel.ERROR;
			trace.log(LogType.SQL, level, TITLE, entry.toJson(isEncryptLogging(), entry),
					entry.getTag());
//			ISpan urlSpan = entry.getUrlSpan();
//			urlSpan.stop();
		}
	}

	public static void error(String desc, Throwable e) {
		try {
			String msg = LoggerHelper.getExceptionStack(e);

			String logMsg = desc + System.lineSeparator()
					+ System.lineSeparator()
					+ "********** Exception Info **********"
					+ System.lineSeparator() + msg;
			logger.error(TITLE, logMsg);
		} catch (Throwable e1) {
			logger.error(e1);
		}
	}

	public static void getConnectionFailed(String realDbName, Throwable e) {
		try {
			StringBuffer sbuffer = new StringBuffer();
			sbuffer.append(String.format("Log Name: %s" + System.lineSeparator(), "Get connection"));
			sbuffer.append(String.format("Event: %s" + System.lineSeparator(), 
					DalEventEnum.CONNECTION_FAILED.getEventId()));
			
			String msg= "Connectiing to " + realDbName
					+ " database failed." + System.lineSeparator();

			sbuffer.append(String.format("Message: %s " + System.lineSeparator(), msg));
			
			error(sbuffer.toString(), e);
		} catch (Throwable e1) {
			logger.error(e1);
		}
	}
	
	public static void log(LogLevel level, String msg) {
		try {
			switch (level) {
			case DEBUG:
				logger.debug(TITLE, msg);
				break;
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
		} catch (Throwable e) {
			logger.error(e);
		}
	}
}

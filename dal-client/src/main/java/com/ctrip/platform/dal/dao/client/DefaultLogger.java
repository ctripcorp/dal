package com.ctrip.platform.dal.dao.client;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;

/**
 * logger that is based on log4j. It is useful when you want to quick start your DAL project without
 * a specified logger
 * @author jhhe
 *
 */
public class DefaultLogger extends LoggerAdapter implements DalLogger {
	
	private Logger logger = LoggerFactory.getLogger(Version.getLoggerName());
	
	private static final String LINESEPARATOR = System.lineSeparator();
	
	@Override
	public void info(final String desc) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					logger.info(desc);
				}
			});
		} else {
			logger.info(desc);
		}
	}

	@Override
	public void warn(final String desc) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					logger.warn(desc);
				}
			});
		} else {
			logger.info(desc);
		}
	}

	@Override
	public void error(final String desc, final Throwable e) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					logger.error(desc, e);
				}
			});
		} else {
			logger.error(desc, e);
		}
	}

	@Override
	public void getConnectionFailed(final String logicDb, final Throwable e) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					logConnectionFailed(logicDb, e);
				}
			});
		} else {
			logConnectionFailed(logicDb, e);
		}
	}
	
	private void logConnectionFailed(String realDbName, Throwable e) {
		StringBuffer sbuffer = new StringBuffer();
		sbuffer.append(String.format("Log Name: %s" + System.lineSeparator(), "Get connection"));
		sbuffer.append(String.format("Event: %s" + System.lineSeparator(), 
				DalEventEnum.CONNECTION_FAILED.getEventId()));
		
		String msg= "Connectiing to " + realDbName + " database failed." + System.lineSeparator();

		sbuffer.append(String.format("Message: %s " + System.lineSeparator(), msg));
		
		logError(sbuffer.toString(), e);
	}
	
	private void logError(String desc, Throwable e) {
		try {
			String msg = LoggerHelper.getExceptionStack(e);

			String logMsg = desc + System.lineSeparator()
					+ System.lineSeparator()
					+ "********** Exception Info **********"
					+ System.lineSeparator() + msg;
			logger.error(logMsg);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public LogEntry createLogEntry() {
		return new LogEntry();
	}

	@Override
	public void start(LogEntry entry) {
		return;
	}

	@Override
	public void success(final LogEntry entry, final int count) {
		if (samplingLogging && !validate(entry) )
			return;
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
			StringBuilder msg = new StringBuilder("success info \n");
			msg.append("\t").append("DAL.version : java-").append(entry.getClientVersion()).append(LINESEPARATOR);
			msg.append("\t").append("source : ").append(entry.getSource()).append(LINESEPARATOR);
			String sql = "*";
			if (!entry.isSensitive()) {
				sql = LoggerHelper.getSqlTpl(entry);
			}
			msg.append("\t").append("sql: ").append(sql).append(LINESEPARATOR);
			if (entry.getPramemters() != null) {
				msg.append("\t").append("parameters : ").append(getEncryptParameters(encryptLogging, entry)).append(LINESEPARATOR);
			} else {
				msg.append("\t").append("parameters : ").append(LINESEPARATOR);
			}
			msg.append("\t").append("CostDetail : ").append(DalWatcher.toJson()).append(LINESEPARATOR);
			msg.append("\t").append("SQL.database : ").append(entry.getDbUrl()).append(LINESEPARATOR);
			logger.info(msg.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private String getEncryptParameters(boolean encryptLogging, LogEntry entry){
		String params = "";
		if(encryptLogging){
			try {
				params = new String(Base64.encodeBase64(LoggerHelper.getParams(entry).getBytes()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			params = LoggerHelper.getParams(entry);
		}
		return params;
	}
	
	@Override
	public void fail(final LogEntry entry, final Throwable e) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					logger.error(e.getMessage(), e);
				}
			});
		} else {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void markdown(final MarkDownInfo markdown) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					logMarkdown(markdown);
				}
			});
		} else {
			logMarkdown(markdown);
		}
	}
	
	private void logMarkdown(MarkDownInfo markdown) {
		StringBuilder msg = new StringBuilder();
		msg.append("arch.dal.markdown.info").append(LINESEPARATOR);
		msg.append("\t total:").append(markdown.getTotal()).append(LINESEPARATOR);
		msg.append("\t AllInOneKey:").append(markdown.getDbKey()).append(LINESEPARATOR);
		msg.append("\t MarkDownPolicy:").append(markdown.getPolicy().toString().toLowerCase()).append(LINESEPARATOR);
		msg.append("\t Status:").append(markdown.getStatus()).append(LINESEPARATOR);
		msg.append("\t SamplingDuration:").append(markdown.getDuration().toString()).append(LINESEPARATOR);
		msg.append("\t Reason:").append(markdown.getReason().toString().toLowerCase()).append(LINESEPARATOR);
		msg.append("\t Client:").append(markdown.getVersion()).append(LINESEPARATOR);
		logger.info(msg.toString());
	}

	@Override
	public void markup(final MarkupInfo markup) {
		if (asyncLogging) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					logMarkup(markup);
				}
			});
		} else {
			logMarkup(markup);
		}
	}
	
	private void logMarkup(MarkupInfo markup) {
		StringBuilder msg = new StringBuilder();
		msg.append("arch.dal.markup.info").append(LINESEPARATOR);
		msg.append("\t Qualifies:").append(markup.getQualifies()).append(LINESEPARATOR);
		msg.append("\t AllInOneKey:").append(markup.getDbKey()).append(LINESEPARATOR);
		msg.append("\t Client:").append(markup.getVersion()).append(LINESEPARATOR);
		logger.info(msg.toString());
	}

	@Override
	public void shutdown() {
		logger.info("shutdown DefaultLogger.");
		super.shutdown();
	}

	@Override
	public String getAppID() {
		return "999999";
	}
}

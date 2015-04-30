package com.ctrip.platform.dal.dao.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;

/**
 * logger that is based on log4j. It is useful when you want to quick start your DAL project without
 * a specified logger
 * @author jhhe
 *
 */
public class DefaultLogger implements DalLogger {
	
	private Logger logger = LoggerFactory.getLogger(DefaultLogger.class);
	
	private static final String LINESEPARATOR = System.lineSeparator();
	
	private static final String SAMPLING = "sampling";
	private static final String ENCRYPT = "encrypt";
	private static final String SIMPLIFIED = "simplified";
	private static final String ASYNCLOGGING = "asyncLogging";
	private static final String CAPACITY = "capacity";
	
	private static boolean sampling = false;
	private static boolean encrypt = true;
	private static boolean simplified = false;
	
	private static boolean asyncLogging = true;
	
	private static ExecutorService executor = null;

	@Override
	public void initLogger(Map<String, String> settings) {
		if(settings == null)
			return;
		
		if(settings.containsKey(SAMPLING))
			sampling = Boolean.parseBoolean(settings.get(SAMPLING));

		if(settings.containsKey(SIMPLIFIED))
			simplified = Boolean.parseBoolean(settings.get(SIMPLIFIED));
		
		if(settings.containsKey(ENCRYPT))
			encrypt = Boolean.parseBoolean(settings.get(ENCRYPT));
		
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
			String msg = getExceptionStack(e);

			String logMsg = desc + System.lineSeparator()
					+ System.lineSeparator()
					+ "********** Exception Info **********"
					+ System.lineSeparator() + msg;
			logger.error(logMsg);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}
	
	private String getExceptionStack(Throwable e) {
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
				sql = getSql(entry);
			}
			msg.append("\t").append("sql: ").append(sql).append(LINESEPARATOR);;
			if (entry.getPramemters() != null) {
				msg.append("\t").append("parameters : ").append(getEncryptParameters(encrypt, entry)).append(LINESEPARATOR);
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
	
	private String getSql(LogEntry entry){
		DalEventEnum event = entry.getEvent();
		
		if(event == DalEventEnum.QUERY || event == DalEventEnum.UPDATE_SIMPLE ||
				event == DalEventEnum.UPDATE_KH || event == DalEventEnum.BATCH_UPDATE_PARAM){
			return entry.getSqls() != null && entry.getSqls().length > 0 ? entry.getSqls()[0] : "";
		}
		if (event == DalEventEnum.BATCH_UPDATE) {
			return Arrays.toString(entry.getSqls());
		}
		if(event == DalEventEnum.CALL || event == DalEventEnum.BATCH_CALL){
			return entry.getCallString();
		}
		
		return "";
	}
	
	private String getEncryptParameters(boolean encryptLogging, LogEntry entry){
		String params = "";
		if(encryptLogging){
			try {
				params = new String(Base64.encodeBase64(this.getParams(entry).getBytes()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			params = getParams(entry);
		}
		return params;
	}
	
	private String getParams(LogEntry entry){
		DalEventEnum event = entry.getEvent();
		String[] pramemters = entry.getPramemters();
		
		StringBuilder sbout = new StringBuilder();
		if(pramemters == null || pramemters.length <= 0){
			return sbout.toString();
		}
		if(event == DalEventEnum.QUERY || 
				event == DalEventEnum.UPDATE_SIMPLE ||
				event == DalEventEnum.UPDATE_KH ||
				event == DalEventEnum.CALL){
			return null != pramemters && pramemters.length > 0 ? pramemters[0] : "";
		}
		if(event == DalEventEnum.BATCH_UPDATE_PARAM ||
				event == DalEventEnum.BATCH_CALL){
			for(String param : pramemters){
				sbout.append(param + ";");
			}
			return sbout.substring(0, sbout.length() - 1);
		}
		return "";
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
		executor.shutdown();
	}

	@Override
	public String getAppID() {
		return "999999";
	}
}

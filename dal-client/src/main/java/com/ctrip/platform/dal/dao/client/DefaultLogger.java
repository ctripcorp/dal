package com.ctrip.platform.dal.dao.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.helper.DalBase64;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;
import com.ctrip.platform.dal.dao.task.DalRequest;

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
	    call(new Runnable() {public void run() {
			logger.info(desc);
	    }});
	}

	@Override
	public void warn(final String desc) {
        call(new Runnable() {public void run() {
            logger.warn(desc);
        }});
	}

	@Override
	public void error(final String desc, final Throwable e) {
        call(new Runnable() {public void run() {
            logger.error(desc, e);
        }});
	}
	
	private void infoOrError(final String desc, final Throwable e) {
        if(e == null)
            info(desc);
        else
            error(desc, e);

	}

	@Override
	public void getConnectionFailed(final String logicDb, final Throwable e) {
        call(new Runnable() {public void run() {
            logConnectionFailed(logicDb, e);
        }});
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
        call(new Runnable() {public void run() {
            recordSuccess(entry, count);
        }});
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
			msg.append("\t").append("CostDetail : ").append(entry.getCostDetail()).append(LINESEPARATOR);
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
				params = new String(DalBase64.encodeBase64(LoggerHelper.getParams(entry).getBytes()));
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
        error(e.getMessage(), e);
	}
	
	@Override
	public void markdown(final MarkDownInfo markdown) {
	    info(logMarkdown(markdown));
	}
	
	private String logMarkdown(MarkDownInfo markdown) {
		StringBuilder msg = new StringBuilder();
		msg.append("arch.dal.markdown.info").append(LINESEPARATOR);
		msg.append("\t total:").append(markdown.getTotal()).append(LINESEPARATOR);
		msg.append("\t AllInOneKey:").append(markdown.getDbKey()).append(LINESEPARATOR);
		msg.append("\t MarkDownPolicy:").append(markdown.getPolicy().toString().toLowerCase()).append(LINESEPARATOR);
		msg.append("\t Status:").append(markdown.getStatus()).append(LINESEPARATOR);
		msg.append("\t SamplingDuration:").append(markdown.getDuration().toString()).append(LINESEPARATOR);
		msg.append("\t Reason:").append(markdown.getReason().toString().toLowerCase()).append(LINESEPARATOR);
		msg.append("\t Client:").append(markdown.getVersion()).append(LINESEPARATOR);
		return msg.toString();
	}

	@Override
	public void markup(final MarkupInfo markup) {
        info(logMarkup(markup));
	}
	
	private String logMarkup(MarkupInfo markup) {
		StringBuilder msg = new StringBuilder();
		msg.append("arch.dal.markup.info").append(LINESEPARATOR);
		msg.append("\t Qualifies:").append(markup.getQualifies()).append(LINESEPARATOR);
		msg.append("\t AllInOneKey:").append(markup.getDbKey()).append(LINESEPARATOR);
		msg.append("\t Client:").append(markup.getVersion()).append(LINESEPARATOR);
		return msg.toString();
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

    @Override
    public <T> LogContext start(DalRequest<T> request) {
        logger.info("start request");
        return new LogContext();
    }

    @Override
    public void end(LogContext logContext, Throwable e) {
        infoOrError("end request", e);
    }

    @Override
    public void startCrossShardTasks(LogContext logContext, boolean isSequentialExecution) {
        info("Start Cross Shard Tasks");
    }

    @Override
    public void endCrossShards(LogContext logContext, Throwable e) {
        infoOrError("End Cross Shards", e);
    }

    @Override
    public void startTask(LogContext logContext, String shard) {
        info("Start Task: " +shard);
    }

    @Override
    public void endTask(LogContext logContext, String shard, Throwable e) {
        infoOrError("End Task: " + shard, e);
    }

    @Override
    public void startStatement(LogEntry entry) {
        info("Start Statement");
    }

    @Override
    public void endStatement(LogEntry entry, Throwable e) {
        infoOrError("End Statement", e);
    }
}
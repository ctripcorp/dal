package com.ctrip.platform.dal.sql.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.framework.clogging.agent.MessageManager;
import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;
import com.ctrip.platform.dal.dao.task.DalRequest;
import com.dianping.cat.status.ProductVersionManager;

public class CtripDalLogger extends LoggerAdapter implements DalLogger {

    private Logger logger = LoggerFactory.getLogger(Version.getLoggerName());
    public static final String DAL_VERSION = "DAL.version";
    private static final String DAL_VALIDATION = "DAL.validation";
    private static final String LOG_SAMPLING_DISABLED="LogSamplingDisabled";
    private static final AtomicReference<String> version = new AtomicReference<>();

    @Override
    public void initialize(Map<String, String> settings) {
        super.initialize(settings);
        if (samplingLogging == false)
            DalCatLogger.logEvent(DAL_VALIDATION, LOG_SAMPLING_DISABLED);
        version.set(initVersion());
        ProductVersionManager.getInstance().register(DAL_VERSION, "java-" + version.get());
        DalCLogger.setEncryptLogging(encryptLogging);
        DalCLogger.setSimplifyLogging(simplifyLogging);
        DalCLogger.setSamplingLogging(samplingLogging);
        DalCLogger.setSamplingStrategy(logSamplingStrategy);
    }

    public static String getDalVersion() {
        return "java-" + version.get();
    }

    private String initVersion(){
        String path = "/ctrip-dal-client.version.prop";
        InputStream stream = Version.class.getResourceAsStream(path);
        if (stream == null) {
            return "UNKNOWN";
        }
        Properties props = new Properties();
        try {
            props.load(stream);
            stream.close();
            return (String)props.get("version");
        } catch (IOException e) {
            return "UNKNOWN";
        }
    }

    @Override
    public void info(final String msg) {
        call(new Runnable() {public void run() {
            DalCLogger.log(LogLevel.INFO, msg);
        }});
    }

    @Override
    public void warn(final String msg) {
        call(new Runnable() {public void run() {
            DalCLogger.log(LogLevel.WARN, msg);
        }});
    }

    @Override
    public void error(final String msg, final Throwable e) {
        call(new Runnable() {public void run() {
            DalCLogger.error(msg, e);
            DalCatLogger.logError(e);
        }});
    }

    @Override
    public void getConnectionFailed(final String dbName, final Throwable e) {
        call(new Runnable() {public void run() {
            DalCLogger.getConnectionFailed(dbName, e);
        }});
    }

    @Override
    public LogEntry createLogEntry() {
        return new CtripLogEntry();
    }

    @Override
    public void start(final LogEntry entry) {
        call(new Runnable() {public void run() {
            recordStart((CtripLogEntry) entry);
        }});
    }

    private void recordStart(final LogEntry entry) {
        DalCatLogger.start((CtripLogEntry) entry);
//        DalCLogger.start((CtripLogEntry) entry);
    }

    @Override
    public void success(final LogEntry entry, final int count) {
        call(new Runnable() {public void run() {
            recordSuccess(entry, count);
        }});
    }

    private void recordSuccess(final LogEntry entry, final int count) {
        try {
            DalCatLogger.catTransactionSuccess((CtripLogEntry) entry, count);
            Metrics.success((CtripLogEntry) entry, entry.getDuration());
            DalCLogger.success((CtripLogEntry) entry, count);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fail(final LogEntry entry, final Throwable e) {
        call(new Runnable() {public void run() {
            recordFail(entry, e);
        }});
    }

    private void recordFail(final LogEntry entry, final Throwable e) {
        try {
            DalCatLogger.catTransactionFailed((CtripLogEntry) entry, e);
            DalCLogger.fail((CtripLogEntry) entry, e);
            Metrics.fail((CtripLogEntry) entry, entry.getDuration());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void markdown(final MarkDownInfo markdown) {
        call(new Runnable() {public void run() {
            Metrics.report(markdown);
        }});
    }

    @Override
    public void markup(final MarkupInfo markup) {
        call(new Runnable() {public void run() {
            Metrics.report(markup);
        }});
    }

    @Override
    public String getAppID() {
        return LogConfig.getAppID();
    }

    @Override
    public void shutdown() {
        logger.info("shutdown clogging");
        try {
            MessageManager.getInstance().shutdown();
        } catch (Throwable e) {
        }
        super.shutdown();
    }

    @Override
    public <T> LogContext start(DalRequest<T> request) {
        return DalCatLogger.start(request);
    }

    @Override
    public void end(LogContext logContext, final Throwable e) {
        DalCatLogger.end(logContext, e);
        Metrics.reportDALCost(logContext, e);
    }

    @Override
    public void startCrossShardTasks(LogContext logContext, boolean isSequentialExecution) {
        DalCatLogger.startCrossShardTasks(logContext, isSequentialExecution);
    }

    @Override
    public void endCrossShards(LogContext logContext, Throwable e) {
        DalCatLogger.endCrossShards(logContext, e);
    }

    @Override
    public void startTask(LogContext logContext, String shard) {
        DalCatLogger.startTask(logContext, shard);
    }

    @Override
    public void endTask(LogContext logContext, String shard, Throwable e) {
        DalCatLogger.endTask(logContext, shard, e);
    }

    @Override
    public void startStatement(LogEntry entry) {
        DalCatLogger.startStatement((CtripLogEntry)entry);
    }

    @Override
    public void endStatement(LogEntry entry, Throwable e) {
        DalCatLogger.endStatement((CtripLogEntry)entry, e);
    }
}

package com.ctrip.platform.dal.sql.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.framework.clogging.agent.MessageManager;
import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;
import com.dianping.cat.status.ProductVersionManager;

public class CtripDalLogger extends LoggerAdapter implements DalLogger {

    private Logger logger = LoggerFactory.getLogger(Version.getLoggerName());
    private static final String DAL_VERSION = "DAL.version";

    @Override
    public void initialize(Map<String, String> settings) {
        super.initialize(settings);
        ProductVersionManager.getInstance().register(DAL_VERSION, "java-" + initVersion());
        DalCLogger.setEncryptLogging(encryptLogging);
        DalCLogger.setSimplifyLogging(simplifyLogging);
    }
    
    private String initVersion(){
        String path = "/CtripClientVersion.prop";
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
                    recordStart((CtripLogEntry) entry);
                }
            });
        } else {
            recordStart((CtripLogEntry) entry);
        }
    }

    private void recordStart(final LogEntry entry) {
        DalCatLogger.start((CtripLogEntry) entry);
        DalCLogger.start((CtripLogEntry) entry);
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
            DalCatLogger.catTransactionSuccess((CtripLogEntry) entry);
            if (samplingLogging && !validate(entry))
                return;
            DalCLogger.success((CtripLogEntry) entry, count);
            Metrics.success((CtripLogEntry) entry, entry.getDuration());
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
            DalCatLogger.catTransactionFailed((CtripLogEntry) entry, e);
            DalCLogger.fail((CtripLogEntry) entry, e);
            Metrics.fail((CtripLogEntry) entry, entry.getDuration());
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

}

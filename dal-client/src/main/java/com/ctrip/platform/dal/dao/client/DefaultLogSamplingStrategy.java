package com.ctrip.platform.dal.dao.client;


import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultLogSamplingStrategy implements ILogSamplingStrategy {
    private AtomicBoolean started = new AtomicBoolean(false);
    private LogCacheForDatabaseSetAndTables logCacheForDatabaseSetAndTables;

    @Override
    public boolean validate(ILogEntry entry) {
        if (LoggerAdapter.samplingRate == 0)
            return false;
        if (started.compareAndSet(false, true))
            logCacheForDatabaseSetAndTables = new LogCacheForDatabaseSetAndTables();
        return logCacheForDatabaseSetAndTables.validateDatabaseSetAndTablesCache(entry);
    }

    protected LogCacheForDatabaseSetAndTables getLogCacheForDatabaseSetAndTables() {
        return logCacheForDatabaseSetAndTables;
    }
}

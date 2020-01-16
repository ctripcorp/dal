package com.ctrip.platform.dal.dao.client;


public class DefaultLogSamplingStrategy implements ILogSamplingStrategy {
    private LogCacheForDatabaseSetAndTables logCacheForDatabaseSetAndTables = new LogCacheForDatabaseSetAndTables();

    @Override
    public boolean validate(ILogEntry entry) {
        if (LoggerAdapter.samplingRate == 0)
            return false;
        return logCacheForDatabaseSetAndTables.validateDatabaseSetAndTablesCache(entry);
    }

    protected LogCacheForDatabaseSetAndTables getLogCacheForDatabaseSetAndTables() {
        return logCacheForDatabaseSetAndTables;
    }
}

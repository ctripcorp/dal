package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;

import java.util.concurrent.ConcurrentHashMap;


public class LogCacheForDatabaseSetAndTables {
    private ConcurrentHashMap<String, LogCacheForTableAndOperations> databaseSetAndTableMap = new ConcurrentHashMap<>();

    public LogCacheForDatabaseSetAndTables() {
        for (String databaseSetName : DalClientFactory.getDalConfigure().getDatabaseSetNames()) {
            databaseSetAndTableMap.put(databaseSetName, new LogCacheForTableAndOperations());
        }
    }

    public boolean validateDatabaseSetAndTablesCache(LogEntry entry) {
        String table = LoggerHelper.setToOrderedString(entry.getTables());
        LogCacheForTableAndOperations tableAndOperationCache = getTableAndOperationCache(entry.getLogicDbName());

        return tableAndOperationCache.validateTableAndOperationCache(table, entry);
    }

    private LogCacheForTableAndOperations getTableAndOperationCache(String logicDBName) {
        return databaseSetAndTableMap.get(logicDBName);
    }
}

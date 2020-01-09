package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.helper.LoggerHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class LogCacheForDatabaseSetAndTables {
    final private Map<String, LogCacheForTableAndOperations> databaseSetAndTableCache = new ConcurrentHashMap<>();

    public boolean validateDatabaseSetAndTablesCache(ILogEntry entry) {
        String logicDbName = entry.getLogicDbName();
        LogCacheForTableAndOperations tableAndOperationCache = databaseSetAndTableCache.get(logicDbName);

        if (tableAndOperationCache == null) {
            synchronized (databaseSetAndTableCache) {
                tableAndOperationCache = databaseSetAndTableCache.get(logicDbName);
                if (tableAndOperationCache == null) {
                    tableAndOperationCache = new LogCacheForTableAndOperations();
                    databaseSetAndTableCache.put(logicDbName, tableAndOperationCache);
                }
            }
        }

        return tableAndOperationCache.validateTableAndOperationCache(LoggerHelper.setToOrderedString(entry.getTables()), entry);
    }

    protected Map<String, LogCacheForTableAndOperations> getDatabaseSetAndTableCache() {
        return databaseSetAndTableCache;
    }
}

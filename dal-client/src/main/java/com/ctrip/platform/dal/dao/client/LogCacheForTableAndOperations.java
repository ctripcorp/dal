package com.ctrip.platform.dal.dao.client;

import java.util.concurrent.ConcurrentHashMap;

public class LogCacheForTableAndOperations {
    private ConcurrentHashMap<String, LogCacheForOperationAndDaoMethods> tableAndOperationCache = new ConcurrentHashMap<>();


    public boolean validateTableAndOperationCache(String table, LogEntry entry) {
        if (!tableAndOperationCache.containsKey(table)) {
            addTableAndOperationCache(table);
            return true;
        }

        LogCacheForOperationAndDaoMethods operationAndDaoMethodsCache = getOperationAndDaoMethodsCache(table);
        return operationAndDaoMethodsCache.validateOperationAndDaoMethodsCache(entry);
    }

    public void addTableAndOperationCache(String tableName) {
        tableAndOperationCache.put(tableName, new LogCacheForOperationAndDaoMethods());
    }

    private LogCacheForOperationAndDaoMethods getOperationAndDaoMethodsCache(String tableName) {
        return tableAndOperationCache.get(tableName);
    }
}

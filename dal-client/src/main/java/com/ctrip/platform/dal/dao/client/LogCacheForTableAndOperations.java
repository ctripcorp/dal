package com.ctrip.platform.dal.dao.client;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogCacheForTableAndOperations {
    private Map<String, LogCacheForOperationAndDaoMethods> tableAndOperationCache = new ConcurrentHashMap<>();

    public boolean validateTableAndOperationCache(String table, ILogEntry entry) {
        LogCacheForOperationAndDaoMethods operationAndDaoMethodsCache = tableAndOperationCache.get(table);

        if (operationAndDaoMethodsCache == null) {
            synchronized (tableAndOperationCache) {
                operationAndDaoMethodsCache = tableAndOperationCache.get(table);
                if (operationAndDaoMethodsCache == null) {
                    operationAndDaoMethodsCache = new LogCacheForOperationAndDaoMethods();
                    tableAndOperationCache.put(table, operationAndDaoMethodsCache);
                }
            }
        }

        return operationAndDaoMethodsCache.validateOperationAndDaoMethodsCache(entry);
    }

    protected Map<String, LogCacheForOperationAndDaoMethods> getTableAndOperationCache() {
        return tableAndOperationCache;
    }
}

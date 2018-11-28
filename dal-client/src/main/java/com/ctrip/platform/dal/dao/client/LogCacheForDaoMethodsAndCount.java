package com.ctrip.platform.dal.dao.client;

import java.util.concurrent.ConcurrentHashMap;

public class LogCacheForDaoMethodsAndCount {
    private ConcurrentHashMap<String, Integer> methodsAnCountCache = new ConcurrentHashMap<>();
    private int logNumberInEachBatch;
    private Integer samplingRate;
    private final static Integer MAX_METHODS_SIZE = 100;

    public LogCacheForDaoMethodsAndCount() {
        samplingRate = LoggerAdapter.samplingRate;
        logNumberInEachBatch = 100 / samplingRate;
    }

    private void addDaoMethodAndCountCache(LogEntry entry) {
        String daoMethod = entry.getSource();
        Integer count = methodsAnCountCache.get(daoMethod);
        if (count == null)
            methodsAnCountCache.put(daoMethod, 1);
        else
            methodsAnCountCache.put(daoMethod, ++count);
    }

    public boolean validateMethodAndCountCache(LogEntry entry) {
        String method = entry.getSource();

        if (!methodsAnCountCache.contains(method)) {
            if (methodsAnCountCache.size() >= MAX_METHODS_SIZE)
                return true;
            addDaoMethodAndCountCache(entry);
            return true;
        }

        Integer count = methodsAnCountCache.get(method);
        if (count >= logNumberInEachBatch) {
            methodsAnCountCache.remove(method);
        }
        return false;
    }
}

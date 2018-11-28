package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.DalEventEnum;

import java.util.concurrent.ConcurrentHashMap;

public class LogCacheForOperationAndDaoMethods {
    private ConcurrentHashMap<String, LogCacheForDaoMethodsAndCount> operationAndDaoMethodCache = new ConcurrentHashMap<>();
    private final static String QUERY = "query";
    private final static String UPDATE = "update";
    private final static String CALL = "call";
    private final static String OTHERS = "others";


    public LogCacheForOperationAndDaoMethods() {
        operationAndDaoMethodCache.put(QUERY, new LogCacheForDaoMethodsAndCount());
        operationAndDaoMethodCache.put(UPDATE, new LogCacheForDaoMethodsAndCount());
        operationAndDaoMethodCache.put(CALL, new LogCacheForDaoMethodsAndCount());
        operationAndDaoMethodCache.put(OTHERS, new LogCacheForDaoMethodsAndCount());
    }

    public boolean validateOperationAndDaoMethodsCache(LogEntry entry) {
        LogCacheForDaoMethodsAndCount daoMethodsAndCountCache = getDaoMethod(entry.getEvent());
        return daoMethodsAndCountCache.validateMethodAndCountCache(entry);
    }

    private LogCacheForDaoMethodsAndCount getDaoMethod(DalEventEnum eventEnum) {
        if (eventEnum == DalEventEnum.QUERY) {
            return operationAndDaoMethodCache.get(QUERY);
        } else if (eventEnum == DalEventEnum.UPDATE_SIMPLE || eventEnum == DalEventEnum.UPDATE_KH || eventEnum == DalEventEnum.BATCH_UPDATE || eventEnum == DalEventEnum.BATCH_UPDATE_PARAM) {
            return operationAndDaoMethodCache.get(UPDATE);
        } else if (eventEnum == DalEventEnum.CALL || eventEnum == DalEventEnum.BATCH_CALL) {
            return operationAndDaoMethodCache.get(CALL);
        } else
            return operationAndDaoMethodCache.get(OTHERS);
    }
}

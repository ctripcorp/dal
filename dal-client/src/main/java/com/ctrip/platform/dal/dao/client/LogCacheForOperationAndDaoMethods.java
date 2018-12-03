package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.DalEventEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogCacheForOperationAndDaoMethods {
    private ConcurrentHashMap<String, LogCacheForDaoMethodsAndRandomNum> operationAndDaoMethodCache = new ConcurrentHashMap<>();
    private final static String QUERY = "query";
    private final static String UPDATE = "update";
    private final static String CALL = "call";
    private final static String OTHERS = "others";


    public boolean validateOperationAndDaoMethodsCache(ILogEntry entry) {
        String operation = getOperation(entry.getEvent());
        LogCacheForDaoMethodsAndRandomNum daoMethodsAndCountCache = operationAndDaoMethodCache.get(operation);

        if (daoMethodsAndCountCache == null) {
            synchronized (operationAndDaoMethodCache) {
                daoMethodsAndCountCache = operationAndDaoMethodCache.get(operation);
                if (daoMethodsAndCountCache == null) {
                    daoMethodsAndCountCache = new LogCacheForDaoMethodsAndRandomNum();
                    operationAndDaoMethodCache.put(operation, daoMethodsAndCountCache);
                }
            }
        }

        return daoMethodsAndCountCache.validateMethodAndCountCache(entry);
    }

    private String getOperation(DalEventEnum eventEnum) {
        if (eventEnum == DalEventEnum.QUERY) {
            return QUERY;
        } else if (eventEnum == DalEventEnum.UPDATE_SIMPLE || eventEnum == DalEventEnum.UPDATE_KH || eventEnum == DalEventEnum.BATCH_UPDATE || eventEnum == DalEventEnum.BATCH_UPDATE_PARAM) {
            return UPDATE;
        } else if (eventEnum == DalEventEnum.CALL || eventEnum == DalEventEnum.BATCH_CALL) {
            return CALL;
        } else
            return OTHERS;
    }

    protected Map<String, LogCacheForDaoMethodsAndRandomNum> getOperationAndDaoMethodCache() {
        return operationAndDaoMethodCache;
    }
}

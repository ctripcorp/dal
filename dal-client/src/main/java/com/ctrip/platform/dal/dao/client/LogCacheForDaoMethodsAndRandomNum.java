package com.ctrip.platform.dal.dao.client;


import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class LogCacheForDaoMethodsAndRandomNum {
    private Map<String, Random> methodsAndRandomNumCache = new ConcurrentHashMap<>();
    private Integer samplingRate = LoggerAdapter.samplingRate;
    private final static int MAX_METHODS_COUNT = 100;
    private final static int MAX_RANDOM_NUMBER = 100;

    public boolean validateMethodAndCountCache(ILogEntry entry) {
        String method = entry.getSource();
        Random random = methodsAndRandomNumCache.get(method);
        if (random == null) {
            synchronized (methodsAndRandomNumCache) {
                random = methodsAndRandomNumCache.get(method);
                if (random == null) {
                    if (methodsAndRandomNumCache.size() >= MAX_METHODS_COUNT)
                        return true;
                    random = new Random();
                    methodsAndRandomNumCache.put(method, random);
                    return true;
                }
            }
        }
        if (random.nextInt(MAX_RANDOM_NUMBER) >= samplingRate)
            return false;
        return true;
    }


    protected Map<String, Random> getMethodsAndRandomNumCache() {
        return methodsAndRandomNumCache;
    }

    protected void setSamplingRate(Integer samplingRate) {
        this.samplingRate = samplingRate;
    }
}

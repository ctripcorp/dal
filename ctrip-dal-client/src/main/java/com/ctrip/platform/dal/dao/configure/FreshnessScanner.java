package com.ctrip.platform.dal.dao.configure;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.util.internal.ConcurrentHashMap;

public class FreshnessScanner implements Runnable {
    private static final int INVALID = FreshnessHelper.INVALID;
    
    private Map<String, Map<String, Integer>> freshnessCache = new ConcurrentHashMap<>();

    public FreshnessScanner(Map<String, Map<String, Integer>> freshnessCache) {
        this.freshnessCache = freshnessCache;
    }
    
    @Override
    public void run() {
        for(String logicDbName: freshnessCache.keySet()) {
            Map<String, Integer> logicDbFreshnessMap = freshnessCache.get(logicDbName);
            Set<String> slaves = new HashSet<>(logicDbFreshnessMap.keySet());
            for(String slaveDbName: slaves) {
                int freshness = FreshnessHelper.getSlaveFreshness(logicDbName, slaveDbName);
                freshness = freshness > 0 ? freshness : INVALID;
                logicDbFreshnessMap.put(slaveDbName, freshness);
            }
        }
    }        
}

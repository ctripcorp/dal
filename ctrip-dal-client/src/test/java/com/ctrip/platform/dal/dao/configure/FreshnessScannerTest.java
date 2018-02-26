package com.ctrip.platform.dal.dao.configure;

import java.util.Map;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DataBase;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.configure.FreshnessScanner;

public class FreshnessScannerTest {
    @Test
    public void testRun() {
        Map<String, Map<String, Integer>> freshnessCache = new ConcurrentHashMap<>();
        
        DalConfigure configure = DalClientFactory.getDalConfigure();
        for(String logicDbName: configure.getDatabaseSetNames()) {
            Map<String, Integer> logicDbFreshnessMap = new ConcurrentHashMap<>();
            freshnessCache.put(logicDbName, logicDbFreshnessMap);
            
            DatabaseSet dbSet = configure.getDatabaseSet(logicDbName);
            for(Map.Entry<String, DataBase> dbEntry: dbSet.getDatabases().entrySet()) {
                if(!dbEntry.getValue().isMaster())
                    logicDbFreshnessMap.put(dbEntry.getValue().getConnectionString(), -1);
            }
        }
        
        FreshnessScanner test = new FreshnessScanner(freshnessCache);
        test.run();
        for(String logicDbName: freshnessCache.keySet()) {
            System.out.println("Logic DB: " + logicDbName);
            Map<String, Integer> logicDbFreshnessMap = freshnessCache.get(logicDbName);
            for(String slaveDbName: logicDbFreshnessMap.keySet()) {
                int freshness = logicDbFreshnessMap.get(slaveDbName);
                System.out.println("\tSlave freshness: " + freshness);
            }
            System.out.println();
        }
        
    }    
}

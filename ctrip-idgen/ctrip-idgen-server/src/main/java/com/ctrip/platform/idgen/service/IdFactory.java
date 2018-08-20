package com.ctrip.platform.idgen.service;

import com.ctrip.platform.idgen.config.Constants;
import com.ctrip.platform.idgen.config.PropertiesLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IdFactory {

    private static final ConcurrentMap<String, IdWorker> workerCache = new ConcurrentHashMap<>();

    // rename: getOrCreate
    public static IdWorker getIdWorker(String sequenceName) {
        if (!validate(sequenceName)) {
            return null;
        }

        // lock + double check
        IdWorker idWorker = workerCache.get(sequenceName);
        if (null == idWorker) {
            idWorker = new SnowflakeWorker(sequenceName);
            IdWorker previous = workerCache.putIfAbsent(sequenceName, idWorker);
            if (previous != null) {
                idWorker = previous;
            }
        }

        return idWorker;
    }

    private static boolean validate(String sequenceName) {
        if (null == sequenceName) {
            System.out.println("sequenceName is null");
            return false;
        }

        Map<String, String> registerProperties = PropertiesLoader.getRegisterProperties();
        if (null == registerProperties) {
            System.out.println("registerProperties is empty");
            return false;
        }
        if (!registerProperties.containsKey(sequenceName)){
            System.out.println("sequenceName [" + sequenceName + "] has not been registered");
            return false;
        }
        if (!Constants.SEQUENCENAME_REGISTER_STATE_ON.equalsIgnoreCase(registerProperties.get(sequenceName))) {
            System.out.println("sequenceName [" + sequenceName + "] is not enabled");
            return false;
        }
        return true;
    }

}

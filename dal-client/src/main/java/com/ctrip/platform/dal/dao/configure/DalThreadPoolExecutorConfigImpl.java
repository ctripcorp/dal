package com.ctrip.platform.dal.dao.configure;

import java.util.Map;

/**
 * @author c7ch23en
 */
public class DalThreadPoolExecutorConfigImpl implements DalThreadPoolExecutorConfig {

    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveSeconds;
    private final int globalMaxThreadsPerShard;
    private final Map<String, Integer> dbMaxThreadsPerShardMap;

    DalThreadPoolExecutorConfigImpl(int corePoolSize, int maxPoolSize, long keepAliveSeconds,
                                    int globalMaxThreadsPerShard, Map<String, Integer> dbMaxThreadsPerShardMap) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveSeconds = keepAliveSeconds;
        this.globalMaxThreadsPerShard = globalMaxThreadsPerShard;
        this.dbMaxThreadsPerShardMap = dbMaxThreadsPerShardMap;
    }

    @Override
    public int getCorePoolSize() {
        return corePoolSize;
    }

    @Override
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    @Override
    public long getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    @Override
    public int getMaxThreadsPerShard(String logicDbName) {
        Integer dbMaxThreadsPerShard = dbMaxThreadsPerShardMap.get(logicDbName);
        return dbMaxThreadsPerShard != null ? dbMaxThreadsPerShard : globalMaxThreadsPerShard;
    }

}

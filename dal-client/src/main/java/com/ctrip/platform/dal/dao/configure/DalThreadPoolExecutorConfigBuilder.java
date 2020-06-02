package com.ctrip.platform.dal.dao.configure;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class DalThreadPoolExecutorConfigBuilder {

    private int corePoolSize;
    private int maxPoolSize;
    private long keepAliveSeconds;
    private int globalMaxThreadsPerShard;
    private final Map<String, Integer> dbMaxThreadsPerShardMap = new HashMap<>();

    public DalThreadPoolExecutorConfigBuilder() {}

    public DalThreadPoolExecutorConfigBuilder setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0)
            throw new IllegalArgumentException("corePoolSize < 0, actual value is " + corePoolSize);
        this.corePoolSize = corePoolSize;
        return this;
    }

    public DalThreadPoolExecutorConfigBuilder setMaxPoolSize(int maxPoolSize) {
        if (maxPoolSize <= 0)
            throw new IllegalArgumentException("maxPoolSize <= 0, actual value is " + maxPoolSize);
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public DalThreadPoolExecutorConfigBuilder setKeepAliveSeconds(long keepAliveSeconds) {
        if (keepAliveSeconds < 0)
            throw new IllegalArgumentException("keepAliveSeconds < 0, actual value is " + keepAliveSeconds);
        this.keepAliveSeconds = keepAliveSeconds;
        return this;
    }

    public DalThreadPoolExecutorConfigBuilder setMaxThreadsPerShard(int maxThreadsPerShard) {
        if (maxThreadsPerShard < 0)
            throw new IllegalArgumentException("maxThreadsPerShard < 0, actual value is " + maxThreadsPerShard);
        globalMaxThreadsPerShard = maxThreadsPerShard;
        return this;
    }

    public DalThreadPoolExecutorConfigBuilder setMaxThreadsPerShard(String logicDbName, int maxThreadsPerShard) {
        if (maxThreadsPerShard < 0)
            throw new IllegalArgumentException("maxThreadsPerShard < 0, actual value is " + maxThreadsPerShard);
        dbMaxThreadsPerShardMap.put(logicDbName, maxThreadsPerShard);
        return this;
    }

    public DalThreadPoolExecutorConfig build() {
        if (maxPoolSize < corePoolSize)
            throw new IllegalArgumentException("maxPoolSize < corePoolSize, maxPoolSize is " + maxPoolSize +
                    ", corePoolSize is " + corePoolSize);
        return new DalThreadPoolExecutorConfigImpl(corePoolSize, maxPoolSize, keepAliveSeconds,
                globalMaxThreadsPerShard, dbMaxThreadsPerShardMap);
    }

}

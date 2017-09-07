package com.ctrip.platform.dal.dao.configure;

import org.apache.tomcat.jdbc.pool.PoolProperties;

@Deprecated
public class DatabasePoolConfig {
    private PoolProperties poolProperties;

    public DatabasePoolConfig(PoolProperties poolProperties) {
        this.poolProperties = poolProperties;
    }

    public PoolProperties getPoolProperties() {
        return poolProperties;
    }
}

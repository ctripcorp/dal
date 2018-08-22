package com.ctrip.platform.idgen.client;

import com.ctrip.platform.dal.sharding.idgen.IdGenerator;

public class SnowflakeIdGenerator implements IdGenerator {

    // cat transaction
    @Override
    public Number nextId() {
        return nextId("testName1");
    }

    public Number nextId(String sequenceName) {
        IdPool idPool = PoolManager.getPool(sequenceName);
        if (null == idPool) {
            return null;
        }
        return idPool.getId();
    }

}

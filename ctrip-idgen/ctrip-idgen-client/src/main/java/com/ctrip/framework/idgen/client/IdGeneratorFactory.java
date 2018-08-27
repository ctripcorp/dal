package com.ctrip.framework.idgen.client;

import com.ctrip.framework.idgen.client.generator.DynamicIdGenerator;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IdGeneratorFactory {
    private volatile static IdGeneratorFactory factory = null;
    private static final Object object = new Object();

    private final ConcurrentMap<String, LongIdGenerator> idGeneratorCache = new ConcurrentHashMap<>();

    public static IdGeneratorFactory getInstance() {
        if (null == factory) {
            synchronized (object) {
                if (null == factory) {
                    factory = new IdGeneratorFactory();
                }
            }
        }
        return factory;
    }

    public LongIdGenerator getOrCreateLongIdGenerator(String sequenceName) {
        LongIdGenerator idGenerator = idGeneratorCache.get(sequenceName);
        if (null == idGenerator) {
            synchronized (this) {
                idGenerator = idGeneratorCache.get(sequenceName);
                if (null == idGenerator) {
                    idGenerator = createLongIdGenerator(sequenceName);
                    idGeneratorCache.put(sequenceName, idGenerator);
                }
            }
        }
        return idGenerator;
    }

    private LongIdGenerator createLongIdGenerator(String sequenceName) {
        LongIdGenerator idGenerator = new DynamicIdGenerator(sequenceName);
        ((DynamicIdGenerator) idGenerator).fetchPool();
        return idGenerator;
    }

}

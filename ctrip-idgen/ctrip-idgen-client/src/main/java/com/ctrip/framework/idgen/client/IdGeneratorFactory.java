package com.ctrip.framework.idgen.client;

import com.ctrip.framework.idgen.client.generator.DynamicIdGenerator;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IdGeneratorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGeneratorFactory.class);
    private volatile static IdGeneratorFactory factory = null;
    private static final Object object = new Object();

    private final ConcurrentMap<String, IdGenerator> idGeneratorCache = new ConcurrentHashMap<>();

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

    public IdGenerator getOrCreateIdGenerator(String sequenceName) {
        IdGenerator idGenerator = idGeneratorCache.get(sequenceName);
        if (null == idGenerator) {
            synchronized (this) {
                idGenerator = idGeneratorCache.get(sequenceName);
                if (null == idGenerator) {
                    idGenerator = createIdGenerator(sequenceName);
                    idGeneratorCache.put(sequenceName, idGenerator);
                }
            }
        }
        return idGenerator;
    }

    private IdGenerator createIdGenerator(String sequenceName) {
        IdGenerator idGenerator = new DynamicIdGenerator(sequenceName);
        ((DynamicIdGenerator) idGenerator).initialize();
        return idGenerator;
    }

}

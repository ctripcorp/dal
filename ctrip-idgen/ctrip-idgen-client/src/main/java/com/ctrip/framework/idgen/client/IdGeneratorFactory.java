package com.ctrip.framework.idgen.client;

import com.ctrip.framework.idgen.client.common.Version;
import com.ctrip.framework.idgen.client.constant.CatConstants;
import com.ctrip.framework.idgen.client.generator.DynamicIdGenerator;
import com.ctrip.framework.idgen.client.log.IdGenLogger;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IdGeneratorFactory {

    private volatile static IdGeneratorFactory factory = null;
    private static final Object object = new Object();

    private final ConcurrentMap<String, LongIdGenerator> idGeneratorCache = new ConcurrentHashMap<>();

    private IdGeneratorFactory() {}

    public static IdGeneratorFactory getInstance() {
        if (null == factory) {
            synchronized (object) {
                if (null == factory) {
                    factory = new IdGeneratorFactory();
                    factory.initialize();
                }
            }
        }
        return factory;
    }

    private void initialize() {
        new Version().initialize();
        IdGenLogger.registerVersion();
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
        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_ID_GENERATOR_FACTORY, "createLongIdGenerator");
        IdGenLogger.logVersion();
        IdGenLogger.logEvent(CatConstants.TYPE_SEQUENCE_NAME, sequenceName);
        try {
            DynamicIdGenerator idGenerator = new DynamicIdGenerator(sequenceName);
            idGenerator.initialize();
            transaction.setStatus(CatConstants.STATUS_SUCCESS);
            return idGenerator;
        } catch (Exception e) {
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }
    }

}

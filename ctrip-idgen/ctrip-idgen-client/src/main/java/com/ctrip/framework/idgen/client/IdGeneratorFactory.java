package com.ctrip.framework.idgen.client;

import com.ctrip.framework.idgen.client.common.Version;
import com.ctrip.framework.idgen.client.generator.DynamicIdGenerator;
import com.ctrip.framework.idgen.client.log.CatConstants;
import com.ctrip.framework.idgen.client.log.IdGenLogger;
import com.ctrip.framework.idgen.client.service.IServiceManager;
import com.ctrip.framework.idgen.client.service.ServiceManager;
import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorFactory;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IdGeneratorFactory implements IIdGeneratorFactory {

    private volatile static IdGeneratorFactory factory = null;

    private final Map<String, LongIdGenerator> idGeneratorCache = new ConcurrentHashMap<>();
    private final IServiceManager service = new ServiceManager();

    private IdGeneratorFactory() {}

    public static IdGeneratorFactory getInstance() {
        if (null == factory) {
            synchronized (IdGeneratorFactory.class) {
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

    @Override
    public IdGenerator getIdGenerator(String sequenceName) {
        return getOrCreateLongIdGenerator(sequenceName);
    }

    public LongIdGenerator getOrCreateLongIdGenerator(String sequenceName) {
        LongIdGenerator idGenerator = idGeneratorCache.get(sequenceName);
        if (null == idGenerator) {
            synchronized (idGeneratorCache) {
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
        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_CREATE, sequenceName);
        try {
            DynamicIdGenerator idGenerator = new DynamicIdGenerator(sequenceName, service);
            idGenerator.initialize();
            transaction.setStatus(Transaction.SUCCESS);
            return idGenerator;
        } catch (Exception e) {
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }
    }

    @Override
    public int getOrder() {
        return 100;
    }

}

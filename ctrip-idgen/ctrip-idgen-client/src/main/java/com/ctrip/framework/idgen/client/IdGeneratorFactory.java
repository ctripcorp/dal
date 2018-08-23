package com.ctrip.framework.idgen.client;

import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
import com.ctrip.platform.idgen.service.api.IdGenRequestType;
import com.ctrip.platform.idgen.service.api.IdGenResponseType;
import com.ctrip.platform.idgen.service.api.IdGenerateService;
import com.ctrip.platform.idgen.service.api.IdSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        IdGenRequestType request = new IdGenRequestType(sequenceName,
                PrefetchStrategy.REQUESTSIZE_DEFAULT_VALUE, PrefetchStrategy.TIMEOUTMILLIS_DEFAULT_VALUE);

        IdGenerateService service = ServiceManager.getInstance().getIdGenServiceInstance();
        if (null == service) {
            throw new RuntimeException("Get IdGenerateService failed");
        }

        IdGenResponseType response = service.fetchIdPool(request);
        if (null == response) {
            throw new RuntimeException("Get IdGenerateService response failed");
        }

        List<IdSegment> segments = response.getIdSegments();
        if (null == segments || segments.isEmpty()) {
            throw new RuntimeException("Get IdSegments failed");
        }

        return new DynamicIdGenerator(sequenceName);
    }

}

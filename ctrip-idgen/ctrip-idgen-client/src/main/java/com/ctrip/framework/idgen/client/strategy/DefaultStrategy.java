package com.ctrip.framework.idgen.client.strategy;

import com.ctrip.framework.idgen.client.generator.DynamicIdGenerator;
import com.ctrip.framework.idgen.client.generator.StaticIdGenerator;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Iterator;

public class DefaultStrategy implements PrefetchStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStrategy.class);
    private static final int PERCENTAGE_THRESHOLD = 40;

    private long initialSize = 0;
    private long remainedSize = 0;

    @Override
    public int getSuggestedRequestSize() {
        return REQUESTSIZE_DEFAULT_VALUE;
    }

    @Override
    public int getSuggestedTimeoutMillis() {
        return TIMEOUTMILLIS_DEFAULT_VALUE;
    }

    public void decreaseRemainedSize() {
        if (remainedSize > 0) {
            remainedSize--;
        }
    }

    public void initialize(Deque<IdGenerator> idGenerators) {
        Iterator<IdGenerator> iterator = idGenerators.iterator();
        long refreshedSize = 0;
        while (iterator.hasNext()) {
            IdGenerator idGenerator = iterator.next();
            if (idGenerator != null) {
                refreshedSize += ((StaticIdGenerator) idGenerator).getRemainedSize();
            }
        }
        initialSize = refreshedSize;
        remainedSize = initialSize;
        LOGGER.info("DynamicIdGenerator refreshed size: " + remainedSize);
    }

    @Override
    public boolean checkIfNeedPrefetch() {
        return (remainedSize * 100 < initialSize * PERCENTAGE_THRESHOLD);
    }

}

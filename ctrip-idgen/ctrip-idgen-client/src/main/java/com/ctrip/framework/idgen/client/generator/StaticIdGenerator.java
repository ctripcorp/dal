package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.constant.CatConstants;
import com.ctrip.framework.idgen.client.log.IdGenLogger;
import com.ctrip.framework.idgen.client.service.ServiceManager;
import com.ctrip.framework.idgen.client.strategy.DefaultStrategy;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import com.ctrip.platform.idgen.service.api.IdSegment;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StaticIdGenerator implements LongIdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticIdGenerator.class);

    private final String sequenceName;
    private final Deque<IdSegment> idSegments = new ConcurrentLinkedDeque<>();
    private long initialSize = 0;
    private long remainedSize = 0;
    private long currentId = -1;
    private final PrefetchStrategy strategy;

    public StaticIdGenerator(String sequenceName) {
        this(sequenceName, new DefaultStrategy());
    }

    public StaticIdGenerator(String sequenceName, final PrefetchStrategy strategy) {
        this.sequenceName = sequenceName;
        this.strategy = (strategy != null) ? strategy : new DefaultStrategy();
    }

    public void initialize() {
        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_STATIC_GENERATOR, "initialize");
        try {
            importPool(fetchPool());
            IdGenLogger.logSizeEvent(CatConstants.TYPE_FETCH_POOL_SIZE, initialSize);
            transaction.setStatus(CatConstants.STATUS_SUCCESS);
        } catch (Exception e) {
            LOGGER.warn("StaticIdGenerator initialization failed.", e);
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }
    }

    private List<IdSegment> fetchPool() {
        int requestSize = strategy.getSuggestedRequestSize();
        int timeoutMillis = strategy.getSuggestedTimeoutMillis();
        List<IdSegment> pool = ServiceManager.getInstance().fetchIdPool(sequenceName, requestSize, timeoutMillis);
        if (null == pool || pool.isEmpty()) {
            throw new RuntimeException("Failed to fetch id pool (sequence name: '" + sequenceName + "')");
        }
        return pool;
    }

    private void importPool(List<IdSegment> pool) {
        for (IdSegment segment : pool) {
            idSegments.addLast(segment);
            initialSize += segment.getEnd().longValue() - segment.getStart().longValue() + 1;
        }
        remainedSize = initialSize;
    }

    @Override
    public synchronized Long nextId() {
        IdSegment first = idSegments.peekFirst();
        if (null == first) {
            return null;
        }

        if (currentId >= first.getStart().longValue() && currentId < first.getEnd().longValue()) {
            currentId++;
        } else if (currentId < first.getStart().longValue()) {
            currentId = first.getStart().longValue();
        } else {
            idSegments.removeFirst();
            first = idSegments.peekFirst();
            if (null == first) {
                return null;
            }
            currentId = first.getStart().longValue();
        }

        remainedSize--;
        return currentId;
    }

    public long getInitialSize() {
        return initialSize;
    }

    public long getRemainedSize() {
        return remainedSize;
    }

    public Deque<IdSegment> getIdSegments() {
        return idSegments;
    }

    public long getCurrentId() {
        return currentId;
    }

}

package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.log.CatConstants;
import com.ctrip.framework.idgen.client.log.IdGenLogger;
import com.ctrip.framework.idgen.client.service.IServiceManager;
import com.ctrip.framework.idgen.client.strategy.DefaultStrategy;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import com.ctrip.platform.idgen.service.api.IdSegment;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StaticIdGenerator implements LongIdGenerator {

    private final String sequenceName;
    private final Deque<IdSegment> idSegments = new ConcurrentLinkedDeque<>();
    private long initialSize = 0;
    private long remainedSize = 0;
    private long currentId = -1;
    private long maxId = -1;
    private final PrefetchStrategy strategy;
    private final IServiceManager service;

    public StaticIdGenerator(String sequenceName, IServiceManager service) {
        this(sequenceName, new DefaultStrategy(), service);
    }

    public StaticIdGenerator(String sequenceName, final PrefetchStrategy strategy, IServiceManager service) {
        this.sequenceName = sequenceName;
        this.strategy = (strategy != null) ? strategy : new DefaultStrategy();
        this.service = service;
    }

    public void initialize() {
        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_ROOT,
                CatConstants.NAME_STATIC_GENERATOR + ":initialize");
        try {
            List<IdSegment> idPool = service.fetchIdPool(sequenceName,
                    strategy.getSuggestedRequestSize(), strategy.getSuggestedTimeoutMillis());
            importIdPool(idPool);
            IdGenLogger.logSizeEvent(CatConstants.NAME_STATIC_GENERATOR + ":initialSize", initialSize);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }
    }

    private void importIdPool(List<IdSegment> idPool) {
        if (null == idPool) {
            return;
        }
        for (IdSegment segment : idPool) {
            idSegments.addLast(segment);
            initialSize += segment.getEnd().longValue() - segment.getStart().longValue() + 1;
            maxId = segment.getEnd().longValue();
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

    public boolean checkIncrement(StaticIdGenerator generator) {
        if (null == generator) {
            return true;
        }
        IdSegment firstSegment = this.idSegments.peekFirst();
        if (null == firstSegment) {
            return false;
        }
        return firstSegment.getStart().longValue() > generator.getMaxId();
    }

    public long getRemainedSize() {
        return remainedSize;
    }

    public long getMaxId() {
        return maxId;
    }

}

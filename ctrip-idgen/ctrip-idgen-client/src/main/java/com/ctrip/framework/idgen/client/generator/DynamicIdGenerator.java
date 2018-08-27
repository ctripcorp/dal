package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.exception.AwaitTimeoutException;
import com.ctrip.framework.idgen.client.strategy.DefaultStrategy;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DynamicIdGenerator implements LongIdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicIdGenerator.class);

    private static final String CAT_TYPE = "IdGen";

    private static final int CLIENT_TIMEOUTMILLIS_DEFAULT_VALUE = 1500;

    private final String sequenceName;
    private final Deque<LongIdGenerator> staticGeneratorQueue = new ConcurrentLinkedDeque<>();
    private final PrefetchStrategy strategy;
    private AtomicBoolean isPrefetching = new AtomicBoolean(false);
    private ExecutorService prefetchExecutorService = Executors.newSingleThreadExecutor();

    public DynamicIdGenerator(String sequenceName) {
        this(sequenceName, new DefaultStrategy());
    }

    public DynamicIdGenerator(String sequenceName, PrefetchStrategy strategy) {
        this.sequenceName = sequenceName;
        this.strategy = (strategy != null) ? strategy : new DefaultStrategy();
    }

    public void fetchPool() {
        StaticIdGenerator staticGenerator = new StaticIdGenerator(sequenceName, strategy);
        staticGenerator.initialize();
        ((DefaultStrategy) strategy).increase(staticGenerator.getRemainedSize());
        addStaticGenerator(staticGenerator);
        isPrefetching.set(false);
    }

    @Override
    public Long nextId() {

        Transaction transaction = Cat.newTransaction(CAT_TYPE, "nextId");
        try {
            Long id = blockingNextId(CLIENT_TIMEOUTMILLIS_DEFAULT_VALUE);
            transaction.setStatus(Transaction.SUCCESS);
            return id;
        } catch (Exception e) {
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }
    }

    public Long blockingNextId(int awaitTimeoutMillis) {
        Long id = null;
        long timeEnd = getTime() + awaitTimeoutMillis;
        int attempt = 0;
        do {
            id = simpleNextId();
            attempt++;
            if (id != null) {
                break;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                LOGGER.warn("blockingNextId sleep InterruptedException", e);
            }
        } while (getTime() < timeEnd);

        if (null == id) {
            Cat.logError("Client timeout after " + attempt + " attempts", "AwaitTimeoutException");
            throw new AwaitTimeoutException("Client timeout after " + attempt + " attempts");
        }
        return id;
    }

    public Long simpleNextId() {
        Long id = null;
        Iterator<LongIdGenerator> iterator = staticGeneratorQueue.iterator();
        while (iterator.hasNext()) {
            id = iterator.next().nextId();
            if (id != null) {
                ((DefaultStrategy) strategy).decrease();
                break;
            } else {
                iterator.remove();
            }
        }

        prefetchIfNecessary();

        return id;
    }

    public void prefetchIfNecessary() {
        if (isPrefetching.get()) {
            return;
        }
        if (strategy.checkIfNeedPrefetch() && isPrefetching.compareAndSet(false, true)) {
            prefetchExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        fetchPool();
                    } catch (Throwable t) {
                        LOGGER.warn("Prefetch failed. SequenceName: " + sequenceName, t);
                    } finally {
                        isPrefetching.set(false);
                    }
                }
            });
        }
    }

    private long getTime() {
        return System.currentTimeMillis();
    }

    public void addStaticGenerator(LongIdGenerator staticGenerator) {
        staticGeneratorQueue.addLast(staticGenerator);
    }

    public String getSequenceName() {
        return sequenceName;
    }

    protected Deque<LongIdGenerator> getStaticGeneratorQueue() {
        return staticGeneratorQueue;
    }
}

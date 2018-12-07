package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.exception.ClientTimeoutException;
import com.ctrip.framework.idgen.client.log.CatConstants;
import com.ctrip.framework.idgen.client.log.IdGenLogger;
import com.ctrip.framework.idgen.client.service.IServiceManager;
import com.ctrip.framework.idgen.client.strategy.AbstractStrategy;
import com.ctrip.framework.idgen.client.strategy.DynamicStrategy;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import com.dianping.cat.Cat;
import com.dianping.cat.message.ForkedTransaction;
import com.dianping.cat.message.Transaction;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DynamicIdGenerator implements LongIdGenerator {

    private static final int PREFETCH_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int CLIENT_TIMEOUT_MILLIS_DEFAULT_VALUE = 1500;
    private static final long FETCH_ID_RETRY_BASE_INTERVAL = 10;

    private final String sequenceName;
    private final Deque<LongIdGenerator> staticGeneratorQueue = new ConcurrentLinkedDeque<>();
    private final AbstractStrategy strategy;
    private final IServiceManager service;
    private AtomicBoolean isFetching = new AtomicBoolean(false);
    private ExecutorService prefetchExecutor = Executors.newFixedThreadPool(PREFETCH_THREAD_POOL_SIZE);

    public DynamicIdGenerator(String sequenceName, IServiceManager service) {
        this(sequenceName, new DynamicStrategy(), service);
    }

    public DynamicIdGenerator(String sequenceName, AbstractStrategy strategy, IServiceManager service) {
        this.sequenceName = sequenceName;
        this.strategy = (strategy != null) ? strategy : new DynamicStrategy();
        this.service = service;
    }

    public void initialize() {
        strategy.initialize();
        fetchIdPool();
    }

    @Override
    public Long nextId() {
        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_NEXT_ID, sequenceName);
        IdGenLogger.logVersion();
        try {
            Long id = nextIdWithActiveFetch(CLIENT_TIMEOUT_MILLIS_DEFAULT_VALUE);
            transaction.setStatus(Transaction.SUCCESS);
            return id;
        } catch (Exception e) {
            IdGenLogger.logError("Get id failed", e);
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }
    }

    protected Long simpleNextId() {
        Long id = null;
        Iterator<LongIdGenerator> iterator = staticGeneratorQueue.iterator();
        while (iterator.hasNext()) {
            id = iterator.next().nextId();
            if (id != null) {
                strategy.consume();
                break;
            } else {
                iterator.remove();
            }
        }
        prefetchIfNecessary();
        return id;
    }

    protected Long nextIdWithActiveFetch(int timeoutMillis) {
        long endTime = getMilliTime() + timeoutMillis;
        Long id = simpleNextId();
        if (id != null) {
            return id;
        }

        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_ACTIVE_FETCH, sequenceName);
        try {
            int retries = 0;
            while (getMilliTime() < endTime) {
                retries++;
                try {
                    fetchIdPool();
                    id = simpleNextId();
                    if (id != null) {
                        break;
                    }
                    Thread.sleep(getRetryInterval(FETCH_ID_RETRY_BASE_INTERVAL, retries));
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                } catch (Exception e2) {
                    IdGenLogger.logError(null, e2);
                }
            }

            IdGenLogger.logSizeEvent(CatConstants.TYPE_ACTIVE_FETCH + ":attempts", retries);
            if (null == id) {
                throw new ClientTimeoutException(String.format("IdGen client timeout after %d retries", retries));
            }
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable t) {
            transaction.setStatus(t);
            throw t;
        } finally {
            transaction.complete();
        }

        return id;
    }

    protected boolean prefetchIfNecessary() {
        if (!isFetching.get() && strategy.checkIfNeedPrefetch() && isFetching.compareAndSet(false, true)) {
            final ForkedTransaction transaction = Cat.newForkedTransaction(CatConstants.TYPE_PREFETCH, sequenceName);
            prefetchExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        transaction.fork();
                        fetchIdPool();
                        transaction.setStatus(Transaction.SUCCESS);
                    } catch (Exception e) {
                        IdGenLogger.logError("Prefetch failed", e);
                        transaction.setStatus(e);
                    } finally {
                        isFetching.set(false);
                        transaction.complete();
                    }
                }
            });
            return true;
        }
        return false;
    }

    private void fetchIdPool() {
        logPoolStatistics();
        StaticIdGenerator staticGenerator = new StaticIdGenerator(sequenceName, strategy, service);
        staticGenerator.initialize();
        if (staticGenerator.checkIncrement((StaticIdGenerator) staticGeneratorQueue.peekLast())) {
            strategy.provide(staticGenerator.getRemainedSize());
            staticGeneratorQueue.addLast(staticGenerator);
        }
    }

    private void logPoolStatistics() {
        IdGenLogger.logSizeEvent(CatConstants.TYPE_ID_POOL + ":remainedSize",
                strategy.getRemainedSize());
        if (strategy instanceof DynamicStrategy) {
            IdGenLogger.logSizeEvent(CatConstants.TYPE_ID_POOL + ":qps",
                    ((DynamicStrategy) strategy).getQps());
        }
    }

    private long getMilliTime() {
        return System.currentTimeMillis();
    }

    private long getRetryInterval(long baseInterval, int attempts) {
        if (attempts <= 1) {
            return baseInterval;
        } else {
            return baseInterval << (attempts - 1);
        }
    }

    protected String getSequenceName() {
        return sequenceName;
    }

}

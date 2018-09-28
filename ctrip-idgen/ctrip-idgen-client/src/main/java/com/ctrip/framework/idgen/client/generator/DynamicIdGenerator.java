package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.constant.CatConstants;
import com.ctrip.framework.idgen.client.exception.ClientTimeoutException;
import com.ctrip.framework.idgen.client.log.IdGenLogger;
import com.ctrip.framework.idgen.client.service.ServiceManager;
import com.ctrip.framework.idgen.client.strategy.AbstractStrategy;
import com.ctrip.framework.idgen.client.strategy.DynamicStrategy;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
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
    private final String nextIdCatName;
    private final String prefetchCatName;
    private final Deque<LongIdGenerator> staticGeneratorQueue = new ConcurrentLinkedDeque<>();
    private final PrefetchStrategy strategy;
    private AtomicBoolean isFetching = new AtomicBoolean(false);
    private ExecutorService prefetchExecutor = Executors.newFixedThreadPool(PREFETCH_THREAD_POOL_SIZE);

    public DynamicIdGenerator(String sequenceName) {
        this(sequenceName, new DynamicStrategy());
    }

    public DynamicIdGenerator(String sequenceName, PrefetchStrategy strategy) {
        this.sequenceName = sequenceName;
        this.nextIdCatName = "nextId:" + sequenceName;
        this.prefetchCatName = "prefetch:" + sequenceName;
        this.strategy = (strategy != null) ? strategy : new DynamicStrategy();
    }

    public void initialize() {
        if (strategy instanceof DynamicStrategy) {
            ((DynamicStrategy) strategy).initialize();
        }
        prefetch();
    }

    @Override
    public Long nextId() {
        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_DYNAMIC_GENERATOR, nextIdCatName);
        logVersion();
        try {
            Long id = simpleNextId();
            if (null == id) {
                id = activeFetch(CLIENT_TIMEOUT_MILLIS_DEFAULT_VALUE);
            }
            transaction.setStatus(CatConstants.STATUS_SUCCESS);
            return id;
        } catch (Exception e) {
            IdGenLogger.logError("Next id failed", e);
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

    protected Long activeFetch(int timeoutMillis) {
        long endTime = getMilliTime() + timeoutMillis;
        Long id = null;
        int retries = 0;
        do {
            retries++;
            try {
                id = fetchSingleId();
                Thread.sleep(getRetryInterval(FETCH_ID_RETRY_BASE_INTERVAL, retries));
            } catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
            } catch (Exception e2) {
                IdGenLogger.logError(null, e2);
            }
        } while (null == id && getMilliTime() < endTime);

        IdGenLogger.logSizeEvent(CatConstants.TYPE_ACTIVE_FETCH_RETRIES, retries);
        if (null == id) {
            throw new ClientTimeoutException(String.format("IdGen client timeout after %d retries", retries));
        }

        return id;
    }

    protected Long fetchSingleId() {
        return (Long) ServiceManager.getInstance().fetchId(sequenceName, strategy.getSuggestedTimeoutMillis());
    }

    private void prefetch() {
        StaticIdGenerator staticGenerator = createStaticGenerator();
        strategy.provide(staticGenerator.getRemainedSize());
        staticGeneratorQueue.addLast(staticGenerator);
    }

    protected StaticIdGenerator createStaticGenerator() {
        StaticIdGenerator staticGenerator = new StaticIdGenerator(sequenceName, strategy);
        staticGenerator.initialize();
        return staticGenerator;
    }

    protected boolean prefetchIfNecessary() {
        if (!isFetching.get() && strategy.checkIfNeedPrefetch() && isFetching.compareAndSet(false, true)) {
            final ForkedTransaction transaction = Cat.newForkedTransaction(CatConstants.TYPE_DYNAMIC_GENERATOR, prefetchCatName);
            prefetchExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        transaction.fork();
                        logPoolStatistics();
                        prefetch();
                        transaction.setStatus(CatConstants.STATUS_SUCCESS);
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

    private void logPoolStatistics() {
        if (strategy instanceof AbstractStrategy) {
            String remainedSize = String.valueOf(((AbstractStrategy) strategy).getRemainedSize());
            IdGenLogger.logEvent(CatConstants.TYPE_REMAINED_POOL_SIZE, remainedSize);
            if (strategy instanceof DynamicStrategy) {
                String qps = String.valueOf(((DynamicStrategy) strategy).getQps());
                IdGenLogger.logEvent(CatConstants.TYPE_POOL_QPS, qps);
            }
        }
    }

    private void logVersion() {
        IdGenLogger.logVersion();
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

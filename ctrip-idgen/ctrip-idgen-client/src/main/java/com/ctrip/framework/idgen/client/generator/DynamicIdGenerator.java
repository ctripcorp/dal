package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.constant.CatConstants;
import com.ctrip.framework.idgen.client.exception.IdGenTimeoutException;
import com.ctrip.framework.idgen.client.log.IdGenLogger;
import com.ctrip.framework.idgen.client.service.ServiceManager;
import com.ctrip.framework.idgen.client.strategy.AbstractStrategy;
import com.ctrip.framework.idgen.client.strategy.DefaultStrategy;
import com.ctrip.framework.idgen.client.strategy.DynamicStrategy;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
import com.dianping.cat.Cat;
import com.dianping.cat.message.ForkedTransaction;
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

    private static final int PREFETCH_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int CLIENT_TIMEOUT_MILLIS_DEFAULT_VALUE = 1500;
    private static final long FETCH_ID_RETRY_BASE_INTERVAL = 1;

    private final String sequenceName;
    private final Deque<LongIdGenerator> staticGeneratorQueue = new ConcurrentLinkedDeque<>();
    private final PrefetchStrategy strategy;
    private AtomicBoolean isFetching = new AtomicBoolean(false);
    private ExecutorService prefetchExecutor = Executors.newFixedThreadPool(PREFETCH_THREAD_POOL_SIZE);

    public DynamicIdGenerator(String sequenceName) {
        this(sequenceName, new DefaultStrategy());
    }

    public DynamicIdGenerator(String sequenceName, PrefetchStrategy strategy) {
        this.sequenceName = sequenceName;
        this.strategy = (strategy != null) ? strategy : new DefaultStrategy();
    }

    public void initialize() {
        prefetch();
    }

    @Override
    public Long nextId() {
        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_DYNAMIC_GENERATOR, "nextId");
        IdGenLogger.logVersion();
        IdGenLogger.logEvent(CatConstants.TYPE_SEQUENCE_NAME, sequenceName);
        try {
            Long id = simpleNextId();
            if (null == id) {
                id = activeFetch(CLIENT_TIMEOUT_MILLIS_DEFAULT_VALUE);
            }
            transaction.setStatus(CatConstants.STATUS_SUCCESS);
            return id;
        } catch (Exception e) {
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }
    }

    private Long simpleNextId() {
        Long id = null;
        Iterator<LongIdGenerator> iterator = staticGeneratorQueue.iterator();
        while (iterator.hasNext()) {
            id = iterator.next().nextId();
            if (id != null) {
                ((DefaultStrategy) strategy).consume();
                break;
            } else {
                iterator.remove();
            }
        }
        prefetchIfNecessary();
        return id;
    }

    private Long activeFetch(int timeoutMillis) {
        long endTime = getMilliTime() + timeoutMillis;
        Long id = null;
        int retries = 0;
        do {
            retries++;
            try {
                id = (Long) ServiceManager.getInstance().fetchId(sequenceName, strategy.getSuggestedTimeoutMillis());
                Thread.sleep(getRetryInterval(FETCH_ID_RETRY_BASE_INTERVAL, retries));
            } catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
            } catch (Exception e2) {
                IdGenLogger.logError(null, e2);
            }
        } while (null == id && getMilliTime() < endTime);

        IdGenLogger.logSizeEvent(CatConstants.TYPE_ACTIVE_FETCH_RETRIES, retries);
        if (null == id) {
            IdGenTimeoutException e = new IdGenTimeoutException(String.format("IdGen client timeout after %d retries", retries));
            IdGenLogger.logError(null, e);
            throw e;
        }

        return id;
    }

    private void prefetch() {
        StaticIdGenerator staticGenerator = new StaticIdGenerator(sequenceName, strategy);
        staticGenerator.initialize();
        ((DefaultStrategy) strategy).provide(staticGenerator.getRemainedSize());
        addStaticGenerator(staticGenerator);
    }

    private void prefetchIfNecessary() {
        if (isFetching.get()) {
            return;
        }
        if (strategy.checkIfNeedPrefetch() && isFetching.compareAndSet(false, true)) {
            final ForkedTransaction transaction = Cat.newForkedTransaction(CatConstants.TYPE_DYNAMIC_GENERATOR, "prefetch");
            prefetchExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        transaction.fork();
                        logTransactionForPrefetch();
                        prefetch();
                        transaction.setStatus(CatConstants.STATUS_SUCCESS);
                    } catch (Exception e) {
                        LOGGER.warn("Prefetch failed. SequenceName: " + sequenceName, e);
                        transaction.setStatus(e);
                    } finally {
                        isFetching.set(false);
                        transaction.complete();
                    }
                }
            });
        }
    }

    private void logTransactionForPrefetch() {
        IdGenLogger.logVersion();
        IdGenLogger.logEvent(CatConstants.TYPE_SEQUENCE_NAME, sequenceName);
        if (strategy instanceof DynamicStrategy) {
            String qps = String.valueOf(((DynamicStrategy) strategy).getQps());
            IdGenLogger.logEvent(CatConstants.TYPE_POOL_QPS, qps);
        }
        if (strategy instanceof AbstractStrategy) {
            String remainedSize = String.valueOf(((AbstractStrategy) strategy).getRemainedSize());
            IdGenLogger.logEvent(CatConstants.TYPE_REMAINED_POOL_SIZE, remainedSize);
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

    private void addStaticGenerator(LongIdGenerator staticGenerator) {
        staticGeneratorQueue.addLast(staticGenerator);
    }

}

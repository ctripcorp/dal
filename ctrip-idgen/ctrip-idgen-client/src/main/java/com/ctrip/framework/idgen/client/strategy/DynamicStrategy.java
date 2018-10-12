package com.ctrip.framework.idgen.client.strategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DynamicStrategy extends AbstractStrategy {

    private static final long QPS_CHECK_PERIOD_MILLIS = 5000;
    private static final long REMAINED_ENDURANCE_MILLIS = 200;
    private static final long PREFETCH_ENDURANCE_MILLIS = 800;
    private static final int REQUEST_SIZE_MIN_VALUE = 10;
    private static final int REQUEST_SIZE_MAX_VALUE = 5000;

    private AtomicLong consumedCount = new AtomicLong();
    private long lastCount;
    private long lastTime;
    private volatile long qps = 0;
    private AtomicLong qpsCheckTimes = new AtomicLong(0);
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private AtomicBoolean isInitialized = new AtomicBoolean(false);

    @Override
    public boolean initialize() {
        if (!isInitialized.get() && isInitialized.compareAndSet(false, true)) {
            consumedCount.set(0);
            lastCount = 0;
            lastTime = System.currentTimeMillis();
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    long now = System.currentTimeMillis();
                    long count = consumedCount.get();
                    qps = 1000 * (count - lastCount) / (now - lastTime);
                    qpsCheckTimes.incrementAndGet();
                    lastCount = count;
                    lastTime = now;
                }
            }, QPS_CHECK_PERIOD_MILLIS, QPS_CHECK_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }

    @Override
    public int getSuggestedRequestSize() {
        if (qpsCheckTimes.get() == 0) {
            return REQUEST_SIZE_DEFAULT_VALUE;
        }
        long size = qps * PREFETCH_ENDURANCE_MILLIS / 1000;
        if (size > 0 && size <= REQUEST_SIZE_MAX_VALUE) {
            return (int) size;
        } else if (size == 0) {
            return REQUEST_SIZE_MIN_VALUE;
        } else {
            return REQUEST_SIZE_MAX_VALUE;
        }
    }

    @Override
    public boolean checkIfNeedPrefetch() {
        if (remainedSize.get() == 0) {
            return true;
        }
        if (qps == 0) {
            return false;
        }
        return (1000 * remainedSize.get() / qps) < REMAINED_ENDURANCE_MILLIS;
    }

    @Override
    public void consume() {
        super.consume();
        consumedCount.incrementAndGet();
    }

    public long getQps() {
        return qps;
    }

}

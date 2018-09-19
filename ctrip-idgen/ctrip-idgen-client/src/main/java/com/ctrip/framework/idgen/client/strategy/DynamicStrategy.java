package com.ctrip.framework.idgen.client.strategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DynamicStrategy extends AbstractStrategy {

    private static final long QPS_CHECK_PERIOD_MILLIS = 500;
    private static final long REMAINED_ENDURANCE_MILLIS = 200;
    private static final long PREFETCH_ENDURANCE_MILLIS = 800;
    private static final int REQUEST_SIZE_MIN_VALUE = 1;

    private AtomicLong consumedCount = new AtomicLong();
    private volatile long lastTime;
    private volatile long qps = 0;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private AtomicBoolean isInitialized = new AtomicBoolean(false);

    @Override
    public void initialize() {
        if (isInitialized.get()) {
            return;
        }
        if (isInitialized.compareAndSet(false, true)) {
            consumedCount.set(0);
            lastTime = System.currentTimeMillis();
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    long duration = System.currentTimeMillis() - lastTime;
                    qps = 1000 * consumedCount.get() / duration;
                    consumedCount.set(0);
                    lastTime = System.currentTimeMillis();
                }
            }, 0, QPS_CHECK_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public int getSuggestedRequestSize() {
        long size = qps * PREFETCH_ENDURANCE_MILLIS / 1000;
        return (size > 0) ? (int) size : REQUEST_SIZE_MIN_VALUE;
    }

    @Override
    public boolean checkIfNeedPrefetch() {
        if (qps == 0) {
            return false;
        }
        return (1000 * remainedSize.get() / qps) < REMAINED_ENDURANCE_MILLIS;
    }

    @Override
    public void decrease() {
        super.decrease();
        consumedCount.incrementAndGet();
    }

    public long getQps() {
        return qps;
    }

}

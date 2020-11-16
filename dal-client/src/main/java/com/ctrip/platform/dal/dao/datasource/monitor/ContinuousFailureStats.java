package com.ctrip.platform.dal.dao.datasource.monitor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author c7ch23en
 */
public class ContinuousFailureStats {

    private final AtomicLong startTime = new AtomicLong(0);
    private final AtomicLong count = new AtomicLong(0);

    public void record() {
        synchronized (startTime) {
            startTime.compareAndSet(0, System.currentTimeMillis());
            count.incrementAndGet();
        }
    }

    public void clear() {
        synchronized (startTime) {
            startTime.set(0);
            count.set(0);
        }
    }

    public long getStartTime() {
        synchronized (startTime) {
            return startTime.get();
        }
    }

    public long getCount() {
        synchronized (startTime) {
            return count.get();
        }
    }

}

package com.ctrip.framework.idgen.client.strategy;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractStrategy implements PrefetchStrategy {

    protected AtomicLong remainedSize = new AtomicLong(0);

    abstract public void initialize();

    @Override
    public int getSuggestedTimeoutMillis() {
        return TIMEOUT_MILLIS_DEFAULT_VALUE;
    }

    public void consume() {
        for (;;) {
            long value = remainedSize.get();
            if (value > 0) {
                long newValue = value - 1;
                if (remainedSize.compareAndSet(value, newValue)) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    public void provide(long increment) {
        remainedSize.addAndGet(increment);
    }

    public long getRemainedSize() {
        return remainedSize.get();
    }

}

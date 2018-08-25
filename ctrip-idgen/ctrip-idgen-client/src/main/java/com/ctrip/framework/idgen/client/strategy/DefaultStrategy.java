package com.ctrip.framework.idgen.client.strategy;

import java.util.concurrent.atomic.AtomicLong;

public class DefaultStrategy implements PrefetchStrategy {

    private AtomicLong remainedSize = new AtomicLong(0);

    @Override
    public int getSuggestedRequestSize() {
        return REQUESTSIZE_DEFAULT_VALUE;
    }

    @Override
    public int getSuggestedTimeoutMillis() {
        return TIMEOUTMILLIS_DEFAULT_VALUE;
    }

    public void decrease() {
        while (true) {
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

    public void increase(long increment) {
        while (true) {
            long value = remainedSize.get();
            long newValue = value + increment;
            if (remainedSize.compareAndSet(value, newValue)) {
                return;
            }
        }
    }

    @Override
    public boolean checkIfNeedPrefetch() {
        return (remainedSize.get() < (getSuggestedRequestSize() >> 1));
    }

}

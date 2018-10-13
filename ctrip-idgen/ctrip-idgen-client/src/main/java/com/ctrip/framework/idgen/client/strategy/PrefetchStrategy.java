package com.ctrip.framework.idgen.client.strategy;

public interface PrefetchStrategy {

    int REQUEST_SIZE_DEFAULT_VALUE = 1000;
    int TIMEOUT_MILLIS_DEFAULT_VALUE = 800;

    int getSuggestedRequestSize();

    int getSuggestedTimeoutMillis();

    boolean checkIfNeedPrefetch();

    void consume();

    void provide(long increment);

}

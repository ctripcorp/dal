package com.ctrip.framework.idgen.client.strategy;

public interface PrefetchStrategy {

    int REQUESTSIZE_DEFAULT_VALUE = 2000;
    int TIMEOUTMILLIS_DEFAULT_VALUE = 800;

    int getSuggestedRequestSize();

    int getSuggestedTimeoutMillis();

    boolean checkIfNeedPrefetch();

}

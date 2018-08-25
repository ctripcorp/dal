package com.ctrip.framework.idgen.client.strategy;

public interface PrefetchStrategy {

    int REQUESTSIZE_DEFAULT_VALUE = 2000;
    int TIMEOUTMILLIS_DEFAULT_VALUE = 1500;

    int getSuggestedRequestSize();

    int getSuggestedTimeoutMillis();

    boolean checkIfNeedPrefetch();

}

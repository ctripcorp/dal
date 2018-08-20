package com.ctrip.platform.idgen.client;

public interface PoolManageStrategy {

    int DEFAULT_REQUEST_SIZE = 1000;
    int DEFAULT_TIMEOUT_MILLIS = 0;

    int getSuggestedRequestSize();

    int getSuggestedTimeoutMillis();

    boolean ifNeedExtendPool();

}

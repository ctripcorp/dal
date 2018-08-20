package com.ctrip.platform.idgen.client;

public class DefaultPoolManageStrategy implements PoolManageStrategy {

    @Override
    public int getSuggestedRequestSize() {
        return DEFAULT_REQUEST_SIZE;
    }

    @Override
    public int getSuggestedTimeoutMillis() {
        return DEFAULT_TIMEOUT_MILLIS;
    }

    @Override
    public boolean ifNeedExtendPool() {
        // to be continued...
        return false;
    }

}

package com.ctrip.framework.idgen.client.strategy;

public class DefaultStrategy extends AbstractStrategy {

    @Override
    public int getSuggestedRequestSize() {
        return REQUEST_SIZE_DEFAULT_VALUE;
    }

    @Override
    public boolean checkIfNeedPrefetch() {
        return remainedSize.get() < (getSuggestedRequestSize() >> 1);
    }

}

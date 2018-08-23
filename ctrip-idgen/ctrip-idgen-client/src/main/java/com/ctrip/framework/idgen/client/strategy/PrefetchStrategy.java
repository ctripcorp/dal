package com.ctrip.framework.idgen.client.strategy;

import com.ctrip.platform.dal.sharding.idgen.IdGenerator;

public interface PrefetchStrategy {

    int REQUESTSIZE_DEFAULT_VALUE = 1000;
    int TIMEOUTMILLIS_DEFAULT_VALUE = 2;

    int getSuggestedRequestSize();

    int getSuggestedTimeoutMillis();

    boolean checkIfNeedPrefetch();

}

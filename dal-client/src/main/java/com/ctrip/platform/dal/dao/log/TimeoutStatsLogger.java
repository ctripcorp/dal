package com.ctrip.platform.dal.dao.log;

import java.util.Map;

public interface TimeoutStatsLogger {

    void register(Map<String, TimeoutCollection> timeoutCollectionMap);
}

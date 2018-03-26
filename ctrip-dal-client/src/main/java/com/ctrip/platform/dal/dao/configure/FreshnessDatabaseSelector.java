package com.ctrip.platform.dal.dao.configure;

import java.util.Map;

public class FreshnessDatabaseSelector extends FreshnessSelector {
    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        settings.clear();
        settings.put(FRESHNESS_READER, "com.ctrip.platform.dal.dao.configure.FreshnessHelper");
        settings.put(UPDATE_INTERVAL, "5");
        super.initialize(settings);
    }
}

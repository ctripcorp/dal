package com.ctrip.datasource.configure.qconfig;

import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;
import qunar.tc.qconfig.client.exception.ResultUnexpectedException;

import java.util.Map;

public class MockConfigNotExistsMapConfig extends MapConfig {
    public MockConfigNotExistsMapConfig(Feature feature) {
        super(feature);
    }

    public Map<String, String> asMap() {
       throw new ResultUnexpectedException(404,505,"MockConfigNotExistsMapConfig");
    }
}

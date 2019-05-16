package com.ctrip.datasource.configure.qconfig;

import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;

public class MockNullMapConfig extends MapConfig {
    public MockNullMapConfig(Feature feature) {
        super(feature);
    }

    public Map<String, String> asMap() {
       return null;
    }
}

package com.ctrip.datasource.configure.qconfig;

import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;

public class MockExceptionMapConfig extends MapConfig {
    public MockExceptionMapConfig(Feature feature) {
        super(feature);
    }

    public Map<String, String> asMap() {
       throw new RuntimeException("MockExceptionMapConfig");
    }
}

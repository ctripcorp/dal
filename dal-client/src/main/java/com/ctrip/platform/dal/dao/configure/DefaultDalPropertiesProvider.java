package com.ctrip.platform.dal.dao.configure;

import java.util.HashMap;
import java.util.Map;

public class DefaultDalPropertiesProvider implements IDalPropertiesProvider {
    @Override
    public Map<String, String> getProperties() {
        return new HashMap<>();
    }

    @Override
    public void addPropertiesChangedListener(IDalPropertiesChanged callback) {}
}

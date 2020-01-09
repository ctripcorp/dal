package com.ctrip.platform.dal.dao.configure.dalproperties;

import java.util.HashMap;
import java.util.Map;

public class DefaultDalPropertiesProvider implements DalPropertiesProvider {
    @Override
    public Map<String, String> getProperties() {
        return new HashMap<>();
    }

    @Override
    public void addPropertiesChangedListener(DalPropertiesChanged callback) {}

}

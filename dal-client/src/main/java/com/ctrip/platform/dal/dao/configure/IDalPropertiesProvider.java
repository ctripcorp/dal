package com.ctrip.platform.dal.dao.configure;

import java.util.Map;

public interface IDalPropertiesProvider {
    Map<String, String> getProperties();

    void addPropertiesChangedListener(IDalPropertiesChanged callback);
}

package com.ctrip.platform.dal.dao.configure;

import java.util.Map;
import java.util.Set;

public class DefaultVariableDataSourceConfigureProvider extends AbstractVariableDataSourceConfigureProvider {

    @Override
    public Map<String, DalConnectionStringConfigure> getConnectionStrings(Set<String> dbNames) {
        return null;
    }
}

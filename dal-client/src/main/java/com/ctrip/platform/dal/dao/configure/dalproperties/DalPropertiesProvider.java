package com.ctrip.platform.dal.dao.configure.dalproperties;

import java.util.Map;

/**
 * Created by lilj on 2018/7/22.
 */
public interface DalPropertiesProvider {
    Map<String, String> getProperties();

    void addPropertiesChangedListener(DalPropertiesChanged callback);

}

package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesChanged;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesProvider;
import com.ctrip.platform.dal.dao.configure.dalproperties.DefaultDalPropertiesLocator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lilj on 2018/7/24.
 */
public class AbstractDalPropertiesProvider implements DalPropertiesProvider {
    protected Boolean defaultStatus = true;
    protected Boolean defaultImplicitAllShardsStatus = false;
    protected static final String SWITCH_KEYNAME = DefaultDalPropertiesLocator.TABLE_PARSE_SWITCH_KEYNAME;
    protected static final String IMPLICIT_ALL_SHARDS_SWITCH_KEYNAME = DefaultDalPropertiesLocator.IMPLICIT_ALL_SHARDS_SWITCH;

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>();
        map.put(SWITCH_KEYNAME, defaultStatus.toString());
        map.put(IMPLICIT_ALL_SHARDS_SWITCH_KEYNAME, defaultImplicitAllShardsStatus.toString());
        return map;
    }

    @Override
    public void addPropertiesChangedListener(DalPropertiesChanged callback) {
    }

}

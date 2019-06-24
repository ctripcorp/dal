package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesChanged;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lilj on 2018/7/24.
 */
public class AbstractDalPropertiesProvider implements DalPropertiesProvider {
    protected Boolean defaultStatus = true;
    protected Boolean implicitAllShardsSwitch=true;
    protected static final String SWITCH_KEYNAME = "TableParseSwitch";
    protected static final String IMPLICIT_ALL_SHARDS_SWITCH_KEYNAME = "ImplicitAllShardsSwitch";

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>();
        map.put(SWITCH_KEYNAME, defaultStatus.toString());
        map.put(IMPLICIT_ALL_SHARDS_SWITCH_KEYNAME,implicitAllShardsSwitch.toString());
        return map;
    }

    @Override
    public void addPropertiesChangedListener(DalPropertiesChanged callback) {}

}

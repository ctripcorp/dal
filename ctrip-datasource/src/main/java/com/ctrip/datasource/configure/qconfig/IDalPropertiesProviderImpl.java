package com.ctrip.datasource.configure.qconfig;

import com.ctrip.platform.dal.dao.configure.IDalPropertiesChanged;
import com.ctrip.platform.dal.dao.configure.IDalPropertiesProvider;
import com.dianping.cat.Cat;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.HashMap;
import java.util.Map;

public class IDalPropertiesProviderImpl implements IDalPropertiesProvider {
    private static final String DAL = "dal";
    private static final String DAL_PROPERTIES = "dal.properties";
    private static final Object LOCK = new Object();
    private volatile MapConfig mapConfig = null;

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>();

        try {
            MapConfig config = getMapConfig();
            Map<String, String> temp = config.asMap();
            for (Map.Entry<String, String> entry : temp.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }

        } catch (Throwable ex) {
            Cat.logError("An error occured while getting dal.properties from QConfig.", ex);
        }

        return map;
    }

    private MapConfig getMapConfig() {
        if (mapConfig == null) {
            synchronized (LOCK) {
                if (mapConfig == null) {
                    mapConfig = MapConfig.get(DAL, DAL_PROPERTIES, null);
                }
            }
        }

        return mapConfig;
    }

    @Override
    public void addPropertiesChangedListener(final IDalPropertiesChanged callback) {
        try {
            MapConfig config = getMapConfig();
            config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
                @Override
                public void onLoad(Map<String, String> map) {
                    callback.onChanged(map);
                }
            });
        } catch (Throwable ex) {
            Cat.logError("An error occured while adding listener for dal.properties.", ex);
        }
    }

}

package com.ctrip.datasource.configure.qconfig;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PoolPropertiesProviderImpl implements PoolPropertiesProvider, DataSourceConfigureConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoolPropertiesProviderImpl.class);
    private static final String DAL_APPNAME = "dal";
    private static final String DATASOURCE_PROPERTIES = "datasource.properties";

    private AtomicReference<MapConfig> mapConfigReference = new AtomicReference<>();
    private AtomicReference<Boolean> isFirstTime = new AtomicReference<>(true);

    private MapConfig getConfig() {
        return mapConfigReference.get();
    }

    @Override
    public Map<String, String> getPoolProperties() {
        refreshPoolPropertiesMapConfig();
        return _getPoolProperties();
    }

    private void refreshPoolPropertiesMapConfig() {
        if (!Foundation.app().isAppIdSet())
            return;

        try {
            MapConfig config = getMapConfig();
            if (config != null) {
                mapConfigReference.set(config);
            }
        } catch (Throwable e) {
            String msg = "从QConfig读取DataSource配置时发生异常，如果您没有使用配置中心，可以忽略这个异常:" + e.getMessage();
            LOGGER.warn(msg, e);
        }
    }

    private MapConfig getMapConfig() {
        return MapConfig.get(DAL_APPNAME, DATASOURCE_PROPERTIES, null); // get datasource.properties from QConfig
    }

    private Map<String, String> _getPoolProperties() {
        Map<String, String> map = new HashMap<>();
        MapConfig config = mapConfigReference.get();
        if (config == null)
            return map;

        try {
            map = config.asMap();
            String log = "PoolProperties 配置:" + PoolPropertiesHelper.getInstance().mapToString(map);
            LOGGER.info(log);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }

        return map;
    }

    @Override
    public void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback) {
        MapConfig config = getConfig();
        if (config == null)
            return;

        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                if (map == null || map.isEmpty())
                    throw new RuntimeException("Parameter for onLoad event is null.");

                Boolean firstTime = isFirstTime.get().booleanValue();
                if (firstTime) {
                    isFirstTime.compareAndSet(true, false);
                    return;
                }

                boolean dynamicEnabled = dynamicPoolPropertiesEnabled(map);
                if (!dynamicEnabled) {
                    LOGGER.info(String.format("DAL DataSource DynamicPoolProperties does not enabled."));
                    return;
                }

                callback.onChanged(map);
            }
        });
    }

    private boolean dynamicPoolPropertiesEnabled(Map<String, String> map) {
        if (map == null || map.isEmpty())
            return false;

        String value = map.get(DataSourceConfigureConstants.ENABLE_DYNAMIC_POOL_PROPERTIES);
        if (value == null)
            return false;

        return Boolean.parseBoolean(value);
    }

}

package com.ctrip.datasource.configure.qconfig;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PoolPropertiesProviderImpl implements PoolPropertiesProvider, DataSourceConfigureConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoolPropertiesProviderImpl.class);

    private static final String DAL_APPNAME = "dal";
    private static final String DATASOURCE_PROPERTIES = "datasource.properties";
    private static final String DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE =
            "An error occured while getting datasource.properties from QConfig.";
    private static final String POOL_PROPERTIES_CONFIGURATION = "PoolProperties configuration:";
    private static final String ON_LOAD_EXCEPTION = "Parameter for onLoad event is null.";
    private static final String DYNAMIC_POOL_PROPERTIES_NOT_ENABLED =
            "DAL DataSource DynamicPoolProperties does not enabled.";

    private AtomicReference<MapConfig> mapConfigReference = new AtomicReference<>();
    private AtomicReference<Boolean> isFirstTime = new AtomicReference<>(true);

    private MapConfig getConfig() {
        return mapConfigReference.get();
    }

    @Override
    public PoolPropertiesConfigure getPoolProperties() {
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
            LOGGER.error(DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE, e);
            Cat.logError(DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE, e);
            throw e;
        }
    }

    private MapConfig getMapConfig() {
        return MapConfig.get(DAL_APPNAME, DATASOURCE_PROPERTIES, null); // get datasource.properties from QConfig
    }

    private PoolPropertiesConfigure _getPoolProperties() {
        DataSourceConfigure configure = null;
        MapConfig config = mapConfigReference.get();
        if (config == null)
            return configure;

        try {
            Map<String, String> map = config.asMap();
            String log = POOL_PROPERTIES_CONFIGURATION + PoolPropertiesHelper.getInstance().mapToString(map);
            LOGGER.info(log);
            configure = new DataSourceConfigure("", map);
        } catch (Throwable e) {
            String message = e.getMessage();
            LOGGER.error(message, e);
            Cat.logError(message, e);
        }

        return configure;
    }

    @Override
    public void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback) {
        MapConfig config = getConfig();
        if (config == null)
            return;

        _addPoolPropertiesChangedListener(config, callback);
    }

    private void _addPoolPropertiesChangedListener(MapConfig config, final PoolPropertiesChanged callback) {
        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                if (map == null || map.isEmpty())
                    throw new RuntimeException(ON_LOAD_EXCEPTION);

                Boolean firstTime = isFirstTime.get().booleanValue();
                if (firstTime) {
                    isFirstTime.compareAndSet(true, false);
                    return;
                }

                boolean dynamicEnabled = dynamicPoolPropertiesEnabled(map);
                if (!dynamicEnabled) {
                    LOGGER.info(DYNAMIC_POOL_PROPERTIES_NOT_ENABLED);
                    return;
                }

                DataSourceConfigure configure = new DataSourceConfigure("", map);
                callback.onChanged(configure);
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

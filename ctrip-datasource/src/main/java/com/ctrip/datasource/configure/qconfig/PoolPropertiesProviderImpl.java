package com.ctrip.datasource.configure.qconfig;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
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
    private static final String POOLPROPERTIES_CONFIGURATION = "PoolProperties configuration: ";
    private static final String ON_LOAD_PARAMETER_EXCEPTION = "Map parameter of onLoad event is null.";
    private static final String DYNAMIC_POOL_PROPERTIES_NOT_ENABLED =
            "DAL DataSource DynamicPoolProperties does not enabled.";
    private static final String NULL_MAPCONFIG_EXCEPTION = "MapConfig for datasource.properties is null.";

    private static final String DAL = "DAL";
    private static final String POOLPROPERTIES_GET_MAPCONFIG = "PoolProperties::getMapConfig";
    private static final String POOLPROPERTIES_GET_POOLPROPERTIES = "PoolProperties::getPoolProperties";
    private static final String POOLPROPERTIES_ADD_LISTENER = "PoolProperties::addListener";
    private static final String POOLPROPERTIES_LISTENER_ON_LOAD = "PoolProperties:listenerOnLoad";

    private AtomicReference<MapConfig> mapConfigReference = new AtomicReference<>();
    private AtomicReference<Boolean> isFirstTimeLoadReference = new AtomicReference<>(true);

    @Override
    public PoolPropertiesConfigure getPoolProperties() {
        MapConfig config = getMapConfig();
        DataSourceConfigure configure;

        try {
            Map<String, String> map = getPoolPropertiesMap(config);
            configure = new DataSourceConfigure("", map);
        } catch (Throwable e) {
            throw e;
        }

        return configure;
    }

    private MapConfig getMapConfig() {
        MapConfig mapConfig = mapConfigReference.get();
        if (mapConfig == null) {
            mapConfig = _getMapConfig();
            mapConfigReference.set(mapConfig);
        }

        return mapConfig;
    }

    private MapConfig _getMapConfig() {
        MapConfig config = null;
        Transaction transaction = Cat.newTransaction(DAL, POOLPROPERTIES_GET_MAPCONFIG);
        try {
            config = MapConfig.get(DAL_APPNAME, DATASOURCE_PROPERTIES, null); // get datasource.properties from QConfig
            if (config == null)
                throw new RuntimeException(NULL_MAPCONFIG_EXCEPTION);

            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            Cat.logError(DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE, e);
            LOGGER.error(DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE, e);
            throw e;
        } finally {
            transaction.complete();
        }

        return config;
    }

    private Map<String, String> getPoolPropertiesMap(MapConfig config) {
        Map<String, String> map = null;
        Transaction transaction = Cat.newTransaction(DAL, POOLPROPERTIES_GET_POOLPROPERTIES);

        try {
            map = config.asMap();
            String log = POOLPROPERTIES_CONFIGURATION + PoolPropertiesHelper.getInstance().mapToString(map);
            LOGGER.info(log);
            transaction.addData(log);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            Cat.logError(e);
            String message = e.getMessage();
            LOGGER.error(message, e);
            throw e;
        } finally {
            transaction.complete();
        }

        return map;
    }

    @Override
    public void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback) {
        MapConfig config = getMapConfig();
        Transaction transaction = Cat.newTransaction(DAL, POOLPROPERTIES_ADD_LISTENER);

        try {
            _addPoolPropertiesChangedListener(config, callback);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            transaction.complete();
        }
    }

    private void _addPoolPropertiesChangedListener(MapConfig config, final PoolPropertiesChanged callback) {
        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                Transaction transaction = Cat.newTransaction(DAL, POOLPROPERTIES_LISTENER_ON_LOAD);
                try {
                    if (map == null || map.isEmpty())
                        throw new RuntimeException(ON_LOAD_PARAMETER_EXCEPTION);

                    if (isFirstTimeLoad())
                        return;

                    if (!dynamicPoolPropertiesEnabled(map)) {
                        LOGGER.info(DYNAMIC_POOL_PROPERTIES_NOT_ENABLED);
                        return;
                    }

                    DataSourceConfigure configure = new DataSourceConfigure("", map);
                    callback.onChanged(configure);
                    transaction.setStatus(Transaction.SUCCESS);
                } catch (Throwable e) {
                    transaction.setStatus(e);
                    Cat.logError(e);
                    throw e;
                } finally {
                    transaction.complete();
                }
            }
        });
    }

    private boolean isFirstTimeLoad() {
        Boolean isFirstTime = isFirstTimeLoadReference.get().booleanValue();
        if (isFirstTime) {
            isFirstTimeLoadReference.compareAndSet(true, false);
        }

        return isFirstTime;
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

package com.ctrip.datasource.configure;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureWrapper;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PoolPropertiesProviderImpl implements PoolPropertiesProvider, DataSourceConfigureConstants {
    private static final Logger logger = LoggerFactory.getLogger(PoolPropertiesProviderImpl.class);
    private static final String DAL_APPNAME = "dal";
    private static final String DAL_DATASOURCE_PROPERTIES = "datasource.properties";
    private static final String SEPARATOR = "\\.";

    private static final String DAL_DATASOURCE = "DAL";
    private static final String DAL_GET_DATASOURCE = "DataSource::getRemoteDataSourceConfig";

    private AtomicReference<MapConfig> mapConfigReference = new AtomicReference<>();
    private AtomicReference<Boolean> isFirstTime = new AtomicReference<>(true);

    private DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();

    private volatile static PoolPropertiesProviderImpl instance = null;

    public synchronized static PoolPropertiesProviderImpl getInstance() {
        if (instance == null) {
            instance = new PoolPropertiesProviderImpl();
            instance.initializePoolProperties();
        }
        return instance;
    }

    private MapConfig getConfig() {
        return mapConfigReference.get();
    }

    @Override
    public void initializePoolProperties() {
        refreshPoolPropertiesMapConfig();
        refreshPoolProperties();
    }

    private void refreshPoolPropertiesMapConfig() {
        if (!Foundation.app().isAppIdSet())
            return;

        Transaction transaction = Cat.newTransaction(DAL_DATASOURCE, DAL_GET_DATASOURCE);
        try {
            MapConfig config = getMapConfig();
            if (config != null) {
                mapConfigReference.set(config);
            }
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            String msg = "从QConfig读取DataSource配置时发生异常，如果您没有使用配置中心，可以忽略这个异常:" + e.getMessage();
            transaction.setStatus(Transaction.SUCCESS);
            transaction.addData(DAL_GET_DATASOURCE, msg);
            logger.warn(msg, e);
        } finally {
            transaction.complete();
        }
    }

    private MapConfig getMapConfig() {
        return MapConfig.get(DAL_APPNAME, DAL_DATASOURCE_PROPERTIES, null); // get datasource.properties from QConfig
    }

    private void refreshPoolProperties() {
        MapConfig config = mapConfigReference.get();
        if (config == null)
            return;

        Map<String, String> map = config.asMap();
        setPoolProperties(map);
    }

    @Override
    public void setPoolProperties(Map<String, String> map) {
        Map<String, String> originalMap = new HashMap<>(map);
        Map<String, String> datasource = new HashMap<>(); // app level
        Map<String, Map<String, String>> datasourceMap = new HashMap<>(); // datasource level
        processDataSourceConfigure(map, datasource, datasourceMap);

        // set app level map
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        setDataSourceConfigure(dataSourceConfigure, datasource);

        // set datasource level map
        Map<String, DataSourceConfigure> dataSourceConfigureMap = new HashMap<>();
        setDataSourceConfigureMap(dataSourceConfigureMap, datasourceMap);

        DataSourceConfigureWrapper wrapper =
                new DataSourceConfigureWrapper(originalMap, dataSourceConfigure, dataSourceConfigureMap);
        setDataSourceConfigureWrapperReference(wrapper);

        String log = "DataSource配置:" + poolPropertiesHelper.mapToString(map);
        Cat.logEvent(DAL_DATASOURCE, DAL_GET_DATASOURCE, Message.SUCCESS, log);
        logger.info(log);
    }

    private void setDataSourceConfigureWrapperReference(DataSourceConfigureWrapper wrapper) {
        dataSourceConfigureLocator.setDataSourceConfigureWrapperReference(wrapper);
    }

    private void setDataSourceConfigure(DataSourceConfigure configure, Map<String, String> datasource) {
        if (configure == null || datasource.isEmpty())
            return;
        configure.setMap(datasource);
    }

    private void setDataSourceConfigureMap(Map<String, DataSourceConfigure> configureMap,
            Map<String, Map<String, String>> datasourceMap) {
        if (configureMap == null || datasourceMap.isEmpty())
            return;
        for (Map.Entry<String, Map<String, String>> entry : datasourceMap.entrySet()) {
            DataSourceConfigure config = new DataSourceConfigure();
            setDataSourceConfigure(config, entry.getValue());
            String keyName = ConnectionStringKeyHelper.getKeyName(entry.getKey());
            configureMap.put(keyName, config);
        }
    }

    private void processDataSourceConfigure(Map<String, String> map, Map<String, String> datasource,
            Map<String, Map<String, String>> datasourceMap) {
        if (map == null || map.isEmpty())
            return;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String[] array = entry.getKey().split(SEPARATOR);
            if (array.length == 1) { // app level
                datasource.put(array[0], entry.getValue());
            } else if (array.length == 2) { // datasource level
                String datasourceName = array[0];
                if (!datasourceMap.containsKey(datasourceName))
                    datasourceMap.put(datasourceName, new HashMap<String, String>());
                Map<String, String> temp = datasourceMap.get(datasourceName);
                temp.put(array[1], entry.getValue());
            }
        }
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
                    logger.debug("DAL debug:(addPoolPropertiesChangedListener)first time onLoad");
                    return;
                }

                boolean dynamicEnabled = dynamicPoolPropertiesEnabled(map);
                if (!dynamicEnabled) {
                    logger.info(String.format("DAL DataSource DynamicPoolProperties does not enabled."));
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

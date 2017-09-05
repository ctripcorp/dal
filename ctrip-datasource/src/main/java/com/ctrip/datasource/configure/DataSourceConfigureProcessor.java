package com.ctrip.datasource.configure;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceConfigureProcessor implements DataSourceConfigureConstants {
    private static DataSourceConfigureProcessor instance = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigureProcessor.class);
    private static final String DAL_APPNAME = "dal";
    private static final String DAL_DATASOURCE_PROPERTIES = "datasource.properties";
    private static final String SEPARATOR = "\\.";
    private static final String PROD_SUFFIX = "_SH";
    private Map<String, String> originalMap = null;
    private DataSourceConfigure dataSourceConfigure = null;
    private Map<String, DataSourceConfigure> dataSourceConfigureMap = null;
    private static final String DAL_DATASOURCE = "DAL";
    private static final String DAL_GET_DATASOURCE = "DataSource::getRemoteDataSourceConfig";
    private static final String DAL_MERGE_DATASOURCE = "DataSource::mergeDataSourceConfig";

    public synchronized static DataSourceConfigureProcessor getInstance() {
        if (instance == null) {
            instance = new DataSourceConfigureProcessor();
            instance.setDataSourceConfigurePoolSettings();
        }

        return instance;
    }

    private void setDataSourceConfigurePoolSettings() {
        if (!Foundation.app().isAppIdSet())
            return;
        Transaction transaction = Cat.newTransaction(DAL_DATASOURCE, DAL_GET_DATASOURCE);
        try {
            MapConfig config = MapConfig.get(DAL_APPNAME, DAL_DATASOURCE_PROPERTIES, null);// Get config from QConfig
            if (config != null) {
                Map<String, String> map = config.asMap();
                loadPoolSettings(map);

                config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
                    @Override
                    public void onLoad(Map<String, String> map) {
                        try {
                            notifyListeners(map);
                        } catch (Throwable e) {
                            String msg = "DataSourceConfigurePoolSettings notifyListeners warn:" + e.getMessage();
                            LOGGER.warn(msg, e);
                        }
                    }
                });
            }
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            String msg = "从QConfig读取DataSource配置时发生异常，如果您没有使用配置中心，可以忽略这个异常:" + e.getMessage();
            transaction.setStatus(Transaction.SUCCESS);
            transaction.addData(DAL_GET_DATASOURCE, msg);
            LOGGER.warn(msg, e);
        } finally {
            transaction.complete();
        }
    }

    private void loadPoolSettings(Map<String, String> map) {
        originalMap = new HashMap<>(map);
        Map<String, String> datasource = new HashMap<>(); // app level
        Map<String, Map<String, String>> datasourceMap = new HashMap<>(); // datasource level
        processDataSourceConfigure(map, datasource, datasourceMap);
        dataSourceConfigure = new DataSourceConfigure();
        setDataSourceConfigure(dataSourceConfigure, datasource);// set app level map
        dataSourceConfigureMap = new ConcurrentHashMap<>();
        setDataSourceConfigureMap(dataSourceConfigureMap, datasourceMap);// set datasource level map

        String log = "DataSource配置:" + mapToString(map);
        Cat.logEvent(DAL_DATASOURCE, DAL_GET_DATASOURCE, Message.SUCCESS, log);
        LOGGER.info(log);
    }

    private void notifyListeners(Map<String, String> map) throws Exception {
        if (map == null || map.size() == 0)
            return;
        if (map.equals(originalMap)) // nothing changes
            return;

        // reload pool settings
        loadPoolSettings(map);

        // refresh pool settings of DataSourceConfigures
        Map<String, DataSourceConfigure> configures = DataSourceConfigureParser.getInstance().getDataSourceConfigures();
        if (configures == null)
            return;

        Map<String, DataSourceConfigure> newConfigures = new HashMap<>();
        for (Map.Entry<String, DataSourceConfigure> entry : configures.entrySet()) {
            DataSourceConfigure configure = entry.getValue();
            DataSourceConfigure newConfigure = getDataSourceConfigure(configure);
            newConfigures.put(entry.getKey(), newConfigure);
        }

        Map<String, DataSourceConfigureChangeListener> listeners =
                DataSourceConfigureParser.getInstance().getChangeListeners();
        for (Map.Entry<String, DataSourceConfigure> entry : newConfigures.entrySet()) {
            String name = entry.getKey();
            DataSourceConfigure newConfigure = entry.getValue();
            DataSourceConfigure oldConfigure = DataSourceConfigureParser.getInstance().getDataSourceConfigure(name);
            DataSourceConfigureParser.getInstance().addDataSourceConfigure(name, newConfigure);
            DataSourceConfigureChangeListener listener = listeners.get(name);
            if (listener == null)
                continue;

            // notify listener to recreate datasource,destroy old datasource,etc
            DataSourceConfigureChangeEvent event = new DataSourceConfigureChangeEvent(name, newConfigure, oldConfigure);
            listener.configChanged(event);
        }
    }

    private void setDataSourceConfigure(DataSourceConfigure configure, Map<String, String> datasource) {
        if (configure == null || datasource.size() == 0)
            return;
        configure.setMap(datasource);
    }

    private void setDataSourceConfigureMap(Map<String, DataSourceConfigure> configureMap,
            Map<String, Map<String, String>> datasourceMap) {
        if (configureMap == null || datasourceMap.size() == 0)
            return;
        for (Map.Entry<String, Map<String, String>> entry : datasourceMap.entrySet()) {
            DataSourceConfigure config = new DataSourceConfigure();
            setDataSourceConfigure(config, entry.getValue());
            configureMap.put(entry.getKey(), config);
        }
    }

    private void processDataSourceConfigure(Map<String, String> map, Map<String, String> datasource,
            Map<String, Map<String, String>> datasourceMap) {
        if (map == null || map.size() == 0)
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

    /*
     * Input parameter 'DatabasePoolConfig config' currently indicates the datasource config which read from
     * datasource.xml Override order: Config center global datasource <-- datasource.xml <-- Config center app
     * datasource <-- Config center per datasource
     */
    public DataSourceConfigure getDataSourceConfigure(DataSourceConfigure configure) {
        DataSourceConfigure c = cloneDataSourceConfigure(null);
        Transaction transaction = Cat.newTransaction(DAL_DATASOURCE, DAL_MERGE_DATASOURCE);

        try {
            // override app-level config from QConfig
            if (dataSourceConfigure != null) {
                overrideDataSourceConfigure(c, dataSourceConfigure);
                String log = "App 覆盖结果:" + mapToString(c.getMap());
                LOGGER.info(log);
            }
            // override datasource-level config from QConfig
            String name = configure.getName();
            if (name != null && dataSourceConfigureMap != null) {
                DataSourceConfigure sourceConfigure = dataSourceConfigureMap.get(name);
                if (sourceConfigure != null) {
                    overrideDataSourceConfigure(c, sourceConfigure);
                    String log = name + " 覆盖结果:" + mapToString(c.getMap());
                    LOGGER.info(log);
                } else {
                    String possibleName = name.endsWith(PROD_SUFFIX)
                            ? name.substring(0, name.length() - PROD_SUFFIX.length()) : name + PROD_SUFFIX;
                    DataSourceConfigure sc = dataSourceConfigureMap.get(possibleName);
                    if (sc != null) {
                        overrideDataSourceConfigure(c, sc);
                        String log = possibleName + " 覆盖结果:" + mapToString(c.getMap());
                        LOGGER.info(log);
                    }
                }
            }
            // override config from datasource.xml,connection settings
            if (configure != null) {
                overrideDataSourceConfigure(c, configure);
                String log = "datasource.xml 覆盖结果:" + mapToString(c.getMap());
                LOGGER.info(log);
            }
            Cat.logEvent(DAL_DATASOURCE, DAL_MERGE_DATASOURCE, Message.SUCCESS, mapToString(c.getMap()));
            Map<String, String> datasource = c.getMap();
            Properties prop = c.getProperties();
            setProperties(datasource, prop); // set properties from map
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            LOGGER.error(e.getMessage(), e);
        } finally {
            transaction.complete();
        }
        return c;
    }

    public DataSourceConfigure refreshConnectionSettings(DataSourceConfigure connectionSettingsConfigure,
            DataSourceConfigure oldConfigure) {
        if (connectionSettingsConfigure == null || oldConfigure == null)
            return null;

        DataSourceConfigure configure = cloneDataSourceConfigure(oldConfigure);

        String connectionUrl = connectionSettingsConfigure.getConnectionUrl();
        if (connectionUrl != null && !connectionUrl.isEmpty())
            configure.setConnectionUrl(connectionUrl);

        String userName = connectionSettingsConfigure.getUserName();
        if (userName != null && !userName.isEmpty())
            configure.setUserName(userName);

        String password = connectionSettingsConfigure.getPassword();
        if (password != null && !password.isEmpty())
            configure.setPassword(password);

        String driverClass = connectionSettingsConfigure.getDriverClass();
        if (driverClass != null && !driverClass.isEmpty())
            configure.setDriverClass(driverClass);

        String version = connectionSettingsConfigure.getVersion();
        if (version != null && !version.isEmpty())
            configure.setVersion(version);

        return configure;
    }

    private DataSourceConfigure cloneDataSourceConfigure(DataSourceConfigure configure) {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        if (configure == null)
            return dataSourceConfigure;
        dataSourceConfigure.setName(configure.getName());
        dataSourceConfigure.setProperties(configure.getProperties());
        dataSourceConfigure.setMap(new HashMap<>(configure.getMap()));
        return dataSourceConfigure;
    }

    private void overrideDataSourceConfigure(DataSourceConfigure lowlevel, DataSourceConfigure highlevel) {
        if (lowlevel == null || highlevel == null)
            return;
        Map<String, String> lowlevelMap = lowlevel.getMap();
        Map<String, String> highlevelMap = highlevel.getMap();
        if (lowlevelMap == null || highlevelMap == null)
            return;
        for (Map.Entry<String, String> entry : highlevelMap.entrySet()) {
            lowlevelMap.put(entry.getKey(), entry.getValue()); // override entry of map
        }
    }

    private void setProperties(Map<String, String> datasource, Properties prop) {
        if (datasource == null || prop == null)
            return;

        for (Map.Entry<String, String> entry : datasource.entrySet()) {
            prop.setProperty(entry.getKey(), entry.getValue());
        }
    }

    private String mapToString(Map<String, String> map) {
        String result = "";
        try {
            if (map != null && map.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sb.append(entry.getKey() + "=" + entry.getValue() + ",");
                }
                result = sb.substring(0, sb.length() - 1);
            }
        } catch (Throwable e) {
        }
        return result;
    }

}

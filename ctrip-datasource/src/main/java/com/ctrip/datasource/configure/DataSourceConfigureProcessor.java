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
import java.util.concurrent.atomic.AtomicReference;

public class DataSourceConfigureProcessor implements DataSourceConfigureConstants {
    private static DataSourceConfigureProcessor instance = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigureProcessor.class);
    private static final String DAL_APPNAME = "dal";
    private static final String DAL_DATASOURCE_PROPERTIES = "datasource.properties";
    private static final String SEPARATOR = "\\.";
    private static final String PROD_SUFFIX = "_SH";
    private AtomicReference<DataSourceConfigureWrapper> dataSourceConfigureWrapperReference = new AtomicReference<>();
    private static final String DAL_DATASOURCE = "DAL";
    private static final String DAL_GET_DATASOURCE = "DataSource::getRemoteDataSourceConfig";
    private static final String DAL_MERGE_DATASOURCE = "DataSource::mergeDataSourceConfig";

    private static final String DAL_DYNAMIC_DATASOURCE = "DAL";
    private static final String DAL_NOTIFY_LISTENER = "DataSource::notifyListener";
    private static final String DAL_NOTIFY_LISTENER_START = "DataSource.notifyListener.start";
    private static final String DAL_NOTIFY_LISTENER_END = "DataSource.notifyListener.end";

    private static final String DAL_REFRESH_DATASOURCE = "DataSource::refreshDataSourceConfig";
    private static final String DATASOURCE_OLD_CONFIGURE = "DataSource::oldConfigure";
    private static final String DATASOURCE_NEW_CONFIGURE = "DataSource::newConfigure";

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
            MapConfig config = getMapConfig();
            if (config != null) {
                Map<String, String> map = config.asMap();
                loadPoolSettings(map);

                config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
                    @Override
                    public void onLoad(Map<String, String> map) {
                        Transaction t = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DAL_NOTIFY_LISTENER);
                        t.addData(DAL_NOTIFY_LISTENER_START);
                        try {
                            notifyListeners();
                            t.addData(DAL_NOTIFY_LISTENER_END);
                            t.setStatus(Transaction.SUCCESS);
                        } catch (Throwable e) {
                            String msg = "DataSourceConfigurePoolSettings notifyListeners warn:" + e.getMessage();
                            LOGGER.warn(msg, e);
                        } finally {
                            t.complete();
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

    private MapConfig getMapConfig() {
        return MapConfig.get(DAL_APPNAME, DAL_DATASOURCE_PROPERTIES, null); // get datasource.properties from QConfig
    }

    private synchronized void loadPoolSettings(Map<String, String> map) {
        Map<String, String> originalMap = new HashMap<>(map);
        Map<String, String> datasource = new HashMap<>(); // app level
        Map<String, Map<String, String>> datasourceMap = new HashMap<>(); // datasource level
        processDataSourceConfigure(map, datasource, datasourceMap);
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        setDataSourceConfigure(dataSourceConfigure, datasource);// set app level map
        Map<String, DataSourceConfigure> dataSourceConfigureMap = new ConcurrentHashMap<>();
        setDataSourceConfigureMap(dataSourceConfigureMap, datasourceMap);// set datasource level map

        DataSourceConfigureWrapper updated =
                new DataSourceConfigureWrapper(originalMap, dataSourceConfigure, dataSourceConfigureMap);

        dataSourceConfigureWrapperReference.set(updated);

        String log = "DataSource配置:" + mapToString(map);
        Cat.logEvent(DAL_DATASOURCE, DAL_GET_DATASOURCE, Message.SUCCESS, log);
        LOGGER.info(log);
    }

    private void notifyListeners() throws Exception {
        Map<String, DataSourceConfigureChangeListener> listeners =
                DataSourceConfigureParser.getInstance().getChangeListeners();
        if (listeners == null || listeners.isEmpty())
            return;

        // retrieve datasource.properties
        Map<String, String> map = null;
        MapConfig config = getMapConfig();
        if (config != null) {
            map = new HashMap<>(config.asMap());
        }

        if (map == null || map.isEmpty())
            return;
        DataSourceConfigureWrapper reference = dataSourceConfigureWrapperReference.get();
        if (map.equals(reference.getOriginalMap())) // nothing changes
            return;

        // reload pool settings
        loadPoolSettings(map);

        // refresh pool settings of DataSourceConfigures
        Map<String, DataSourceConfigure> configures = DataSourceConfigureParser.getInstance().getDataSourceConfigures();
        if (configures == null)
            return;

        Map<String, DataSourceConfigure> newConfigures = new HashMap<>();
        for (Map.Entry<String, DataSourceConfigure> entry : configures.entrySet()) {
            String name = entry.getKey();
            DataSourceConfigure c = entry.getValue();
            DataSourceConfigure configure = cloneDataSourceConfigure(c);
            DataSourceConfigure newConfigure = getDataSourceConfigure(null);
            overrideDataSourceConfigure(configure, newConfigure);
            DataSourceConfigure localConfigure =
                    DataSourceConfigureParser.getInstance().getUserDataSourceConfigure(name);
            if (localConfigure != null) {
                overrideDataSourceConfigure(configure, localConfigure);
            }

            newConfigures.put(name, configure);
        }

        for (Map.Entry<String, DataSourceConfigure> entry : newConfigures.entrySet()) {
            String name = entry.getKey();
            DataSourceConfigureChangeListener listener = listeners.get(name);
            if (listener == null)
                continue;

            Transaction transaction = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE);
            try {
                DataSourceConfigure c = DataSourceConfigureParser.getInstance().getDataSourceConfigure(name);
                DataSourceConfigure oldConfigure = cloneDataSourceConfigure(c);
                transaction.addData(DATASOURCE_OLD_CONFIGURE,
                        String.format("%s:%s", name, mapToString(oldConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_OLD_CONFIGURE, name, mapToString(oldConfigure.toMap())));

                DataSourceConfigure newConfigure = entry.getValue();
                transaction.addData(DATASOURCE_NEW_CONFIGURE,
                        String.format("%s:%s", name, mapToString(newConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_NEW_CONFIGURE, name, mapToString(newConfigure.toMap())));

                DataSourceConfigureParser.getInstance().addDataSourceConfigure(name, newConfigure);

                // notify listener to recreate datasource,destroy old datasource,etc
                DataSourceConfigureChangeEvent event =
                        new DataSourceConfigureChangeEvent(name, newConfigure, oldConfigure);
                listener.configChanged(event);
                transaction.setStatus(Transaction.SUCCESS);
            } catch (Throwable e) {
                transaction.setStatus(e);
                Cat.logError(e);
                throw e;
            } finally {
                transaction.complete();
            }
        }
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
            configureMap.put(entry.getKey(), config);
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

    /*
     * Input parameter 'DataSourceConfigure configure' currently indicates the datasource config which read from
     * datasource.xml ***Override order: global datasource <-- app level setting <-- datasource level setting <--
     * datasource.xml datasource
     */
    public DataSourceConfigure getDataSourceConfigure(DataSourceConfigure configure) {
        DataSourceConfigure c = cloneDataSourceConfigure(null);
        Transaction transaction = Cat.newTransaction(DAL_DATASOURCE, DAL_MERGE_DATASOURCE);

        try {
            DataSourceConfigureWrapper reference = dataSourceConfigureWrapperReference.get();

            // override app-level config from QConfig
            DataSourceConfigure dataSourceConfigure = reference.getDataSourceConfigure();
            if (dataSourceConfigure != null) {
                overrideDataSourceConfigure(c, dataSourceConfigure);
                String log = "App 覆盖结果:" + mapToString(c.toMap());
                LOGGER.info(log);
            }
            // override datasource-level config from QConfig
            Map<String, DataSourceConfigure> dataSourceConfigureMap = reference.getDataSourceConfigureMap();
            if (configure != null && dataSourceConfigureMap != null) {
                String name = configure.getName();
                if (name != null) {
                    DataSourceConfigure sourceConfigure = dataSourceConfigureMap.get(name);
                    if (sourceConfigure != null) {
                        overrideDataSourceConfigure(c, sourceConfigure);
                        String log = name + " 覆盖结果:" + mapToString(c.toMap());
                        LOGGER.info(log);
                    } else {
                        String possibleName = name.endsWith(PROD_SUFFIX)
                                ? name.substring(0, name.length() - PROD_SUFFIX.length()) : name + PROD_SUFFIX;
                        DataSourceConfigure sc = dataSourceConfigureMap.get(possibleName);
                        if (sc != null) {
                            overrideDataSourceConfigure(c, sc);
                            String log = possibleName + " 覆盖结果:" + mapToString(c.toMap());
                            LOGGER.info(log);
                        }
                    }
                }
            }
            // override config from datasource.xml,connection settings
            if (configure != null) {
                overrideDataSourceConfigure(c, configure);
                c.setVersion(configure.getVersion());
                String log = "datasource.xml 覆盖结果:" + mapToString(c.toMap());
                LOGGER.info(log);
            }
            Cat.logEvent(DAL_DATASOURCE, DAL_MERGE_DATASOURCE, Message.SUCCESS, mapToString(c.toMap()));
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
        configure.setVersion(version);

        return configure;
    }

    private DataSourceConfigure cloneDataSourceConfigure(DataSourceConfigure configure) {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        if (configure == null)
            return dataSourceConfigure;

        dataSourceConfigure.setName(configure.getName());
        dataSourceConfigure.setMap(new HashMap<>(configure.getMap()));
        dataSourceConfigure.setProperties(deepCopyProperties(configure.getProperties()));
        dataSourceConfigure.setVersion(configure.getVersion());
        return dataSourceConfigure;
    }

    private Properties deepCopyProperties(Properties properties) {
        if (properties == null)
            return null;

        Properties p = new Properties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            p.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }

        return p;
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

        Properties prop = lowlevel.getProperties();
        setProperties(lowlevelMap, prop); // set properties from map
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

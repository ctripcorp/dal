package com.ctrip.datasource.configure;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureHolder;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.MapConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class DataSourceConfigureProcessor implements DataSourceConfigureConstants {
    private static DataSourceConfigureProcessor instance = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigureProcessor.class);
    private static final String DAL_APPNAME = "dal";
    private static final String DAL_DATASOURCE_PROPERTIES = "datasource.properties";
    private static final String SEPARATOR = "\\.";
    private static final String PROD_SUFFIX = "_SH";

    private static final String DAL_DATASOURCE = "DAL";
    private static final String DAL_GET_DATASOURCE = "DataSource::getRemoteDataSourceConfig";
    private static final String DAL_MERGE_DATASOURCE = "DataSource::mergeDataSourceConfig";

    private AtomicReference<MapConfig> mapConfigReference = new AtomicReference<>();
    private AtomicReference<DataSourceConfigureWrapper> dataSourceConfigureWrapperReference = new AtomicReference<>();

    public synchronized static DataSourceConfigureProcessor getInstance() {
        if (instance == null) {
            instance = new DataSourceConfigureProcessor();
            instance.refreshPoolSettingsConfig();
            instance.setPoolSettings();
        }
        return instance;
    }

    public MapConfig getConfig() {
        return mapConfigReference.get();
    }

    public void refreshPoolSettingsConfig() {
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
            LOGGER.warn(msg, e);
        } finally {
            transaction.complete();
        }
    }

    private MapConfig getMapConfig() {
        return MapConfig.get(DAL_APPNAME, DAL_DATASOURCE_PROPERTIES, null); // get datasource.properties from QConfig
    }

    public synchronized void setPoolSettings() {
        MapConfig config = mapConfigReference.get();
        if (config == null)
            return;

        Map<String, String> map = config.asMap();
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

        // duplicate configure
        dataSourceConfigureMap = DataSourceConfigureParser.getInstance().getDuplicatedMap(dataSourceConfigureMap);

        DataSourceConfigureWrapper wrapper =
                new DataSourceConfigureWrapper(originalMap, dataSourceConfigure, dataSourceConfigureMap);
        dataSourceConfigureWrapperReference.set(wrapper);

        String log = "DataSource配置:" + mapToString(map);
        Cat.logEvent(DAL_DATASOURCE, DAL_GET_DATASOURCE, Message.SUCCESS, log);
        LOGGER.info(log);
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
            configureMap.put(entry.getKey().toUpperCase(), config);
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
            DataSourceConfigureWrapper wrapper = dataSourceConfigureWrapperReference.get();

            // override app-level config from QConfig
            DataSourceConfigure dataSourceConfigure = wrapper.getDataSourceConfigure();
            if (dataSourceConfigure != null) {
                overrideDataSourceConfigure(c, dataSourceConfigure);
                String log = "App 覆盖结果:" + mapToString(c.toMap());
                LOGGER.info(log);
            }

            // override datasource-level config from QConfig
            Map<String, DataSourceConfigure> dataSourceConfigureMap = wrapper.getDataSourceConfigureMap();
            if (configure != null && dataSourceConfigureMap != null) {
                String name = configure.getName();
                if (name != null) {
                    DataSourceConfigure sourceConfigure = dataSourceConfigureMap.get(name);
                    if (sourceConfigure != null) {
                        overrideDataSourceConfigure(c, sourceConfigure);
                        String log = name + " 覆盖结果:" + mapToString(c.toMap());
                        LOGGER.info(log);
                    }
                }
            }

            // override config from connection settings,datasource.xml
            if (configure != null) {
                // override connection settings
                overrideDataSourceConfigure(c, configure);
                c.setName(configure.getName());
                c.setVersion(configure.getVersion());
                String log = "connection settings 覆盖结果:" + mapToString(c.toMap());
                LOGGER.info(log);

                // override datasource.xml
                String name = configure.getName();
                if (name != null) {
                    DataSourceConfigure dataSourceXml =
                            DataSourceConfigureHolder.getInstance().getUserDataSourceConfigure(name);
                    if (dataSourceXml != null) {
                        overrideDataSourceConfigure(c, dataSourceXml);
                        String xmlLog = "datasource.xml 覆盖结果:" + mapToString(c.toMap());
                        LOGGER.info(xmlLog);
                    }
                }
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

    public Properties deepCopyProperties(Properties properties) {
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

        // Properties prop = lowlevel.getProperties();
        // setProperties(lowlevelMap, prop); // set properties from map
    }

    private void setProperties(Map<String, String> datasource, Properties prop) {
        if (datasource == null || prop == null)
            return;

        for (Map.Entry<String, String> entry : datasource.entrySet()) {
            prop.setProperty(entry.getKey(), entry.getValue());
        }
    }

    public String mapToString(Map<String, String> map) {
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

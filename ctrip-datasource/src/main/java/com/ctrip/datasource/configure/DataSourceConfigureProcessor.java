package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DatabasePoolConfig;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigConstants;
import com.dianping.cat.Cat;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.MapConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceConfigureProcessor extends DatabasePoolConfigConstants {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigureProcessor.class);
  private static final String DAL_APPNAME = "fx_dal";
  private static final String DAL_DATASOURCE_PROPERTIES = "dal.datasource.properties";
  private static final String SEPARATOR = "\\.";
  private static DatabasePoolConfig globalPoolConfig = null;
  private static DatabasePoolConfig appPoolConfig = null;
  private static Map<String, DatabasePoolConfig> datasourcePoolConfig = null;

  static {
    // Thread safe
    setGlobalDataSourceConfig();
    setAppDataSourceConfig();
  }

  private static void setGlobalDataSourceConfig() {
    try {
      MapConfig config = MapConfig.get(DAL_APPNAME, DAL_DATASOURCE_PROPERTIES, null);
      if (config != null) {
        Map<String, String> datasource = config.asMap();
        Map<String, String> map = new HashMap<>(datasource); // avoid UnsupportedOperationException
        globalPoolConfig = new DatabasePoolConfig();
        setDataSourceConfig(globalPoolConfig, map);
        LOGGER.info("全局DataSource配置:" + mapToString(datasource));
      }
    } catch (Throwable e) {
      String msg = "从QConfig读取全局DataSource配置时发生异常:";
      LOGGER.warn(msg + e.getMessage(), e);
      Cat.logError(msg + e.getMessage(), e);
    }
  }

  private static void setAppDataSourceConfig() {
    try {
      MapConfig config = MapConfig.get(DAL_DATASOURCE_PROPERTIES);
      if (config != null) {
        Map<String, String> map = config.asMap();
        Map<String, String> datasource = new HashMap<>(); // app level
        Map<String, Map<String, String>> datasourceMap = new HashMap<>(); // datasource level
        processAppDatasourceConfig(map, datasource, datasourceMap);
        appPoolConfig = new DatabasePoolConfig();
        setDataSourceConfig(appPoolConfig, datasource);
        datasourcePoolConfig = new ConcurrentHashMap<>();
        setDataSourceConfigMap(datasourcePoolConfig, datasourceMap);
        LOGGER.info("App DataSource配置:" + mapToString(map));
      }
    } catch (Throwable e) {
      String msg = "从QConfig读取App DataSource配置时发生异常，如果您没有使用配置中心，可以忽略这个异常:";
      LOGGER.warn(msg + e.getMessage(), e);
    }
  }

  private static void setDataSourceConfig(DatabasePoolConfig poolConfig, Map<String, String> datasource) {
    if (poolConfig == null || datasource.size() == 0)
      return;

    poolConfig.setMap(datasource);
    // PoolProperties
    /*
     * PoolProperties prop = poolConfig.getPoolProperties(); setPoolProperties(datasource, prop);
     */
    // Option
    String option = datasource.get(OPTION);
    if (option != null)
      poolConfig.setOption(option);
  }

  private static void setDataSourceConfigMap(Map<String, DatabasePoolConfig> poolConfigMap,
      Map<String, Map<String, String>> datasourceMap) {
    if (poolConfigMap == null || datasourceMap.size() == 0)
      return;
    for (Map.Entry<String, Map<String, String>> entry : datasourceMap.entrySet()) {
      DatabasePoolConfig config = new DatabasePoolConfig();
      setDataSourceConfig(config, entry.getValue());
      poolConfigMap.put(entry.getKey(), config);
    }
  }

  private static void processAppDatasourceConfig(Map<String, String> map, Map<String, String> datasource,
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
   * datasource.xml Override order: Config center global datasource <-- datasource.xml <-- Config center app datasource
   * <-- Config center per datasource
   */
  public static DatabasePoolConfig getDatabasePoolConfig(DatabasePoolConfig config) {
    DatabasePoolConfig c = cloneDatabasePoolConfig(globalPoolConfig);
    if (config != null) {
      overrideDatabasePoolConfig(c, config);
      LOGGER.info("datasource.xml 覆盖结果:" + mapToString(c.getMap()));
    }
    if (appPoolConfig != null) {
      overrideDatabasePoolConfig(c, appPoolConfig);
      LOGGER.info("App 覆盖结果:" + mapToString(c.getMap()));
    }
    String name = config.getName();
    if (name != null && datasourcePoolConfig != null) {
      DatabasePoolConfig poolConfig = datasourcePoolConfig.get(name);
      if (poolConfig != null) {
        overrideDatabasePoolConfig(c, poolConfig);
        LOGGER.info(name + " 覆盖结果:" + c.getMap());
      }
    }

    Map<String, String> datasource = c.getMap();
    PoolProperties prop = c.getPoolProperties();
    setPoolProperties(datasource, prop);
    return c;
  }

  private static DatabasePoolConfig cloneDatabasePoolConfig(DatabasePoolConfig config) {
    DatabasePoolConfig c = new DatabasePoolConfig();
    if (config == null)
      return c;
    c.setName(config.getName());
    c.setMap(config.getMap());
    c.setPoolProperties(config.getPoolProperties());
    c.setOption(config.getOption());
    return c;
  }

  private static void overrideDatabasePoolConfig(DatabasePoolConfig lowlevel, DatabasePoolConfig highlevel) {
    if (lowlevel == null || highlevel == null)
      return;
    String option = highlevel.getOption();
    if (option != null)
      lowlevel.setOption(option);
    Map<String, String> lowlevelMap = lowlevel.getMap();
    Map<String, String> highlevelMap = highlevel.getMap();
    if (lowlevelMap == null || highlevelMap == null)
      return;
    for (Map.Entry<String, String> entry : highlevelMap.entrySet()) {
      lowlevelMap.put(entry.getKey(), entry.getValue()); // override entry of map
    }
  }

  private static void setPoolProperties(Map<String, String> datasource, PoolProperties prop) {
    if (datasource == null || prop == null)
      return;

    String testWhileIdle = datasource.get(TESTWHILEIDLE);
    if (testWhileIdle != null)
      prop.setTestWhileIdle(Boolean.parseBoolean(testWhileIdle));

    String testOnBorrow = datasource.get(TESTONBORROW);
    if (testOnBorrow != null)
      prop.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));

    String testOnReturn = datasource.get(TESTONRETURN);
    if (testOnReturn != null)
      prop.setTestOnReturn(Boolean.parseBoolean(testOnReturn));

    String validationQuery = datasource.get(VALIDATIONQUERY);
    if (validationQuery != null)
      prop.setValidationQuery(validationQuery);

    String validationInterval = datasource.get(VALIDATIONINTERVAL);
    if (validationInterval != null)
      prop.setValidationInterval(Long.parseLong(validationInterval));

    String timeBetweenEvictionRunsMillis = datasource.get(TIMEBETWEENEVICTIONRUNSMILLIS);
    if (timeBetweenEvictionRunsMillis != null)
      prop.setTimeBetweenEvictionRunsMillis(Integer.parseInt(timeBetweenEvictionRunsMillis));

    String maxAge = datasource.get(MAX_AGE);
    if (maxAge != null)
      prop.setMaxAge(Integer.parseInt(maxAge));

    String maxActive = datasource.get(MAXACTIVE);
    if (maxActive != null)
      prop.setMaxActive(Integer.parseInt(maxActive));

    String minIdle = datasource.get(MINIDLE);
    if (minIdle != null)
      prop.setMinIdle(Integer.parseInt(minIdle));

    String maxWait = datasource.get(MAXWAIT);
    if (maxWait != null)
      prop.setMaxWait(Integer.parseInt(maxWait));

    String initialSize = datasource.get(INITIALSIZE);
    if (initialSize != null)
      prop.setInitialSize(Integer.parseInt(initialSize));

    String removeAbandonedTimeout = datasource.get(REMOVEABANDONEDTIMEOUT);
    if (removeAbandonedTimeout != null)
      prop.setRemoveAbandonedTimeout(Integer.parseInt(removeAbandonedTimeout));

    String removeAbandoned = datasource.get(REMOVEABANDONED);
    if (removeAbandoned != null)
      prop.setRemoveAbandoned(Boolean.parseBoolean(removeAbandoned));

    String logAbandoned = datasource.get(LOGABANDONED);
    if (logAbandoned != null)
      prop.setLogAbandoned(Boolean.parseBoolean(logAbandoned));

    String minEvictableIdleTimeMillis = datasource.get(MINEVICTABLEIDLETIMEMILLIS);
    if (minEvictableIdleTimeMillis != null)
      prop.setMinEvictableIdleTimeMillis(Integer.parseInt(minEvictableIdleTimeMillis));

    String connectionProperties = datasource.get(CONNECTIONPROPERTIES);
    if (connectionProperties != null)
      prop.setConnectionProperties(connectionProperties);

    String initSql = datasource.get(INIT_SQL);
    if (initSql != null)
      prop.setInitSQL(initSql);

    String initSQL = datasource.get(INIT_SQL2);
    if (initSQL != null)
      prop.setInitSQL(initSQL);
  }

  private static String mapToString(Map<String, String> map) {
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

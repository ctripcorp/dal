package com.ctrip.platform.dal.dao.configure.dalproperties;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.common.enums.ImplicitAllShardsSwitch;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lilj on 2018/7/22.
 */
public class DefaultDalPropertiesLocator implements DalPropertiesLocator {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    public static final String TABLE_PARSE_SWITCH_KEYNAME = "TableParseSwitch";
    private static final String DAL_PROPERTIES_SET_TABLE_PARSE_SWITCH = "DalProperties::setTableParseSwitch";
    public static final String IMPLICIT_ALL_SHARDS_SWITCH = "ImplicitAllShardsSwitch";
    private static final String SET_IMPLICIT_ALL_SHARDS_SWITCH = "DalProperties::setImplicitAllShardsSwitch";
    private static final String SET_ALL_PROPERTIES = "DalProperties::setAllProperties";
    public static final String STATEMENT_INTERCEPTORS = "statementInterceptors";
    public static final String TABLE_PARSER_CACHE_SIZE = "tableParserCacheSize";
    public static final String CUSTOM_DAL_CLIENT = "customDalClient";
    public static final String TABLE_PARSER_CACHE_KEY_BYTES = "tableParserCacheKeyBytes";
    public static final String ENABLE_UCS_CONTEXT_LOG = "enableUcsContextLog";
    public static final String DATASOURCE_MONITOR_FILTER_EXCEPTIONS = "datasourceMonitorFilterExceptions";
    public static final String DAO_PACKAGE_PATH = "daoPackagePath";

    private static final String PROPERTY_NAME_CLUSTER_INFO_QUERY_URL = "ClusterInfoQueryUrl";
    private static final String PROPERTY_NAME_DRC_STAGE = "DrcStage";
    private static final String PROPERTY_NAME_FORMAT_DRC_ROUTE_CTRL = "DrcStage.%s.Localized";

    private static final String CONNECTION_STRING_MYSQL_API_URL = "ConnectionStringMysqlApiUrl";
    private static final String CONNECTION_STRING_MYSQL_API_URL_SHAJQ = "ConnectionStringMysqlApiUrl_shajq";
    private static final String CONNECTION_STRING_MYSQL_API_URL_SHAOY = "ConnectionStringMysqlApiUrl_shaoy";
    private static final String CONNECTION_STRING_MYSQL_API_URL_SHARB = "ConnectionStringMysqlApiUrl_sharb";
    private static final String CONNECTION_STRING_MYSQL_API_URL_SHAFQ = "ConnectionStringMysqlApiUrl_shafq";

    private static final String DEFAULT_DRC_STAGE = "test";
    private static final String DEFAULT_DRC_LOCALIZED = "false";

    private AtomicReference<TableParseSwitch> tableParseSwitchRef = new AtomicReference<>(TableParseSwitch.ON);
    private AtomicReference<ImplicitAllShardsSwitch> implicitAllShardsSwitchRef = new AtomicReference<>(ImplicitAllShardsSwitch.OFF);
    private AtomicReference<Map<String, String>> allProperties = new AtomicReference<>(new HashMap<>());

    @Override
    public void setProperties(Map<String, String> properties) {
        if (properties == null || properties.isEmpty())
            return;

        setAllProperties(properties);
        setTableParseSwitch(properties);
        setImplicitAllShardsSwitch(properties);
    }

    private void setTableParseSwitch(Map<String, String> properties) {
        String value = properties.get(TABLE_PARSE_SWITCH_KEYNAME);
        if (value == null)
            return;

        Boolean status = Boolean.parseBoolean(value);
        TableParseSwitch tableParseSwitch = status ? TableParseSwitch.ON : TableParseSwitch.OFF;
        tableParseSwitchRef.set(tableParseSwitch);
        String message = String.format("TableParseSwitch status:%s", tableParseSwitch.toString());
        LOGGER.logEvent(DalLogTypes.DAL, DAL_PROPERTIES_SET_TABLE_PARSE_SWITCH, message);
    }

    @Override
    public TableParseSwitch getTableParseSwitch() {
        return tableParseSwitchRef.get();
    }

    @Override
    public Map<String, ErrorCodeInfo> getErrorCodes() {
        throw new UnsupportedOperationException("getErrorCodes not supported.");
    }

    private void setImplicitAllShardsSwitch(Map<String, String> properties) {
        String value = properties.get(IMPLICIT_ALL_SHARDS_SWITCH);
        if (value == null)
            return;

        Boolean status = Boolean.parseBoolean(value);
        ImplicitAllShardsSwitch implicitAllShardsSwitch = status ? ImplicitAllShardsSwitch.ON : ImplicitAllShardsSwitch.OFF;
        implicitAllShardsSwitchRef.set(implicitAllShardsSwitch);
        String message = String.format("ImplicitAllShardsSwitch status:%s", implicitAllShardsSwitch.toString());
        LOGGER.logEvent(DalLogTypes.DAL, SET_IMPLICIT_ALL_SHARDS_SWITCH, message);
    }

    @Override
    public ImplicitAllShardsSwitch getImplicitAllShardsSwitch() {
        return implicitAllShardsSwitchRef.get();
    }

    private void setAllProperties(Map<String, String> properties) {
        Map<String, String> currProperties = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            if (key != null)
                currProperties.put(key.toLowerCase(), entry.getValue());
        }
        Map<String, String> prevProperties = allProperties.getAndSet(currProperties);
        String message = String.format("current: %s; previous: %s", currProperties.toString(), prevProperties.toString());
        LOGGER.logEvent(DalLogTypes.DAL, SET_ALL_PROPERTIES, message);
    }

    @Override
    public String getClusterInfoQueryUrl() {
        return getProperty(PROPERTY_NAME_CLUSTER_INFO_QUERY_URL);
    }

    @Override
    public String getProperty(String name) {
        return allProperties.get().get(name.toLowerCase());
    }

    @Override
    public String getConnectionStringMysqlApiUrl() {
        return getProperty(CONNECTION_STRING_MYSQL_API_URL);
    }

    @Override
    public String getStatementInterceptor() {
        return getProperty(STATEMENT_INTERCEPTORS);
    }

    @Override
    public String getTableParserCacheInitSize(String defaultSize) {
        return getProperty(TABLE_PARSER_CACHE_SIZE, defaultSize);
    }

    @Override
    public int getTableParserCacheKeyBytes(String defaultBytes) {
        return Integer.valueOf(getProperty(TABLE_PARSER_CACHE_KEY_BYTES, defaultBytes));
    }

    @Override
    public String getCustomerClientClassName() {
        return getProperty(CUSTOM_DAL_CLIENT);
    }

    @Override
    public boolean enableUcsContextLog() {
        return Boolean.valueOf(getProperty(ENABLE_UCS_CONTEXT_LOG, "true"));
    }

    @Override
    public String ignoreExceptionsForDataSourceMonitor() {
        return getProperty(DATASOURCE_MONITOR_FILTER_EXCEPTIONS);
    }

    @Override
    public Set<String> getDaoPackagesPath() {
        Set<String> result = new HashSet<>();
        String packageString = getProperty(DAO_PACKAGE_PATH, "com.ctrip");
        String[] packageStrings = packageString.split(",");

        for (String pack : packageStrings) {
            result.add(pack.trim());
        }
        return result;
    }

    private String getProperty(String name, String defaultValue) {
        String value = allProperties.get().get(name.toLowerCase());
        return StringUtils.isEmpty(value) ? defaultValue : value.trim();
    }

}

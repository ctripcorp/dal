package com.ctrip.platform.dal.dao.configure.dalproperties;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.common.enums.ImplicitAllShardsSwitch;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.HashMap;
import java.util.Map;
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

    private static final String PROPERTY_NAME_CLUSTER_INFO_QUERY_URL = "ClusterInfoQueryUrl";
    private static final String PROPERTY_NAME_DRC_STAGE = "DrcStage";
    private static final String PROPERTY_NAME_FORMAT_DRC_ROUTE_CTRL = "DrcStage.%s.Localized";

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
    public boolean localizedForDrc() {
        return localizedForDrc(null);
    }

    @Override
    public boolean localizedForDrc(String situation) {
        String drcStage = getProperty(PROPERTY_NAME_DRC_STAGE, DEFAULT_DRC_STAGE);
        String propKey = String.format(PROPERTY_NAME_FORMAT_DRC_ROUTE_CTRL, drcStage);
        String localized = getProperty(propKey, DEFAULT_DRC_LOCALIZED);
        if (!StringUtils.isEmpty(situation)) {
            propKey = String.format(PROPERTY_NAME_FORMAT_DRC_ROUTE_CTRL, String.format("%s.%s", drcStage, situation));
            localized = getProperty(propKey, localized);
        }
        return Boolean.parseBoolean(localized);
    }

    @Override
    public String getProperty(String name) {
        return allProperties.get().get(name.toLowerCase());
    }

    private String getProperty(String name, String defaultValue) {
        String value = allProperties.get().get(name.toLowerCase());
        return StringUtils.isEmpty(value) ? defaultValue : value.trim();
    }

}

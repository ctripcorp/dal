package com.ctrip.platform.dal.dao.configure.dalproperties;

import com.ctrip.platform.dal.common.enums.ImplicitAllShardsSwitch;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
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

    private AtomicReference<TableParseSwitch> tableParseSwitchRef = new AtomicReference<>(TableParseSwitch.ON);
    private AtomicReference<ImplicitAllShardsSwitch> implicitAllShardsSwitchRef = new AtomicReference<>(ImplicitAllShardsSwitch.OFF);

    @Override
    public void setProperties(Map<String, String> properties) {
        if (properties == null || properties.isEmpty())
            return;

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
        LOGGER.logEvent(DalLogTypes.DAL,SET_IMPLICIT_ALL_SHARDS_SWITCH, message);
    }

    @Override
    public ImplicitAllShardsSwitch getImplicitAllShardsSwitch() {
        return implicitAllShardsSwitchRef.get();
    }

}

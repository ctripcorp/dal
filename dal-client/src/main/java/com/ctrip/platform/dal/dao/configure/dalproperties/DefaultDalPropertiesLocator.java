package com.ctrip.platform.dal.dao.configure.dalproperties;

import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lilj on 2018/7/22.
 */
public class DefaultDalPropertiesLocator implements DalPropertiesLocator {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String SWITCH_KEYNAME = "TableParseSwitch";
    private static final String DAL = "DAL";
    private static final String DAL_PROPERTIES_SET_TABLE_PARSE_SWITCH = "DalProperties::setTableParseSwitch";

    private AtomicReference<TableParseSwitch> tableParseSwitchRef = new AtomicReference<>(TableParseSwitch.ON);

    @Override
    public void setProperties(Map<String, String> properties) {
        if (properties == null || properties.isEmpty())
            return;

        setTableParseSwitch(properties);
    }

    private void setTableParseSwitch(Map<String, String> properties) {
        String value = properties.get(SWITCH_KEYNAME);
        if (value == null)
            return;

        Boolean status = Boolean.parseBoolean(value);
        TableParseSwitch tableParseSwitch = status ? TableParseSwitch.ON : TableParseSwitch.OFF;
        tableParseSwitchRef.set(tableParseSwitch);
        String message = String.format("TableParseSwitch status:%s", tableParseSwitch.toString());
        LOGGER.logEvent(DAL, DAL_PROPERTIES_SET_TABLE_PARSE_SWITCH, message);
    }

    @Override
    public TableParseSwitch getTableParseSwitch() {
        return tableParseSwitchRef.get();
    }

    @Override
    public Map<String, ErrorCodeInfo> getErrorCodes() {
        throw new UnsupportedOperationException("getErrorCodes not supported.");
    }

}

package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.datasource.datasource.DalPropertiesChanged;
import com.ctrip.datasource.datasource.DalPropertiesProvider;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;

/**
 * Created by lilj on 2018/7/24.
 */
public class AbstractDalPropertiesProvider implements DalPropertiesProvider {
    protected TableParseSwitch defaultStatus = TableParseSwitch.ON;

    @Override
    public TableParseSwitch getTableParseSwitch() {
        return defaultStatus;
    }

    @Override
    public void addTableParseSwitchChangedListener(DalPropertiesChanged callback) {}

}

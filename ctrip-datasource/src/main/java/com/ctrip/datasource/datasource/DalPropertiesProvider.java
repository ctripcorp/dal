package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.common.enums.TableParseSwitch;

/**
 * Created by lilj on 2018/7/22.
 */
public interface DalPropertiesProvider {
    TableParseSwitch getTableParseSwitch();

    void addTableParseSwitchChangedListener(final DalPropertiesChanged callback);
}

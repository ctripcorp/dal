package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.common.enums.TableParseSwitch;

/**
 * Created by lilj on 2018/7/22.
 */
public interface DalPropertiesChanged {
    void onTableParseSwitchChanged(TableParseSwitch status);
}

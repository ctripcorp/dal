package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.helper.Ordered;


/**
 * Created by lilj on 2018/7/22.
 */
public interface DalPropertiesLocator {
    void setTableParseSwitch(TableParseSwitch status);

    TableParseSwitch getTableParseSwitch();
}

package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.common.enums.TableParseSwitch;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lilj on 2018/7/22.
 */
public class DefaultDalPropertiesLocator implements DalPropertiesLocator {
    private AtomicReference<TableParseSwitch> tableParseSwitchReference = new AtomicReference<>(TableParseSwitch.ON);

    @Override
    public void setTableParseSwitch(TableParseSwitch status) {
        tableParseSwitchReference.set(status);
    }

    @Override
    public TableParseSwitch getTableParseSwitch() {
        return tableParseSwitchReference.get();
    }

}

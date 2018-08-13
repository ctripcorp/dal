package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.datasource.datasource.DalPropertiesChanged;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lilj on 2018/7/24.
 */
public class LocalDalPropertiesProvider extends AbstractDalPropertiesProvider {
    private AtomicBoolean atomicStatus = new AtomicBoolean(true);
    private DalPropertiesChanged callback;

    public void setOn() {
        defaultStatus = TableParseSwitch.ON;
    }

    public void setOff() {
        defaultStatus = TableParseSwitch.OFF;
    }

    public void initStatus() {
        TableParseSwitch status = getTableParseSwitch();
        boolean value = status.equals(TableParseSwitch.ON) ? true : false;
        atomicStatus.set(value);
    }

    public void triggerTableParseSwitchChanged() {
        boolean value = atomicStatus.get();
        value = !value;
        atomicStatus.set(value);

        TableParseSwitch status = value ? TableParseSwitch.ON : TableParseSwitch.OFF;
        System.out.println(String.format("********** Current status: %s **********", status.toString()));
        callback.onTableParseSwitchChanged(status);
    }

    @Override
    public void addTableParseSwitchChangedListener(final DalPropertiesChanged callback) {
        if (callback == null)
            return;

        this.callback = callback;
    }
}

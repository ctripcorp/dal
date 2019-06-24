package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.common.enums.ImplicitAllShardsSwitch;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesChanged;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lilj on 2018/7/24.
 */
public class LocalDalPropertiesProvider extends AbstractDalPropertiesProvider {
    private AtomicBoolean atomicStatus = new AtomicBoolean(true);
    private AtomicBoolean implicitAllShardsStatus = new AtomicBoolean(false);
    private DalPropertiesChanged callback;

    public void setOn() {
        defaultStatus = true;
    }

    public void setOff() {
        defaultStatus = false;
    }

    public void initStatus() {
        Map<String, String> map = getProperties();
        boolean value = Boolean.parseBoolean(map.get(SWITCH_KEYNAME));
        atomicStatus.set(value);

        value = Boolean.parseBoolean(map.get(IMPLICIT_ALL_SHARDS_SWITCH_KEYNAME));
        implicitAllShardsStatus.set(value);
    }

    public void triggerTableParseSwitchChanged() {
        boolean value = atomicStatus.get();
        value = !value;
        atomicStatus.set(value);

        TableParseSwitch status = value ? TableParseSwitch.ON : TableParseSwitch.OFF;
        System.out.println(String.format("********** Current status: %s **********", status.toString()));

        Map<String, String> map = new HashMap<>();
        map.put(SWITCH_KEYNAME, new Boolean(value).toString());
        callback.onChanged(map);
    }

    public void triggerImplicitAllShardsSwitchChanged() {
        boolean value = implicitAllShardsStatus.get();
        value = !value;
        implicitAllShardsStatus.set(value);

        ImplicitAllShardsSwitch status = value ? ImplicitAllShardsSwitch.ON : ImplicitAllShardsSwitch.OFF;
        System.out.println(String.format("********** Current status: %s **********", status.toString()));

        Map<String, String> map = new HashMap<>();
        map.put(IMPLICIT_ALL_SHARDS_SWITCH_KEYNAME, new Boolean(value).toString());
        callback.onChanged(map);
    }

    @Override
    public void addPropertiesChangedListener(final DalPropertiesChanged callback) {
        if (callback == null)
            return;

        this.callback = callback;
    }

}

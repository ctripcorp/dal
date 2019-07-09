package com.ctrip.platform.dal.daogen.DynamicDS;

import com.ctrip.platform.dal.daogen.entity.SwitchHostIPInfo;
import com.ctrip.platform.dal.daogen.entity.SwitchTitanKey;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by taochen on 2019/7/3.
 */
public interface SwitchDSDataProvider {
    public boolean isSwitchInAppID(String titanKey, String appID, Date checkTime, List<SwitchHostIPInfo> hostIPList, String env);

    public Set<SwitchTitanKey> getSwitchTitanKey(Date checkTime, String env);
}

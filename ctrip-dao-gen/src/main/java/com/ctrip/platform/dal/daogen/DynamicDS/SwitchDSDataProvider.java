package com.ctrip.platform.dal.daogen.DynamicDS;

import com.ctrip.platform.dal.daogen.entity.SwitchHostIPInfo;
import com.ctrip.platform.dal.daogen.entity.SwitchTitanKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by taochen on 2019/7/3.
 */
public interface SwitchDSDataProvider {
    public boolean isSwitchInAppID(String titanKey, String appID, String checkTime, List<String> hostIPList, Map<Integer, Integer> appIDSwitchTime, String env);

    public Set<SwitchTitanKey> getSwitchTitanKey(String checkTime, Set<String> checkTitanKeys, String env);
}

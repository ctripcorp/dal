package com.ctrip.platform.dal.daogen.DynamicDS;

import com.ctrip.platform.dal.daogen.entity.AppIDInfo;
import com.ctrip.platform.dal.daogen.entity.SwitchTitanKey;
import com.ctrip.platform.dal.daogen.entity.TransactionSimple;

import java.util.Set;

/**
 * Created by taochen on 2019/7/3.
 */
public interface SwitchDSDataProvider {
    public AppIDInfo checkSwitchInAppID(String titanKey, String checkTime, String appID, String env);

    public Set<SwitchTitanKey> getSwitchTitanKey(String checkTime, Set<String> checkTitanKeys, String env);

    public TransactionSimple getTransactionSimpleByMessageId(String appID, String ip, long hour, int index);
}

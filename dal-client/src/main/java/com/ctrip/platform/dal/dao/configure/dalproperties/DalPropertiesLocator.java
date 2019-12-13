package com.ctrip.platform.dal.dao.configure.dalproperties;

import com.ctrip.framework.dal.cluster.client.base.Listenable;
import com.ctrip.platform.dal.common.enums.ImplicitAllShardsSwitch;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;

import java.util.Map;

/**
 * Created by lilj on 2018/7/22.
 */
public interface DalPropertiesLocator extends Listenable<Void> {

    void setProperties(Map<String, String> properties);

    void refresh(Map<String, String> properties);

    TableParseSwitch getTableParseSwitch();

    Map<String, ErrorCodeInfo> getErrorCodes();

    ImplicitAllShardsSwitch getImplicitAllShardsSwitch();

    String getClusterInfoQueryUrl();

    boolean localizedForDrc();

    String getProperty(String name);

}

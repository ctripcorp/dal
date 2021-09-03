package com.ctrip.platform.dal.dao.configure;


import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.common.enums.DBModel;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface MultiHostConnectionStringConfigure extends DalConnectionStringConfigure {

    List<HostSpec> getHosts();

    String getDbName();

    String getZonesPriority();

    Long getFailoverTimeMS();

    Long getBlacklistTimeoutMS();

    Long getFixedValidatePeriodMS();

    boolean isMultiMaster();

    DBModel getDbModel();

    @Override
    default String getHostName() {
        return null;
    }

}

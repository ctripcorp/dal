package com.ctrip.platform.dal.dao.configure;


import com.ctrip.platform.dal.cluster.base.HostSpec;

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

    @Override
    default String getHostName() {
        return null;
    }

}

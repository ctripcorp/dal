package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.datasource.datasource.IPDomainStatusChanged;
import com.ctrip.datasource.datasource.IPDomainStatusProvider;
import com.ctrip.platform.dal.common.enums.IPDomainStatus;

public class AbstractIPDomainStatusProvider implements IPDomainStatusProvider {
    protected IPDomainStatus defaultStatus = IPDomainStatus.IP;

    @Override
    public IPDomainStatus getStatus() {
        return defaultStatus;
    }

    @Override
    public void addIPDomainStatusChangedListener(IPDomainStatusChanged callback) {}

}

package com.ctrip.datasource.configure.qconfig;

import com.ctrip.datasource.datasource.IPDomainStatusChanged;
import com.ctrip.datasource.datasource.IPDomainStatusProvider;
import com.ctrip.platform.dal.common.enums.IPDomainStatus;

public class TempIPDomainStatusProviderImpl implements IPDomainStatusProvider {
    @Override
    public IPDomainStatus getStatus() {
        return IPDomainStatus.IP;
    }

    @Override
    public void addIPDomainStatusChangedListener(IPDomainStatusChanged callback) {

    }
}

package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;

public interface IPDomainStatusProvider {
    IPDomainStatus getStatus();

    void addIPDomainStatusChangedListener(final IPDomainStatusChanged callback);
}

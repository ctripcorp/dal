package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;

public interface IPDomainStatusChanged {
    void onChanged(IPDomainStatus status);
}

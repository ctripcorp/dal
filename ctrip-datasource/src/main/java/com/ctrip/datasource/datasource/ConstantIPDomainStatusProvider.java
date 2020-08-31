package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;

/**
 * @author c7ch23en
 */
public class ConstantIPDomainStatusProvider implements IPDomainStatusProvider {

    private final IPDomainStatus status;

    public ConstantIPDomainStatusProvider(IPDomainStatus status) {
        this.status = status;
    }

    @Override
    public IPDomainStatus getStatus() {
        return status;
    }

    @Override
    public void addIPDomainStatusChangedListener(IPDomainStatusChanged callback) {}

}

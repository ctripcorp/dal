package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.datasource.datasource.IPDomainStatusChanged;
import com.ctrip.datasource.datasource.IPDomainStatusProvider;

public class AbstractIPDomainStatusProvider implements IPDomainStatusProvider {
    @Override
    public void addIPDomainStatusChangedListener(IPDomainStatusChanged callback) {}
}

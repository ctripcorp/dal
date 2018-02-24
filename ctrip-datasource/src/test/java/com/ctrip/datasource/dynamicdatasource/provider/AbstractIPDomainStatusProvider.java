package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.datasource.IPDomainStatusChanged;
import com.ctrip.platform.dal.dao.datasource.IPDomainStatusProvider;

public class AbstractIPDomainStatusProvider implements IPDomainStatusProvider {
    @Override
    public void addIPDomainStatusChangedListener(IPDomainStatusChanged callback) {}
}

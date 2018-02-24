package com.ctrip.platform.dal.dao.configure.file;

import com.ctrip.platform.dal.dao.datasource.IPDomainStatusChanged;
import com.ctrip.platform.dal.dao.datasource.IPDomainStatusProvider;

public class PropertyFileIPDomainStatusProvider implements IPDomainStatusProvider {
    @Override
    public void addIPDomainStatusChangedListener(IPDomainStatusChanged callback) {}
}

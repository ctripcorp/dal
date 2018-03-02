package com.ctrip.datasource.configure.file;

import com.ctrip.datasource.datasource.IPDomainStatusChanged;
import com.ctrip.datasource.datasource.IPDomainStatusProvider;

public class PropertyFileIPDomainStatusProvider implements IPDomainStatusProvider {
    @Override
    public void addIPDomainStatusChangedListener(IPDomainStatusChanged callback) {}
}

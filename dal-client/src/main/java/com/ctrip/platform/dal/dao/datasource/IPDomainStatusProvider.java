package com.ctrip.platform.dal.dao.datasource;

public interface IPDomainStatusProvider {
    void addIPDomainStatusChangedListener(final IPDomainStatusChanged callback);
}

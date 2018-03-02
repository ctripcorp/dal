package com.ctrip.datasource.datasource;

public interface IPDomainStatusProvider {
    void addIPDomainStatusChangedListener(final IPDomainStatusChanged callback);
}

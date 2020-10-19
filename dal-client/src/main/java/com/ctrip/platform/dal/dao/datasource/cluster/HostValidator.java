package com.ctrip.platform.dal.dao.datasource.cluster;

public interface HostValidator {

    boolean available(HostSpec host);

    void triggerValidate();
}

package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.DalConfigCustomizedClass;

public class DefaultDalConfigCustomizedClass implements DalConfigCustomizedClass {

    private String consistencyTypeCustomizedClass;

    @Override
    public String getConsistencyTypeCustomizedClass() {
        return consistencyTypeCustomizedClass;
    }

    public void setConsistencyTypeCustomizedClass(String consistencyTypeCustomizedClass) {
        this.consistencyTypeCustomizedClass = consistencyTypeCustomizedClass;
    }
}

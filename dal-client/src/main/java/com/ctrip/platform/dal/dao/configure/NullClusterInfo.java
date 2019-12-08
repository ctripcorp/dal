package com.ctrip.platform.dal.dao.configure;

public class NullClusterInfo extends ClusterInfo {

    @Override
    public boolean isValid() {
        return false;
    }

}

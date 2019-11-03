package com.ctrip.framework.dal.dbconfig.plugin.entity.dal;

import java.util.List;

/**
 * Created by shenjie on 2019/7/10.
 */
public class DalClusterUpdateInputEntity {
    private String env;
    private List<DalClusterEntity> data;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public List<DalClusterEntity> getData() {
        return data;
    }

    public void setData(List<DalClusterEntity> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DalClusterUpdateInputEntity{" +
                "env='" + env + '\'' +
                ", data=" + data +
                '}';
    }
}

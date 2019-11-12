package com.ctrip.framework.dal.dbconfig.plugin.entity.dal;

import java.util.List;

/**
 * Created by shenjie on 2019/8/8.
 */
public class DalClusterAddInputEntity {
    private List<DalClusterEntity> data;

    public DalClusterAddInputEntity() {
    }

    public DalClusterAddInputEntity(List<DalClusterEntity> data) {
        this.data = data;
    }

    public List<DalClusterEntity> getData() {
        return data;
    }

    public void setData(List<DalClusterEntity> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DalClusterAddInputEntity{" +
                "data=" + data +
                '}';
    }
}

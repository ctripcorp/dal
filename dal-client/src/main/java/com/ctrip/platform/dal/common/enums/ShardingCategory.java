package com.ctrip.platform.dal.common.enums;

/**
 * Created by lilj on 2018/9/6.
 */
public enum ShardingCategory {
    DBShard(0), TableShard(1), DBTableShard(2), NoShard(3);
    private int intVal;

    ShardingCategory(int intVal) {
        this.intVal = intVal;
    }

    public int getIntVal() {
        return intVal;
    }
}

package com.ctrip.platform.dal.common.enums;

/**
 * Created by lilj on 2018/7/9.
 */
public enum DalTransactionStatus {
    Initial(0), Commit(1), Rollback(2), Conflict(3);

    private int intVal;

    DalTransactionStatus(int intVal) {
        this.intVal = intVal;
    }

    public int getIntVal() {
        return intVal;
    }
}

package com.ctrip.platform.dal.common.enums;

/**
 * Created by lilj on 2018/7/22.
 */
public enum TableParseSwitch {
    ON(0), OFF(1);

    private int intVal;

    TableParseSwitch(int intVal) {
        this.intVal = intVal;
    }

    public int getIntVal() {
        return intVal;
    }
}

package com.ctrip.platform.dal.common.enums;

public enum ImplicitAllShardsSwitch {
    ON(0), OFF(1);

    private int intVal;

    ImplicitAllShardsSwitch(int intVal) {
        this.intVal = intVal;
    }

    public int getIntVal() {
        return intVal;
    }
}

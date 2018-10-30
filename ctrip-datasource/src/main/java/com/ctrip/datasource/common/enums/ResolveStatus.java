package com.ctrip.datasource.common.enums;

public enum ResolveStatus {
    Success(0), Fail(1);

    private int intVal;

    ResolveStatus(int intVal) {
        this.intVal = intVal;
    }

    public int getIntVal() {
        return intVal;
    }

}

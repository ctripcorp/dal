package com.ctrip.datasource.common.enums;

public enum SourceType {
    Local(0), Remote(1);

    private int intVal;

    SourceType(int intVal) {
        this.intVal = intVal;
    }

    public int getIntVal() {
        return intVal;
    }

}

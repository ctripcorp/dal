package com.ctrip.platform.dal.cluster.cluster;

import com.ctrip.platform.dal.cluster.exception.ClusterRuntimeException;

public enum ClusterType {

    NORMAL("normal"),

    DRC("drc");

    private String value;

    ClusterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ClusterType parse(String value) {
        if (NORMAL.getValue().equalsIgnoreCase(value))
            return NORMAL;
        if (DRC.getValue().equalsIgnoreCase(value))
            return DRC;
        throw new ClusterRuntimeException("Invalid cluster type");
    }

}

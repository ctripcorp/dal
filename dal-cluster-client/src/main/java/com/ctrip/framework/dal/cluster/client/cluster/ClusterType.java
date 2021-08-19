package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;

public enum ClusterType {

    NORMAL("normal") {
        @Override
        public boolean isAllMaster() {
            return false;
        }
    },
    DRC("drc") {
        @Override
        public boolean isAllMaster() {
            return false;
        }
    },
    MGR("mgr") {
        @Override
        public boolean isAllMaster() {
            return true;
        }
    },
    OB("ob") {
        @Override
        public boolean isAllMaster() {
            return true;
        }
    };

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
        if (MGR.getValue().equalsIgnoreCase(value))
            return MGR;
        if (OB.getValue().equalsIgnoreCase(value))
            return OB;
        throw new ClusterRuntimeException("Invalid cluster type");
    }

    public abstract boolean isAllMaster();

}

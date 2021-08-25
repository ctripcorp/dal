package com.ctrip.platform.dal.cluster.cluster;

import com.ctrip.platform.dal.cluster.exception.ClusterRuntimeException;

import static com.ctrip.platform.dal.cluster.config.ClusterConfigXMLConstants.LOCALIZED_ACCESS_STRATEGY;
import static com.ctrip.platform.dal.cluster.config.ClusterConfigXMLConstants.ORDERED_ACCESS_STRATEGY;

public enum ClusterType {

    NORMAL("normal") {
        @Override
        public boolean isAllMaster() {
            return false;
        }

        @Override
        public String defaultRouteStrategies() {
            throw new UnsupportedOperationException("not support");
        }
    },
    DRC("drc") {
        @Override
        public boolean isAllMaster() {
            return false;
        }

        @Override
        public String defaultRouteStrategies() {
            throw new UnsupportedOperationException("not support");
        }
    },
    MGR("mgr") {
        @Override
        public boolean isAllMaster() {
            return true;
        }

        @Override
        public String defaultRouteStrategies() {
            return ORDERED_ACCESS_STRATEGY;
        }
    },
    OB("ob") {
        @Override
        public boolean isAllMaster() {
            return true;
        }

        @Override
        public String defaultRouteStrategies() {
            return LOCALIZED_ACCESS_STRATEGY;
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

    public abstract String defaultRouteStrategies();

}

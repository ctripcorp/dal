package com.ctrip.platform.dal.cluster.database;

import com.ctrip.platform.dal.cluster.exception.ClusterRuntimeException;

/**
 * @author c7ch23en
 */
public enum DatabaseRole {

    MASTER("master"),
    SLAVE("slave");

    private String value;

    DatabaseRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DatabaseRole parse(String value) {
        if (MASTER.getValue().equalsIgnoreCase(value))
            return MASTER;
        if (SLAVE.getValue().equalsIgnoreCase(value))
            return SLAVE;
        throw new ClusterRuntimeException("invalid database role");
    }

}

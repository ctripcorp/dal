package com.ctrip.framework.dal.cluster.client.database;

import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;

/**
 * @author c7ch23en
 */
public enum DatabaseRole {

    MASTER("master"),
    SLAVE("slave"),
    MIX("mix"),
    SLAVES("mix-read");

    private String value;

    DatabaseRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DatabaseRole parse(String value) {
        for (DatabaseRole role : DatabaseRole.values())
            if (role.getValue().equalsIgnoreCase(value))
                return role;
        throw new ClusterRuntimeException("invalid database role");
    }

}

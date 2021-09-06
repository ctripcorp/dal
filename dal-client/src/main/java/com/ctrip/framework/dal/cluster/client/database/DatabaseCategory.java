package com.ctrip.framework.dal.cluster.client.database;

import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;

/**
 * @author c7ch23en
 */
public enum DatabaseCategory {

    MYSQL("mysql"),
    SQLSERVER("sqlserver");

    private String value;

    DatabaseCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DatabaseCategory parse(String value) {
        if (MYSQL.getValue().equalsIgnoreCase(value))
            return MYSQL;
        if (SQLSERVER.getValue().equalsIgnoreCase(value))
            return SQLSERVER;
        throw new ClusterRuntimeException("invalid database category");
    }

}

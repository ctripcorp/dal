package com.ctrip.platform.dal.common.enums;

public enum MySqlUrlTemplate {
    NORMAL_JDBC_URL_PREFIX("jdbc:mysql"),
    LOADBALANCE_JDBC_URL_PREFIX("jdbc:mysql:loadbalance"),
    REPLICATION_JDBC_URL_PREFIX("jdbc:mysql:replication"),
    X_JDBC_URL_PREFIX("mysqlx");

    private String urlPrefix;
    MySqlUrlTemplate(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }
    public String getUrlPrefix() {
        return urlPrefix;
    }
}

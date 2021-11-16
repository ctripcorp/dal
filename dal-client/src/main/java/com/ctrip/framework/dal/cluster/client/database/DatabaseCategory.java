package com.ctrip.framework.dal.cluster.client.database;

/**
 * @author c7ch23en
 */
public enum DatabaseCategory {

    MYSQL("mysql"),
    SQLSERVER("sqlserver"),
    CUSTOM("__ignore");

    private String value;

    DatabaseCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DatabaseCategory parse(String value) {
        for (DatabaseCategory category : values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return category;
            }
        }
        return CUSTOM;
    }

}

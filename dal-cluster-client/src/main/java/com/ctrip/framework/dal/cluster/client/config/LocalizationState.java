package com.ctrip.framework.dal.cluster.client.config;

/**
 * @author c7ch23en
 */
public enum LocalizationState {

    NONE("none"),
    PREPARED("prepared"),
    ACTIVE("active");

    private String value;

    LocalizationState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

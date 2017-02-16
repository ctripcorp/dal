package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by yn.wang on 2017/2/15.
 */
public class Parameter {
    private String name;
    private int type;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

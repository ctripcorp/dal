package com.ctrip.platform.dal.daogen.enums;

public enum DbModeTypeEnum {
    Cluster(0, "dalcluster"),
    Titan(1, "titankey");

    DbModeTypeEnum(int code, String des) {
        this.code =  code;
        this.des = des;
    }

    private int code;
    private String des;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}

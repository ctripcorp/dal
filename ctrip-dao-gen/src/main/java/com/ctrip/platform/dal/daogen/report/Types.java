package com.ctrip.platform.dal.daogen.report;

import com.alibaba.fastjson.annotation.JSONField;

public class Types {
    @JSONField(name = "DAL.Version")
    private DALVersion dalVersion;

    public DALVersion getDalVersion() {
        return dalVersion;
    }

    public void setDalVersion(DALVersion dalVersion) {
        this.dalVersion = dalVersion;
    }
}

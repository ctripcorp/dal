package com.ctrip.platform.dal.daogen.report.newReport;

import com.alibaba.fastjson.annotation.JSONField;

public class TypeDetails {
    @JSONField(name = "DAL.version")
    private NewDALversion daLVersion;

    public NewDALversion getDaLVersion() {
        return daLVersion;
    }

    public void setDaLVersion(NewDALversion daLVersion) {
        this.daLVersion = daLVersion;
    }
}

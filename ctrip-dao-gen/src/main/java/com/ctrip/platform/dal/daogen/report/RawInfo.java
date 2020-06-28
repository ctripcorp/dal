package com.ctrip.platform.dal.daogen.report;

import java.util.List;

public class RawInfo {
    private List<String> depts;
    private List<String> versions;

    public List<String> getDepts() {
        return depts;
    }

    public void setDepts(List<String> depts) {
        this.depts = depts;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }
}

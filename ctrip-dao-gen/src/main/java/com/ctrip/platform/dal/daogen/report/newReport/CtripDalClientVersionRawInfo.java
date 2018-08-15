package com.ctrip.platform.dal.daogen.report.newReport;

import java.util.List;
import java.util.Set;

public class CtripDalClientVersionRawInfo {
    private List<String> depts;

    private List<String> versions;

    private Set<String> javaAppIds;

    private Set<String> netAppIds;

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

    public Set<String> getJavaAppIds() {
        return javaAppIds;
    }

    public void setJavaAppIds(Set<String> javaAppIds) {
        this.javaAppIds = javaAppIds;
    }

    public Set<String> getNetAppIds() {
        return netAppIds;
    }

    public void setNetAppIds(Set<String> netAppIds) {
        this.netAppIds = netAppIds;
    }

}

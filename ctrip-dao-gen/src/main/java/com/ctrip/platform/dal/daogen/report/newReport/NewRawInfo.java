package com.ctrip.platform.dal.daogen.report.newReport;

import java.util.HashSet;

public class NewRawInfo {
    private HashSet<String> depts;

    private HashSet<String> versions;

    public HashSet<String> getDepts() {
        return depts;
    }

    public void setDepts(HashSet<String> depts) {
        this.depts = depts;
    }

    public HashSet<String> getVersions() {
        return versions;
    }

    public void setVersions(HashSet<String> versions) {
        this.versions = versions;
    }

}

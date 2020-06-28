package com.ctrip.platform.dal.daogen.report.newReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CtripDatasourceVersionRawInfo {
    private List<String> depts;

    private List<String> versions;

    private Set<String> datasourceAppIds;

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

    public List<String> getFormattedVersions() {
        List<String> result = new ArrayList<>();
        if (versions == null || versions.isEmpty())
            return result;

        for (String version : versions) {
            String temp = String.format("datasource-%s", version);
            result.add(temp);
        }
        return result;
    }

    public Set<String> getDatasourceAppIds() {
        return datasourceAppIds;
    }

    public void setDatasourceAppIds(Set<String> datasourceAppIds) {
        this.datasourceAppIds = datasourceAppIds;
    }

}

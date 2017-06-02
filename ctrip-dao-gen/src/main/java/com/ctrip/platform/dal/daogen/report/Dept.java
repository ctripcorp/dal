package com.ctrip.platform.dal.daogen.report;

import java.util.List;

public class Dept {
    private String version;
    private List<App> apps;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }
}

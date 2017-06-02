package com.ctrip.platform.dal.daogen.report;

import java.util.List;

public class Client {
    private String dept;
    private List<App> apps;

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }
}

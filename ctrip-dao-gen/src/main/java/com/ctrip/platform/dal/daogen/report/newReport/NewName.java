package com.ctrip.platform.dal.daogen.report.newReport;

import java.util.Map;

public class NewName {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private Map<String, NewApp> appDetails;

    public Map<String, NewApp> getAppDetails() {
        return appDetails;
    }

    public void setAppDetails(Map<String, NewApp> appDetails) {
        this.appDetails = appDetails;
    }

}

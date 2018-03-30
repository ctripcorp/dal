package com.ctrip.platform.dal.daogen.report.newReport;

import java.util.Map;

public class SQLdatabase {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private Map<String, NewName> nameDetails;

    public Map<String, NewName> getNameDetails() {
        return nameDetails;
    }

    public void setNameDetails(Map<String, NewName> nameDetails) {
        this.nameDetails = nameDetails;
    }

}

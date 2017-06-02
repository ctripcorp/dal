package com.ctrip.platform.dal.daogen.report;

import java.util.List;

public class CMSAppInfo {
    private String message;
    private List<CMSApp> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CMSApp> getData() {
        return data;
    }

    public void setData(List<CMSApp> data) {
        this.data = data;
    }
}

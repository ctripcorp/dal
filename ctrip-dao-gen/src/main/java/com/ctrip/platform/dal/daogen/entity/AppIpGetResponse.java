package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/25.
 */
public class AppIpGetResponse {
    private Boolean status;

    private List<App> data;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<App> getData() {
        return data;
    }

    public void setData(List<App> data) {
        this.data = data;
    }
}

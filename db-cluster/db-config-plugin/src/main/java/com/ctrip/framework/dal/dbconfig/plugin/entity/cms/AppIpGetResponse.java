package com.ctrip.framework.dal.dbconfig.plugin.entity.cms;

import java.util.List;

/**
 * Created by shenjie on 2019/6/12.
 */
public class AppIpGetResponse {
    private Boolean status;
    private String message;
    private Integer total;
    private List<App> data;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<App> getData() {
        return data;
    }

    public void setData(List<App> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AppIpGetResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}

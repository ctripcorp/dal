package com.ctrip.framework.dal.dbconfig.plugin.entity.cms;

import java.util.List;

/**
 * Created by shenjie on 2019/6/12.
 */
public class App {
    private String appId;
    private List<Group> groups;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "App{" +
                "appId='" + appId + '\'' +
                ", groups=" + groups +
                '}';
    }
}

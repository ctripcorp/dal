package com.ctrip.framework.dal.dbconfig.plugin.entity.cms;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shenjie on 2019/6/11.
 */
public class Group {
    private String appId;

    @SerializedName("accessGroupMembers")
    private List<GroupMember> groupMembers;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    @Override
    public String toString() {
        return "Group{" +
                "appId='" + appId + '\'' +
                ", groupMembers=" + groupMembers +
                '}';
    }
}

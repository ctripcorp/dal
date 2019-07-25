package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/25.
 */
public class Group {
    private List<GroupMember> accessGroupMembers;

    public List<GroupMember> getAccessGroupMembers() {
        return accessGroupMembers;
    }

    public void setAccessGroupMembers(List<GroupMember> accessGroupMembers) {
        this.accessGroupMembers = accessGroupMembers;
    }
}

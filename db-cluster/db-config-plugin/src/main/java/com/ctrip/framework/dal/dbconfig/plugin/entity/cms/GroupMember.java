package com.ctrip.framework.dal.dbconfig.plugin.entity.cms;

/**
 * Created by shenjie on 2019/6/11.
 */
public class GroupMember {
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "ip='" + ip + '\'' +
                '}';
    }
}

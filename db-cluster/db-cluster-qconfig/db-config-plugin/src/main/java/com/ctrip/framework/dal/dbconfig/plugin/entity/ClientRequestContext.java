package com.ctrip.framework.dal.dbconfig.plugin.entity;

/**
 * Created by shenjie on 2019/5/10.
 */
public class ClientRequestContext {

    private String appId;
    private String ip;
    private String env;
    private boolean fromPublicNet;

    public ClientRequestContext() {
    }

    public ClientRequestContext(String appId, String ip, String env, boolean fromPublicNet) {
        this.appId = appId;
        this.ip = ip;
        this.env = env;
        this.fromPublicNet = fromPublicNet;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isFromPublicNet() {
        return fromPublicNet;
    }

    public void setFromPublicNet(boolean fromPublicNet) {
        this.fromPublicNet = fromPublicNet;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return "ClientRequestContext{" +
                "appId='" + appId + '\'' +
                ", ip='" + ip + '\'' +
                ", env='" + env + '\'' +
                ", fromPublicNet=" + fromPublicNet +
                '}';
    }
}

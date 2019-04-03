package com.ctrip.framework.dal.dbconfig.plugin.entity;

import java.util.List;

/**
 * Created by lzyan on 2018/10/24.
 */
public class AppIdIpCheckEntity {
    private String serviceUrl;
    private String clientAppId;
    private String clientIp;
    private String env;
    private String serviceToken;
    private int timeoutMs;
    private List<String> passCodeList;


    //setter/getter
    public String getServiceUrl() {
        return serviceUrl;
    }
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getClientAppId() {
        return clientAppId;
    }
    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }

    public String getClientIp() {
        return clientIp;
    }
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getEnv() {
        return env;
    }
    public void setEnv(String env) {
        this.env = env;
    }

    public String getServiceToken() {
        return serviceToken;
    }
    public void setServiceToken(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }
    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public List<String> getPassCodeList() {
        return passCodeList;
    }
    public void setPassCodeList(List<String> passCodeList) {
        this.passCodeList = passCodeList;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppIdIpCheckEntity that = (AppIdIpCheckEntity) o;

        if (!getClientAppId().equals(that.getClientAppId())) return false;
        if (!getClientIp().equals(that.getClientIp())) return false;
        return getEnv().equals(that.getEnv());
    }

    @Override
    public int hashCode() {
        int result = getClientAppId().hashCode();
        result = 31 * result + getClientIp().hashCode();
        result = 31 * result + getEnv().hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppIdIpCheckEntity{");
        sb.append("serviceUrl='").append(serviceUrl).append('\'');
        sb.append(", clientAppId='").append(clientAppId).append('\'');
        sb.append(", clientIp='").append(clientIp).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", serviceToken='").append(serviceToken).append('\'');
        sb.append(", timeoutMs=").append(timeoutMs);
        sb.append('}');
        return sb.toString();
    }

}

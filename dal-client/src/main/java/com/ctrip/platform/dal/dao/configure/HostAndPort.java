package com.ctrip.platform.dal.dao.configure;

public class HostAndPort {

    private String host;
    private Integer port;
    private String connectionUrl;
    private boolean isValid = false;

    public HostAndPort() {}

    public HostAndPort(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public HostAndPort(String connectionUrl, String host, Integer port) {
        this.connectionUrl = connectionUrl;
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

}

package com.ctrip.platform.dal.dao.configure;


public class SwitchableDataSourceStatus {
    private boolean isForceSwitched;
    private String hostName;
    private Integer port;
    private boolean isConnected;

    public SwitchableDataSourceStatus(String hostName, Integer port) {
        this.hostName = hostName;
        this.port = port;
    }

    public SwitchableDataSourceStatus(boolean isForceSwitched, String hostName, Integer port) {
        this(hostName, port);
        this.isForceSwitched = isForceSwitched;
    }

    public SwitchableDataSourceStatus(boolean isForceSwitched, String hostName, Integer port, boolean isConnected) {
        this(isForceSwitched, hostName, port);
        this.isConnected = isConnected;
    }

    public boolean isForceSwitched() {
        return isForceSwitched;
    }

    public String getHostName() {
        return hostName;
    }

    public Integer getPort() {
        return port;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public SwitchableDataSourceStatus fork() {
        return new SwitchableDataSourceStatus(isForceSwitched, hostName, port, isConnected);
    }
}

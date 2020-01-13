package com.ctrip.platform.dal.dao.configure;


public class SwitchableDataSourceStatus {
    private boolean isForceSwitched;
    private String hostName;
    private Integer port;
    private boolean poolCreated;

    public SwitchableDataSourceStatus(String hostName, Integer port) {
        this.hostName = hostName;
        this.port = port;
    }

    public SwitchableDataSourceStatus(boolean isForceSwitched, String hostName, Integer port) {
        this(hostName, port);
        this.isForceSwitched = isForceSwitched;
    }

    public SwitchableDataSourceStatus(boolean isForceSwitched, String hostName, Integer port, boolean poolCreated) {
        this(isForceSwitched, hostName, port);
        this.poolCreated = poolCreated;
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

    public boolean isPoolCreated() {
        return poolCreated;
    }

    public String toString() {
        return String.format("isForceSwitched: %s, poolCreated: %s, hostName: %s, port: %s", isForceSwitched, poolCreated, hostName, port);
    }
}

package com.ctrip.framework.dal.cluster.client.base;

import java.util.Objects;

/**
 * @author c7ch23en
 */
public class HostSpec {

    private final String m_host;
    private final int m_port;
    private final String m_zone;
    private final boolean isMaster;

    public static HostSpec of(String host, int port) {
        return new HostSpec(host, port);
    }

    public static HostSpec of(String host, int port, String zone) {
        return new HostSpec(host, port, zone);
    }

    public static HostSpec of(String host, int port, String zone, boolean isMaster) {
        return new HostSpec(host, port, zone, isMaster);
    }

    public HostSpec(String host, int port) {
        this(host, port, null);
    }

    public HostSpec(String host, int port, String zone) {
        this(host, port, zone, false);
    }

    public HostSpec(String host, int port, String zone, boolean isMaster) {
        this.m_host = host != null ? host.toLowerCase() : null;
        this.m_port = port;
        this.m_zone = zone != null ? zone.toUpperCase() : null;
        this.isMaster = isMaster;
    }

    public String host() {
        return m_host;
    }

    public int port() {
        return m_port;
    }

    public String zone() {
        return m_zone;
    }

    public boolean isMaster() {
        return isMaster;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HostSpec host = (HostSpec) o;
        return m_port == host.m_port && Objects.equals(m_host, host.m_host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_host, m_port);
    }

    @Override
    public String toString() {
        return m_host + ':' + m_port + "::" + m_zone;
    }

}

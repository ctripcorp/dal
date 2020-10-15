package com.ctrip.platform.dal.dao.datasource.cluster;

import java.util.Objects;

/**
 * @author c7ch23en
 */
public class HostSpec {

    private final String m_host;
    private final int m_port;
    private final String m_zone;

    public static HostSpec create(String host, int port, String zone) {
        return new HostSpec(host, port, zone);
    }

    public HostSpec(String host, int port, String zone) {
        this.m_host = host != null ? host.toLowerCase() : null;
        this.m_port = port;
        this.m_zone = zone != null ? zone.toUpperCase() : null;
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

    @Override
    public HostSpec clone() {
        HostSpec clone = new HostSpec(this.m_host, this.m_port, this.m_zone);
        return clone;
    }

}

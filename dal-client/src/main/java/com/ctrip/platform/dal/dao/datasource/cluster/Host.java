package com.ctrip.platform.dal.dao.datasource.cluster;

import java.util.Objects;

/**
 * @author c7ch23en
 */
public class Host {

    private final String m_host;
    private final int m_port;

    public static Host build(String host, int port) {
        return new Host(host, port);
    }

    public Host(String host, int port) {
        this.m_host = host;
        this.m_port = port;
    }

    public String host() {
        return m_host;
    }

    public int port() {
        return m_port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Host host = (Host) o;
        return m_port == host.m_port && Objects.equals(m_host, host.m_host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_host, m_port);
    }

    @Override
    public String toString() {
        return m_host + ':' + m_port;
    }

}

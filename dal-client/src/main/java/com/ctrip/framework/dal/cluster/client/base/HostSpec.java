package com.ctrip.framework.dal.cluster.client.base;

import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;

import java.util.Objects;

/**
 * @author c7ch23en
 */
public class HostSpec {

    private final String m_host;
    private final int m_port;
    private final String m_zone;
    private final boolean isMaster;

    private final static String HOST_SPEC_ERROR = " of %s zone msg lost";
    private volatile String trim_lower_zone = null;

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
        return m_port == host.m_port && Objects.equals(m_host, host.m_host) && isMaster == host.isMaster;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_host, m_port, isMaster);
    }

    @Override
    public String toString() {
        return m_host + ':' + m_port + "::" + m_zone + "::" + isMaster;
    }

    public String getTrimLowerCaseZone() {
        if (trim_lower_zone != null)
            return trim_lower_zone;

        if (StringUtils.isTrimmedEmpty(m_zone))
            throw new DalMetadataException(String.format(HOST_SPEC_ERROR, toString()));

        trim_lower_zone = m_zone.trim().toLowerCase();
        return trim_lower_zone;
    }

}

package com.ctrip.platform.dal.dao.datasource.cluster;


import com.ctrip.framework.dal.cluster.client.base.HostSpec;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public class DefaultHostConnection extends ConnectionDelegate implements HostConnection {

    private final Connection connection;
    private final HostSpec host;

    public DefaultHostConnection(Connection connection, HostSpec host) {
        this.connection = connection;
        this.host = host;
    }

    @Override
    public HostSpec getHost() {
        return host;
    }

    @Override
    public Connection getDelegated() {
        return connection;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this))
            return iface.cast(this);
        return super.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isInstance(this))
            return true;
        return super.isWrapperFor(iface);
    }

}

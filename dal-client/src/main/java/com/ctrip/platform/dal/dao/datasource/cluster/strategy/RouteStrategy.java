package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.exceptions.DalException;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface RouteStrategy extends RouteStrategyLifecycle, ExceptionInterceptor, Wrapper {

    String NO_HOST_AVAILABLE = "Router::noHostAvailable:%s";

    HostSpec pickNode(DalHints hints) throws HostNotExpectedException;

    default  <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return iface.cast(this);
        } catch (ClassCastException cce) {
            throw new DalException("Unable to unwrap to " + iface.toString());
        }
    }

    default boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(this);
    }
}
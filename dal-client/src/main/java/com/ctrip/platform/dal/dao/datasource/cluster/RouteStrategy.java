package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface RouteStrategy {

    Connection pickConnection(ConnectionFactory connFactory, RequestContext requestCtx, RouteOptions options)
            throws SQLException;

}

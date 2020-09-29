package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;

/**
 * @author c7ch23en
 */
public interface RouteStrategy {

    void install(RouteConfig config);

    Connection getConnection(ConnectionFactory factory, RequestContext context);

    void uninstall();

}

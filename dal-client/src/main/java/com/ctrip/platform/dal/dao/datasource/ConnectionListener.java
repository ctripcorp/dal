package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.Ordered;

import java.sql.Connection;

public interface ConnectionListener extends Ordered {

    void onCreateConnection(String poolDesc, Connection connection, DataSourceIdentity dataSourceId, long startTime);

    void onCreateConnectionFailed(String poolDesc, String connDesc, DataSourceIdentity dataSourceId, Throwable exception, long startTime);

    void onReleaseConnection(String poolDesc, Connection connection);

    void onAbandonConnection(String poolDesc, Connection connection);

    void onWaitConnection(String poolDEsc, Connection connection, long startTime);

}

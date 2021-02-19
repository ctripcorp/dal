package com.ctrip.platform.dal.dao.datasource.jdbc;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.cluster.DataSourceDelegate;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;
import com.ctrip.platform.dal.dao.datasource.monitor.DataSourceMonitor;
import com.ctrip.platform.dal.dao.datasource.monitor.DefaultDataSourceMonitor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public abstract class DalDataSource extends DataSourceDelegate {

    private final DataSourceMonitor monitor;

    public DalDataSource(DataSourceIdentity dataSourceId) {
        monitor = new DefaultDataSourceMonitor(dataSourceId);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        return new DalConnection(connection, this, createSqlContext());
    }

    protected abstract SqlContext createSqlContext();

    public abstract DatabaseCategory getDatabaseCategory();

    public void handleException(SQLException e, boolean isUpdateOperation, Connection connection) {
        monitor.report(e, isUpdateOperation);
    }

}

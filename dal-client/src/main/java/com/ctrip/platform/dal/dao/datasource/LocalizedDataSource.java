package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

import java.sql.Connection;
import java.sql.SQLException;

public class LocalizedDataSource extends RefreshableDataSource {

    private LocalizationValidator validator;

    public LocalizedDataSource(String name, DataSourceConfigure config) {
        this(LocalizationValidator.DEFAULT, name, config);
    }

    public LocalizedDataSource(LocalizationValidator validator, String name, DataSourceConfigure config) {
        super(name, config);
        this.validator = validator;
    }

    public LocalizedDataSource(DataSourceIdentity id, DataSourceConfigure config) {
        this(LocalizationValidator.DEFAULT, id, config);
    }

    public LocalizedDataSource(LocalizationValidator validator, DataSourceIdentity id, DataSourceConfigure config) {
        super(id, config);
        this.validator = validator;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        if (connection != null && validator != null)
            try {
                validator.validateZone();
            } catch (Throwable t) {
                // ignore
            }
        return connection != null ? new LocalizedConnection(validator, connection) : null;
    }

    @Override
    public Connection getConnection(String paramString1, String paramString2) throws SQLException {
        Connection connection = super.getConnection(paramString1, paramString2);
        if (connection != null && validator != null)
            try {
                validator.validateZone();
            } catch (Throwable t) {
                // ignore
            }
        return connection != null ? new LocalizedConnection(validator, connection) : null;
    }

}

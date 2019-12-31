package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

import java.sql.Connection;
import java.sql.SQLException;

public class LocalizedDataSource extends RefreshableDataSource {

    private LocalizationValidator validator;
    private LocalizationConfig localizationConfig;

    public LocalizedDataSource(String name, DataSourceConfigure config) {
        this(LocalizationValidator.DEFAULT, null, name, config);
    }

    public LocalizedDataSource(LocalizationValidator validator, LocalizationConfig localizationConfig, String name, DataSourceConfigure config) {
        super(name, config);
        this.validator = validator;
        this.localizationConfig = localizationConfig;
    }

    public LocalizedDataSource(DataSourceIdentity id, DataSourceConfigure config) {
        this(LocalizationValidator.DEFAULT, null, id, config);
    }

    public LocalizedDataSource(LocalizationValidator validator, LocalizationConfig localizationConfig, DataSourceIdentity id, DataSourceConfigure config) {
        super(id, config);
        this.validator = validator;
        this.localizationConfig = localizationConfig;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        return connection != null ? new LocalizedConnection(validator, localizationConfig, connection) : null;
    }

    @Override
    public Connection getConnection(String paramString1, String paramString2) throws SQLException {
        Connection connection = super.getConnection(paramString1, paramString2);
        return connection != null ? new LocalizedConnection(validator, localizationConfig, connection) : null;
    }

}

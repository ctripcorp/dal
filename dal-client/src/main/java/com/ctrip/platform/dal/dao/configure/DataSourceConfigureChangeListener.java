package com.ctrip.platform.dal.dao.configure;

import java.sql.SQLException;

public interface DataSourceConfigureChangeListener {
    void configChanged(DataSourceConfigure config) throws SQLException;
}

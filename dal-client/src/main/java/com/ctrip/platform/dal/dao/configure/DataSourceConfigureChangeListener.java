package com.ctrip.platform.dal.dao.configure;

import java.sql.SQLException;

public interface DataSourceConfigureChangeListener {
    void configChanged(DataSourceConfigureChangeEvent event) throws SQLException;
}

package com.ctrip.platform.dal.dao.datasource.monitor;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface DataSourceMonitor {

    void report(SQLException e, boolean isUpdateOperation);

}

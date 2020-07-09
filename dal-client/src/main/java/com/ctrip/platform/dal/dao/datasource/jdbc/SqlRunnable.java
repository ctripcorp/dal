package com.ctrip.platform.dal.dao.datasource.jdbc;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface SqlRunnable {

    void run() throws SQLException;

}

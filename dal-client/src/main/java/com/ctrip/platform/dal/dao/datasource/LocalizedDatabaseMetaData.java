package com.ctrip.platform.dal.dao.datasource;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface LocalizedDatabaseMetaData extends DatabaseMetaData {

    String getLocalizedURL() throws SQLException;

}

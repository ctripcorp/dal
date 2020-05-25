package com.ctrip.platform.dal.dao.datasource.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface DalDatabaseMetaData extends DatabaseMetaData {

    String getExtendedURL() throws SQLException;

}

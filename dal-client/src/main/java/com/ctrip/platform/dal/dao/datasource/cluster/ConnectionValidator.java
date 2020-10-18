package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface ConnectionValidator {

    boolean validate(Connection connection) throws SQLException;

}

package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface ConnectionValidator {

    boolean validate(HostConnection connection) throws SQLException;

}

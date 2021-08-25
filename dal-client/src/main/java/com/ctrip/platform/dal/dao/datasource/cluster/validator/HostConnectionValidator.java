package com.ctrip.platform.dal.dao.datasource.cluster.validator;

import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface HostConnectionValidator {

    boolean validate(HostConnection connection) throws SQLException;

}

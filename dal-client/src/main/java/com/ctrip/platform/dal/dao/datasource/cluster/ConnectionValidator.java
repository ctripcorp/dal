package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;

/**
 * @author c7ch23en
 */
public interface ConnectionValidator {

    Result validate(Connection connection);

    enum Result {
        OK,
        FAILED,
        UNKNOWN
    }

}

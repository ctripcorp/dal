package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;

/**
 * @author c7ch23en
 */
public interface ConnectionValidator {

    Result validate(Connection connection, RouteOptions options);

    enum Result {
        OK,
        FAILED,
        UNKNOWN
    }

}

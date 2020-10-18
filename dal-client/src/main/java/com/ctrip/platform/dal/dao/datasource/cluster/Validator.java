package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;

public interface Validator {

    /**
     * according to type select corresponding way validate the connection
     * @param connection
     * @param type 1(default):execute select sql; 2:execute insert/update sql; 3:execute create table sql;
     * @return
     */
    boolean validate(Connection connection, int type);
}

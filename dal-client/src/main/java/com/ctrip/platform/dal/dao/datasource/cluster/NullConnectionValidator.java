package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class NullConnectionValidator implements ConnectionValidator {

    @Override
    public boolean validate(Connection connection) {
        return true;
    }

}

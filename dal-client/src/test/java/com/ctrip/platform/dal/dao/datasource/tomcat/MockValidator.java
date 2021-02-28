package com.ctrip.platform.dal.dao.datasource.tomcat;

import org.apache.tomcat.jdbc.pool.Validator;

import java.sql.Connection;

/**
 * @author c7ch23en
 */
public class MockValidator implements Validator {

    private int count = 0;

    @Override
    public boolean validate(Connection connection, int i) {
        System.out.println("validate start");
        throw new MockException("mock validate exception");
//        boolean result = ++count % 2 > 0;
//        System.out.println("validate result: " + result);
//        return result;
    }

}

package com.ctrip.platform.dal.dao.util;

import com.ctrip.platform.dal.dao.datasource.DataSourceValidator;
import org.apache.tomcat.jdbc.pool.Validator;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author c7ch23en
 */
public class MockValidator extends DataSourceValidator implements Validator {

    private final AtomicBoolean toggled = new AtomicBoolean(false);
    private long start = System.currentTimeMillis();

    @Override
    public boolean validate(Connection connection, int validateAction) {
        boolean supRes = super.validate(connection, validateAction);
        boolean result = supRes && !((System.currentTimeMillis() - start) > 7000 && toggled.compareAndSet(false, true));
        System.out.println("validation: " + result);
        return result;
    }

}

package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.datasource.log.OperationType;
import org.junit.Test;

import static org.junit.Assert.*;

public class UcsPostValidatorTest {

    @Test
    public void dalValidate() {
        UcsPostValidator validator = new UcsPostValidator(){

            @Override
            public boolean validate(UcsConsistencyValidateContext context) {
                return false;
            }
        };
        assertEquals(true, validator.dalValidate(System.currentTimeMillis() - 1900, false, OperationType.QUERY));
        assertEquals(true, validator.dalValidate(System.currentTimeMillis() - 2100, true, OperationType.QUERY));
        assertEquals(true, validator.dalValidate(System.currentTimeMillis() - 1900, true, OperationType.QUERY));
        assertEquals(true, validator.dalValidate(System.currentTimeMillis() - 2100, false, OperationType.QUERY));
        assertEquals(false, validator.dalValidate(System.currentTimeMillis() - 1900, false, OperationType.QUERY));
    }
}
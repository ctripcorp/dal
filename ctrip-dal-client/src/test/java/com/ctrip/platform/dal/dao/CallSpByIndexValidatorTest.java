package com.ctrip.platform.dal.dao;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class CallSpByIndexValidatorTest {

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        CtripTaskFactory.callSpbyName = false;
    }
    
    @Test
    public void testValidationSuccess() throws SQLException {
        DalParser<?> parser = new PeopleParser();
        CallSpByIndexValidator.validate(parser, true);
        CallSpByIndexValidator.validate(parser, false);
    }
    
    @Test
    public void testValidationFail() throws SQLException {
        DalParser<?> parser = new DalDefaultJpaParser<>(PeopleOldVersion.class);
        CallSpByIndexValidator.validate(parser, true);
        
        try {
            CallSpByIndexValidator.validate(parser, false);
            Assert.fail();
        } catch (Exception e) {
        }        
    }
}

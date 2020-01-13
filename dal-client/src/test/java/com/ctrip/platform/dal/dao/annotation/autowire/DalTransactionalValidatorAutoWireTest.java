package com.ctrip.platform.dal.dao.annotation.autowire;

import com.ctrip.platform.dal.dao.client.DalAnnotationValidator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DalTransactionalValidatorAutoWireTest {
    @Test
    public void testValidateFail() throws InstantiationException, IllegalAccessException {
        ApplicationContext ctx;
        try {
            ctx = new ClassPathXmlApplicationContext("transactionTestFailByAutowire.xml");
            Assert.fail();
        } catch (BeansException e) {
            Assert.assertTrue(e.getMessage().contains(DalAnnotationValidator.VALIDATION_MSG));
        }
    }   
}

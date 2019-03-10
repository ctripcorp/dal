package com.ctrip.platform.dal.dao.annotation.javaConfig;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class EnableTransactionAbnormalTest {

    @Test
    public void testPostProcessBeforeInitialization() throws Exception {
        try {
            new ClassPathXmlApplicationContext("transactionAbnormalTest.xml");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("privateMethod()"));
            Assert.assertTrue(e.getMessage().contains("finalMethod()"));
            Assert.assertTrue(e.getMessage().contains("staticMethod()"));
            Assert.assertFalse(e.getMessage().contains("publicMethod()"));
            Assert.assertFalse(e.getMessage().contains("protectedMethod()"));
            Assert.assertFalse(e.getMessage().contains("defaultMethod()"));
        }
    }

}

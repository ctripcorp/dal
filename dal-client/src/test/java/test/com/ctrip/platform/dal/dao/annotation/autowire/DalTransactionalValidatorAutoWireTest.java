package test.com.ctrip.platform.dal.dao.annotation.autowire;

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
            Assert.assertTrue(e.getCause().getMessage().contains("Bean annotated by @Transactional must be created through DalTransactionManager.create()"));
        }
    }   
}

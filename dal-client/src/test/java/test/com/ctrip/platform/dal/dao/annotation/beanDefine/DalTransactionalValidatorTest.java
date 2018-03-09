package test.com.ctrip.platform.dal.dao.annotation.beanDefine;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ctrip.platform.dal.dao.client.DalAnnotationValidator;

public class DalTransactionalValidatorTest {
    @Test
    public void testValidatePass() throws InstantiationException, IllegalAccessException {
        ApplicationContext ctx;
        try {
            ctx = new ClassPathXmlApplicationContext("transactionTestByBeanDef.xml");
            TransactionAnnoClass b = ctx.getBean(TransactionAnnoClass.class);
            b.perform();
            Assert.assertNotNull(b.getTest());
            b.performOld();
        } catch (BeansException e) {
            Assert.fail();
        }
    }   
    
    @Test
    public void testValidateFail() throws InstantiationException, IllegalAccessException {
        ApplicationContext ctx;
        try {
            ctx = new ClassPathXmlApplicationContext("transactionTestByBeanDefFail.xml");
            Assert.fail();
        } catch (BeansException e) {
            Assert.assertTrue(e.getMessage().contains(DalAnnotationValidator.VALIDATION_MSG));
        }
    }   
}

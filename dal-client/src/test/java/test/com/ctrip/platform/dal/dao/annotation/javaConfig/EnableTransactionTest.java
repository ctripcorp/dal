package test.com.ctrip.platform.dal.dao.annotation.javaConfig;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class EnableTransactionTest {
    @Test
    public void testPostProcessBeforeInitialization() throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext("test.com.ctrip.platform.dal.dao.annotation.javaConfig", "com.ctrip.platform.dal.dao.client");
        TransactionAnnoClass bean = ctx.getBean(TransactionAnnoClass.class);
        assertNull(bean.perform());
        assertNotNull(bean.getTest());
        assertNull(bean.performOld());
    }
}

package com.ctrip.platform.dal.dao.annotation.javaConfig;

import com.ctrip.platform.dal.dao.annotation.javaConfig.normal.TransactionAnnoClass;
import com.ctrip.platform.dal.dao.annotation.javaConfig.normal.TransactionConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TransactionConfig.class)
public class EnableTransactionNormalTest {

    @Autowired
    TransactionAnnoClass bean;

    @Test
    public void testPostProcessBeforeInitialization() throws Exception {
        assertNull(bean.perform());
        assertNotNull(bean.getTest());
        assertNull(bean.performOld());
    }

}

package com.ctrip.platform.dal.dao.tableDao.bitIntegerSetIdentityBack;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class InsertBigIntegerSetIdentityBack {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testBigIntegerSetIdentityBack() {
        try {
            TestBigintIdentityDao dao = new TestBigintIdentityDao();
            TestBigintIdentity pojo = new TestBigintIdentity();
            pojo.setName("Test");
            DalHints hints = new DalHints();
            hints.setIdentityBack();
            int result = dao.insert(hints, pojo);
            Assert.assertTrue(result > 0);
            Assert.assertTrue(pojo.getId().longValue() > 0);
        } catch (Throwable e) {
            System.out.println(e);
            Assert.assertFalse(true);
        }

    }

}

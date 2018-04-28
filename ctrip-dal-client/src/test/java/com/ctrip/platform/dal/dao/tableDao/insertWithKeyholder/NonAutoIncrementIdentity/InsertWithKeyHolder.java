package com.ctrip.platform.dal.dao.tableDao.insertWithKeyholder.NonAutoIncrementIdentity;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class InsertWithKeyHolder {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testInsertByNonAutoIncrementIdentity() {
        try {
            TestNonAutoIncrementIdentityDao dao = new TestNonAutoIncrementIdentityDao();
            DalHints hints = new DalHints();
            KeyHolder holder = new KeyHolder();
            TestNonAutoIncrementIdentity pojo1 = new TestNonAutoIncrementIdentity();
            pojo1.setId(1);
            pojo1.setName("Test1");

            dao.insert(hints, holder, pojo1);
            Assert.assertTrue(true);
        } catch (Throwable e) {
            System.out.println(e);
            Assert.assertFalse(true);
        }
    }

    @Test
    public void testInsertArrayByNonAutoIncrementIdentity() {
        try {
            TestNonAutoIncrementIdentityDao dao = new TestNonAutoIncrementIdentityDao();
            DalHints hints = new DalHints();
            KeyHolder holder = new KeyHolder();
            List<TestNonAutoIncrementIdentity> list = new ArrayList<>();
            TestNonAutoIncrementIdentity pojo2 = new TestNonAutoIncrementIdentity();
            pojo2.setId(2);
            pojo2.setName("Test2");
            list.add(pojo2);
            TestNonAutoIncrementIdentity pojo3 = new TestNonAutoIncrementIdentity();
            pojo3.setId(3);
            pojo3.setName("Test3");
            list.add(pojo3);

            dao.insert(hints, holder, list);
            Assert.assertTrue(true);
        } catch (Throwable e) {
            System.out.println(e);
            Assert.assertFalse(true);
        }
    }

    @Test
    public void testCombinedInsertByNonAutoIncrementIdentity() {
        try {
            TestNonAutoIncrementIdentityDao dao = new TestNonAutoIncrementIdentityDao();
            DalHints hints = new DalHints();
            KeyHolder holder = new KeyHolder();
            List<TestNonAutoIncrementIdentity> list = new ArrayList<>();
            TestNonAutoIncrementIdentity pojo4 = new TestNonAutoIncrementIdentity();
            pojo4.setId(4);
            pojo4.setName("Test4");
            list.add(pojo4);
            TestNonAutoIncrementIdentity pojo5 = new TestNonAutoIncrementIdentity();
            pojo5.setId(5);
            pojo5.setName("Test5");
            list.add(pojo5);
            TestNonAutoIncrementIdentity pojo6 = new TestNonAutoIncrementIdentity();
            pojo6.setId(6);
            pojo6.setName("Test6");
            list.add(pojo6);

            dao.combinedInsert(hints, holder, list);
            Assert.assertTrue(true);
        } catch (Throwable e) {
            System.out.println(e);
            Assert.assertFalse(true);
        }
    }

    @Test
    public void testInsertSetIdentityBack() {
        try {
            TestNonAutoIncrementIdentityDao dao = new TestNonAutoIncrementIdentityDao();
            DalHints hints = new DalHints();
            hints.setIdentityBack();
            KeyHolder holder = new KeyHolder();
            TestNonAutoIncrementIdentity pojo1 = new TestNonAutoIncrementIdentity();
            pojo1.setId(7);
            pojo1.setName("Test7");

            dao.insert(hints, holder, pojo1);
            Assert.assertTrue(pojo1.getId() != null);
        } catch (Throwable e) {
            System.out.println(e);
            Assert.assertFalse(true);
        }
    }

}

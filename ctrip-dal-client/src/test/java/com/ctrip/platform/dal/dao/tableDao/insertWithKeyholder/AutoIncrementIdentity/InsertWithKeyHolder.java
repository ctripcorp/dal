package com.ctrip.platform.dal.dao.tableDao.insertWithKeyholder.AutoIncrementIdentity;

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
    public void testInsertByAutoIncrementIdentity() {
        try {
            TestAutoIncrementIdentityDao dao = new TestAutoIncrementIdentityDao();
            DalHints hints = new DalHints();
            KeyHolder holder = new KeyHolder();
            TestAutoIncrementIdentity pojo = new TestAutoIncrementIdentity();
            pojo.setId(1);
            pojo.setName("Test1");

            dao.insert(hints, holder, pojo);
            int key = holder.getKey().intValue();
            System.out.print(String.format("******************** identity key:%s ********************", key));
            Assert.assertTrue(key > 0);
        } catch (Throwable e) {
            System.out.println(e);
            Assert.assertFalse(true);
        }
    }

    @Test
    public void testInsertArrayByAutoIncrementIdentity() {
        try {
            TestAutoIncrementIdentityDao dao = new TestAutoIncrementIdentityDao();
            DalHints hints = new DalHints();
            KeyHolder holder = new KeyHolder();
            List<TestAutoIncrementIdentity> list = new ArrayList<>();
            TestAutoIncrementIdentity pojo2 = new TestAutoIncrementIdentity();
            pojo2.setId(2);
            pojo2.setName("Test2");
            list.add(pojo2);
            TestAutoIncrementIdentity pojo3 = new TestAutoIncrementIdentity();
            pojo3.setId(3);
            pojo3.setName("Test3");
            list.add(pojo3);

            dao.insert(hints, holder, list);
            List<Number> keys = holder.getIdList();
            for (Number key : keys) {
                System.out.println(String.format("******************** identity key:%s ********************", key));
                Assert.assertTrue(key.intValue() > 0);
            }

        } catch (Throwable e) {
            System.out.println(e);
            Assert.assertFalse(true);
        }
    }

    @Test
    public void testCombinedInsertByAutoIncrementIdentity() {
        try {
            TestAutoIncrementIdentityDao dao = new TestAutoIncrementIdentityDao();
            DalHints hints = new DalHints();
            KeyHolder holder = new KeyHolder();
            List<TestAutoIncrementIdentity> list = new ArrayList<>();
            TestAutoIncrementIdentity pojo4 = new TestAutoIncrementIdentity();
            pojo4.setId(4);
            pojo4.setName("Test4");
            list.add(pojo4);
            TestAutoIncrementIdentity pojo5 = new TestAutoIncrementIdentity();
            pojo5.setId(5);
            pojo5.setName("Test5");
            list.add(pojo5);

            dao.combinedInsert(hints, holder, list);
            List<Number> keys = holder.getIdList();
            for (Number key : keys) {
                System.out.println(String.format("******************** identity key:%s ********************", key));
                Assert.assertTrue(key.intValue() > 0);
            }

        } catch (Throwable e) {
            System.out.println(e);
            Assert.assertFalse(true);
        }
    }

}

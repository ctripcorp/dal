package com.ctrip.platform.dal.dao.client.DalCommand.test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalCommand.RollbackOnlyDalCommand;
import com.ctrip.platform.dal.dao.client.DalCommand.entity.TestTable;
import com.ctrip.platform.dal.dao.client.DalCommand.entity.TestTableDao;
import com.ctrip.platform.dal.dao.client.DalCommand.nesting.rollbackOnly.OneLayerRollbackOnlyDalCommand;
import com.ctrip.platform.dal.dao.client.DalCommand.nesting.rollbackOnly.OneLayerRollbackOnlyWithExceptionAfterDalCommand;
import com.ctrip.platform.dal.dao.client.DalCommand.nesting.rollbackOnly.OneLayerRollbackOnlyWithExceptionBeforeDalCommand;
import com.ctrip.platform.dal.dao.client.DalCommand.nesting.rollbackOnly.TwoLayerRollbackOnlyDalCommandWithRollbackOnlyAndException;
import com.ctrip.platform.dal.dao.client.DalTransaction;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

public class DalCommandRollbackOnlyTest {
    private static final String dbName = "dao_test";

    @Before
    public void setUp() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testRollbackOnlyDalCommand() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new RollbackOnlyDalCommand(), new DalHints());

            TestTableDao dao = new TestTableDao();
            TestTable t1 = new TestTable();
            t1.setName("r1");
            List<TestTable> list1 = dao.queryLike(t1);
            Assert.assertTrue((list1 == null) || (list1.size() == 0));

            TestTable t2 = new TestTable();
            t2.setName("r2");
            List<TestTable> list2 = dao.queryLike(t2);
            Assert.assertTrue((list2 == null) || (list2.size() == 0));

            Assert.assertTrue(isCurrentTransactionNull());
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void testRollbackOnlyWithExceptionAfterDalCommand() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new OneLayerRollbackOnlyWithExceptionAfterDalCommand(), new DalHints());

            TestTableDao dao = new TestTableDao();
            TestTable t1 = new TestTable();
            t1.setName("r1");
            List<TestTable> list1 = dao.queryLike(t1);
            Assert.assertTrue((list1 == null) || (list1.size() == 0));

            TestTable t2 = new TestTable();
            t2.setName("r2");
            List<TestTable> list2 = dao.queryLike(t2);
            Assert.assertTrue((list2 == null) || (list2.size() == 0));

            Assert.assertTrue(isCurrentTransactionNull());
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Manual throw exception.")) {
                Assert.assertTrue(true);
            } else {
                Assert.fail();
            }
        }
    }

    @Test
    public void testRollbackOnlyWithExceptionBeforeDalCommand() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new OneLayerRollbackOnlyWithExceptionBeforeDalCommand(), new DalHints());
            Assert.fail();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Manual throw exception.")) {
                TestTableDao dao = new TestTableDao();
                TestTable t1 = new TestTable();
                t1.setName("r1");
                List<TestTable> list1 = dao.queryLike(t1);
                Assert.assertTrue((list1 == null) || (list1.size() == 0));

                TestTable t2 = new TestTable();
                t2.setName("r2");
                List<TestTable> list2 = dao.queryLike(t2);
                Assert.assertTrue((list2 == null) || (list2.size() == 0));

                Assert.assertTrue(isCurrentTransactionNull());
                Assert.assertTrue(true);
            } else {
                Assert.fail();
            }
        }
    }

    @Test
    public void testNestingRollbackOnlyDalCommand() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new OneLayerRollbackOnlyDalCommand(), new DalHints());

            TestTableDao dao = new TestTableDao();
            TestTable t1 = new TestTable();
            t1.setName("r1");
            List<TestTable> list1 = dao.queryLike(t1);
            Assert.assertTrue((list1 == null) || (list1.size() == 0));

            TestTable t2 = new TestTable();
            t2.setName("r2");
            List<TestTable> list2 = dao.queryLike(t2);
            Assert.assertTrue((list2 == null) || (list2.size() == 0));

            Assert.assertTrue(isCurrentTransactionNull());
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void testNestingRollbackOnlyAndExceptionDalCommand() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new TwoLayerRollbackOnlyDalCommandWithRollbackOnlyAndException(), new DalHints());
            Assert.fail();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e.getMessage().equals(
                    "The state of nesting transactions are conflicted,transaction has been rollbacked. Transaction level 1 conflicted with level 2, original error message:[Manual throw exception.] , all levels of transaction status:[level 1, status:Conflict, actual status:Commit] [level 2, status:Rollback, actual status:Rollback] .")) {
                TestTableDao dao = new TestTableDao();
                TestTable t1 = new TestTable();
                t1.setName("r1");
                List<TestTable> list1 = dao.queryLike(t1);
                Assert.assertTrue((list1 == null) || (list1.size() == 0));

                TestTable t2 = new TestTable();
                t2.setName("r2");
                List<TestTable> list2 = dao.queryLike(t2);
                Assert.assertTrue((list2 == null) || (list2.size() == 0));

                Assert.assertTrue(isCurrentTransactionNull());
                Assert.assertTrue(true);
            } else {
                Assert.fail();
            }
        }
    }

    // check if the current transaction is null
    private Boolean isCurrentTransactionNull() throws Exception {
        Class clazz = DalTransactionManager.class;
        Field field = clazz.getDeclaredField("transactionHolder");
        field.setAccessible(true);
        Object value = field.get(clazz);
        ThreadLocal<DalTransaction> threadLocal = (ThreadLocal<DalTransaction>) value;
        DalTransaction dalTransaction = threadLocal.get();
        return dalTransaction == null;
    }

}

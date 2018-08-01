package test.com.ctrip.platform.dal.dao.client.DalCommand;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalTransaction;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import test.com.ctrip.platform.dal.dao.client.DalCommand.nesting.OneLayerConflictDalCommand;
import test.com.ctrip.platform.dal.dao.client.DalCommand.nesting.OneLayerExceptionDalCommand;
import test.com.ctrip.platform.dal.dao.client.DalCommand.nesting.OneLayerSuccessDalCommand;
import test.com.ctrip.platform.dal.dao.client.DalCommand.nesting.TwoLayerConflictDalCommand;
import test.com.ctrip.platform.dal.dao.client.DalCommand.nesting.TwoLayerExceptionDalCommand;
import test.com.ctrip.platform.dal.dao.client.DalCommand.nesting.TwoLayerSucessDalCommand;

import java.lang.reflect.Field;
import java.sql.BatchUpdateException;

public class DalCommandTest {
    private static final String dbName = "dao_test";

    @Before
    public void setUp() throws Exception {
        DalClientFactory.initClientFactory();
    }

    /// start
    // Test different execution order of mutiple transaction to
    // see if it will result in exception on each other.
    @Test
    public void testBatchOperationDalCommandCombine1() throws Exception {
        testBatchOperationSwallowExceptionInDalCommand();
        testBatchOperationThrowExceptionInDalCommand();
        testBatchOperationSuccessfulInDalCommand();
    }

    @Test
    public void testBatchOperationDalCommandCombine2() throws Exception {
        testBatchOperationThrowExceptionInDalCommand();
        testBatchOperationSwallowExceptionInDalCommand();
        testBatchOperationSuccessfulInDalCommand();
    }

    @Test
    public void testBatchOperationDalCommandCombine3() throws Exception {
        testBatchOperationSuccessfulInDalCommand();
        testBatchOperationThrowExceptionInDalCommand();
        testBatchOperationSwallowExceptionInDalCommand();
    }

    @Test
    public void testBatchOperationDalCommandCombine4() throws Exception {
        testBatchOperationSuccessfulInDalCommand();
        testBatchOperationSwallowExceptionInDalCommand();
        testBatchOperationThrowExceptionInDalCommand();
    }
    /// end

    // Two layer transaction conflicted which should throws conflicted exception
    @Test
    public void testBatchOperationSwallowExceptionInDalCommand() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new SwallowExceptionDalCommand(), new DalHints());
            Assert.fail();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(e.getMessage().equals(
                    "The state of nesting transactions are conflicted,transaction has been rollbacked. Transaction level 1 conflicted with level 2, original error message:[Data truncation: Data too long for column 'Name' at row 1] , all levels of transaction status:[level 1, status:Conflict, actual status:Commit] [level 2, status:Rollback, actual status:Rollback] ."));
            Assert.assertTrue(isCurrentTransactionNull());
        }
    }

    // Two layer transaction with exception which should throws actual exception
    @Test
    public void testBatchOperationThrowExceptionInDalCommand() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new ThrowExceptionDalCommand(), new DalHints());
            Assert.fail();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(e instanceof BatchUpdateException);
            Assert.assertTrue(e.getMessage().equals("Data truncation: Data too long for column 'Name' at row 1"));
            Assert.assertTrue(isCurrentTransactionNull());
        }
    }

    // Two layer transaction executed successfully.
    @Test
    public void testBatchOperationSuccessfulInDalCommand() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new SuccessDalCommand(), new DalHints());
            Assert.assertTrue(isCurrentTransactionNull());
            Assert.assertTrue(true);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    // Four layer transaction conflicted
    @Test
    public void testFourLayerConflictedTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new TwoLayerConflictDalCommand(), new DalHints());
            Assert.fail();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(e.getMessage().equals(
                    "The state of nesting transactions are conflicted,transaction has been rollbacked. Transaction level 3 conflicted with level 4, original error message:[Data truncation: Data too long for column 'Name' at row 1] , all levels of transaction status:[level 1, status:Conflict, actual status:Commit] [level 2, status:Conflict, actual status:Commit] [level 3, status:Conflict, actual status:Commit] [level 4, status:Rollback, actual status:Rollback] ."));
            Assert.assertTrue(isCurrentTransactionNull());
        }
    }

    // Four layer transaction successful
    @Test
    public void testFourLayerSuccessfulTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new TwoLayerSucessDalCommand(), new DalHints());
            Assert.assertTrue(isCurrentTransactionNull());
            Assert.assertTrue(true);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    // Four layer transaction throws exception
    @Test
    public void testFourLayerExceptionTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new TwoLayerExceptionDalCommand(), new DalHints());
            Assert.fail();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(e instanceof BatchUpdateException);
            Assert.assertTrue(e.getMessage().equals("Data truncation: Data too long for column 'Name' at row 1"));
            Assert.assertTrue(isCurrentTransactionNull());
        }
    }

    // Three layer transaction conflicted
    @Test
    public void testThreeLayerConflictedTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new OneLayerConflictDalCommand(), new DalHints());
            Assert.fail();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(e.getMessage().equals(
                    "The state of nesting transactions are conflicted,transaction has been rollbacked. Transaction level 2 conflicted with level 3, original error message:[Data truncation: Data too long for column 'Name' at row 1] , all levels of transaction status:[level 1, status:Conflict, actual status:Commit] [level 2, status:Conflict, actual status:Commit] [level 3, status:Rollback, actual status:Rollback] ."));
            Assert.assertTrue(isCurrentTransactionNull());
        }
    }

    // Three layer transaction successful
    @Test
    public void testThreeLayerSuccessfulTranasction() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new OneLayerSuccessDalCommand(), new DalHints());
            Assert.assertTrue(isCurrentTransactionNull());
            Assert.assertTrue(true);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    // Three layer transaction throws exception
    @Test
    public void testThreeLayerExceptionTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(dbName);
        try {
            client.execute(new OneLayerExceptionDalCommand(), new DalHints());
            Assert.fail();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(e instanceof BatchUpdateException);
            Assert.assertTrue(e.getMessage().equals("Data truncation: Data too long for column 'Name' at row 1"));
            Assert.assertTrue(isCurrentTransactionNull());
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

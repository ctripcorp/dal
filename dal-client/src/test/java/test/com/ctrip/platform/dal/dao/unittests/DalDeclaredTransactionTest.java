package test.com.ctrip.platform.dal.dao.unittests;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;
import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub;
import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.annotation.DalTransactionalBeanPostProcessor;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;

public class DalDeclaredTransactionTest extends BaseTestStub {
    private static SqlServerDatabaseInitializer initializer = new SqlServerDatabaseInitializer();
    private static final String DATABASE_NAME = SqlServerDatabaseInitializer.DATABASE_NAME;
    
    public DalDeclaredTransactionTest() {
        super(initializer.DATABASE_NAME, initializer.diff);
    }
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        initializer.setUpBeforeClass();
        DalQueryDaoTestStub.prepareData(initializer.DATABASE_NAME);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        initializer.tearDownAfterClass();
    }
    
    @Test
    public void testPostProcess() {
        DalTransactionalBeanPostProcessor bpp = new DalTransactionalBeanPostProcessor();
        TransactionTestClassSqlServer test = (TransactionTestClassSqlServer)bpp.postProcessAfterInitialization(new TransactionTestClassSqlServer(), "");
        test.perform();
    }
    
    @Test
    public void testDeclareOnClass() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(TransactionTestClassSqlServer.class);
        test.perform();
    }
    
    @Test
    public void testDeclareOnObject() {
        TransactionTestClassSqlServer test = new TransactionTestClassSqlServer();
        test = DalTransactionManager.enable(test);
        test.perform();
    }
    
    @Test
    public void testTransactionFail() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(TransactionTestClassSqlServer.class);
        assertTrue(DalTransactionManager.isInTransaction() == false);
        
        try {
            test.performFail();
            fail();
        } catch (Throwable e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testWithShard() {
        TransactionTestClassSqlServer test = new TransactionTestClassSqlServer();
        test = DalTransactionManager.enable(test);
        test.perform("1");
        test.perform(1);
        test.perform(new Integer(1));
    }
    
    @Test
    public void testWithHints() {
        TransactionTestClassSqlServer test = new TransactionTestClassSqlServer();
        test = DalTransactionManager.enable(test);
        test.perform("1", new DalHints().inShard(1));
        test.perform("1", new DalHints().inShard("1"));
        test.perform("1", new DalHints().inShard(0));
        test.perform("1", new DalHints().inShard("0"));
    }
    
    @Test
    public void testWithHintsFail() {
        TransactionTestClassSqlServer test = new TransactionTestClassSqlServer();
        test = DalTransactionManager.enable(test);
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
        
        try {
            test.performFail("1", new DalHints().inShard(1));
            fail();
        } catch (Throwable e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testWithShardAndHints() {
        TransactionTestClassSqlServer test = new TransactionTestClassSqlServer();
        test = DalTransactionManager.enable(test);
        test.performWitShard("1", new DalHints().inShard(1));
        test.performWitShard("1", new DalHints().inShard(1));
        test.performWitShard("1", new DalHints().inShard(0));
        test.performWitShard("1", new DalHints().inShard("0"));
    }
    
    @Test
    public void testWithShardAndHintsNest() {
        TransactionTestClassSqlServer test = new TransactionTestClassSqlServer();
        test = DalTransactionManager.enable(test);
        test.performWitShardNest("1", new DalHints().inShard(1));
        test.performWitShardNest("1", new DalHints().inShard(1));
        test.performWitShardNest("1", new DalHints().inShard(0));
        test.performWitShardNest("1", new DalHints().inShard("0"));
    }
    
    @Test
    public void testWithShardAndHintsNestFail() {
        TransactionTestClassSqlServer test = new TransactionTestClassSqlServer();
        test = DalTransactionManager.enable(test);
        test.performWitShardNest("1", new DalHints().inShard(1));
        test.performWitShardNest("1", new DalHints().inShard(1));
        test.performWitShardNest("1", new DalHints().inShard(0));
        test.performWitShardNest("1", new DalHints().inShard("0"));
    }
    
    @Test
    public void testWithShardAndHintsNestWithCommand() throws SQLException {
        final TransactionTestClassSqlServer test = DalTransactionManager.enable(new TransactionTestClassSqlServer());
        DalClientFactory.getClient(test.DB_NAME_SHARD).execute(new DalCommand() {
            
            @Override
            public boolean execute(DalClient client) throws SQLException {
                test.perform("1", new DalHints().inShard(0));
                test.perform("1", new DalHints().inShard("0"));
                test.performWitShardNest("1", new DalHints().inShard(1));
                return false;
            }
        }, new DalHints().inShard(1));
    }
    
    @Test
    public void testWithShardAndHintsNestWithCommandFail() throws SQLException {
        final TransactionTestClassSqlServer test = DalTransactionManager.enable(new TransactionTestClassSqlServer());
        try {
            assertTrue(DalTransactionManager.isInTransaction() == false);
            
            DalClientFactory.getClient(test.DB_NAME_SHARD).execute(new DalCommand() {
                
                @Override
                public boolean execute(DalClient client) throws SQLException {
                    test.perform("1", new DalHints().inShard(0));
                    test.perform("1", new DalHints().inShard("0"));
                    test.performWitShardNest("1", new DalHints().inShard(1));
                    throw new RuntimeException();
                }
            }, new DalHints().inShard(1));
        } catch (Throwable e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testCommandNestWithShardAndHints() throws SQLException {
        TransactionTestClassSqlServer test = DalTransactionManager.enable(new TransactionTestClassSqlServer());
        test.performCommandWitShardNest("1", new DalHints());
    }
    
    @Test
    public void testCommandNestWithShardAndHintsFail() throws SQLException {
        TransactionTestClassSqlServer test = DalTransactionManager.enable(new TransactionTestClassSqlServer());
        try {
            assertTrue(DalTransactionManager.isInTransaction() == false);
            test.performCommandWitShardNestFail("1", new DalHints());
            fail();
        } catch (Throwable e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testDeclareOnClassInternal() throws InstantiationException, IllegalAccessException {
        TransactionTestInternal test = DalTransactionManager.create(TransactionTestInternal.class);
        test.perform();
    }

    @Test
    public void testDeclareOnClassInternal1() throws InstantiationException, IllegalAccessException {
        TransactionTestInternal1 test;
        try {
            test = DalTransactionManager.create(TransactionTestInternal1.class);
            fail();
            test.perform();
        } catch (Exception e) {
        }
    }

    @Test
    public void testDeclareOnClassInternal2() throws InstantiationException, IllegalAccessException {
        try {
            TransactionTestInternal2 test = DalTransactionManager.create(TransactionTestInternal2.class);
            fail();
            test.perform();
        } catch (Exception e) {
        }
    }

    public static class TransactionTestInternal {

        @Transactional(logicalDbName = DATABASE_NAME)
        public String perform() {
            assertTrue(DalTransactionManager.isInTransaction());
            return null;
        }
        
    }

    public class TransactionTestInternal1 {

        @Transactional(logicalDbName = DATABASE_NAME)
        public String perform() {
            assertTrue(DalTransactionManager.isInTransaction());
            return null;
        }
        
    }

    private static class TransactionTestInternal2 {

        @Transactional(logicalDbName = DATABASE_NAME)
        public String perform() {
            assertTrue(DalTransactionManager.isInTransaction());
            return null;
        }
        
    }
}
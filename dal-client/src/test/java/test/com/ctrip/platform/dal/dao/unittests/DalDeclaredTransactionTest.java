package test.com.ctrip.platform.dal.dao.unittests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub;
import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;

public class DalDeclaredTransactionTest extends BaseTestStub {
    private final String DONE = "done";
    
    private static SqlServerDatabaseInitializer initializer = new SqlServerDatabaseInitializer();
    private static final String DATABASE_NAME = SqlServerDatabaseInitializer.DATABASE_NAME;
    private static ApplicationContext ctx;
    
    public DalDeclaredTransactionTest() {
        super(initializer.DATABASE_NAME, initializer.diff);
    }
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        initializer.setUpBeforeClass();
        ctx = new ClassPathXmlApplicationContext("transactionTest.xml");
        
        DalQueryDaoTestStub.prepareData(initializer.DATABASE_NAME);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        initializer.tearDownAfterClass();
    }
    
    @Test
    public void testGetFromContext() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = (TransactionTestClassSqlServer)ctx.getBean("TransactionTestClassSqlServer");
        Assert.assertEquals(DONE, test.perform());
    }
    
    @Test
    public void testAutoWire() throws InstantiationException, IllegalAccessException {
        TransactionTestUser test = (TransactionTestUser)ctx.getBean(TransactionTestUser.class);
        Assert.assertEquals(DONE, test.perform());
    }
    
    @Test
    public void testAutoWireNest() throws InstantiationException, IllegalAccessException {
        TransactionTestUser test = (TransactionTestUser)ctx.getBean(TransactionTestUser.class);
        Assert.assertEquals(DONE, test.performNest());
    }
    
    @Test
    public void testDeclareOnClass() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(TransactionTestClassSqlServer.class);
        Assert.assertEquals(DONE, test.perform());
    }
    
    @Test
    public void testDeclareOnObject() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        Assert.assertEquals(DONE, test.perform());
    }
    
    Class targetClass = TransactionTestClassSqlServer.class;
            
    @Test
    public void testNestedTransaction() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        Assert.assertEquals(DONE, test.performNest());
        
        test = (TransactionTestClassSqlServer)ctx.getBean("TransactionTestClassSqlServer");
        Assert.assertEquals(DONE, test.performNest());
    }
    
    @Test
    public void testNestedDistributedTransaction() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        assertTrue(DalTransactionManager.isInTransaction() == false);
        
        try {
            test.performNestDistributedTransaction();
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testNestedTransaction2() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        Assert.assertEquals(DONE, test.performNest2());
    }
    
    @Test
    public void testNestedTransaction3() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        Assert.assertEquals(DONE, test.performNest3());
    }
    
    @Test
    public void testTransactionFail() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(TransactionTestClassSqlServer.class);
        assertTrue(DalTransactionManager.isInTransaction() == false);
        
        try {
            test.performFail();
            fail();
        } catch (Exception e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testWithShard() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        Assert.assertEquals(DONE, test.perform("1"));
        Assert.assertEquals(DONE, test.perform(1));
        Assert.assertEquals(DONE, test.perform(new Integer(1)));
    }
    
    @Test
    public void testWithHints() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        Assert.assertEquals(DONE, test.perform("1", new DalHints().inShard(1)));
        Assert.assertEquals(DONE, test.perform("1", new DalHints().inShard("1")));
        Assert.assertEquals(DONE, test.perform("1", new DalHints().inShard(0)));
        Assert.assertEquals(DONE, test.perform("1", new DalHints().inShard("0")));
    }
    
    @Test
    public void testWithHintsFail() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
        
        try {
            test.performFail("1", new DalHints().inShard(1));
            fail();
        } catch (Exception e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testWithShardAndHints() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        Assert.assertEquals(DONE, test.performWitShard("1", new DalHints().inShard(1)));
        Assert.assertEquals(DONE, test.performWitShard("1", new DalHints().inShard("1")));
        Assert.assertEquals(DONE, test.performWitShard("1", new DalHints().inShard(0)));
        Assert.assertEquals(DONE, test.performWitShard("1", new DalHints().inShard("0")));
    }
    
    @Test
    public void testWithShardAndHintsNest() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        Assert.assertEquals(DONE, test.performWitShardNest("1", new DalHints().inShard(1)));
        Assert.assertEquals(DONE, test.performWitShardNest("1", new DalHints().inShard("1")));
        Assert.assertEquals(DONE, test.performWitShardNest("1", new DalHints().inShard(0)));
        Assert.assertEquals(DONE, test.performWitShardNest("1", new DalHints().inShard("0")));
    }
    
    @Test
    public void testWithShardAndHintsNestFail() throws InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        try {
            test.performWitShardNestFail("1", new DalHints().inShard(1));
            fail();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testWithShardAndHintsNestWithCommand() throws SQLException, InstantiationException, IllegalAccessException {
        final TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
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
    public void testWithShardAndHintsNestWithCommandFail() throws SQLException, InstantiationException, IllegalAccessException {
        final TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
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
        } catch (Exception e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testCommandNestWithShardAndHints() throws SQLException, InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        test.performCommandWitShardNest("1", new DalHints());
    }
    
    @Test
    public void testCommandNestWithShardAndHintsFail() throws SQLException, InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        try {
            assertTrue(DalTransactionManager.isInTransaction() == false);
            test.performCommandWitShardNestFail("1", new DalHints());
            fail();
        } catch (Exception e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testDetectDistributedTransaction() throws SQLException, InstantiationException, IllegalAccessException {
        TransactionTestClassSqlServer test = DalTransactionManager.create(targetClass);
        try {
            assertTrue(DalTransactionManager.isInTransaction() == false);
            test.performDetectDistributedTransaction("1", new DalHints());
            fail();
        } catch (Exception e) {
            e.printStackTrace();
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

        @Transactional(logicDbName = DATABASE_NAME)
        public String perform() {
            assertTrue(DalTransactionManager.isInTransaction());
            return null;
        }
        
    }

    public class TransactionTestInternal1 {

        @Transactional(logicDbName = DATABASE_NAME)
        public String perform() {
            assertTrue(DalTransactionManager.isInTransaction());
            return null;
        }
        
    }

    private static class TransactionTestInternal2 {

        @Transactional(logicDbName = DATABASE_NAME)
        public String perform() {
            assertTrue(DalTransactionManager.isInTransaction());
            return null;
        }
        
    }
}
package test.com.ctrip.platform.dal.dao.annotation.normal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;

@RunWith(Parameterized.class)
public class BaseDalTransactionalAnnotationTest{
    private int createOption; 
    public final String DONE = "done";
    
    private static final String DATABASE_NAME = SqlServerDatabaseInitializer.DATABASE_NAME;
    private static ApplicationContext ctx = new ClassPathXmlApplicationContext("transactionTest.xml");

    Class<BaseTransactionAnnoClass> targetClass;
    Class<TransactionTestUser> autoWireClass;
    
    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                {1, TransactionAnnoClassMySql.class, TransactionTestMySqlUser.class},
                {2, TransactionAnnoClassMySql.class, TransactionTestMySqlUser.class},
                {3, TransactionAnnoClassMySql.class, TransactionTestMySqlUser.class},
                {1, TransactionAnnoClassMySqlNew.class, TransactionTestMySqlUserNew.class},
                {2, TransactionAnnoClassMySqlNew.class, TransactionTestMySqlUserNew.class},
                {3, TransactionAnnoClassMySqlNew.class, TransactionTestMySqlUserNew.class},
                {1, TransactionAnnoClassSqlServer.class, TransactionTestSqlServerUser.class},
                {2, TransactionAnnoClassSqlServer.class, TransactionTestSqlServerUser.class},
                {3, TransactionAnnoClassSqlServer.class, TransactionTestSqlServerUser.class},
                {1, TransactionAnnoClassSqlServerNew.class, TransactionTestSqlServerUserNew.class},
                {2, TransactionAnnoClassSqlServerNew.class, TransactionTestSqlServerUserNew.class},
                {3, TransactionAnnoClassSqlServerNew.class, TransactionTestSqlServerUserNew.class},
                }
        );
    }
    
    public BaseDalTransactionalAnnotationTest(int option, Class annoTestClass, Class autoWireClass) {
        this.createOption = option;
        this.targetClass = annoTestClass;
        this.autoWireClass = autoWireClass;
    }

    @Test
    public void testAutoWireWithinFactoryCreatedBean() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        if(createOption != 3)
            Assert.assertNotNull(test.getJac());
    }

    private BaseTransactionAnnoClass create() throws InstantiationException, IllegalAccessException {
        switch (createOption) {
            case 1:
                return (BaseTransactionAnnoClass)ctx.getBean(targetClass);
            case 2:
                return (BaseTransactionAnnoClass)ctx.getBean(autoWireClass).getTransactionAnnoTest();
            case 3:
                return DalTransactionManager.create(targetClass);

            default:
                throw new RuntimeException("wrong option");
        }
    }

    @Test
    public void testAutoWire() throws InstantiationException, IllegalAccessException {
        TransactionTestUser test = (TransactionTestUser)ctx.getBean(autoWireClass);
        Assert.assertEquals(DONE, test.perform());

        Assert.assertEquals(DONE, test.performNest());
    }

    @Test
    public void testGetFromApplicationContext() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = (BaseTransactionAnnoClass)ctx.getBean(targetClass.getSimpleName());
        Assert.assertEquals(DONE, test.perform());
        
        Assert.assertEquals(DONE, test.performNest());
    }
    
    @Test
    public void testSingleLevel() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        Assert.assertEquals(DONE, test.perform());
    }
    
    @Test
    public void testTransactionFail() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        assertTrue(DalTransactionManager.isInTransaction() == false);
        
        try {
            test.performFail();
            fail();
        } catch (Exception e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testNestedTransaction() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        Assert.assertEquals(DONE, test.performNest());
    }
    
    @Test
    public void testNestedTransaction2() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        Assert.assertEquals(DONE, test.performNest2());
    }
    
    @Test
    public void testNestedTransaction3() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        Assert.assertEquals(DONE, test.performNest3());
    }
    
    @Test
    public void testNestedDistributedTransaction() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        assertTrue(DalTransactionManager.isInTransaction() == false);
        
        try {
            test.performNestDistributedTransaction();
            fail();
        } catch (Exception e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    @Test
    public void testDistributedTransaction() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        assertTrue(DalTransactionManager.isInTransaction() == false);
        
        try {
            test.performDistributedTransaction();
            fail();
        } catch (Exception e) {
        }
        
        assertTrue(DalTransactionManager.isInTransaction() == false);
    }
    
    
    @Test
    public void testWithShard() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        Assert.assertEquals(DONE, test.perform("1"));
        Assert.assertEquals(DONE, test.perform(1));
        Assert.assertEquals(DONE, test.perform(new Integer(1)));
    }
    
    @Test
    public void testWithHints() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        Assert.assertEquals(DONE, test.perform("1", new DalHints().inShard(1)));
        Assert.assertEquals(DONE, test.perform("1", new DalHints().inShard("1")));
        Assert.assertEquals(DONE, test.perform("1", new DalHints().inShard(0)));
        Assert.assertEquals(DONE, test.perform("1", new DalHints().inShard("0")));
        
        try {
            //Can not locate a shard id
            test.perform("1", new DalHints());
            fail();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testWithHintsFail() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        
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
        BaseTransactionAnnoClass test = create();
        Assert.assertEquals(DONE, test.performWitShard("1", new DalHints().inShard(1)));
        Assert.assertEquals(DONE, test.performWitShard("1", new DalHints().inShard("1")));
        Assert.assertEquals(DONE, test.performWitShard("1", new DalHints().inShard(0)));
        Assert.assertEquals(DONE, test.performWitShard("1", new DalHints().inShard("0")));

        Assert.assertEquals(DONE, test.performWitShard(null, new DalHints().inShard(1)));
        Assert.assertEquals(DONE, test.performWitShard(null, new DalHints().inShard("1")));
        Assert.assertEquals(DONE, test.performWitShard(null, new DalHints().inShard(0)));
        Assert.assertEquals(DONE, test.performWitShard(null, new DalHints().inShard("0")));
    }
    
    @Test
    public void testWithShardAndHintsNest() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        Assert.assertEquals(DONE, test.performWitShardNest("1", new DalHints().inShard(1)));
        Assert.assertEquals(DONE, test.performWitShardNest("1", new DalHints().inShard("1")));
        Assert.assertEquals(DONE, test.performWitShardNest("1", new DalHints().inShard(0)));
        Assert.assertEquals(DONE, test.performWitShardNest("1", new DalHints().inShard("0")));

        Assert.assertEquals(DONE, test.performWitShardNest(null, new DalHints().inShard(1)));
        Assert.assertEquals(DONE, test.performWitShardNest(null, new DalHints().inShard("1")));
        Assert.assertEquals(DONE, test.performWitShardNest(null, new DalHints().inShard(0)));
        Assert.assertEquals(DONE, test.performWitShardNest(null, new DalHints().inShard("0")));
    }
    
    @Test
    public void testWithShardAndHintsNestConflict() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        try {
            test.performWitShardNestConflict("1", new DalHints().inShard(1));
            fail();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testWithShardAndHintsNestFail() throws InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
        try {
            test.performWitShardNestFail("1", new DalHints().inShard(1));
            fail();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testSameShardWithCommand() throws SQLException, InstantiationException, IllegalAccessException {
        final BaseTransactionAnnoClass test = create();
        DalClientFactory.getClient(test.getShardDb()).execute(new DalCommand() {
            
            @Override
            public boolean execute(DalClient client) throws SQLException {
                test.perform("1", new DalHints().inShard("1"));
                test.perform("1", new DalHints().inShard(1));
                test.performWitShard("1", new DalHints());
                test.performWitShard("1", new DalHints().inShard(0));
                test.performWitShardNest("1", new DalHints());
                return false;
            }
        }, new DalHints().inShard(1));
    }
    
    @Test
    public void testShardIdConfilictInCommand() throws SQLException, InstantiationException, IllegalAccessException {
        final BaseTransactionAnnoClass test = create();
        try {
            DalClientFactory.getClient(test.getShardDb()).execute(new DalCommand() {
                
                @Override
                public boolean execute(DalClient client) throws SQLException {
                    test.perform("1", new DalHints().inShard("1"));
                    test.perform("1", new DalHints().inShard(1));
                    test.performWitShard("1", new DalHints());
                    test.performWitShard("1", new DalHints().inShard(0));
                    test.perform("1", new DalHints().inShard(0));
                    fail();
                    return false;
                }
            }, new DalHints().inShard(1));
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testWithShardAndHintsNestWithCommandFail() throws SQLException, InstantiationException, IllegalAccessException {
        final BaseTransactionAnnoClass test = create();
        try {
            assertTrue(DalTransactionManager.isInTransaction() == false);
            
            DalClientFactory.getClient(test.getShardDb()).execute(new DalCommand() {
                
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
        BaseTransactionAnnoClass test = create();
        test.performCommandWitShardNest("1", new DalHints());
    }
    
    @Test
    public void testCommandNestWithShardAndHintsFail() throws SQLException, InstantiationException, IllegalAccessException {
        BaseTransactionAnnoClass test = create();
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
        BaseTransactionAnnoClass test = create();
        try {
            assertTrue(DalTransactionManager.isInTransaction() == false);
            test.performDetectDistributedTransaction("1", new DalHints());
            fail();
        } catch (Exception e) {
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
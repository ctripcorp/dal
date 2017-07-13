package test.com.ctrip.platform.dal.dao.unittests;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.MySqlDatabaseInitializer;

public class DalTransactionalAnnotationMySqlTest extends BaseDalTransactionalAnnotationTest {
    private static MySqlDatabaseInitializer initializer = new MySqlDatabaseInitializer();

    public DalTransactionalAnnotationMySqlTest() {
        super(TransactionAnnoClassMySql.class, TransactionTestMySqlUser.class);
    }
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        initializer.tearDownAfterClass();
    }
}
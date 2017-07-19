package test.com.ctrip.platform.dal.dao.annotation.normal;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;

public class DalTransactionalAnnotationSqlServerTest extends BaseDalTransactionalAnnotationTest {
    private static SqlServerDatabaseInitializer initializer = new SqlServerDatabaseInitializer();

    public DalTransactionalAnnotationSqlServerTest() {
        super(TransactionAnnoClassSqlServer.class, TransactionTestSqlServerUser.class);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        initializer.tearDownAfterClass();
    }
}

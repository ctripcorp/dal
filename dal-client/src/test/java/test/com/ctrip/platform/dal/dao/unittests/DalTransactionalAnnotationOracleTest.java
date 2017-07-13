package test.com.ctrip.platform.dal.dao.unittests;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer;

public class DalTransactionalAnnotationOracleTest extends BaseDalTransactionalAnnotationTest {
    private static OracleDatabaseInitializer initializer = new OracleDatabaseInitializer();

    public DalTransactionalAnnotationOracleTest() {
        super(TransactionAnnoClassOracle.class, TransactionTestOracleUser.class);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        initializer.tearDownAfterClass();
    }
}

package test.com.ctrip.platform.dal.dao.annotation.normal;

import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DalTransactionalAnnotationMySqlTest extends BaseDalTransactionalAnnotationTest {
    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                {1, TransactionAnnoClassMySql.class, TransactionTestMySqlUser.class},
                {2, TransactionAnnoClassMySql.class, TransactionTestMySqlUser.class},
                {3, TransactionAnnoClassMySql.class, TransactionTestMySqlUser.class},
                {1, TransactionAnnoClassMySqlNew.class, TransactionTestMySqlUserNew.class},
                {2, TransactionAnnoClassMySqlNew.class, TransactionTestMySqlUserNew.class},
                {3, TransactionAnnoClassMySqlNew.class, TransactionTestMySqlUserNew.class},
                }
        );
    }
    public DalTransactionalAnnotationMySqlTest(int option, Class annoTestClass, Class autoWireClass) {
        super(option, annoTestClass, autoWireClass);
    }
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
}
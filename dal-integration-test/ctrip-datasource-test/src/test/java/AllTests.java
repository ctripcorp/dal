import datasourcetest.ConnectionPhantomReferenceCleanerOnDalDatasourceTest;
import datasourcetest.QConfigTest1;
import idgentest.IdGenClientTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by lilj on 2018/7/12.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConnectionPhantomReferenceCleanerOnDalDatasourceTest.class,
     //   QConfigTest1.class,
        IdGenClientTest.class
})
public class AllTests {
}

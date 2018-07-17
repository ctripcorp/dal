import datasourcetest.ConnectionPhantomReferenceCleanerOnDalDatasourceTest;
import datasourcetest.QConfigTest1;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by lilj on 2018/7/12.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConnectionPhantomReferenceCleanerOnDalDatasourceTest.class,
        QConfigTest1.class,
})
public class AllTests {
}

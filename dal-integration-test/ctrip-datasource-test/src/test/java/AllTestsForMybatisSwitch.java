import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import switchtest.MybatisDRTestMultipleKeysTest;
import switchtest.MybatisDRTestSingleKeyTest;
import switchtest.MybatisPoolPropertiesTest;

/**
 * Created by lilj on 2018/7/12.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MybatisPoolPropertiesTest.class,
        MybatisDRTestMultipleKeysTest.class,
        MybatisDRTestSingleKeyTest.class,
})
public class AllTestsForMybatisSwitch {
}

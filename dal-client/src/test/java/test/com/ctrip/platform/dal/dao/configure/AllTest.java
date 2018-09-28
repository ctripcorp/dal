package test.com.ctrip.platform.dal.dao.configure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        SlaveFreshnessScannerMysqlTest.class,
        DataSourceConfigureLocatorTest.class
})
public class AllTest {

}

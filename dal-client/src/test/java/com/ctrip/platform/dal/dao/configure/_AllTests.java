package com.ctrip.platform.dal.dao.configure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ClasspathResourceLoaderTest.class,
        CommonFileLoaderTest.class,
        InjectableComponentTest.class,
        LocalDatabaseSetAdapterTest.class,
        LocalDefaultDatabaseSetTest.class,
        SlaveFreshnessScannerMysqlTest.class,
        DataSourceConfigureLocatorTest.class,
        ClusterConfigParserTest.class,
        ClusterConfigValidatorTest.class,
        ConnectionStringParserTest.class
})
public class _AllTests {

}

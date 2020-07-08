package com.ctrip.platform.dal.dao.configure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    CtripDalConfigLoaderTest.class,
    FreshnessHelperTest.class,
    SlaveFreshnessScannerMysqlTest.class,
    SlaveFreshnessScannerSqlSvrTest.class
})
public class _AllTests {}

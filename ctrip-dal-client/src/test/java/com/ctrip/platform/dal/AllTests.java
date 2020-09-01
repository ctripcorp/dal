package com.ctrip.platform.dal;

import com.ctrip.platform.dal.vi.DalIgniteTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctrip.platform.dal.dao.configure.CtripDalConfigLoaderTest;
import com.ctrip.platform.dal.dao.helper.SQLParserTests;
import com.ctrip.platform.dal.sql.logging.CommonUtilTest;
import com.ctrip.platform.dal.sql.logging.DalCatLoggerTest;

@RunWith(Suite.class)
@SuiteClasses({
    com.ctrip.platform.dal.cluster._AllTests.class,
    com.ctrip.platform.dal.codegen._AllTests.class,
    CtripDalConfigLoaderTest.class,
    SQLParserTests.class,
    CommonUtilTest.class,
    DalCatLoggerTest.class,
    com.ctrip.platform.dal.dao.configure._AllTests.class,
    com.ctrip.platform.dal.dao._AllTests.class,
    DalIgniteTest.class
    })
public class AllTests {

}

package com.ctrip.platform.dal;

import com.ctrip.platform.dal.dao.configure.CtripDalConfigLoaderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctrip.platform.dal.dao.helper.SQLParserTests;
import com.ctrip.platform.dal.sql.logging.CommonUtilTest;
import com.ctrip.platform.dal.sql.logging.DalCatLoggerTest;

@RunWith(Suite.class)
@SuiteClasses({
    com.ctrip.platform.dal.codegen.AllTests.class,
    com.ctrip.platform.dal.dao.AllTests.class, 
    CtripDalConfigLoaderTest.class, 
    SQLParserTests.class,
    CommonUtilTest.class,
    DalCatLoggerTest.class,})
public class AllTests {

}

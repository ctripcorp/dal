package com.ctrip.platform.dal.tester.datasource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
		DatabasePoolConfigParserTest.class, 
		DataSourceLocatorTest.class })
public class AllTests {

}

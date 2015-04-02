package com.ctrip.datasource.configure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DatabaseConfigParserTest.class,
		DatabasePoolConfigParserTest.class })
public class AllTests {

}

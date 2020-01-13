package com.ctrip.platform.dal.dao.datasource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
		DatabasePoolConfigParserTest.class,
		RefreshableDataSourceTest.class,
		ForceSwitchableDataSourceTest.class,
		LocalizedDataSourceTest.class
})
public class AllTests {

}

package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.datasource.jdbc.DalConnectionTest;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalDaoTest;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalStatementTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
		DatabasePoolConfigParserTest.class,
		RefreshableDataSourceTest.class,
		ForceSwitchableDataSourceTest.class,
		LocalizedDataSourceTest.class,
        DalConnectionTest.class,
        DalDaoTest.class,
        DalStatementTest.class
})
public class AllTests {

}

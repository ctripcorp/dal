package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.datasource.jdbc.DalConnectionTest;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalDaoTest;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalStatementTest;
import com.ctrip.platform.dal.dao.datasource.log.ClusterDbSqlContextTest;
import com.ctrip.platform.dal.dao.datasource.log.KeyedDbSqlContextTest;
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
        DalStatementTest.class,
		ClusterDbSqlContextTest.class,
		KeyedDbSqlContextTest.class
})
public class _AllTests {

}

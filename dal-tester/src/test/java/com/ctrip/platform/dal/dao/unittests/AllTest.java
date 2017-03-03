package com.ctrip.platform.dal.dao.unittests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DalDirectClientMySqlTest.class,
	DalDirectClientSqlServerTest.class,
	DalDirectClientOracleTest.class,
	DalQueryDaoMySqlTest.class,
	DalQueryDaoSqlServerTest.class,
	DalTabelDaoMySqlTest.class,
	DalTableDaoSqlServerTest.class,
	DatabaseSelectorTest.class,
	DalClientFactoryTest.class,
	DalClientFactoryLazeLoadTest.class,
	DalStatusManagerTest.class,
})
public class AllTest {}

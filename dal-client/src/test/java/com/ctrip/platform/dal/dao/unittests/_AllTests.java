package com.ctrip.platform.dal.dao.unittests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DalDirectClientMySqlTest.class,
	DalDirectClientSqlServerTest.class,
	
	DalQueryDaoMySqlTest.class,
	DalQueryDaoSqlServerTest.class,
	
	DalTableDaoMySqlTest.class,
	DalTableDaoSqlServerTest.class,
		
	DatabaseSelectorTest.class,
	DalClientFactoryTest.class,
	DalClientFactoryLazeLoadTest.class,
	DalStatusManagerTest.class,
	StatementParametersTest.class,
	
	KeyHolderTest.class,
})
public class _AllTests {}

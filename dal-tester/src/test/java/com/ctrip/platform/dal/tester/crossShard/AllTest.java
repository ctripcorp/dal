package com.ctrip.platform.dal.tester.crossShard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DalQueryDaoMySqlTest.class,
	DalQueryDaoSqlSvrTest.class,
})
public class AllTest {}
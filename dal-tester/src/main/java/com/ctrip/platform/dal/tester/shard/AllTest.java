package com.ctrip.platform.dal.tester.shard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CrossShardTableDaoTest.class,
	ShardColModShardStrategyTest.class,
	DalTabelDaoTableShardTest.class,
	DalTabelDaoTableShardMySqlTest.class,
	DalTableDaoShardByDbTest.class,
	DalTableDaoShardByDbMySqlTest.class,
})
public class AllTest {}

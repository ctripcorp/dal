package test.com.ctrip.platform.dal.dao.shard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ShardColModShardStrategyTest.class,
	DalTabelDaoTableShardTest.class,
	DalTabelDaoTableShardMySqlTest.class,
	DalTableDaoShardByDbTest.class,
	DalTableDaoShardByDbMySqlTest.class,
	DalTableDaoShardByDbTableTest.class,
	DalTableDaoShardByDbTableMySqlTest.class,
})
public class AllTest {}

package test.com.ctrip.platform.dal.dao.shard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ShardColModShardStrategyTest.class,
	SimpleShardHintStrategyTest.class,
	
	DalTabelDaoShardByTableSqlSvrTest.class,
	DalTabelDaoShardByTableMySqlTest.class,
	
	DalTableDaoShardByDbSqlSvrTest.class,
	DalTableDaoShardByDbMySqlTest.class,
	
	DalTableDaoShardByDbTableSqlSvrTest.class,
	DalTableDaoShardByDbTableMySqlTest.class,
	
	DalQueryDaoMySqlTest.class,
	DalQueryDaoSqlSvrTest.class,
})
public class AllTest {}

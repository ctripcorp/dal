package test.com.ctrip.platform.dal.dao.shard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ShardColModShardStrategyTest.class,
	
	DalTabelDaoShardByTableSqlSvrTest.class,
	DalTabelDaoShardByTableMySqlTest.class,
	
	DalTableDaoShardByDbSqlSvrTest.class,
	DalTableDaoShardByDbMySqlTest.class,
	
	DalTableDaoShardByDbTableSqlSvrTest.class,
	DalTableDaoShardByDbTableMySqlTest.class,
	
	DalQueryDaoMySqlTest.class,
	DalQueryDaoSqlSvrTest.class,
	DalQueryDaoOracleTest.class,
})
public class AllTest {}

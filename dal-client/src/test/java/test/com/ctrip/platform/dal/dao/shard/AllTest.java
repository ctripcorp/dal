package test.com.ctrip.platform.dal.dao.shard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ShardColModShardStrategyTest.class,
	
	DalTabelDaoShardByTableSqlSvrTest.class,
	DalTabelDaoShardByTableMySqlTest.class,
	DalTabelDaoShardByTableOracleTest.class,
	
	DalTableDaoShardByDbSqlSvrTest.class,
	DalTableDaoShardByDbMySqlTest.class,
	DalTableDaoShardByDbOracleTest.class,
	
	DalTableDaoShardByDbTableSqlSvrTest.class,
	DalTableDaoShardByDbTableMySqlTest.class,
	DalTableDaoShardByDbTableOracleTest.class,
	
	DalQueryDaoMySqlTest.class,
	DalQueryDaoSqlSvrTest.class,
	DalQueryDaoOracleTest.class,
})
public class AllTest {}

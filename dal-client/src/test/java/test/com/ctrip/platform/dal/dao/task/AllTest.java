package test.com.ctrip.platform.dal.dao.task;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	BatchDeleteTaskSqlSvrTest.class,
	BatchInsertTaskSqlSvrTest.class,
	BatchUpdateTaskSqlSvrTest.class,
	CombinedInsertTaskSqlSvrTest.class,
	SingleDeleteTaskSqlSvrTest.class,
	SingleInsertTaskSqlSvrTest.class,
	SingleUpdateTaskSqlSvrTest.class,
	QuerySqlTaskSqlSvrTest.class,
	DeleteSqlTaskSqlSvrTest.class,
	UpdateSqlTaskSqlSvrTest.class,

	BatchDeleteTaskMySqlTest.class,
	BatchInsertTaskMySqlTest.class,
	BatchUpdateTaskMySqlTest.class,
	CombinedInsertTaskMySqlTest.class,
	SingleDeleteTaskMySqlTest.class,
	SingleInsertTaskMySqlTest.class,
	SingleUpdateTaskMySqlTest.class,
	QuerySqlTaskMySqlTest.class,
	DeleteSqlTaskMySqlTest.class,
	UpdateSqlTaskMySqlTest.class,
	
	ShardedIntArrayResultMergerTest.class,
	DalSingleTaskRequestTest.class,
	DalBulkTaskRequestTest.class,
	DalSingleTaskRequestTest.class,
	DalSqlTaskRequestTest.class,
	
	DalRequestExecutorTest.class
})
public class AllTest {

}

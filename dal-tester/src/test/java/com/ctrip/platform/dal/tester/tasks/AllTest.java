package com.ctrip.platform.dal.tester.tasks;

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
})
public class AllTest {

}

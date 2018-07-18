package test.com.ctrip.platform.dal.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.com.ctrip.platform.dal.dao.shard.DalQueryDaoOracleTest;
import test.com.ctrip.platform.dal.dao.shard.DalTabelDaoShardByTableOracleTest;
import test.com.ctrip.platform.dal.dao.shard.DalTableDaoShardByDbOracleTest;
import test.com.ctrip.platform.dal.dao.shard.DalTableDaoShardByDbTableOracleTest;
import test.com.ctrip.platform.dal.dao.task.BatchDeleteTaskOracleTest;
import test.com.ctrip.platform.dal.dao.task.BatchInsertTaskOracleTest;
import test.com.ctrip.platform.dal.dao.task.BatchUpdateTaskOracleTest;
import test.com.ctrip.platform.dal.dao.task.DeleteSqlTaskOracleTest;
import test.com.ctrip.platform.dal.dao.task.QuerySqlTaskOracleTest;
import test.com.ctrip.platform.dal.dao.task.SingleDeleteTaskOracleTest;
import test.com.ctrip.platform.dal.dao.task.SingleInsertTaskOracleTest;
import test.com.ctrip.platform.dal.dao.task.SingleUpdateTaskOracleTest;
import test.com.ctrip.platform.dal.dao.task.UpdateSqlTaskOracleTest;
import test.com.ctrip.platform.dal.dao.unittests.DalDirectClientOracleTest;
import test.com.ctrip.platform.dal.dao.unittests.DalTableDaoOracleTest;

@RunWith(Suite.class)
@SuiteClasses({
    DalTabelDaoShardByTableOracleTest.class,
    DalTableDaoShardByDbOracleTest.class,
    DalTableDaoShardByDbTableOracleTest.class,
    DalQueryDaoOracleTest.class,

    BatchDeleteTaskOracleTest.class,
    BatchInsertTaskOracleTest.class,
    BatchUpdateTaskOracleTest.class,
//  CombinedInsertTaskOracleTest.class,
    SingleDeleteTaskOracleTest.class,
    SingleInsertTaskOracleTest.class,
    SingleUpdateTaskOracleTest.class,
    QuerySqlTaskOracleTest.class,
    DeleteSqlTaskOracleTest.class,
    UpdateSqlTaskOracleTest.class,
    
    DalDirectClientOracleTest.class,
    DalQueryDaoOracleTest.class,
    DalTableDaoOracleTest.class,

})
public class AllOracleTests {

}

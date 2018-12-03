package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.shard.DalQueryDaoOracleTest;
import com.ctrip.platform.dal.dao.shard.DalTabelDaoShardByTableOracleTest;
import com.ctrip.platform.dal.dao.shard.DalTableDaoShardByDbOracleTest;
import com.ctrip.platform.dal.dao.shard.DalTableDaoShardByDbTableOracleTest;
import com.ctrip.platform.dal.dao.task.*;
import com.ctrip.platform.dal.dao.unittests.DalDirectClientOracleTest;
import com.ctrip.platform.dal.dao.unittests.DalTableDaoOracleTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

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

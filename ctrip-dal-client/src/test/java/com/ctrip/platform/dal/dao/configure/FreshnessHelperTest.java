package com.ctrip.platform.dal.dao.configure;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.configure.FreshnessHelper;

public class FreshnessHelperTest {
    private static final String SQLSERVER_DATABASE_NAME = "SimpleSqlServerFreshness";
    private static final String MYSQL_DATABASE_NAME = "SimpleMysqlFreshness";
    
    private static final String SQLSERVER_SHARD_DATABASE_NAME = "SimpleSqlServerShardFreshness";
    private static final String MYSQL_SHARD_DATABASE_NAME = "SimpleMysqlShardFreshness";
    
    private static final Map<String, Integer> sqlserverFreshnessMap = new HashMap<>();

    private static final Map<String, Integer> mysqlFreshnessMap = new HashMap<>();
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        sqlserverFreshnessMap.put("SqlSvrShard_0", 3);
        sqlserverFreshnessMap.put("SqlSvrShard_1", 5);
        sqlserverFreshnessMap.put("SimpleShard_0", 7);
        sqlserverFreshnessMap.put("SimpleShard_1", 9);
        
        mysqlFreshnessMap.put("MySqlShard_0", 3);
        mysqlFreshnessMap.put("MySqlShard_1", 5);
        mysqlFreshnessMap.put("MySqlShard_2", 7);
        mysqlFreshnessMap.put("MySqlShard_3", 9);
    }
    
    @Test
    public void testSqlServerNoFreshness() throws InterruptedException {
        assertEquals(FreshnessHelper.INVALID, FreshnessHelper.getSlaveFreshness("SqlServerNoFreshness", "dao_test_sqlsvr"));
    }
    
    @Test
    public void testSqlServer() throws InterruptedException {
        for(String slaveName: sqlserverFreshnessMap.keySet()) {
            Integer value = FreshnessHelper.getSlaveFreshness(SQLSERVER_DATABASE_NAME, slaveName);
            assertEquals(sqlserverFreshnessMap.get(slaveName), value);
        }
    }
    
    @Test
    public void testSqlServerShard() throws InterruptedException {
        assertEquals(3, FreshnessHelper.getSlaveFreshness(SQLSERVER_SHARD_DATABASE_NAME, "SqlSvrShard_0"));
        assertEquals(5, FreshnessHelper.getSlaveFreshness(SQLSERVER_SHARD_DATABASE_NAME, "SqlSvrShard_1"));
        assertEquals(7, FreshnessHelper.getSlaveFreshness(SQLSERVER_SHARD_DATABASE_NAME, "SimpleShard_0"));
        assertEquals(9, FreshnessHelper.getSlaveFreshness(SQLSERVER_SHARD_DATABASE_NAME, "SimpleShard_1"));
    }
    
    @Test
    public void testMySqlNoFreshness() throws InterruptedException {
        assertEquals(FreshnessHelper.INVALID, FreshnessHelper.getSlaveFreshness("MysqlNoFreshness", "dao_test_mysql"));
    }
    
    @Test
    public void testMySql() {
        for(String slaveName: mysqlFreshnessMap.keySet()) {
            Integer value = FreshnessHelper.getSlaveFreshness(MYSQL_DATABASE_NAME, slaveName);
            assertEquals(mysqlFreshnessMap.get(slaveName), value);
        }
    }

    @Test
    public void testMysqlShard() throws InterruptedException {
        assertEquals(3, FreshnessHelper.getSlaveFreshness(MYSQL_SHARD_DATABASE_NAME, "MySqlShard_0"));
        assertEquals(5, FreshnessHelper.getSlaveFreshness(MYSQL_SHARD_DATABASE_NAME, "MySqlShard_1"));
        assertEquals(7, FreshnessHelper.getSlaveFreshness(MYSQL_SHARD_DATABASE_NAME, "MySqlShard_2"));
        assertEquals(9, FreshnessHelper.getSlaveFreshness(MYSQL_SHARD_DATABASE_NAME, "MySqlShard_3"));
    }
    
}

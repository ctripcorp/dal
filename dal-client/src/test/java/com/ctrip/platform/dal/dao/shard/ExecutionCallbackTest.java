package com.ctrip.platform.dal.dao.shard;

import com.ctrip.platform.dal.dao.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author c7ch23en
 */
public class ExecutionCallbackTest {

    private static final String DB_NAME = "dao_test_mysql_exception_shard";
    private static final String TABLE_NAME = "no_shard_tbl";
    private static final int DB_SHARDS = 4;
    private static final int TABLE_SHARDS = 1;
    private static final String DROP_TABLE_SQL_TPL = "DROP TABLE IF EXISTS %s";
    private static final String CREATE_TABLE_SQL_TPL = "CREATE TABLE %s (" +
            "id int NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
            "dbIndex int NOT NULL, " +
            "tableIndex int NOT NULL, " +
            "intCol int, " +
            "charCol varchar(64), " +
            "lastUpdateTime timestamp DEFAULT CURRENT_TIMESTAMP)";

    private static DalClient dalClient;
    private DalTableDao<ExecutionCallbackTestTable> tableDao;
    private DalQueryDao queryDao;

    public ExecutionCallbackTest() throws SQLException {
        tableDao = new DalTableDao<>(ExecutionCallbackTestTable.class);
        queryDao = new DalQueryDao(DB_NAME);
    }

    @BeforeClass
    public static void beforeClass() throws SQLException {
        dalClient = DalClientFactory.getClient(DB_NAME);
        createTables();
    }

    @AfterClass
    public static void afterClass() throws SQLException {
        dropTables();
    }

    @Test
    public void testInsertList() throws SQLException {
        Set<Integer> successIndexes = new HashSet<>();
        Set<Integer> errorIndexes = new HashSet<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        tableDao.insert(new DalHints().continueOnError(), createPojos(),
                new TestPojoCallback(successIndexes, errorIndexes, successCount, errorCount));
        Assert.assertEquals(2, successCount.get());
        Assert.assertEquals(2, successIndexes.size());
        Assert.assertTrue(successIndexes.contains(0));
        Assert.assertTrue(successIndexes.contains(2));
        Assert.assertEquals(2, errorCount.get());
        Assert.assertEquals(2, errorIndexes.size());
        Assert.assertTrue(errorIndexes.contains(1));
        Assert.assertTrue(errorIndexes.contains(3));
    }

    @Test
    public void testBatchInsert() throws SQLException {
        Set<String> successShards = new HashSet<>();
        Set<String> errorShards = new HashSet<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        tableDao.batchInsert(new DalHints().continueOnError(), createPojos(),
                new TestShardCallback<>(successShards, errorShards, successCount, errorCount));
        Assert.assertEquals(2, successCount.get());
        Assert.assertEquals(2, successShards.size());
        Assert.assertTrue(successShards.contains("0"));
        Assert.assertTrue(successShards.contains("2"));
        Assert.assertEquals(2, errorCount.get());
        Assert.assertEquals(2, errorShards.size());
        Assert.assertTrue(errorShards.contains("1"));
        Assert.assertTrue(errorShards.contains("3"));
    }

    @Test
    public void testQueryByPojo() throws SQLException {
        Set<String> successShards = new HashSet<>();
        Set<String> errorShards = new HashSet<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        tableDao.queryBy(createQueryPojoWithTableIndex(0), new DalHints().inAllShards().continueOnError(),
                new TestShardCallback<>(successShards, errorShards, successCount, errorCount));
        Assert.assertEquals(2, successCount.get());
        Assert.assertEquals(2, successShards.size());
        Assert.assertTrue(successShards.contains("0"));
        Assert.assertTrue(successShards.contains("2"));
        Assert.assertEquals(2, errorCount.get());
        Assert.assertEquals(2, errorShards.size());
        Assert.assertTrue(errorShards.contains("1"));
        Assert.assertTrue(errorShards.contains("3"));
    }

    @Test
    public void testQueryBySql() throws SQLException {
        Set<String> successShards = new HashSet<>();
        Set<String> errorShards = new HashSet<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        String sql = String.format("select * from %s where id = 1", TABLE_NAME);
        queryDao.query(sql, new StatementParameters(), new DalHints().inAllShards().continueOnError(),
                ExecutionCallbackTestTable.class,
                new TestShardCallback<>(successShards, errorShards, successCount, errorCount));
        Assert.assertEquals(2, successCount.get());
        Assert.assertEquals(2, successShards.size());
        Assert.assertTrue(successShards.contains("0"));
        Assert.assertTrue(successShards.contains("2"));
        Assert.assertEquals(2, errorCount.get());
        Assert.assertEquals(2, errorShards.size());
        Assert.assertTrue(errorShards.contains("1"));
        Assert.assertTrue(errorShards.contains("3"));
    }

    private List<ExecutionCallbackTestTable> createPojos() {
        List<ExecutionCallbackTestTable> pojos = new ArrayList<>();
        for (int i = 0; i < DB_SHARDS; i++)
            for (int j = 0; j < TABLE_SHARDS; j++)
                pojos.add(createPojo(i, j));
        return pojos;
    }

    private ExecutionCallbackTestTable createPojo(int dbIndex, int tableIndex) {
        ExecutionCallbackTestTable pojo = new ExecutionCallbackTestTable();
        pojo.setDbIndex(dbIndex);
        pojo.setTableIndex(tableIndex);
        pojo.setIntCol(dbIndex + tableIndex);
        pojo.setCharCol(String.valueOf(dbIndex + tableIndex));
        return pojo;
    }

    private ExecutionCallbackTestTable createQueryPojoWithTableIndex(int tableIndex) {
        ExecutionCallbackTestTable pojo = new ExecutionCallbackTestTable();
        pojo.setTableIndex(tableIndex);
        return pojo;
    }

    private static void createTables() throws SQLException {
        String[] sqls = new String[] {
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME),
                String.format(CREATE_TABLE_SQL_TPL, TABLE_NAME)
        };
        dalClient.batchUpdate(sqls, new DalHints().inShard(0));
        sqls = new String[] {
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME)
        };
        dalClient.batchUpdate(sqls, new DalHints().inShard(1));
    }

    private static void dropTables() throws SQLException {
        String[] sqls = new String[] {
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME)
        };
        dalClient.batchUpdate(sqls, new DalHints().inShard(0));
        dalClient.batchUpdate(sqls, new DalHints().inShard(1));
    }

    static class TestPojoCallback implements PojoExecutionCallback {
        private Set<Integer> successIndexes;
        private Set<Integer> errorIndexes;
        private AtomicInteger successCount;
        private AtomicInteger errorCount;

        public TestPojoCallback(Set<Integer> successIndexes, Set<Integer> errorIndexes,
                                AtomicInteger successCount, AtomicInteger errorCount) {
            this.successIndexes = successIndexes;
            this.errorIndexes = errorIndexes;
            this.successCount = successCount;
            this.errorCount = errorCount;
        }

        @Override
        public void handle(PojoExecutionResult pojoResult) {
            if (pojoResult.isSuccess()) {
                successCount.incrementAndGet();
                successIndexes.add(pojoResult.getPojoIndex());
            } else {
                errorCount.incrementAndGet();
                errorIndexes.add(pojoResult.getPojoIndex());
                pojoResult.getErrorCause().printStackTrace();
            }
        }
    }

    static class TestShardCallback<V> implements ShardExecutionCallback<V> {
        private Set<String> successShards;
        private Set<String> errorShards;
        private AtomicInteger successCount;
        private AtomicInteger errorCount;

        public TestShardCallback(Set<String> successShards, Set<String> errorShards,
                                 AtomicInteger successCount, AtomicInteger errorCount) {
            this.successShards = successShards;
            this.errorShards = errorShards;
            this.successCount = successCount;
            this.errorCount = errorCount;
        }

        @Override
        public void handle(ShardExecutionResult<V> shardResult) {
            if (shardResult.isSuccess()) {
                successCount.incrementAndGet();
                successShards.add(shardResult.getDbShard());
            } else {
                errorCount.incrementAndGet();
                errorShards.add(shardResult.getDbShard());
                shardResult.getErrorCause().printStackTrace();
            }
        }
    }

}

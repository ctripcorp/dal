package com.ctrip.platform.dal.dao.shard;

import com.ctrip.platform.dal.dao.*;
import javafx.util.Pair;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author c7ch23en
 */
public class ShardExecutionCallbackTest {

    private static final String DB_NAME = "dao_test_mysql_exception_shard";
    private static final String TABLE_NAME = "shard_tbl";
    private static final int DB_SHARDS = 2;
    private static final int TABLE_SHARDS = 4;
    private static final String DROP_TABLE_SQL_TPL = "DROP TABLE IF EXISTS %s";
    private static final String CREATE_TABLE_SQL_TPL = "CREATE TABLE %s (" +
            "id int NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
            "dbIndex int NOT NULL, " +
            "tableIndex int NOT NULL, " +
            "intCol int, " +
            "charCol varchar(64))";

    private static DalClient dalClient;
    private DalTableDao<ShardExecutionCallbackTestTable> tableDao;
    private DalQueryDao queryDao;

    public ShardExecutionCallbackTest() throws SQLException {
        tableDao = new DalTableDao<>(ShardExecutionCallbackTestTable.class);
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
        try {
            tableDao.insert(new DalHints().continueOnError(), createPojos(), pojoResult -> {
                if (pojoResult.isSuccess()) {
                    successIndexes.add(pojoResult.getPojoIndex());
                } else {
                    errorIndexes.add(pojoResult.getPojoIndex());
                    pojoResult.getErrorCause().printStackTrace();
                }
            });
            Assert.assertEquals(6, successIndexes.size());
            Assert.assertTrue(successIndexes.contains(1));
            Assert.assertTrue(successIndexes.contains(2));
            Assert.assertTrue(successIndexes.contains(3));
            Assert.assertTrue(successIndexes.contains(4));
            Assert.assertTrue(successIndexes.contains(5));
            Assert.assertTrue(successIndexes.contains(6));
            Assert.assertEquals(2, errorIndexes.size());
            Assert.assertTrue(errorIndexes.contains(0));
            Assert.assertTrue(errorIndexes.contains(7));
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testCombinedInsert() throws SQLException {
        Map<String, String> errorShards = new ConcurrentHashMap<>();
        try {
            tableDao.combinedInsert(new DalHints(), createPojos(), shardResult -> {
                if (!shardResult.isSuccess()) {
                    errorShards.put(shardResult.getDbShard(), shardResult.getTableShard());
                    shardResult.getErrorCause().printStackTrace();
                }
            });
            Assert.fail();
        } catch (SQLException e) {
            Assert.assertEquals(1, errorShards.size());
        }
    }

    @Test
    public void testBatchInsert() throws SQLException {
        List<Pair<String, String>> successShards = new ArrayList<>();
        Map<String, String> errorShards = new HashMap<>();
        try {
            tableDao.batchInsert(new DalHints().continueOnError(), createPojos(), shardResult -> {
                if (shardResult.isSuccess()) {
                    successShards.add(new Pair<>(shardResult.getDbShard(), shardResult.getTableShard()));
                } else {
                    errorShards.put(shardResult.getDbShard(), shardResult.getTableShard());
                    shardResult.getErrorCause().printStackTrace();
                }
            });
            Assert.assertEquals(6, successShards.size());
            Assert.assertEquals(2, errorShards.size());
            Assert.assertEquals("0", errorShards.get("0"));
            Assert.assertEquals("3", errorShards.get("1"));
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryByPojo() throws SQLException {
        List<Pair<String, String>> successShards = new ArrayList<>();
        Map<String, String> errorShards = new HashMap<>();
        try {
            tableDao.queryBy(createQueryPojoWithTableIndex(7),
                    new DalHints().inAllShards().continueOnError(), shardResult -> {
                        if (shardResult.isSuccess()) {
                            successShards.add(new Pair<>(shardResult.getDbShard(), shardResult.getTableShard()));
                        } else {
                            errorShards.put(shardResult.getDbShard(), shardResult.getTableShard());
                            shardResult.getErrorCause().printStackTrace();
                        }
            });
            Assert.assertEquals(1, successShards.size());
            Assert.assertEquals(1, errorShards.size());
            Assert.assertEquals("3", errorShards.get("1"));
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryBySql() throws SQLException {
        List<Pair<String, String>> successShards = new ArrayList<>();
        Map<String, String> errorShards = new HashMap<>();
        try {
            String sql = "select * from shard_tbl_0 where id = 1";
            queryDao.query(sql, new StatementParameters(), new DalHints().inAllShards().continueOnError(),
                    ShardExecutionCallbackTestTable.class, shardResult -> {
                        if (shardResult.isSuccess()) {
                            successShards.add(new Pair<>(shardResult.getDbShard(), shardResult.getTableShard()));
                        } else {
                            errorShards.put(shardResult.getDbShard(), shardResult.getTableShard());
                            shardResult.getErrorCause().printStackTrace();
                        }
            });
            Assert.assertEquals(1, successShards.size());
            Assert.assertEquals(1, errorShards.size());
            Assert.assertEquals("0", errorShards.get("0"));
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    private List<ShardExecutionCallbackTestTable> createPojos() {
        List<ShardExecutionCallbackTestTable> pojos = new ArrayList<>();
        for (int i = 0; i < DB_SHARDS; i++)
            for (int j = 0; j < TABLE_SHARDS; j++)
                pojos.add(createPojo(i, j));
        return pojos;
    }

    private ShardExecutionCallbackTestTable createPojo(int dbIndex, int tableIndex) {
        ShardExecutionCallbackTestTable pojo = new ShardExecutionCallbackTestTable();
        pojo.setDbIndex(dbIndex);
        pojo.setTableIndex(tableIndex);
        pojo.setIntCol(dbIndex + tableIndex);
        pojo.setCharCol(String.valueOf(dbIndex + tableIndex));
        return pojo;
    }

    private ShardExecutionCallbackTestTable createQueryPojoWithDbIndex(int dbIndex) {
        ShardExecutionCallbackTestTable pojo = new ShardExecutionCallbackTestTable();
        pojo.setDbIndex(dbIndex);
        return pojo;
    }

    private ShardExecutionCallbackTestTable createQueryPojoWithTableIndex(int tableIndex) {
        ShardExecutionCallbackTestTable pojo = new ShardExecutionCallbackTestTable();
        pojo.setTableIndex(tableIndex);
        return pojo;
    }

    private static void createTables() throws SQLException {
        String[] sqls = new String[] {
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_0"),
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_1"),
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_2"),
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_3"),
                String.format(CREATE_TABLE_SQL_TPL, TABLE_NAME + "_1"),
                String.format(CREATE_TABLE_SQL_TPL, TABLE_NAME + "_2"),
                String.format(CREATE_TABLE_SQL_TPL, TABLE_NAME + "_3")
        };
        dalClient.batchUpdate(sqls, new DalHints().inShard(0));
        sqls = new String[] {
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_0"),
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_1"),
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_2"),
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_3"),
                String.format(CREATE_TABLE_SQL_TPL, TABLE_NAME + "_0"),
                String.format(CREATE_TABLE_SQL_TPL, TABLE_NAME + "_1"),
                String.format(CREATE_TABLE_SQL_TPL, TABLE_NAME + "_2")
        };
        dalClient.batchUpdate(sqls, new DalHints().inShard(1));
    }

    private static void dropTables() throws SQLException {
        String[] sqls = new String[] {
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_0"),
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_1"),
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_2"),
                String.format(DROP_TABLE_SQL_TPL, TABLE_NAME + "_3")
        };
        dalClient.batchUpdate(sqls, new DalHints().inShard(0));
        dalClient.batchUpdate(sqls, new DalHints().inShard(1));
    }

}

package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.client.DalCommand.TestTable;
import com.ctrip.platform.dal.dao.sqlbuilder.InsertSqlBuilder;
import org.junit.*;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DalShardingInTransactionTest {
    private final static String DATABASE_NAME_MYSQL = "dao_sharding_transaction_mysql";

    private final static String TABLE_NAME = "test_table";
    private final static int mod = 2;


    private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"("
            + "id int NOT NULL PRIMARY KEY AUTO_INCREMENT, "
            + "name VARCHAR(64))";

    private static DalClient clientMySql;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
        clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        DalHints hints = new DalHints();
        String[] sqls = null;
        for(int i = 0; i < mod; i++) {
            sqls = new String[] { DROP_TABLE_SQL_MYSQL_TPL, CREATE_TABLE_SQL_MYSQL_TPL};
            clientMySql.batchUpdate(sqls, hints.inShard(i));
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DalHints hints = new DalHints();
        String[] sqls = null;
        for(int i = 0; i < mod; i++) {
            sqls = new String[] { DROP_TABLE_SQL_MYSQL_TPL};
            clientMySql.batchUpdate(sqls, hints.inShard(i));
        }
    }

    @Before
    public void setUp() throws Exception {
//        SqlServerTestInitializer.setUp();
    }

    @After
    public void tearDown() throws Exception {
        String sql = "DELETE FROM " + TABLE_NAME;
        StatementParameters parameters = new StatementParameters();
        DalHints hints = new DalHints();
        try {
            for(int i = 0; i < mod; i++) {
                clientMySql.update(sql, parameters, hints.inShard(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSingleTaskWithNoShardIdInTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        final DalTableDao dao = new DalTableDao(TestTable.class, DATABASE_NAME_MYSQL);
        final TestTable pojo1 = new TestTable();
        final TestTable pojo2 = new TestTable();
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                pojo1.setName("testSingleTaskTransaction1");
                pojo2.setID(2);
                pojo2.setName("testSingleTaskTransaction2");
                List<TestTable> list = new ArrayList<>();
                list.add(pojo1);
                list.add(pojo2);
                dao.insert(new DalHints(), list);
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("testSingleTaskTransaction1", ((TestTable) dao.queryBy(pojo1, new DalHints().inShard(0)).get(0)).getName());
        assertEquals("testSingleTaskTransaction2", ((TestTable) dao.queryBy(pojo2, new DalHints().inShard(0)).get(0)).getName());
    }

    @Test
    public void testSingleTaskWithShardIdInDistributedTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        final DalTableDao dao = new DalTableDao(TestTable.class, DATABASE_NAME_MYSQL);
        final TestTable pojo1 = new TestTable();
        final TestTable pojo2 = new TestTable();
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                pojo1.setName("testSingleTaskTransaction1");
                pojo2.setID(1);
                pojo2.setName("testSingleTaskTransaction2");
                List<TestTable> list = new ArrayList<>();
                list.add(pojo1);
                list.add(pojo2);
                dao.insert(new DalHints(), list);
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(0, dao.queryBy(pojo1, new DalHints().inShard(0)).size());
        assertEquals(0, dao.queryBy(pojo2, new DalHints().inShard(0)).size());
        assertEquals(0, dao.queryBy(pojo1, new DalHints().inShard(1)).size());
        assertEquals(0, dao.queryBy(pojo2, new DalHints().inShard(1)).size());
    }

    @Test
    public void testSingleTaskCrossShardInTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        final DalTableDao dao = new DalTableDao(TestTable.class, DATABASE_NAME_MYSQL);
        final TestTable pojo1 = new TestTable();
        final TestTable pojo2 = new TestTable();
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                pojo1.setID(2);
                pojo1.setName("testSingleTaskTransaction1");
                pojo2.setID(1);
                pojo2.setName("testSingleTaskTransaction2");
                List<TestTable> list = new ArrayList<>();
                list.add(pojo1);
                list.add(pojo2);
                dao.insert(new DalHints(), list);
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Potential distributed operation detected in shards"));
        }
        assertEquals(0, dao.queryBy(pojo1, new DalHints().inShard(0)).size());
        assertEquals(0, dao.queryBy(pojo2, new DalHints().inShard(0)).size());
        assertEquals(0, dao.queryBy(pojo1, new DalHints().inShard(1)).size());
        assertEquals(0, dao.queryBy(pojo2, new DalHints().inShard(1)).size());
    }

    @Test
    public void testBulkTaskWithNoShardIdInTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        final DalTableDao dao = new DalTableDao(TestTable.class, DATABASE_NAME_MYSQL);
        final TestTable pojo1 = new TestTable();
        final TestTable pojo2 = new TestTable();
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                pojo1.setName("testSingleTaskTransaction1");
                pojo2.setName("testSingleTaskTransaction2");
                List<TestTable> list = new ArrayList<>();
                list.add(pojo1);
                list.add(pojo2);
                dao.batchInsert(new DalHints(), list);
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
        } catch (Exception e) {
            fail();
        }
        assertEquals("testSingleTaskTransaction1", ((TestTable) dao.queryBy(pojo1, new DalHints().inShard(0)).get(0)).getName());
        assertEquals("testSingleTaskTransaction2", ((TestTable) dao.queryBy(pojo2, new DalHints().inShard(0)).get(0)).getName());
        assertEquals(0, dao.queryBy(pojo1, new DalHints().inShard(1)).size());
        assertEquals(0, dao.queryBy(pojo2, new DalHints().inShard(1)).size());
    }

//    no cross shard detect in transaction now ,but will detect later
    @Test
    public void testBulkTaskCrossShardIdInTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        final DalTableDao dao = new DalTableDao(TestTable.class, DATABASE_NAME_MYSQL);
        final TestTable pojo1 = new TestTable();
        final TestTable pojo2 = new TestTable();
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                pojo1.setID(1);
                pojo1.setName("testSingleTaskTransaction1");
                pojo2.setID(2);
                pojo2.setName("testSingleTaskTransaction2");
                List<TestTable> list = new ArrayList<>();
                list.add(pojo1);
                list.add(pojo2);
                dao.batchInsert(new DalHints(), list);
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
        } catch (Exception e) {
            fail();
        }
        assertEquals("testSingleTaskTransaction1", ((TestTable) dao.queryBy(pojo1, new DalHints().inShard(0)).get(0)).getName());
        assertEquals("testSingleTaskTransaction2", ((TestTable) dao.queryBy(pojo2, new DalHints().inShard(0)).get(0)).getName());
        assertEquals(0, dao.queryBy(pojo1, new DalHints().inShard(1)).size());
        assertEquals(0, dao.queryBy(pojo2, new DalHints().inShard(1)).size());
    }

    @Test
    public void testBulkTaskWithShardIdInDistributedTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        final DalTableDao dao = new DalTableDao(TestTable.class, DATABASE_NAME_MYSQL);
        final TestTable pojo1 = new TestTable();
        final TestTable pojo2 = new TestTable();
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                pojo1.setName("testSingleTaskTransaction1");
                pojo2.setName("testSingleTaskTransaction2");
                List<TestTable> list = new ArrayList<>();
                list.add(pojo1);
                list.add(pojo2);
                dao.batchInsert(new DalHints().inShard(1), list);
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
            fail();
        } catch (Exception e) {
            assertEquals("DAL do not support distributed transaction in same DB but different shard. Current shard: 0, requested in hints: 1",e.getMessage());
        }
        assertEquals(0, dao.queryBy(pojo1, new DalHints().inShard(0)).size());
        assertEquals(0, dao.queryBy(pojo2, new DalHints().inShard(0)).size());
        assertEquals(0, dao.queryBy(pojo1, new DalHints().inShard(1)).size());
        assertEquals(0, dao.queryBy(pojo2, new DalHints().inShard(1)).size());
    }

    @Test
    public void testSqlTaskWithNoShardIdInTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        final DalTableDao dao = new DalTableDao(TestTable.class, DATABASE_NAME_MYSQL);

        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                InsertSqlBuilder builder = new InsertSqlBuilder();
                builder.set("Name", "testSqlTaskWithShardIdInDistributedTransaction", Types.VARCHAR);
                dao.insert(builder, new DalHints());
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
        } catch (Exception e) {
            fail();
        }
        TestTable testTable=new TestTable();
        testTable.setName("testSqlTaskWithShardIdInDistributedTransaction");
        assertEquals(1, dao.queryBy(testTable, new DalHints().inShard(0)).size());
        assertEquals(0, dao.queryBy(testTable, new DalHints().inShard(1)).size());
    }

    @Test
    public void testSqlTaskWithShardIdInDistributedTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        final DalTableDao dao = new DalTableDao(TestTable.class, DATABASE_NAME_MYSQL);

        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                InsertSqlBuilder builder = new InsertSqlBuilder();
                builder.set("Name", "testSqlTaskWithShardIdInDistributedTransaction", Types.VARCHAR);
                dao.insert(builder, new DalHints().inShard(1));
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
            fail();
        } catch (Exception e) {
            assertEquals("DAL do not support distributed transaction in same DB but different shard. Current shard: 0, requested in hints: 1",e.getMessage());
        }
        TestTable testTable=new TestTable();
        testTable.setName("testSqlTaskWithShardIdInDistributedTransaction");
        assertEquals(0, dao.queryBy(testTable, new DalHints().inShard(0)).size());
        assertEquals(0, dao.queryBy(testTable, new DalHints().inShard(1)).size());
    }

    @Test
    public void testSqlTaskCrossShardIdInTransaction() throws Exception {
        DalClient client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        final DalTableDao dao = new DalTableDao(TestTable.class, DATABASE_NAME_MYSQL);

        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                InsertSqlBuilder builder = new InsertSqlBuilder();
                builder.set("Name", "testSqlTaskCrossShardIdInTransaction", Types.VARCHAR);
                dao.insert(builder, new DalHints().inShard(0));
                dao.query("name='testSqlTaskCrossShardIdInTransaction'",new StatementParameters(),new DalHints().inAllShards());
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Potential distributed operation detected in shards"));
        }
        TestTable testTable=new TestTable();
        testTable.setName("testSqlTaskCrossShardIdInTransaction");
        assertEquals(0, dao.queryBy(testTable, new DalHints().inShard(0)).size());
        assertEquals(0, dao.queryBy(testTable, new DalHints().inShard(1)).size());
    }
}

package cluster;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.configure.ClusterDatabaseSet;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;
import entity.MysqlPersonTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class ClusterDaoTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterDaoTest.class);

    private static final String CLUSTER_NAME = "dal_sharding_cluster";
    private static final String PLUGIN_API_URL = "http://qconfig.ctripcorp.com/plugins/dal/config/batchrelease?env=fat&operator=dal-integration-test";
    private static final long BASE_VERSION = 1;
    private static final int CHECK_MAX_COUNT = 20;
    private static final int CHECK_PERIOD_MS = 500;
    private static final String SELECT_SQL_TEMPLATE = "select name from person where id = ?";
    private static final String INSERT_SQL_TEMPLATE = "insert into person (name, age) values (?, 10)";
    private static final String UPDATE_SQL_TEMPLATE = "update person set age = 1 where name = ?";
    private static final String DELETE_SQL_TEMPLATE = "delete from person where name = ?";

    private DalTableDao<MysqlPersonTable> tableDao;
    private DalQueryDao queryDao;
    private Cluster cluster;

    public ClusterDaoTest() throws Exception {
        tableDao = new DalTableDao<>(MysqlPersonTable.class, CLUSTER_NAME);
        queryDao = new DalQueryDao(CLUSTER_NAME);
        cluster = ((ClusterDatabaseSet) DalClientFactory.getDalConfigure().getDatabaseSet(CLUSTER_NAME)).getCluster();
    }

    @Before
    public void before() {
        setDrcProps();
    }

    @After
    public void after() throws Exception {
        restoreCluster();
    }

    @Test
    public void doTests() throws Exception {
        // drc cluster
        setupDrcCluster();
        doWriteTests(AssertHandler.DRC);
        doReadTests();
        // normal cluster
        setupNormalCluster();
        doWriteTests(AssertHandler.NORMAL);
        doReadTests();
    }

    private void doWriteTests(AssertHandler handler) {
        doTest(this::testInsertPojo, handler);
        doTest(this::testInsertPojos, handler);
        doTest(this::testCombinedInsertPojos, handler);
        doTest(this::testBatchInsertPojos, handler);
        doTest(this::testInsertWithBuilder, handler);
        doTest(this::testInsertWithFreeSql, handler);
        doTest(this::testUpdatePojo, handler);
        doTest(this::testUpdatePojos, handler);
        doTest(this::testBatchUpdatePojos, handler);
        doTest(this::testUpdateWithBuilder, handler);
        doTest(this::testUpdateWithFreeSql, handler);
        doTest(this::testDeletePojo, handler);
        doTest(this::testDeletePojos, handler);
        doTest(this::testBatchDeletePojos, handler);
        doTest(this::testDeleteWithBuilder, handler);
        doTest(this::testDeleteWithFreeSql, handler);
        doTest(this::testReplacePojo, handler);
        doTest(this::testReplacePojos, handler);
        doTest(this::testCombinedReplacePojos, handler);
        doTest(this::testBatchReplacePojos, handler);
    }

    private void doReadTests() {
        doTest(this::testQueryByPk, AssertHandler.NORMAL);
        doTest(this::testQueryBy, AssertHandler.NORMAL);
        doTest(this::testQueryWithBuilder, AssertHandler.NORMAL);
        doTest(this::testQueryWithFreeSql, AssertHandler.NORMAL);
        doTest(this::testQueryWithMultiSql, AssertHandler.NORMAL);
    }

    private void doTest(TestRunner task, AssertHandler handler) {
        try {
            task.run();
            handler.assertPass();
        } catch (Exception e) {
            handler.assertFail(e);
        }
    }

    private void testInsertPojo() throws Exception {
        tableDao.insert(createHints(), createPojo());
    }

    private void testInsertPojos() throws Exception {
        tableDao.insert(createHints(), createPojos());
    }

    private void testCombinedInsertPojos() throws Exception {
        tableDao.combinedInsert(createHints(), createPojos());
    }

    private void testBatchInsertPojos() throws Exception {
        tableDao.batchInsert(createHints(), createPojos());
    }

    private void testInsertWithBuilder() throws Exception {
        tableDao.insert(createInsertBuilder(), createHints());
    }

    private void testInsertWithFreeSql() throws Exception {
        queryDao.update(createFreeUpdateBuilder(INSERT_SQL_TEMPLATE), createHints());
    }

    private void testUpdatePojo() throws Exception {
        tableDao.update(createHints(), createPojo());
    }

    private void testUpdatePojos() throws Exception {
        tableDao.update(createHints(), createPojos());
    }

    private void testBatchUpdatePojos() throws Exception {
        tableDao.batchUpdate(createHints(), createPojos());
    }

    private void testUpdateWithBuilder() throws Exception {
        tableDao.update(createUpdateBuilder(), createHints());
    }

    private void testUpdateWithFreeSql() throws Exception {
        queryDao.update(createFreeUpdateBuilder(UPDATE_SQL_TEMPLATE), createHints());
    }

    private void testDeletePojo() throws Exception {
        tableDao.delete(createHints(), createPojo());
    }

    private void testDeletePojos() throws Exception {
        tableDao.delete(createHints(), createPojos());
    }

    private void testBatchDeletePojos() throws Exception {
        tableDao.batchDelete(createHints(), createPojos());
    }

    private void testDeleteWithBuilder() throws Exception {
        tableDao.delete(createDeleteBuilder(), createHints());
    }

    private void testDeleteWithFreeSql() throws Exception {
        queryDao.update(createFreeUpdateBuilder(DELETE_SQL_TEMPLATE), createHints());
    }

    private void testReplacePojo() throws Exception {
        tableDao.replace(createHints(), createPojo());
    }

    private void testReplacePojos() throws Exception {
        tableDao.replace(createHints(), createPojos());
    }

    private void testCombinedReplacePojos() throws Exception {
        tableDao.combinedReplace(createHints(), createPojos());
    }

    private void testBatchReplacePojos() throws Exception {
        tableDao.batchReplace(createHints(), createPojos());
    }

    private void testQueryByPk() throws Exception {
        tableDao.queryByPk(createPojo(), createHints());
    }

    private void testQueryBy() throws Exception {
        tableDao.queryBy(createPojo(), createHints());
    }

    private void testQueryWithBuilder() throws Exception {
        tableDao.query(createSelectBuilder(), createHints());
    }

    private void testQueryWithFreeSql() throws Exception {
        queryDao.query(createFreeSelectBuilder(), createHints());
    }

    private void testQueryWithMultiSql() throws Exception {
        queryDao.query(createMultipleBuilder(), createHints());
    }

    private DalHints createHints() {
        return new DalHints().inShard(0);
    }

    private MysqlPersonTable createPojo() {
        return createPojo(10, "testName", 100);
    }

    private List<MysqlPersonTable> createPojos() {
        List<MysqlPersonTable> pojos = new ArrayList<>();
        pojos.add(createPojo(11, "testName1", 101));
        pojos.add(createPojo(12, "testName2", 102));
        return pojos;
    }

    private InsertSqlBuilder createInsertBuilder() {
        InsertSqlBuilder builder = new InsertSqlBuilder();
        builder.set("name", "testName", Types.VARCHAR);
        builder.set("age", 100, Types.INTEGER);
        return builder;
    }

    private UpdateSqlBuilder createUpdateBuilder() throws Exception {
        UpdateSqlBuilder builder = new UpdateSqlBuilder();
        builder.update("age", 100, Types.INTEGER);
        builder.equal("name", "testName", Types.VARCHAR);
        return builder;
    }

    private DeleteSqlBuilder createDeleteBuilder() throws Exception {
        DeleteSqlBuilder builder = new DeleteSqlBuilder();
        builder.equal("name", "testName", Types.VARCHAR);
        return builder;
    }

    private SelectSqlBuilder createSelectBuilder() throws Exception {
        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.equal("name", "testName", Types.VARCHAR);
        return builder;
    }

    private FreeUpdateSqlBuilder createFreeUpdateBuilder(String sql) {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
        builder.setTemplate(sql);
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.VARCHAR, "testName");
        builder.with(parameters);
        return builder;
    }

    private FreeSelectSqlBuilder<List<String>> createFreeSelectBuilder() {
        FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>();
        builder.setTemplate(SELECT_SQL_TEMPLATE);
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.INTEGER, 10);
        builder.with(parameters);
        return builder;
    }

    private MultipleSqlBuilder createMultipleBuilder() throws Exception {
        MultipleSqlBuilder builder = new MultipleSqlBuilder();
        StatementParameters parameters1 = new StatementParameters();
        parameters1.set(1, Types.INTEGER, 11);
        builder.addQuery(SELECT_SQL_TEMPLATE, parameters1, String.class);
        StatementParameters parameters2 = new StatementParameters();
        parameters2.set(1, Types.INTEGER, 12);
        builder.addQuery(SELECT_SQL_TEMPLATE, parameters2, String.class);
        return builder;
    }

    private MysqlPersonTable createPojo(Integer id, String name, Integer age) {
        MysqlPersonTable pojo = new MysqlPersonTable();
        pojo.setID(id);
        pojo.setName(name);
        pojo.setAge(age);
        return pojo;
    }

    interface TestRunner {
        void run() throws Exception;
    }

    enum AssertHandler {
        NORMAL() {
            @Override
            void assertPass() {}
            @Override
            void assertFail(Exception e) {
                throw new RuntimeException(e);
            }
        },
        DRC() {
            @Override
            void assertPass() {
                Assert.fail();
            }
            @Override
            void assertFail(Exception e) {
                if (e instanceof DalException)
                    Assert.assertEquals(ErrorCode.NonLocalRequestBlocked.getCode(), ((DalException) e).getErrorCode());
                else
                    throw new RuntimeException(e);
            }
        };
        abstract void assertPass();
        abstract void assertFail(Exception e);
    }

    private void setDrcProps() {
        new MockElementFactory().getLocator().setProperties(buildDrcProps());
    }

    private Map<String, String> buildDrcProps() {
        Map<String, String> props = new HashMap<>();
        props.put("DrcStage", "test");
        props.put("DrcStage.test.Localized", "true");
        return props;
    }

    private void setupDrcCluster() throws Exception {
        HttpExecutor.getInstance().executePost(PLUGIN_API_URL, new HashMap<>(),
                buildClusterBody(ClusterType.DRC, BASE_VERSION + 1), 2000);
        checkCluster(ClusterType.DRC);
    }

    private void setupNormalCluster() throws Exception {
        HttpExecutor.getInstance().executePost(PLUGIN_API_URL, new HashMap<>(),
                buildClusterBody(ClusterType.NORMAL, BASE_VERSION + 2), 2000);
        checkCluster(ClusterType.NORMAL);
    }

    private void restoreCluster() throws Exception {
        HttpExecutor.getInstance().executePost(PLUGIN_API_URL, new HashMap<>(),
                buildClusterBody(ClusterType.NORMAL, BASE_VERSION), 2000);
    }

    private void checkCluster(ClusterType type) throws Exception {
        int current = CHECK_MAX_COUNT;
        while (current-- > 0 && cluster.getClusterType() != type)
            Thread.sleep(CHECK_PERIOD_MS);
        if (current < 0)
            Assert.fail(String.format("Failed to switch cluster to %s after %d ms",
                    type.getValue(), CHECK_MAX_COUNT * CHECK_PERIOD_MS));
        else
            LOGGER.info(String.format("Cluster switched to %s after %d ms",
                    type.getValue(), CHECK_PERIOD_MS * (CHECK_MAX_COUNT - 1 - current)));
    }

    private String buildClusterBody(ClusterType type, long version) {
        String format = "{\n" +
                "  \"toRelease\": [\n" +
                "    {\n" +
                "      \"clusterName\": \"%s\",\n" +
                "      \"dbCategory\": \"mysql\",\n" +
                "      \"version\": %d,\n" +
                "      \"databaseShards\": [\n" +
                "        {\n" +
                "          \"index\": 0,\n" +
                "          \"masterDomain\": \"10.32.20.139\",\n" +
                "          \"masterPort\": 3306,\n" +
                "          \"databases\": [\n" +
                "            {\n" +
                "              \"ip\": \"10.32.20.139\",\n" +
                "              \"port\": 3306,\n" +
                "              \"dbName\": \"llj_crossshardbymod_0\",\n" +
                "              \"uid\": \"root\",\n" +
                "              \"password\": \"!QAZ@WSX1qaz2wsx\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"index\": 1,\n" +
                "          \"masterDomain\": \"10.32.20.139\",\n" +
                "          \"masterPort\": 3306,\n" +
                "          \"databases\": [\n" +
                "            {\n" +
                "              \"ip\": \"10.32.20.139\",\n" +
                "              \"port\": 3306,\n" +
                "              \"dbName\": \"llj_crossshardbymod_1\",\n" +
                "              \"uid\": \"root\",\n" +
                "              \"password\": \"!QAZ@WSX1qaz2wsx\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"index\": 2,\n" +
                "          \"masterDomain\": \"10.32.20.139\",\n" +
                "          \"masterPort\": 3306,\n" +
                "          \"databases\": [\n" +
                "            {\n" +
                "              \"ip\": \"10.32.20.139\",\n" +
                "              \"port\": 3306,\n" +
                "              \"dbName\": \"llj_crossshardbymod_2\",\n" +
                "              \"uid\": \"root\",\n" +
                "              \"password\": \"!QAZ@WSX1qaz2wsx\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"index\": 3,\n" +
                "          \"masterDomain\": \"10.32.20.139\",\n" +
                "          \"masterPort\": 3306,\n" +
                "          \"databases\": [\n" +
                "            {\n" +
                "              \"ip\": \"10.32.20.139\",\n" +
                "              \"port\": 3306,\n" +
                "              \"dbName\": \"llj_crossshardbymod_3\",\n" +
                "              \"uid\": \"root\",\n" +
                "              \"password\": \"!QAZ@WSX1qaz2wsx\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"shardStrategies\": \"<UserHintStrategy default=\\\"true\\\"/>\",\n" +
                "      \"type\": \"%s\",\n" +
                "      \"unitStrategyId\": 1\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        return String.format(format, CLUSTER_NAME, version, type.getValue());
    }

}

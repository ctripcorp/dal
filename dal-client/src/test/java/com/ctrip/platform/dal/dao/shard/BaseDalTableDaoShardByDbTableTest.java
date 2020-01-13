package com.ctrip.platform.dal.dao.shard;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.configure.LocalDalPropertiesProvider;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.ctrip.platform.dal.dao.sqlbuilder.DeleteSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.Expressions;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.InsertSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.UpdateSqlBuilder;
import org.junit.Assert;
import org.junit.Test;
import com.ctrip.platform.dal.dao.unitbase.BaseTestStub.DatabaseDifference;

import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.Map.Entry;

import static org.junit.Assert.*;
import static com.ctrip.platform.dal.dao.unittests.DalTestHelper.deleteAllShardsByDbTable;
import static com.ctrip.platform.dal.dao.unittests.DalTestHelper.getCountByDbTable;
import static org.junit.Assert.assertEquals;

public abstract class BaseDalTableDaoShardByDbTableTest {
    private boolean INSERT_PK_BACK_ALLOWED = false;
    public final static String TABLE_NAME = "dal_client_test";
    public final static int mod = 2;
    public final static int tableMod = 4;

    private DatabaseDifference diff;
    private static DalTableDao<ClientTestModel> dao;
    private static DalQueryDao queryDao;


    public BaseDalTableDaoShardByDbTableTest(String databaseName, DatabaseDifference diff) {
        this.diff = diff;
        try {
            DalClientFactory.initClientFactory();
            DalParser<ClientTestModel> clientTestParser = new ClientTestDalParser(databaseName);
            dao = new DalTableDao<ClientTestModel>(clientTestParser);
            ASSERT_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;
            INSERT_PK_BACK_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;

            queryDao = new DalQueryDao(databaseName);
        } catch (Exception e) {

        }
    }

    public abstract void reset() throws SQLException;

    private boolean ASSERT_ALLOWED = true;

    public void assertResEquals(int exp, int res) {
        if (ASSERT_ALLOWED)
            Assert.assertEquals(exp, res);
    }

    public void assertResEquals(int exp, int[] res) {
        if (ASSERT_ALLOWED) {
            int total = 0;
            for (int t : res)
                total += t;
            Assert.assertEquals(exp, total);
        }
    }

    public int getCount(int shardId, int tableShardId) throws SQLException {
        return getCountByDbTable(dao, shardId, tableShardId);
    }

    public void deleteAllShards(int shardId) throws SQLException {
        for (int i = 0; i < tableMod; i++) {
            int j = 1;
            dao.delete("1=1", new StatementParameters(), new DalHints().inShard(shardId).inTableShard(i));
        }
    }

    public ClientTestModel[] createNoId(int count) throws SQLException {
        ClientTestModel[] entities = new ClientTestModel[count];
        for (int i = 0; i < count; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities[i] = model;
        }
        return entities;
    }

    public List<ClientTestModel> create(int count) throws SQLException {
        ClientTestModel[] entities = createNoId(count);
        for (int i = 0; i < count; i++) {
            entities[i].setId(i + 1);
        }
        return Arrays.asList(entities);
    }

    public List<ClientTestModel> createListNoId(int count) throws SQLException {
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }
        return entities;
    }

    List<ClientTestModel> getModels(int shardId, int tableShardId) throws SQLException {
        ClientTestModel[] entities =
                dao.query("1=1", new StatementParameters(), new DalHints().inShard(shardId).inTableShard(tableShardId))
                        .toArray(new ClientTestModel[tableShardId + 1]);
        for (ClientTestModel model : entities) {
            model.setTableIndex(null);
            model.setDbIndex(null);
        }
        return Arrays.asList(entities);
    }

    private final static String GENERATED_KEY = "GENERATED_KEY";

    public void assertKeyHolder(KeyHolder holder) throws SQLException {
        if (!ASSERT_ALLOWED)
            return;

        Assert.assertEquals(3, holder.size());
        Assert.assertTrue(holder.getKey(0).longValue() > 0);
        Assert.assertTrue(holder.getKeyList().get(0).containsKey(GENERATED_KEY));
    }

    // Only for sql server
    public KeyHolder createKeyHolder() {
        return ASSERT_ALLOWED ? new KeyHolder() : null;
    }

    // Only for sql server
    public void assertResEquals(int[] expected, int[] res) {
        Assert.assertEquals(expected.length, res.length);
        if (ASSERT_ALLOWED)
            Assert.assertArrayEquals(expected, res);
    }

    private class TestQueryResultCallback extends DefaultResultCallback {

        public ClientTestModel get() {
            try {
                waitForDone();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return (ClientTestModel) getResult();
        }

        public List<ClientTestModel> getModels() {
            try {
                waitForDone();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return (List<ClientTestModel>) getResult();
        }
    }

    private DalHints asyncHints() {
        return new DalHints().asyncExecution();
    }

    private DalHints callbackHints() {
        return new DalHints().callbackWith(new TestQueryResultCallback());
    }

    private DalHints intHints() {
        return new DalHints().callbackWith(new IntCallback());
    }

    private DalHints copy(DalHints oldhints) {
        DalHints hints = oldhints.clone();
        if (hints.is(DalHintEnum.resultCallback)) {
            DefaultResultCallback callback = (DefaultResultCallback) hints.get(DalHintEnum.resultCallback);
            callback.reset();
        }
        return hints;
    }

    private ClientTestModel assertModel(Object model, DalHints hints) throws SQLException {
        if (!hints.isAsyncExecution())
            return (ClientTestModel) model;

        assertNull(model);
        if (hints.is(DalHintEnum.resultCallback)) {
            TestQueryResultCallback callback = (TestQueryResultCallback) hints.get(DalHintEnum.resultCallback);
            try {
                callback.waitForDone();
            } catch (InterruptedException e) {
                throw new SQLException(e);
            }
            if (callback.isSuccess())
                return callback.get();
            else
                throw new SQLException(callback.getError());
        }

        try {
            return (ClientTestModel) hints.getAsyncResult().get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    private List<ClientTestModel> assertModels(Object models, DalHints hints) throws SQLException {
        if (!hints.isAsyncExecution())
            return (List<ClientTestModel>) models;

        assertNull(models);
        if (hints.is(DalHintEnum.resultCallback)) {
            TestQueryResultCallback callback = (TestQueryResultCallback) hints.get(DalHintEnum.resultCallback);
            try {
                callback.waitForDone();
            } catch (InterruptedException e) {
                throw new SQLException(e);
            }
            if (callback.isSuccess())
                return callback.getModels();
            else
                throw new SQLException(callback.getError());
        }
        try {
            return (List<ClientTestModel>) hints.getAsyncResult().get();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private int assertInt(int res, DalHints hints) throws SQLException {
        if (!hints.isAsyncExecution())
            return res;

        assertEquals(0, res);
        if (hints.is(DalHintEnum.resultCallback)) {
            IntCallback callback = (IntCallback) hints.get(DalHintEnum.resultCallback);
            try {
                callback.waitForDone();
            } catch (InterruptedException e) {
                throw new SQLException(e);
            }
            if (callback.isSuccess())
                return callback.getInt();
            else
                throw new SQLException(callback.getError());
        }
        try {
            return ((int[]) hints.getAsyncResult().get())[0];
        } catch (Exception e) {
            try {
                return (Integer) hints.getAsyncResult().get();
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
        }
    }

    private int[] assertIntArray(int[] res, DalHints hints) throws SQLException {
        if (!hints.isAsyncExecution())
            return res;

        assertNull(res);
        if (hints.is(DalHintEnum.resultCallback)) {
            IntCallback callback = (IntCallback) hints.get(DalHintEnum.resultCallback);
            try {
                callback.waitForDone();
            } catch (InterruptedException e) {
                throw new SQLException(e);
            }
            if (callback.isSuccess())
                return callback.getIntArray();
            else
                throw new SQLException(callback.getError());
        }
        try {
            return (int[]) hints.getAsyncResult().get();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private void assertFail(Object model, DalHints hints) {
        assertNull(model);
        DefaultResultCallback callback = (DefaultResultCallback) hints.get(DalHintEnum.resultCallback);
        try {
            callback.waitForDone();
        } catch (InterruptedException e) {
            fail(e.toString());
        }
        assertTrue(!callback.isSuccess());
    }

    private void assertFail(int res, DalHints hints) {
        assertEquals(0, res);
        DefaultResultCallback callback = (DefaultResultCallback) hints.get(DalHintEnum.resultCallback);
        try {
            callback.waitForDone();
        } catch (InterruptedException e) {
            fail(e.toString());
        }
        assertTrue(!callback.isSuccess());
    }

    /**
     * Test Query by Primary key
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryByPk() throws SQLException {
        ClientTestModel model = null;

        for (int i = 0; i < mod; i++) {
            // By shard
            if (i % 2 == 0)
                testQueryByPk(i, new DalHints().inShard(String.valueOf(i)));
            else
                testQueryByPk(i, new DalHints().inShard(i));

            // By shardValue
            if (i % 2 == 0)
                testQueryByPk(i, new DalHints().setShardValue(String.valueOf(i)));
            else
                testQueryByPk(i, new DalHints().setShardValue(i));

            // By shardColValue
            if (i % 2 == 0)
                testQueryByPk(i, new DalHints().setShardColValue("index", String.valueOf(i)));
            else
                testQueryByPk(i, new DalHints().setShardColValue("index", i));

            // By shardColValue
            if (i % 2 == 0)
                testQueryByPk(i, new DalHints().setShardColValue("dbIndex", String.valueOf(i)));
            else
                testQueryByPk(i, new DalHints().setShardColValue("dbIndex", i));

        }
    }

    @Test
    public void testQueryByPkAsyncCallback() throws SQLException {
        ClientTestModel model = null;
        DalHints hints;

        for (int i = 0; i < mod; i++) {
            // By shard
            hints = asyncHints();
            if (i % 2 == 0)
                testQueryByPk(i, hints.inShard(String.valueOf(i)));
            else
                testQueryByPk(i, hints.inShard(i));

            // By shardValue
            hints = callbackHints();
            if (i % 2 == 0)
                testQueryByPk(i, hints.setShardValue(String.valueOf(i)));
            else
                testQueryByPk(i, hints.setShardValue(i));

            // By shardColValue
            hints = asyncHints();
            if (i % 2 == 0)
                testQueryByPk(i, hints.setShardColValue("index", String.valueOf(i)));
            else
                testQueryByPk(i, hints.setShardColValue("index", i));

            // By shardColValue
            hints = callbackHints();
            if (i % 2 == 0)
                testQueryByPk(i, hints.setShardColValue("dbIndex", String.valueOf(i)));
            else
                testQueryByPk(i, hints.setShardColValue("dbIndex", i));

        }
    }

    private void testQueryByPk(int shardId, DalHints oldHints) throws SQLException {
        ClientTestModel model = null;
        DalHints hints;

        for (int i = 0; i < tableMod; i++) {
            int id = 1;
            // By tabelShard
            hints = copy(oldHints);
            if (i % 2 == 0)
                model = dao.queryByPk(1, hints.inTableShard(String.valueOf(i)));
            else
                model = dao.queryByPk(1, hints.inTableShard(i));
            model = assertModel(model, hints);
            assertQueryByPk(shardId, model, i, id);

            // By tableShardValue
            hints = copy(oldHints);
            if (i % 2 == 0)
                model = dao.queryByPk(1, hints.setTableShardValue(String.valueOf(i)));
            else
                model = dao.queryByPk(1, hints.setTableShardValue(i));
            model = assertModel(model, hints);
            assertQueryByPk(shardId, model, i, id);

            // By shardColValue
            hints = copy(oldHints);
            if (i % 2 == 0)
                model = dao.queryByPk(1, hints.setShardColValue("table", String.valueOf(i)));
            else
                model = dao.queryByPk(1, hints.setShardColValue("table", i));
            model = assertModel(model, hints);
            assertQueryByPk(shardId, model, i, id);

            // By shardColValue
            hints = copy(oldHints);
            if (i % 2 == 0)
                model = dao.queryByPk(1, hints.setShardColValue("tableIndex", String.valueOf(i)));
            else
                model = dao.queryByPk(1, hints.setShardColValue("tableIndex", i));
            model = assertModel(model, hints);
            assertQueryByPk(shardId, model, i, id);
        }
    }

    private void assertQueryByPk(int shardId, ClientTestModel model, int i, int id) {
        assertQueryFirstWithWhereClause(shardId, model, i);
        Assert.assertEquals(id * (shardId + 1) * (i + 1), model.getQuantity().intValue());
    }

    @Test
    public void testQueryByPkWithEntity() throws SQLException {
        testQueryByPkWithEntity(new DalHints());
        testQueryByPkWithEntity(asyncHints());
        testQueryByPkWithEntity(callbackHints());
    }

    @Test
    public void testQueryByPkWithEntityImplicitAllShards() throws SQLException {
        //		set implicitAllShards on
        DalPropertiesManager.getInstance().tearDown();
        DalPropertiesManager.getInstance().setDalPropertiesProvider(new LocalDalPropertiesProvider());
        DalPropertiesManager.getInstance().setup();

        testQueryByPkWithEntity(new DalHints());
        testQueryByPkWithEntity(asyncHints());
        testQueryByPkWithEntity(callbackHints());

        //		set implicitAllShards on
        DalPropertiesManager.getInstance().tearDown();
        DalPropertiesManager.getInstance().setDalPropertiesProvider(DalElementFactory.DEFAULT.getDalPropertiesProvider());
        DalPropertiesManager.getInstance().setup();
    }

    private void testQueryByPkWithEntity(DalHints hints) throws SQLException {
        for (int i = 0; i < mod; i++) {
            // By shard
            testQueryByPkWithEntity(i, copy(hints).inShard(i));

            // By shardValue
            testQueryByPkWithEntity(i, copy(hints).setShardValue(i));

            // By shardColValue
            testQueryByPkWithEntity(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testQueryByPkWithEntity(i, copy(hints).setShardColValue("dbIndex", i));

            // By fields
            // This is merged with the sub test
        }
    }

    private void testQueryByPkWithEntity(int shardId, DalHints oldHints) throws SQLException {
        ClientTestModel pk = null;
        ClientTestModel model = null;
        DalHints hints;

        for (int i = 0; i < tableMod; i++) {
            int id = 1;
            pk = new ClientTestModel();
            pk.setId(1);

            // By tabelShard
            hints = copy(oldHints);
            model = dao.queryByPk(pk, hints.inTableShard(i));
            model = assertModel(model, hints);
            assertQueryByPk(shardId, model, i, id);

            // By tableShardValue
            hints = copy(oldHints);
            model = dao.queryByPk(pk, hints.setTableShardValue(i));
            model = assertModel(model, hints);
            assertQueryByPk(shardId, model, i, id);

            // By shardColValue
            hints = copy(oldHints);
            model = dao.queryByPk(pk, hints.setShardColValue("table", i));
            model = assertModel(model, hints);
            assertQueryByPk(shardId, model, i, id);

            // By shardColValue
            hints = copy(oldHints);
            model = dao.queryByPk(pk, hints.setShardColValue("tableIndex", i));
            model = assertModel(model, hints);
            assertQueryByPk(shardId, model, i, id);

            // By fields
            pk.setTableIndex(i);
            pk.setDbIndex(shardId);
            hints = copy(oldHints);
            model = dao.queryByPk(pk, hints);
            model = assertModel(model, hints);
            assertQueryByPk(shardId, model, i, id);
        }
    }

    /**
     * Query by Entity without Primary key
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryByPkWithEntityNoId() throws SQLException {
        testQueryByPkWithEntityNoId(new DalHints());
        testQueryByPkWithEntityNoId(asyncHints());
        testQueryByPkWithEntityNoId(callbackHints());
    }

    private void testQueryByPkWithEntityNoId(DalHints hints) throws SQLException {
        for (int i = 0; i < mod; i++) {
            ClientTestModel pk = new ClientTestModel();
            pk.setDbIndex(i);

            // By shard
            testQueryByPkWithEntityNoId(i, copy(hints).inShard(i), pk);

            // By shardValue
            testQueryByPkWithEntityNoId(i, copy(hints).setShardValue(i), pk);

            // By shardColValue
            testQueryByPkWithEntityNoId(i, copy(hints).setShardColValue("index", i), pk);

            // By shardColValue
            testQueryByPkWithEntityNoId(i, copy(hints).setShardColValue("dbIndex", i), pk);
        }
    }

    /**
     * Query by Entity without Primary key
     * 
     * @throws SQLException
     */
    private void testQueryByPkWithEntityNoId(int shardId, DalHints oldHints, ClientTestModel pk) throws SQLException {
        DalHints hints;
        ClientTestModel model = null;
        // By fields
        for (int i = 0; i < tableMod; i++) {
            pk = new ClientTestModel();
            pk.setTableIndex(i);
            hints = copy(oldHints);
            if (i % 2 == 0)
                model = dao.queryByPk(pk, hints);
            else
                model = dao.queryByPk(pk, hints);
            model = assertModel(model, hints);
            Assert.assertNull(model);
        }
    }

    /**
     * Query against sample entity
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryLike() throws SQLException {
        testQueryLike(new DalHints());
        testQueryLike(asyncHints());
        testQueryLike(callbackHints());
    }

    private void testQueryLike(DalHints hints) throws SQLException {
        for (int i = 0; i < mod; i++) {
            // By shard
            testQueryLike(i, copy(hints).inShard(i));

            // By shardValue
            testQueryLike(i, copy(hints).setShardValue(i));

            // By shardColValue
            testQueryLike(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testQueryLike(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    /**
     * Query against sample entity
     * 
     * @throws SQLException
     */
    private void testQueryLike(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;
        List<ClientTestModel> models = null;

        ClientTestModel pk = null;

        for (int i = 0; i < tableMod; i++) {
            pk = new ClientTestModel();
            pk.setType((short) 1);

            // By tabelShard
            hints = copy(oldhints);
            models = dao.queryLike(pk, hints.inTableShard(i));
            models = assertModels(models, hints);
            assertQueryLike(shardId, models, i);

            // By tableShardValue
            hints = copy(oldhints);
            models = dao.queryLike(pk, hints.setTableShardValue(i));
            models = assertModels(models, hints);
            assertQueryLike(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryLike(pk, hints.setShardColValue("table", i));
            models = assertModels(models, hints);
            assertQueryLike(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryLike(pk, hints.setShardColValue("tableIndex", i));
            models = assertModels(models, hints);
            assertQueryLike(shardId, models, i);

            // By fields
            hints = copy(oldhints);
            pk.setDbIndex(shardId);
            pk.setTableIndex(i);
            models = dao.queryLike(pk, hints);
            models = assertModels(models, hints);
            assertQueryLike(shardId, models, i);
        }
    }

    private void assertQueryLike(int shardId, List<ClientTestModel> models, int i) {
        assertQueryX(shardId, models, i);
        Assert.assertEquals(i, models.get(0).getTableIndex().intValue());
    }

    /**
     * Query by Entity with where clause
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryWithWhereClause() throws SQLException {
        testQueryWithWhereClause(new DalHints());
        testQueryWithWhereClause(asyncHints());
        testQueryWithWhereClause(callbackHints());
    }

    private void testQueryWithWhereClause(DalHints hints) throws SQLException {
        List<ClientTestModel> models = null;

        for (int i = 0; i < mod; i++) {
            // By shard
            testQueryWithWhereClause(i, copy(hints).inShard(i));

            // By shardValue
            testQueryWithWhereClause(i, copy(hints).setShardValue(i));

            // By shardColValue
            testQueryWithWhereClause(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testQueryWithWhereClause(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    /**
     * Query by Entity with where clause
     * 
     * @throws SQLException
     */
    private void testQueryWithWhereClause(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;
        List<ClientTestModel> models = null;

        for (int i = 0; i < tableMod; i++) {
            String whereClause = "type=? and id=?";
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);
            parameters.set(2, Types.INTEGER, 1);

            // By tabelShard
            hints = copy(oldhints);
            models = dao.query(whereClause, parameters, hints.inTableShard(i));
            models = assertModels(models, hints);
            assertQueryWithWhereClause(shardId, models, i);

            // By tableShardValue
            hints = copy(oldhints);
            models = dao.query(whereClause, parameters, hints.setTableShardValue(i));
            models = assertModels(models, hints);
            assertQueryWithWhereClause(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.query(whereClause, parameters, hints.setShardColValue("table", i));
            models = assertModels(models, hints);
            assertQueryWithWhereClause(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.query(whereClause, parameters, hints.setShardColValue("tableIndex", i));
            models = assertModels(models, hints);
            assertQueryWithWhereClause(shardId, models, i);

            // By parameters
            whereClause += " and tableIndex=? and dbIndex=?";
            parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "id", Types.SMALLINT, i + 1);
            parameters.set(3, "tableIndex", Types.SMALLINT, i);
            parameters.set(4, "dbIndex", Types.SMALLINT, shardId);

            hints = copy(oldhints);
            models = dao.query(whereClause, parameters, hints);
            models = assertModels(models, hints);
            assertQueryWithWhereClause(shardId, models, i);
        }
    }

    private void assertQueryWithWhereClause(int shardId, List<ClientTestModel> models, int i) {
        Assert.assertEquals(1, models.size());
        Assert.assertEquals("SH INFO", models.get(0).getAddress());
        Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));
        Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
    }

    /**
     * Test Query the first row with where clause
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFirstWithWhereClause() throws SQLException {
        testQueryFirstWithWhereClause(new DalHints());
        testQueryFirstWithWhereClause(asyncHints());
        testQueryFirstWithWhereClause(callbackHints());
    }

    private void testQueryFirstWithWhereClause(DalHints hints) throws SQLException {
        for (int i = 0; i < mod; i++) {
            testQueryFirstWithWhereClause(i, copy(hints).inShard(i));

            // By shardValue
            testQueryFirstWithWhereClause(i, copy(hints).setShardValue(i));

            // By shardColValue
            testQueryFirstWithWhereClause(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testQueryFirstWithWhereClause(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    /**
     * Test Query the first row with where clause
     * 
     * @throws SQLException
     */
    private void testQueryFirstWithWhereClause(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;
        ClientTestModel model = null;
        for (int i = 0; i < tableMod; i++) {
            String whereClause = "type=?";

            // By tabelShard
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            hints = copy(oldhints);
            model = dao.queryFirst(whereClause, parameters, hints.inTableShard(i));
            model = assertModel(model, hints);
            assertQueryFirstWithWhereClause(shardId, model, i);

            // By tableShardValue
            hints = copy(oldhints);
            model = dao.queryFirst(whereClause, parameters, hints.setTableShardValue(i));
            model = assertModel(model, hints);
            assertQueryFirstWithWhereClause(shardId, model, i);

            // By shardColValue
            hints = copy(oldhints);
            model = dao.queryFirst(whereClause, parameters, hints.setShardColValue("table", i));
            model = assertModel(model, hints);
            assertQueryFirstWithWhereClause(shardId, model, i);

            // By shardColValue
            hints = copy(oldhints);
            model = dao.queryFirst(whereClause, parameters, hints.setShardColValue("tableIndex", i));
            model = assertModel(model, hints);
            assertQueryFirstWithWhereClause(shardId, model, i);

            // By parameters
            hints = copy(oldhints);
            whereClause += " and tableIndex=? and dbIndex=?";
            parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "tableIndex", Types.SMALLINT, i);
            parameters.set(3, "dbIndex", Types.SMALLINT, shardId);
            model = dao.queryFirst(whereClause, parameters, hints);
            model = assertModel(model, hints);
            assertQueryFirstWithWhereClause(shardId, model, i);
        }
    }

    private void assertQueryFirstWithWhereClause(int shardId, ClientTestModel model, int i) {
        Assert.assertEquals(1, model.getId().intValue());
        Assert.assertEquals(i, model.getTableIndex().intValue());
        Assert.assertEquals(shardId, model.getDbIndex().intValue());
    }

    /**
     * Test Query the first row with where clause failed
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFirstWithWhereClauseFailed() throws SQLException {
        testQueryFirstWithWhereClauseFailed(new DalHints());
        testQueryFirstWithWhereClauseFailed(asyncHints());
        testQueryFirstWithWhereClauseFailed(callbackHints());
    }

    private void testQueryFirstWithWhereClauseFailed(DalHints hints) throws SQLException {
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 10);
        try {
            hints = copy(hints);
            ClientTestModel model = dao.queryFirst(whereClause, parameters, hints.inTableShard(1).inShard(0));
            assertModel(model, hints);
            Assert.fail();
        } catch (Throwable e) {
        }
    }

    /**
     * Test Query the top rows with where clause
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryTopWithWhereClause() throws SQLException {
        testQueryTopWithWhereClause(new DalHints());
        testQueryTopWithWhereClause(asyncHints());
        testQueryTopWithWhereClause(callbackHints());
    }

    private void testQueryTopWithWhereClause(DalHints hints) throws SQLException {
        for (int i = 0; i < mod; i++) {
            // By shard
            testQueryTopWithWhereClause(i, copy(hints).inShard(i));

            // By shardValue
            testQueryTopWithWhereClause(i, copy(hints).setShardValue(i));

            // By shardColValue
            testQueryTopWithWhereClause(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testQueryTopWithWhereClause(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    /**
     * Test Query the top rows with where clause
     * 
     * @throws SQLException
     */
    private void testQueryTopWithWhereClause(int shardId, DalHints oldhints) throws SQLException {
        List<ClientTestModel> models = null;
        DalHints hints;

        for (int i = 0; i < tableMod; i++) {
            String whereClause = "type=?";
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            // By tabelShard
            hints = copy(oldhints);
            models = dao.queryTop(whereClause, parameters, hints.inTableShard(i), i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);

            // By tableShardValue
            hints = copy(oldhints);
            models = dao.queryTop(whereClause, parameters, hints.setTableShardValue(i), i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryTop(whereClause, parameters, hints.setShardColValue("table", i), i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryTop(whereClause, parameters, hints.setShardColValue("tableIndex", i), i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);

            whereClause += " and tableIndex=? and dbIndex=?";
            // By parameters
            parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "tableIndex", Types.SMALLINT, i);
            parameters.set(3, "dbIndex", Types.SMALLINT, shardId);
            hints = copy(oldhints);
            models = dao.queryTop(whereClause, parameters, hints, i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);
        }
    }

    private void assertQueryX(int shardId, List<ClientTestModel> models, int i) {
        Assert.assertEquals(i + 1, models.size());
        Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
    }

    /**
     * Test Query the top rows with where clause failed
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryTopWithWhereClauseFailed() throws SQLException {
        testQueryTopWithWhereClauseFailed(new DalHints());
        testQueryTopWithWhereClauseFailed(asyncHints());
        testQueryTopWithWhereClauseFailed(callbackHints());
    }

    private void testQueryTopWithWhereClauseFailed(DalHints hints) throws SQLException {
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 10);

        List<ClientTestModel> models;
        try {
            hints = copy(hints);
            models = dao.queryTop(whereClause, parameters, hints, 2);
            models = assertModels(models, hints);

            Assert.fail();
        } catch (Exception e) {
        }

        models = dao.queryTop(whereClause, parameters, new DalHints().inTableShard(1).inShard(0), 2);
        Assert.assertTrue(null != models);
        Assert.assertEquals(0, models.size());
    }

    /**
     * Test Query range of result with where clause
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFromWithWhereClause() throws SQLException {
        testQueryFromWithWhereClause(new DalHints());
        testQueryFromWithWhereClause(asyncHints());
        testQueryFromWithWhereClause(callbackHints());
    }

    private void testQueryFromWithWhereClause(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testQueryFromWithWhereClause(i, copy(hints).inShard(i));

            // By shardValue
            testQueryFromWithWhereClause(i, copy(hints).setShardValue(i));

            // By shardColValue
            testQueryFromWithWhereClause(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testQueryFromWithWhereClause(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    /**
     * Test Query range of result with where clause
     * 
     * @throws SQLException
     */
    private void testQueryFromWithWhereClause(int shardId, DalHints oldhints) throws SQLException {
        List<ClientTestModel> models = null;
        String whereClause = "type=? order by id";
        DalHints hints;

        for (int i = 0; i < tableMod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            // By tabelShard
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.inTableShard(i), 0, i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);

            // By tableShardValue
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.setTableShardValue(i), 0, i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("table", i), 0, i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("tableIndex", i), 0, i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);
        }

        whereClause = "type=?  and tableIndex=? and dbIndex=? order by id";
        // By parameters
        for (int i = 0; i < tableMod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "tableIndex", Types.SMALLINT, i);
            parameters.set(3, "dbIndex", Types.SMALLINT, shardId);

            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints, 0, i + 1);
            models = assertModels(models, hints);
            assertQueryX(shardId, models, i);
        }
    }

    @Test
    public void testQueryFromWithWhereClauseImplicitAllShards() throws SQLException {
        //		set implicitAllShards on
        DalPropertiesManager.getInstance().tearDown();
        DalPropertiesManager.getInstance().setDalPropertiesProvider(new LocalDalPropertiesProvider());
        DalPropertiesManager.getInstance().setup();

        List<ClientTestModel> models = null;
        String whereClause = "dbIndex=? order by id";

        // By parameters
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, "dbIndex", Types.SMALLINT, 0);

        try {
            models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, 10);
            Assert.assertEquals(10, models.size());
        } catch (Exception e) {
            Assert.fail();
        }

        whereClause = "tableIndex=? order by id";
        parameters = new StatementParameters();
        parameters.set(1, "tableIndex", Types.SMALLINT, 0);

        try {
            models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, 10);
            Assert.assertEquals(2, models.size());
        } catch (Exception e) {
            Assert.fail();
        }

        //		set implicitAllShards off
        DalPropertiesManager.getInstance().tearDown();
        DalPropertiesManager.getInstance().setDalPropertiesProvider(DalElementFactory.DEFAULT.getDalPropertiesProvider());
        DalPropertiesManager.getInstance().setup();
    }

    @Test
    public void testInvalidShardid() throws Exception{

        //		set implicitAllShards on
        DalPropertiesManager.getInstance().tearDown();
        DalPropertiesManager.getInstance().setDalPropertiesProvider(new LocalDalPropertiesProvider());
        DalPropertiesManager.getInstance().setup();

        List<ClientTestModel> models = null;
        String whereClause = "type=? order by id";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 1);
        try {
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue("abc").setTableShardValue(1), 0, 10);
        }catch (Exception e){
            assertTrue(e.getMessage().contains("For input string: \"abc\""));
        }

        try {
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue(0).setTableShardValue("abc"), 0, 10);
        }catch (Exception e){
            assertTrue(e.getMessage().contains("For input string: \"abc\""));
        }

        //		set implicitAllShards off
        DalPropertiesManager.getInstance().tearDown();
        DalPropertiesManager.getInstance().setDalPropertiesProvider(DalElementFactory.DEFAULT.getDalPropertiesProvider());
        DalPropertiesManager.getInstance().setup();

        try {
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue("abc").setTableShardValue(1), 0, 10);
        }catch (Exception e){
            assertTrue(e.getMessage().contains("For input string: \"abc\""));
        }

        try {
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue(0).setTableShardValue("abc"), 0, 10);
        }catch (Exception e){
            e.printStackTrace();
            assertTrue(e.getCause().getMessage().contains("For input string: \"abc\""));
        }
    }

    /**
     * Test Query range of result with where clause failed when return not enough recodes
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFromWithWhereClauseFailed() throws SQLException {
        testQueryFromWithWhereClauseFailed(new DalHints());
        testQueryFromWithWhereClauseFailed(asyncHints());
        testQueryFromWithWhereClauseFailed(callbackHints());
    }

    private void testQueryFromWithWhereClauseFailed(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testQueryFromWithWhereClauseFailed(i, copy(hints).inShard(i));

            // By shardValue
            testQueryFromWithWhereClauseFailed(i, copy(hints).setShardValue(i));

            // By shardColValue
            testQueryFromWithWhereClauseFailed(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testQueryFromWithWhereClauseFailed(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    /**
     * Test Query range of result with where clause failed when return not enough recodes
     * 
     * @throws SQLException
     */
    private void testQueryFromWithWhereClauseFailed(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;

        String whereClause = "type=? order by id";
        List<ClientTestModel> models = null;
        for (int i = 0; i < tableMod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            // By tabelShard
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.inTableShard(i), 0, 10);
            models = assertModels(models, hints);
            Assert.assertTrue(null != models);
            assertQueryX(shardId, models, i);

            // By tableShardValue
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.setTableShardValue(i), 0, 10);
            models = assertModels(models, hints);
            Assert.assertTrue(null != models);
            assertQueryX(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("table", i), 0, 10);
            models = assertModels(models, hints);
            Assert.assertTrue(null != models);
            assertQueryX(shardId, models, i);

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("tableIndex", i), 0, 10);
            models = assertModels(models, hints);
            Assert.assertTrue(null != models);
            assertQueryX(shardId, models, i);
        }
    }

    /**
     * Test Query range of result with where clause when return empty collection
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFromWithWhereClauseEmpty() throws SQLException {
        testQueryFromWithWhereClauseEmpty(new DalHints());
        testQueryFromWithWhereClauseEmpty(asyncHints());
        testQueryFromWithWhereClauseEmpty(callbackHints());
    }

    private void testQueryFromWithWhereClauseEmpty(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testQueryFromWithWhereClauseEmpty(i, copy(hints).inShard(i));

            // By shardValue
            testQueryFromWithWhereClauseEmpty(i, copy(hints).setShardValue(i));

            // By shardColValue
            testQueryFromWithWhereClauseEmpty(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testQueryFromWithWhereClauseEmpty(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    /**
     * Test Query range of result with where clause when return empty collection
     * 
     * @throws SQLException
     */
    private void testQueryFromWithWhereClauseEmpty(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;
        String whereClause = "type=? order by id";
        List<ClientTestModel> models = null;
        for (int i = 0; i < tableMod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 10);

            // By tabelShard
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.inTableShard(i), 0, 10);
            models = assertModels(models, hints);
            Assert.assertTrue(null != models);
            Assert.assertEquals(0, models.size());

            // By tableShardValue
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.setTableShardValue(i), 0, 10);
            models = assertModels(models, hints);
            Assert.assertTrue(null != models);
            Assert.assertEquals(0, models.size());

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("table", i), 0, 10);
            models = assertModels(models, hints);
            Assert.assertTrue(null != models);
            Assert.assertEquals(0, models.size());

            // By shardColValue
            hints = copy(oldhints);
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("tableIndex", i), 0, 10);
            models = assertModels(models, hints);
            Assert.assertTrue(null != models);
            Assert.assertEquals(0, models.size());
        }
    }

    /**
     * Test Insert multiple entities one by one
     * 
     * @throws SQLException
     */
    @Test
    public void testInsertSingleFail() throws SQLException {
        testInsertSingleFail(new DalHints());
        testInsertSingleFail(asyncHints());
        testInsertSingleFail(intHints());
    }

    private void testInsertSingleFail(DalHints hints) throws SQLException {
        reset();
        ClientTestModel model = new ClientTestModel();
        model.setQuantity(10 + 1 % 3);
        model.setType(((Number) (1 % 3)).shortValue());
        model.setAddress("CTRIP");
        int res;
        try {
            hints = copy(hints);
            res = dao.insert(hints, model);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testInsertSingle() throws SQLException {
        // testInsertSingle(new DalHints());
        testInsertSingle(asyncHints());
        // testInsertSingle(intHints());
    }

    private void testInsertSingle(DalHints hints) throws SQLException {
        testInsertSingleByShard(hints);
        testInsertSingleByShardValue(hints);
        testInsertSingleByShardCol(hints);
        testInsertSingleByShardCol2(hints);
    }

    private void testInsertSingleByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testInsertSingle(i, copy(hints).inShard(i));
        }
    }

    private void testInsertSingleByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testInsertSingle(i, copy(hints).setShardValue(i));
        }
    }

    private void testInsertSingleByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testInsertSingle(i, copy(hints).setShardColValue("index", i));
        }
    }

    private void testInsertSingleByShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testInsertSingle(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    @Test
    public void testInsertSingleByFields() throws SQLException {
        testInsertSingleByFields(new DalHints());
        testInsertSingleByFields(asyncHints());
        testInsertSingleByFields(intHints());
    }

    private void testInsertSingleByFields(DalHints hints) throws SQLException {
        reset();
        int res;
        deleteAllShardsByDbTable(dao, mod, tableMod);
        ClientTestModel model = new ClientTestModel();
        model.setQuantity(10 + 1 % 3);
        model.setType(((Number) (1 % 3)).shortValue());
        model.setAddress("CTRIP");
        model.setTableIndex(0);
        model.setDbIndex(3);

        hints = copy(hints);
        res = dao.insert(hints, model);
        res = assertInt(res, hints);
        assertResEquals(1, res);
        Assert.assertEquals(1, getCount(1, 0));
    }

    /**
     * Test Insert multiple entities one by one
     * 
     * @throws SQLException
     */
    private void testInsertSingle(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;

        ClientTestModel model = new ClientTestModel();
        model.setQuantity(10 + 1 % 3);
        model.setType(((Number) (1 % 3)).shortValue());
        model.setAddress("CTRIP");
        int res;
        try {
            hints = copy(oldhints);
            res = dao.insert(hints, model);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < tableMod; i++) {
            int j = 1;
            // By tabelShard
            hints = copy(oldhints);
            res = dao.insert(hints.inTableShard(i), model);
            res = assertInt(res, hints);
            assertResEquals(1, res);
            Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));

            // By tableShardValue
            hints = copy(oldhints);
            res = dao.insert(hints.setTableShardValue(i), model);
            res = assertInt(res, hints);
            assertResEquals(1, res);
            Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));

            // By shardColValue
            hints = copy(oldhints);
            res = dao.insert(hints.setShardColValue("table", i), model);
            res = assertInt(res, hints);
            assertResEquals(1, res);
            Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));

            // By shardColValue
            hints = copy(oldhints);
            res = dao.insert(hints.setShardColValue("tableIndex", i), model);
            res = assertInt(res, hints);
            assertResEquals(1, res);
            Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));

            // By fields
            hints = copy(oldhints);
            model.setTableIndex(i);
            res = dao.insert(hints, model);
            res = assertInt(res, hints);
            assertResEquals(1, res);
            Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));
        }
    }

    /**
     * Test Insert multiple entities one by one
     * 
     * @throws SQLException
     */
    @Test
    public void testInsertMultipleAsListFail() throws SQLException {
        testInsertMultipleAsListFail(new DalHints());
        testInsertMultipleAsListFail(asyncHints());
        testInsertMultipleAsListFail(intHints());
    }

    private void testInsertMultipleAsListFail(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = createListNoId(3);

        int[] res;
        try {
            hints = copy(hints);
            res = dao.insert(hints, entities);
            res = assertIntArray(res, hints);

            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testInsertMultipleAsList() throws SQLException {
        testInsertMultipleAsList(new DalHints());
        // testInsertMultipleAsList(asyncHints());
        // testInsertMultipleAsList(intHints());
    }

    private void testInsertMultipleAsList(DalHints hints) throws SQLException {
        testInsertMultipleAsListByShard(hints);
        testInsertMultipleAsListByShardValue(hints);
        testInsertMultipleAsListByShardCol(hints);
        testInsertMultipleAsListByShardCol2(hints);
    }

    private void testInsertMultipleAsListByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testInsertMultipleAsList(i, copy(hints).inShard(i));
        }
    }

    private void testInsertMultipleAsListByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testInsertMultipleAsList(i, copy(hints).setShardValue(i));
        }
    }

    private void testInsertMultipleAsListByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testInsertMultipleAsList(i, copy(hints).setShardColValue("index", i));
        }
    }

    private void testInsertMultipleAsListByShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testInsertMultipleAsList(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    @Test
    public void testInsertMultipleAsListByField() throws SQLException {
        testInsertMultipleAsListByField(new DalHints());
        testInsertMultipleAsListByField(asyncHints());
        testInsertMultipleAsListByField(intHints());
    }

    private void testInsertMultipleAsListByField(DalHints hints) throws SQLException {
        reset();
        int[] res;
        List<ClientTestModel> entities = createListNoId(3);
        deleteAllShardsByDbTable(dao, mod, tableMod);

        // By fields not same shard
        entities.get(0).setTableIndex(0);
        entities.get(0).setDbIndex(0);

        entities.get(1).setTableIndex(1);
        entities.get(1).setDbIndex(1);

        entities.get(2).setTableIndex(2);
        entities.get(2).setDbIndex(2);

        hints = copy(hints);
        res = dao.insert(hints.continueOnError(), entities);
        res = assertIntArray(res, hints);
        assertResEquals(3, res);
        Assert.assertEquals(1, getCountByDbTable(dao, 0, 0));
        Assert.assertEquals(1, getCountByDbTable(dao, 1, 1));
        Assert.assertEquals(1, getCountByDbTable(dao, 0, 2));
    }

    /**
     * Test Insert multiple entities one by one
     * 
     * @throws SQLException
     */
    private void testInsertMultipleAsList(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;
        List<ClientTestModel> entities = createListNoId(3);

        int[] res = null;
        try {
            hints = copy(oldhints);
            res = dao.insert(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < tableMod; i++) {
            int j = 1;
            // By tabelShard
            hints = copy(oldhints);
            res = dao.insert(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

            // By tableShardValue
            hints = copy(oldhints);
            res = dao.insert(hints.setTableShardValue(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

            // By shardColValue
            hints = copy(oldhints);
            res = dao.insert(hints.setShardColValue("table", i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

            // By shardColValue
            hints = copy(oldhints);
            res = dao.insert(hints.setShardColValue("tableIndex", i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

            // By fields same shard
            hints = copy(oldhints);
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            res = dao.insert(hints, entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
        }

        deleteAllShards(shardId);

        // By fields not same shard
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        hints = copy(oldhints);
        res = dao.insert(hints, entities);
        res = assertIntArray(res, hints);
        assertResEquals(3, res);
        Assert.assertEquals(1, getCount(shardId, 0));
        Assert.assertEquals(1, getCount(shardId, 1));
        Assert.assertEquals(1, getCount(shardId, 2));
    }

    /**
     * Test Test Insert multiple entities one by one with continueOnError hints
     * 
     * @throws SQLException
     */
    @Test
    public void testInsertMultipleAsListWithContinueOnErrorHintsFail() throws SQLException {
        testInsertMultipleAsListWithContinueOnErrorHintsFail(new DalHints());
        testInsertMultipleAsListWithContinueOnErrorHintsFail(asyncHints());
        testInsertMultipleAsListWithContinueOnErrorHintsFail(intHints());
    }

    private void testInsertMultipleAsListWithContinueOnErrorHintsFail(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = createListNoId(3);
        entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
                + "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP" + "CTRIPCTRIPCTRIPCTRIP");

        int[] res;
        try {
            hints = copy(hints);
            res = dao.insert(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    public void testMultipleHints(MultipleHintsTester tester) throws SQLException {
        List<List<Entry<DalHintEnum, Object>>> testHints = new ArrayList<>();

        for (Map<DalHintEnum, Object> hints : tester.getTestableHints()) {
            List<Entry<DalHintEnum, Object>> ll = new ArrayList<>();
            for (Entry<DalHintEnum, Object> e : hints.entrySet())
                ll.add(e);
            testHints.add(ll);
        }

        int[] curPos = new int[testHints.size()];

        // Append first one as null
        for (List<Entry<DalHintEnum, Object>> testHint : testHints)
            testHint.add(0, null);

        while (curPos[0] < testHints.get(0).size()) {
            DalHints hints = new DalHints();

            for (int i = 0; i < curPos.length; i++) {
                Entry<DalHintEnum, Object> testHint = testHints.get(i).get(curPos[i]);

                if (testHint != null)
                    hints.set(testHint.getKey(), testHint.getValue());
            }

            // test callback
            tester.test(hints);

            // refresh position
            curPos[curPos.length - 1]++;
            for (int j = curPos.length - 1; j > 0; j--) {
                if (curPos[j] == testHints.get(j).size()) {
                    curPos[j] = 0;
                    curPos[j - 1]++;
                }
            }
        }
    }

    private interface MultipleHintsTester {
        List<Map<DalHintEnum, Object>> getTestableHints();

        void test(DalHints hints) throws SQLException;
    }

    // TODO refine test
    public void testInsertMultiple() throws SQLException {
        testMultipleHints(new MultipleHintsTester() {
            @Override
            public List<Map<DalHintEnum, Object>> getTestableHints() {
                List<Map<DalHintEnum, Object>> allHints = new ArrayList<>();
                Map<DalHintEnum, Object> hints = new HashMap<>();
                // Make first normal case
                hints.put(DalHintEnum.userDefined1, null);
                hints.put(DalHintEnum.asyncExecution, null);
                hints.put(DalHintEnum.resultCallback, new IntCallback());

                allHints.add(hints);

                hints = new HashMap<>();
                for (int i = 0; i < mod; i++) {
                    hints.put(DalHintEnum.shard, i);
                    hints.put(DalHintEnum.shardValue, i);
                    Map<String, Object> shardColValues = new HashMap<String, Object>();
                    shardColValues.put("dbIndex", i);
                    hints.put(DalHintEnum.shardValue, shardColValues);
                }
                allHints.add(hints);

                return allHints;
            }

            @Override
            public void test(DalHints hints) throws SQLException {
                reset();
                hints.continueOnError();
            }
        });
    }

    @Test
    public void testInsertMultipleAsListWithContinueOnErrorHints() throws SQLException {
        testInsertMultipleAsListWithContinueOnErrorHints(new DalHints());
        // testInsertMultipleAsListWithContinueOnErrorHints(asyncHints());
        testInsertMultipleAsListWithContinueOnErrorHints(intHints());
    }

    public void testInsertMultipleAsListWithContinueOnErrorHints(DalHints hints) throws SQLException {
        testInsertMultipleAsListWithContinueOnErrorHintsByShard(hints);
        testInsertMultipleAsListWithContinueOnErrorHintsBYShardValue(hints);
        testInsertMultipleAsListWithContinueOnErrorHintsShardCol(hints);
        testInsertMultipleAsListWithContinueOnErrorHintsShardCol2(hints);
    }

    private void testInsertMultipleAsListWithContinueOnErrorHintsByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testInsertMultipleAsListWithContinueOnErrorHints(i, copy(hints).continueOnError().inShard(i));
        }
    }

    private void testInsertMultipleAsListWithContinueOnErrorHintsBYShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testInsertMultipleAsListWithContinueOnErrorHints(i, copy(hints).continueOnError().setShardValue(i));
        }
    }

    private void testInsertMultipleAsListWithContinueOnErrorHintsShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testInsertMultipleAsListWithContinueOnErrorHints(i,
                    copy(hints).continueOnError().setShardColValue("index", i));
        }
    }

    private void testInsertMultipleAsListWithContinueOnErrorHintsShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testInsertMultipleAsListWithContinueOnErrorHints(i,
                    copy(hints).continueOnError().setShardColValue("dbIndex", i));
        }
    }

    @Test
    public void testInsertMultipleAsListWithContinueOnErrorHintsByFields() throws SQLException {
        testInsertMultipleAsListWithContinueOnErrorHintsByFields(new DalHints());
        testInsertMultipleAsListWithContinueOnErrorHintsByFields(asyncHints());
        testInsertMultipleAsListWithContinueOnErrorHintsByFields(intHints());
    }

    private void testInsertMultipleAsListWithContinueOnErrorHintsByFields(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = createListNoId(3);
        entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
                + "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP" + "CTRIPCTRIPCTRIPCTRIP");

        int[] res;
        deleteAllShardsByDbTable(dao, mod, tableMod);

        // By fields not same shard
        entities.get(0).setTableIndex(0);
        entities.get(0).setDbIndex(0);

        entities.get(1).setTableIndex(1);
        entities.get(1).setDbIndex(1);

        entities.get(2).setTableIndex(2);
        entities.get(2).setDbIndex(2);

        hints = copy(hints);
        res = dao.insert(hints.continueOnError(), entities);
        res = assertIntArray(res, hints);

        assertResEquals(2, res);
        Assert.assertEquals(1, getCount(0, 0));
        Assert.assertEquals(0, getCount(1, 1));
        Assert.assertEquals(1, getCount(0, 2));
    }

    /**
     * Test Test Insert multiple entities one by one with continueOnError hints
     * 
     * @throws SQLException
     */
    public void testInsertMultipleAsListWithContinueOnErrorHints(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;

        List<ClientTestModel> entities = createListNoId(3);
        entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
                + "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP" + "CTRIPCTRIPCTRIPCTRIP");

        int[] res;
        try {
            hints = copy(oldhints);
            res = dao.insert(hints, entities);
            res = assertIntArray(res, hints);
        } catch (Exception e) {
            Assert.fail();
        }

        for (int i = 0; i < tableMod; i++) {
            int j = 1;
            // By tabelShard
            hints = copy(oldhints);
            res = dao.insert(hints.continueOnError().inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(2, res);
            Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

            // By tableShardValue
            hints = copy(oldhints);
            res = dao.insert(hints.continueOnError().setTableShardValue(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(2, res);
            Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

            // By shardColValue
            hints = copy(oldhints);
            res = dao.insert(hints.continueOnError().setShardColValue("table", i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(2, res);
            Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

            // By shardColValue
            hints = copy(oldhints);
            res = dao.insert(hints.continueOnError().setShardColValue("tableIndex", i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(2, res);
            Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

            // By fields same shard
            hints = copy(oldhints);
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            res = dao.insert(hints.continueOnError(), entities);
            res = assertIntArray(res, hints);
            assertResEquals(2, res);
            Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

            if (INSERT_PK_BACK_ALLOWED) {
                hints = copy(oldhints).setKeyHolder(new KeyHolder()).setIdentityBack();
                IdentitySetBackHelper.clearId(entities);
                entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
                        + "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP" + "CTRIPCTRIPCTRIPCTRIP");
                res = dao.insert(hints.continueOnError(), entities);
                res = assertIntArray(res, hints);
                assertResEquals(2, res);
                Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
                IdentitySetBackHelper.assertIdentityWithError(dao, copy(oldhints), entities);
            }
        }

        deleteAllShards(shardId);

        // By fields not same shard
        entities.get(0).setTableIndex(0);
        entities.get(0).setDbIndex(0);

        entities.get(1).setTableIndex(1);
        entities.get(1).setDbIndex(1);

        entities.get(2).setTableIndex(2);
        entities.get(2).setDbIndex(2);
        hints = copy(oldhints);
        res = dao.insert(hints.continueOnError(), entities);
        res = assertIntArray(res, hints);
        assertResEquals(2, res);
        Assert.assertEquals(1, getCount(shardId, 0));
        Assert.assertEquals(0, getCount(shardId, 1));
        Assert.assertEquals(1, getCount(shardId, 2));

        if (INSERT_PK_BACK_ALLOWED) {
            IdentitySetBackHelper.clearId(entities);
            entities.get(0).setTableIndex(0);
            entities.get(0).setDbIndex(0);

            entities.get(1).setTableIndex(1);
            entities.get(1).setDbIndex(1);
            entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
                    + "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP" + "CTRIPCTRIPCTRIPCTRIP");

            entities.get(2).setTableIndex(2);
            entities.get(2).setDbIndex(2);
            hints = copy(oldhints).setKeyHolder(new KeyHolder()).setIdentityBack();
            res = dao.insert(hints.continueOnError(), entities);
            res = assertIntArray(res, hints);
            assertResEquals(2, res);
            Assert.assertEquals(1 + 1, getCount(shardId, 0));
            Assert.assertEquals(0, getCount(shardId, 1));
            Assert.assertEquals(1 + 1, getCount(shardId, 2));
            IdentitySetBackHelper.assertIdentityWithError(dao, entities, shardId);
        }
    }

    @Test
    public void testKeyHolderWithSetIdentityBack() throws SQLException {
        if (!INSERT_PK_BACK_ALLOWED)
            return;

        int shardId = 0;
        deleteAllShards(shardId);
        DalHints hints = new DalHints().inShard(shardId);

        List<ClientTestModel> entities = createListNoId(3);
        entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
                + "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP" + "CTRIPCTRIPCTRIPCTRIP");

        int[] res;

        IdentitySetBackHelper.clearId(entities);
        entities.get(0).setTableIndex(0);
        entities.get(0).setDbIndex(0);

        entities.get(1).setTableIndex(1);
        entities.get(1).setDbIndex(1);
        entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
                + "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP" + "CTRIPCTRIPCTRIPCTRIP");

        entities.get(2).setTableIndex(2);
        entities.get(2).setDbIndex(2);
        hints = hints.setKeyHolder(new KeyHolder()).setIdentityBack();
        res = dao.insert(hints.continueOnError(), entities);
        res = assertIntArray(res, hints);
        assertResEquals(2, res);
        Assert.assertEquals(1, getCount(shardId, 0));
        Assert.assertEquals(0, getCount(shardId, 1));
        Assert.assertEquals(1, getCount(shardId, 2));
        IdentitySetBackHelper.assertIdentityWithError(dao, entities);
    }

    /**
     * Test Insert multiple entities with key-holder
     * 
     * @throws SQLException
     */
    @Test
    public void testInsertMultipleAsListWithKeyHolderFail() throws SQLException {
        testInsertMultipleAsListWithKeyHolderFail(new DalHints());
        testInsertMultipleAsListWithKeyHolderFail(asyncHints());
        testInsertMultipleAsListWithKeyHolderFail(intHints());
    }

    private void testInsertMultipleAsListWithKeyHolderFail(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = createListNoId(3);
        KeyHolder holder = createKeyHolder();
        int[] res;
        try {
            hints = copy(hints);
            res = dao.insert(hints, holder, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void testInsertMultipleAsListWithKeyHolderByShard() throws SQLException {
        testInsertMultipleAsListWithKeyHolder(new DalHints());
        testInsertMultipleAsListWithKeyHolder(asyncHints());
        testInsertMultipleAsListWithKeyHolder(intHints());
    }

    private void testInsertMultipleAsListWithKeyHolder(DalHints hints) throws SQLException {
        testInsertMultipleAsListWithKeyHolderByShard(hints);
        testInsertMultipleAsListWithKeyHolderByShardValue(hints);
        testInsertMultipleAsListWithKeyHolderByShardCol(hints);
        testInsertMultipleAsListWithKeyHolderByShardCol2(hints);
    }

    private void testInsertMultipleAsListWithKeyHolderByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testInsertMultipleAsListWithKeyHolder(i, copy(hints).inShard(i));
        }
    }

    private void testInsertMultipleAsListWithKeyHolderByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testInsertMultipleAsListWithKeyHolder(i, copy(hints).setShardValue(i));
        }
    }

    private void testInsertMultipleAsListWithKeyHolderByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testInsertMultipleAsListWithKeyHolder(i, copy(hints).setShardColValue("index", i));
        }
    }

    private void testInsertMultipleAsListWithKeyHolderByShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testInsertMultipleAsListWithKeyHolder(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    @Test
    public void testInsertMultipleAsListWithKeyHolderByFields() throws SQLException {
        testInsertMultipleAsListWithKeyHolderByFields(new DalHints());
        testInsertMultipleAsListWithKeyHolderByFields(asyncHints());
        testInsertMultipleAsListWithKeyHolderByFields(intHints());

        if (!INSERT_PK_BACK_ALLOWED)
            return;

        testInsertMultipleAsListWithKeyHolderByFieldsSetPkBack(new DalHints());
        testInsertMultipleAsListWithKeyHolderByFieldsSetPkBack(asyncHints());
        testInsertMultipleAsListWithKeyHolderByFieldsSetPkBack(intHints());
    }

    private void testInsertMultipleAsListWithKeyHolderByFields(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = createListNoId(3);
        int[] res;
        KeyHolder holder = createKeyHolder();

        deleteAllShardsByDbTable(dao, mod, tableMod);

        // By fields not same shard
        holder = createKeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(0).setDbIndex(0);

        entities.get(1).setTableIndex(1);
        entities.get(1).setDbIndex(1);

        entities.get(2).setTableIndex(2);
        entities.get(2).setDbIndex(2);

        hints = copy(hints);
        res = dao.insert(hints, holder, entities);
        res = assertIntArray(res, hints);
        assertResEquals(3, res);
        Assert.assertEquals(1, getCount(0, 0));
        Assert.assertEquals(1, getCount(1, 1));
        Assert.assertEquals(1, getCount(0, 2));
        assertKeyHolder(holder);
    }

    private void testInsertMultipleAsListWithKeyHolderByFieldsSetPkBack(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = createListNoId(3);
        int[] res;
        KeyHolder holder = createKeyHolder();

        deleteAllShardsByDbTable(dao, mod, tableMod);

        // By fields not same shard
        holder = createKeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(0).setDbIndex(0);

        entities.get(1).setTableIndex(1);
        entities.get(1).setDbIndex(1);

        entities.get(2).setTableIndex(2);
        entities.get(2).setDbIndex(2);

        hints = copy(hints);
        IdentitySetBackHelper.clearId(entities);
        res = dao.insert(hints.setIdentityBack(), holder, entities);
        res = assertIntArray(res, hints);
        assertResEquals(3, res);
        Assert.assertEquals(1, getCount(0, 0));
        Assert.assertEquals(1, getCount(1, 1));
        Assert.assertEquals(1, getCount(0, 2));
        assertKeyHolder(holder);
        IdentitySetBackHelper.assertIdentity(dao, hints, entities);
    }

    /**
     * Test Insert multiple entities with key-holder
     * 
     * @throws SQLException
     */
    private void testInsertMultipleAsListWithKeyHolder(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;
        List<ClientTestModel> entities = createListNoId(3);

        KeyHolder holder = new KeyHolder();
        int[] res;
        try {
            hints = copy(oldhints);
            res = dao.insert(hints, holder, entities);
            assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < tableMod; i++) {
            int j = 1;
            // By tabelShard
            holder = createKeyHolder();
            hints = copy(oldhints);
            res = dao.insert(hints.inTableShard(i), holder, entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
            assertKeyHolder(holder);

            if (INSERT_PK_BACK_ALLOWED) {
                // check ID set back
                holder = createKeyHolder();
                IdentitySetBackHelper.clearId(entities);
                hints = copy(oldhints);
                res = dao.insert(hints.inTableShard(i).setIdentityBack(), holder, entities);
                res = assertIntArray(res, hints);
                assertResEquals(3, res);
                Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
                IdentitySetBackHelper.assertIdentity(dao, copy(oldhints).inTableShard(i), entities);
            }

            // By tableShardValue
            holder = createKeyHolder();
            hints = copy(oldhints);
            res = dao.insert(hints.setTableShardValue(i), holder, entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
            assertKeyHolder(holder);

            // By shardColValue
            holder = createKeyHolder();
            hints = copy(oldhints);
            res = dao.insert(hints.setShardColValue("table", i), holder, entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
            assertKeyHolder(holder);

            // By shardColValue
            holder = createKeyHolder();
            hints = copy(oldhints);
            res = dao.insert(hints.setShardColValue("tableIndex", i), holder, entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
            assertKeyHolder(holder);

            // By fields same shard
            holder = createKeyHolder();
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            hints = copy(oldhints);
            res = dao.insert(hints, holder, entities);
            res = assertIntArray(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
            assertKeyHolder(holder);
        }

        deleteAllShards(shardId);

        // By fields not same shard
        holder = createKeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        hints = copy(oldhints);
        res = dao.insert(hints, holder, entities);
        res = assertIntArray(res, hints);
        Assert.assertEquals(1, getCount(shardId, 0));
        Assert.assertEquals(1, getCount(shardId, 1));
        Assert.assertEquals(1, getCount(shardId, 2));
        assertResEquals(3, res);
        assertKeyHolder(holder);
        deleteAllShards(shardId);

        // Check set ID back
        if (INSERT_PK_BACK_ALLOWED) {
            holder = createKeyHolder();
            entities.get(0).setTableIndex(0);
            entities.get(1).setTableIndex(1);
            entities.get(2).setTableIndex(2);
            IdentitySetBackHelper.clearId(entities);
            hints = copy(oldhints);
            res = dao.insert(hints.setIdentityBack(), holder, entities);
            res = assertIntArray(res, hints);
            Assert.assertEquals(1, getCount(shardId, 0));
            Assert.assertEquals(1, getCount(shardId, 1));
            Assert.assertEquals(1, getCount(shardId, 2));
            assertResEquals(3, res);
            assertKeyHolder(holder);
            IdentitySetBackHelper.assertIdentity(dao, copy(oldhints), entities);
        }
    }

    /**
     * Test Insert multiple entities with one SQL Statement
     * 
     * @throws SQLException
     */
    @Test
    public void testCombinedInsertFail() throws SQLException {
        if (!diff.supportInsertValues)
            return;

        testCombinedInsertFail(new DalHints());
        testCombinedInsertFail(asyncHints());
        testCombinedInsertFail(intHints());
    }

    private void testCombinedInsertFail(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = create(3);

        KeyHolder holder = createKeyHolder();
        int res;
        try {
            hints = copy(hints);
            res = dao.combinedInsert(hints, holder, entities);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void testCombinedInsertByShard() throws SQLException {
        if (!diff.supportInsertValues)
            return;

        // testCombinedInsert(new DalHints());
        // testCombinedInsert(asyncHints());
        testCombinedInsert(intHints());
    }

    private void testCombinedInsert(DalHints hints) throws SQLException {
        testCombinedInsertByShard(hints);
        testCombinedInsertByShardValue(hints);
        testCombinedInsertByShardCol(hints);
        testCombinedInsertByShardCol2(hints);
    }

    private void testCombinedInsertByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testCombinedInsert(i, copy(hints).inShard(i));
        }
    }

    private void testCombinedInsertByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testCombinedInsert(i, copy(hints).setShardValue(i));
        }
    }

    private void testCombinedInsertByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testCombinedInsert(i, copy(hints).setShardColValue("index", i));
        }
    }

    private void testCombinedInsertByShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testCombinedInsert(i, copy(hints).setShardColValue("dbIndex", i));
        }
        // For combined insert, the shard id must be defined or change bd deduced.
    }

    /**
     * Test Insert multiple entities with one SQL Statement
     * 
     * @throws SQLException
     */
    private void testCombinedInsert(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;
        List<ClientTestModel> entities = create(3);

        KeyHolder holder = createKeyHolder();
        int res;
        try {
            hints = copy(oldhints);
            res = dao.combinedInsert(hints, holder, entities);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {

        }

        for (int i = 0; i < tableMod; i++) {
            int j = 1;
            holder = null;
            // By tabelShard
            holder = createKeyHolder();
            hints = copy(oldhints);
            res = dao.combinedInsert(hints.inTableShard(i), holder, entities);
            res = assertInt(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
            assertKeyHolder(holder);

            // Check set ID back
            if (INSERT_PK_BACK_ALLOWED) {
                holder = createKeyHolder();
                hints = copy(oldhints);
                IdentitySetBackHelper.clearId(entities);
                res = dao.combinedInsert(hints.inTableShard(i).setIdentityBack(), holder, entities);
                res = assertInt(res, hints);
                assertResEquals(3, res);
                Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
                assertKeyHolder(holder);
                IdentitySetBackHelper.assertIdentity(dao, copy(oldhints).inTableShard(i), entities);
            }

            // By tableShardValue
            holder = createKeyHolder();
            hints = copy(oldhints);
            res = dao.combinedInsert(hints.setTableShardValue(i), holder, entities);
            res = assertInt(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
            assertKeyHolder(holder);

            // By shardColValue
            holder = createKeyHolder();
            hints = copy(oldhints);
            res = dao.combinedInsert(hints.setShardColValue("table", i), holder, entities);
            res = assertInt(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
            assertKeyHolder(holder);

            // By shardColValue
            holder = createKeyHolder();
            hints = copy(oldhints);
            res = dao.combinedInsert(hints.setShardColValue("tableIndex", i), holder, entities);
            res = assertInt(res, hints);
            assertResEquals(3, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
            assertKeyHolder(holder);
        }
        // For combined insert, the shard id must be defined or change bd deduced.
    }

    /**
     * Test Batch Insert multiple entities
     * 
     * @throws SQLException
     */
    @Test
    public void testBatchInsertFail() throws SQLException {
        testBatchInsertFail(new DalHints());
        testBatchInsertFail(asyncHints());
        testBatchInsertFail(intHints());
    }

    private void testBatchInsertFail(DalHints hints) throws SQLException {
        List<ClientTestModel> entities = create(3);

        int[] res;
        try {
            hints = copy(hints);
            res = dao.batchInsert(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testBatchInsert() throws SQLException {
        // testBatchInsert(new DalHints());
        testBatchInsert(asyncHints());
        // testBatchInsert(intHints());
    }

    private void testBatchInsert(DalHints hints) throws SQLException {
        testBatchInsertByShard(hints);
        testBatchInsertByShardValue(hints);
        testBatchInsertByShardCol(hints);
        testBatchInsertByShardCol2(hints);
    }

    private void testBatchInsertByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testBatchInsert(i, copy(hints).inShard(i));
        }
    }

    private void testBatchInsertByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testBatchInsert(i, copy(hints).setShardValue(i));
        }
    }

    private void testBatchInsertByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testBatchInsert(i, copy(hints).setShardColValue("index", i));
        }
    }

    private void testBatchInsertByShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testBatchInsert(i, copy(hints).setShardColValue("dbIndex", i));
        }
        // For batch insert, the shard id must be defined or change bd deduced.
    }

    /**
     * Test Batch Insert multiple entities
     * 
     * @throws SQLException
     */
    private void testBatchInsert(int shardId, DalHints oldhints) throws SQLException {
        DalHints hints;
        List<ClientTestModel> entities = create(3);

        int[] res;
        try {
            hints = copy(oldhints);
            res = dao.batchInsert(hints, entities);
            assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }

        // For unknow reason, it returns -2 for now. -2 means sucess no info for batch insert
        int[] exp = new int[] {-2, -2, -2};// {1,1,1}

        for (int i = 0; i < tableMod; i++) {
            int j = 1;
            // By tabelShard
            hints = copy(oldhints);
            res = dao.batchInsert(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(exp, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

            // By tableShardValue
            hints = copy(oldhints);
            res = dao.batchInsert(hints.setTableShardValue(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(exp, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

            // By shardColValue
            hints = copy(oldhints);
            res = dao.batchInsert(hints.setShardColValue("table", i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(exp, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

            // By shardColValue
            hints = copy(oldhints);
            res = dao.batchInsert(hints.setShardColValue("tableIndex", i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(exp, res);
            Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
        }

        // For combined insert, the shard id must be defined or change bd deduced.
    }

    /**
     * Test delete multiple entities
     * 
     * @throws SQLException
     */
    @Test
    public void testDeleteMultipleFail() throws SQLException {
        testDeleteMultipleFail(new DalHints());
        testDeleteMultipleFail(asyncHints());
        testDeleteMultipleFail(intHints());
    }

    private void testDeleteMultipleFail(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = create(3);

        int res[];
        try {
            hints = copy(hints);
            res = dao.delete(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testDeleteMultiple() throws SQLException {
        // testDeleteMultiple(new DalHints());
        // testDeleteMultiple(asyncHints());
        testDeleteMultiple(intHints());
    }

    private void testDeleteMultiple(DalHints hints) throws SQLException {
        testDeleteMultipleByShard(hints);
        testDeleteMultipleByShardValue(hints);
        testDeleteMultipleByShardCol(hints);
        testDeleteMultipleByShardCol2(hints);
    }

    private void testDeleteMultipleByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testDeleteMultiple(i, copy(hints).inShard(i));
        }
    }

    private void testDeleteMultipleByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testDeleteMultiple(i, copy(hints).setShardValue(i));
        }
    }

    private void testDeleteMultipleByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testDeleteMultiple(i, copy(hints).setShardColValue("index", i));
        }
    }

    private void testDeleteMultipleByShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testDeleteMultiple(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    @Test
    public void testDeleteMultipleByFields() throws SQLException {
        testDeleteMultipleByFields(new DalHints());
        testDeleteMultipleByFields(asyncHints());
        testDeleteMultipleByFields(intHints());
    }

    private void testDeleteMultipleByFields(DalHints hints) throws SQLException {
        reset();
        int[] res;

        // By fields not same shard
        List<ClientTestModel> entities = create(3);
        int i = 0;
        for (ClientTestModel model : entities) {
            model.setDbIndex(i % 2);
            model.setTableIndex(i++);
        }

        hints = copy(hints);
        res = dao.delete(hints, entities);
        res = assertIntArray(res, hints);
        assertResEquals(3, res);
        Assert.assertEquals(0, getCount(0, 0));
        Assert.assertEquals(1, getCount(1, 1));
        Assert.assertEquals(2, getCount(0, 2));
    }

    /**
     * Test delete multiple entities
     * 
     * @throws SQLException
     */
    public void testDeleteMultiple(int shardId, DalHints oldhints) throws SQLException {
        List<ClientTestModel> entities = create(3);
        DalHints hints;

        int[] res;
        try {
            hints = copy(oldhints);
            res = dao.delete(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {

        }

        for (int i = 0; i < tableMod; i++) {
            // By tabelShard
            hints = copy(oldhints);
            deleteTest(shardId, i, hints.inTableShard(i));

            // By tableShardValue
            hints = copy(oldhints);
            deleteTest(shardId, i, hints.setTableShardValue(i));

            // By shardColValue
            hints = copy(oldhints);
            deleteTest(shardId, i, hints.setShardColValue("table", i));

            // By shardColValue
            hints = copy(oldhints);
            deleteTest(shardId, i, hints.setShardColValue("tableIndex", i));

            // By fields same shard
            entities = getModels(shardId, i);
            for (ClientTestModel model : entities)
                model.setTableIndex(i);

            Assert.assertEquals(1 + i, getCount(shardId, i));
            hints = copy(oldhints);
            res = dao.delete(hints, entities);
            res = assertIntArray(res, hints);
            assertResEquals(1 + i, res);
            Assert.assertEquals(0, getCount(shardId, i));
        }

        // By fields not same shard
        reset();
        entities = create(3);
        int i = 0;
        for (ClientTestModel model : entities) {
            model.setTableIndex(i++);
            model.setDbIndex(shardId);
        }

        hints = copy(oldhints);
        res = dao.delete(hints, entities);
        res = assertIntArray(res, hints);
        assertResEquals(3, res);
        Assert.assertEquals(0, getCount(shardId, 0));
        Assert.assertEquals(1, getCount(shardId, 1));
        Assert.assertEquals(2, getCount(shardId, 2));
    }

    private void deleteTest(int shardId, int tableShardId, DalHints oldhints) throws SQLException {
        int count = 1 + tableShardId;
        Assert.assertEquals(count, getCount(shardId, tableShardId));
        DalHints hints = copy(oldhints);
        int[] res = dao.delete(hints, getModels(shardId, tableShardId));
        res = assertIntArray(res, hints);
        assertResEquals(1 + tableShardId, res);
        Assert.assertEquals(0, getCount(shardId, tableShardId));
        hints = copy(oldhints);
        res = dao.insert(hints, create(count));
        res = assertIntArray(res, hints);
    }

    /**
     * Test batch delete multiple entities
     * 
     * @throws SQLException
     */
    @Test
    public void testBatchDeleteFail() throws SQLException {
        testBatchDeleteFail(new DalHints());
        testBatchDeleteFail(asyncHints());
        testBatchDeleteFail(intHints());
    }

    private void testBatchDeleteFail(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = create(3);

        int[] res;
        try {
            hints = copy(hints);
            res = dao.batchDelete(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testBatchDeleteByShard() throws SQLException {
        testBatchDelete(new DalHints());
        // testBatchDelete(asyncHints());
        // testBatchDelete(intHints());
    }

    private void testBatchDelete(DalHints hints) throws SQLException {
        testBatchDeleteByShard(hints);
        testBatchDeleteByShardValue(hints);
        testBatchDeleteByShardCol(hints);
        testBatchDeleteByShardCol2(hints);
    }

    private void testBatchDeleteByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testBatchDelete(i, copy(hints).inShard(i));
        }
    }

    private void testBatchDeleteByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testBatchDelete(i, copy(hints).setShardValue(i));
        }
    }

    private void testBatchDeleteByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testBatchDelete(i, copy(hints).setShardColValue("index", i));
        }
    }

    private void testBatchDeleteByShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testBatchDelete(i, copy(hints).setShardColValue("dbIndex", i));
        }
        // Currently does not support not same shard
    }

    /**
     * Test batch delete multiple entities
     * 
     * @throws SQLException
     */
    private void testBatchDelete(int shardId, DalHints oldhints) throws SQLException {
        List<ClientTestModel> entities = create(3);
        DalHints hints;

        int[] res;
        try {
            hints = copy(oldhints);
            res = dao.batchDelete(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {

        }

        for (int i = 0; i < tableMod; i++) {
            // By tabelShard
            hints = copy(oldhints);
            batchDeleteTest(shardId, i, hints.inTableShard(i));

            // By tableShardValue
            hints = copy(oldhints);
            batchDeleteTest(shardId, i, hints.setTableShardValue(i));

            // By shardColValue
            hints = copy(oldhints);
            batchDeleteTest(shardId, i, hints.setShardColValue("table", i));

            // By shardColValue
            hints = copy(oldhints);
            batchDeleteTest(shardId, i, hints.setShardColValue("tableIndex", i));

            // By fields same shard
            entities = getModels(shardId, i);
            int[] exp = new int[i + 1];
            for (int k = 0; k < exp.length; k++)
                exp[k] = 1;

            for (ClientTestModel model : entities)
                model.setTableIndex(i);

            Assert.assertEquals(1 + i, getCount(shardId, i));
            hints = copy(oldhints);
            res = dao.batchDelete(hints, entities);
            res = assertIntArray(res, hints);
            assertResEquals(exp, res);
            Assert.assertEquals(0, getCount(shardId, i));
        }
        // Currently does not support not same shard
    }

    private void batchDeleteTest(int shardId, int tableShardId, DalHints oldhints) throws SQLException {
        int count = 1 + tableShardId;
        Assert.assertEquals(count, getCount(shardId, tableShardId));

        DalHints hints = copy(oldhints);
        int[] res = dao.batchDelete(hints, getModels(shardId, tableShardId));
        res = assertIntArray(res, hints);

        int[] exp = new int[count];
        for (int i = 0; i < count; i++)
            exp[i] = 1;
        assertResEquals(exp, res);
        Assert.assertEquals(0, getCount(shardId, tableShardId));

        hints = copy(oldhints);
        res = dao.insert(hints, create(count));
        res = assertIntArray(res, hints);
    }


    /**
     * Test update multiple entities with primary key
     * 
     * @throws SQLException
     */
    @Test
    public void testUpdateMultipleFail() throws SQLException {
        testUpdateMultipleFail(new DalHints());
        testUpdateMultipleFail(asyncHints());
        testUpdateMultipleFail(intHints());
    }

    private void testUpdateMultipleFail(DalHints hints) throws SQLException {
        reset();
        List<ClientTestModel> entities = create(3);

        int[] res;
        try {
            hints = copy(hints);
            res = dao.update(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testUpdateMultiple() throws SQLException {
        // testUpdateMultiple(new DalHints());
        testUpdateMultiple(asyncHints());
        testUpdateMultiple(intHints());
    }

    private void testUpdateMultiple(DalHints hints) throws SQLException {
        testUpdateMultipleByShard(hints);
        testUpdateMultipleByShardValue(hints);
        testUpdateMultipleByShardCol(hints);
    }

    private void testUpdateMultipleByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testUpdateMultiple(i, copy(hints).inShard(i));
        }
    }

    private void testUpdateMultipleByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testUpdateMultiple(i, copy(hints).setShardValue(i));
        }
    }

    private void testUpdateMultipleByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testUpdateMultiple(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testUpdateMultiple(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    @Test
    public void testUpdateMultipleByFields() throws SQLException {
        testUpdateMultipleByFields(new DalHints());
        testUpdateMultipleByFields(asyncHints());
        testUpdateMultipleByFields(intHints());
    }

    private void testUpdateMultipleByFields(DalHints oldhints) throws SQLException {
        // By fields not same shard
        List<ClientTestModel> entities = create(4);
        int[] res;
        int i = 0;
        for (ClientTestModel model : entities) {
            model.setTableIndex(i);
            model.setDbIndex(i++);
            model.setAddress("1234");
        }

        DalHints hints = copy(oldhints);
        res = dao.update(hints, entities);
        res = assertIntArray(res, hints);
        assertResEquals(4, res);
        for (ClientTestModel model : entities)
            Assert.assertEquals("1234", dao.queryByPk(model, new DalHints()).getAddress());
    }

    /**
     * Test update multiple entities with primary key
     * 
     * @throws SQLException
     */
    private void testUpdateMultiple(int shardId, DalHints oldhints) throws SQLException {
        List<ClientTestModel> entities = create(4);
        DalHints hints;

        int[] res;
        try {
            hints = copy(oldhints);
            res = dao.update(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {

        }

        for (int i = 0; i < tableMod; i++) {
            // By tabelShard
            entities = create(i + 1);
            for (ClientTestModel model : entities)
                model.setAddress("test1");
            hints = copy(oldhints);
            res = dao.update(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(i + 1, res);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals("test1", model.getAddress());

            // By tableShardValue
            entities = create(i + 1);
            for (ClientTestModel model : entities)
                model.setQuantity(-11);
            hints = copy(oldhints);
            res = dao.update(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(i + 1, res);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals(-11, model.getQuantity().intValue());

            // By shardColValue
            entities = create(i + 1);
            for (ClientTestModel model : entities)
                model.setType((short) 3);
            assertResEquals(i + 1, res);
            hints = copy(oldhints);
            res = dao.update(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals((short) 3, model.getType().intValue());

            // By shardColValue
            entities = create(i + 1);
            for (ClientTestModel model : entities)
                model.setAddress("testa");
            hints = copy(oldhints);
            res = dao.update(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(i + 1, res);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals("testa", model.getAddress());

            // By fields same shard
            // holder = new KeyHolder();
            entities = create(i + 1);
            for (ClientTestModel model : entities) {
                model.setAddress("testx");
                model.setTableIndex(i);
                model.setDbIndex(shardId);
            }
            hints = copy(oldhints);
            res = dao.update(hints, entities);
            res = assertIntArray(res, hints);
            assertResEquals(i + 1, res);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals("testx", model.getAddress());
        }

        // By fields not same shard
        entities = create(4);
        int i = 0;
        for (ClientTestModel model : entities) {
            model.setAddress("testy");
            model.setTableIndex(i++);
            model.setDbIndex(shardId);
        }
        hints = copy(oldhints);
        res = dao.update(hints, entities);
        res = assertIntArray(res, hints);
        assertResEquals(4, res);
        for (ClientTestModel model : entities) {
            hints = copy(oldhints);
            hints.callbackWith(new TestQueryResultCallback());
            model = dao.queryByPk(model, hints);
            model = assertModel(model, hints);
            Assert.assertEquals("testy", model.getAddress());
        }
    }

    @Test
    public void testBatchUpdate() throws SQLException {
        // testBatchUpdate(new DalHints());
        testBatchUpdate(asyncHints());
        // testBatchUpdate(intHints());
    }

    private void testBatchUpdate(DalHints hints) throws SQLException {
        testBatchUpdateByShard(hints);
        testBatchUpdateByShardValue(hints);
        testBatchUpdateByShardCol(hints);
    }

    private void testBatchUpdateByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testBatchUpdate(i, copy(hints).inShard(i));
        }
    }

    private void testBatchUpdateByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testBatchUpdate(i, copy(hints).setShardValue(i));
        }
    }

    private void testBatchUpdateByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testUpdateMultiple(i, copy(hints).setShardColValue("index", i));

            // By shardColValue
            testBatchUpdate(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    @Test
    public void testBatchUpdateByFields() throws SQLException {
        testBatchUpdateByFields(new DalHints());
        testBatchUpdateByFields(asyncHints());
        testBatchUpdateByFields(intHints());
    }

    private void testBatchUpdateByFields(DalHints oldhints) throws SQLException {
        reset();
        // By fields not same shard
        List<ClientTestModel> entities = create(4);
        int[] res;
        int i = 0;
        for (ClientTestModel model : entities) {
            model.setTableIndex(i);
            model.setDbIndex(i++);
            model.setAddress("1234");
        }

        try {
            DalHints hints = copy(oldhints);
            res = dao.batchUpdate(hints, entities);
            res = assertIntArray(res, hints);
            assertResEquals(4, res);
            for (ClientTestModel model : entities)
                Assert.assertEquals("1234", dao.queryByPk(model, new DalHints()).getAddress());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Test update multiple entities with primary key
     * 
     * @throws SQLException
     */
    private void testBatchUpdate(int shardId, DalHints oldhints) throws SQLException {
        List<ClientTestModel> entities = create(4);
        DalHints hints;

        int[] res;
        try {
            hints = copy(oldhints);
            res = dao.batchUpdate(hints, entities);
            res = assertIntArray(res, hints);
            Assert.fail();
        } catch (Exception e) {

        }

        for (int i = 0; i < tableMod; i++) {
            // By tabelShard
            entities = create(i + 1);
            for (ClientTestModel model : entities)
                model.setAddress("test1");
            hints = copy(oldhints);
            res = dao.batchUpdate(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(i + 1, res);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals("test1", model.getAddress());

            // By tableShardValue
            entities = create(i + 1);
            for (ClientTestModel model : entities)
                model.setQuantity(-11);
            hints = copy(oldhints);
            res = dao.batchUpdate(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(i + 1, res);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals(-11, model.getQuantity().intValue());

            // By shardColValue
            entities = create(i + 1);
            for (ClientTestModel model : entities)
                model.setType((short) 3);
            assertResEquals(i + 1, res);
            hints = copy(oldhints);
            res = dao.batchUpdate(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals((short) 3, model.getType().intValue());

            // By shardColValue
            entities = create(i + 1);
            for (ClientTestModel model : entities)
                model.setAddress("testa");
            hints = copy(oldhints);
            res = dao.batchUpdate(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertResEquals(i + 1, res);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals("testa", model.getAddress());

            // By fields same shard
            // holder = new KeyHolder();
            entities = create(i + 1);
            for (ClientTestModel model : entities) {
                model.setAddress("testx");
                model.setTableIndex(i);
                model.setDbIndex(shardId);
            }
            hints = copy(oldhints);
            res = dao.batchUpdate(hints, entities);
            res = assertIntArray(res, hints);
            assertResEquals(i + 1, res);
            for (ClientTestModel model : getModels(shardId, i))
                Assert.assertEquals("testx", model.getAddress());
        }

        // By fields not same shard
        entities = create(4);
        int i = 0;
        for (ClientTestModel model : entities) {
            model.setAddress("testy");
            model.setTableIndex(i++);
            model.setDbIndex(shardId);
        }
        hints = copy(oldhints);
        res = dao.batchUpdate(hints, entities);
        res = assertIntArray(res, hints);
        assertResEquals(4, res);
        for (ClientTestModel model : entities) {
            hints = copy(oldhints).callbackWith(new TestQueryResultCallback());
            model = dao.queryByPk(model, hints);
            model = assertModel(model, hints);
            Assert.assertEquals("testy", model.getAddress());
        }
    }

    /**
     * Test delete entities with where clause and parameters
     * 
     * @throws SQLException
     */
    @Test
    public void testDeleteWithWhereClauseFail() throws SQLException {
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 1);

        int res;
        try {
            DalHints hints = new DalHints();
            res = dao.delete(whereClause, parameters, hints);
            Assert.fail();
        } catch (Exception e) {
        }
        // Async
        try {
            DalHints hints = asyncHints();
            res = dao.delete(whereClause, parameters, hints);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }
        // Callback
        try {
            DalHints hints = intHints();
            res = dao.delete(whereClause, parameters, hints);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testDeleteWithWhereClause() throws SQLException {
        testDeleteWithWhereClause(new DalHints());
        testDeleteWithWhereClause(asyncHints());
        testDeleteWithWhereClause(intHints());
    }

    private void testDeleteWithWhereClause(DalHints hints) throws SQLException {
        testDeleteWithWhereClauseByShard(hints);
        testDeleteWithWhereClauseByShardValue(hints);
        testDeleteWithWhereClauseByShardCol(hints);
        testDeleteWithWhereClauseByShardCol2(hints);
    }

    private void testDeleteWithWhereClauseByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testDeleteWithWhereClause(i, copy(hints).inShard(i));
        }
    }

    private void testDeleteWithWhereClauseByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testDeleteWithWhereClause(i, copy(hints).setShardValue(i));
        }
    }

    private void testDeleteWithWhereClauseByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testDeleteWithWhereClause(i, copy(hints).setShardColValue("index", i));
        }
    }

    private void testDeleteWithWhereClauseByShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testDeleteWithWhereClause(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    /**
     * Test delete entities with where clause and parameters
     * 
     * @throws SQLException
     */
    private void testDeleteWithWhereClause(int shardId, DalHints oldhints) throws SQLException {
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 1);
        DalHints hints;

        int res;
        try {
            hints = copy(oldhints);
            res = dao.delete(whereClause, parameters, hints);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {

        }

        // By tabelShard
        Assert.assertEquals(1, getCount(shardId, 0));
        hints = copy(oldhints);
        res = dao.delete(whereClause, parameters, hints.inTableShard(0));
        res = assertInt(res, hints);
        assertResEquals(1, res);
        Assert.assertEquals(0, getSize(hints.inTableShard(0)));

        // By tableShardValue
        Assert.assertEquals(2, getCount(shardId, 1));
        hints = copy(oldhints);
        res = dao.delete(whereClause, parameters, hints.setTableShardValue(1));
        res = assertInt(res, hints);
        assertResEquals(2, res);
        Assert.assertEquals(0, getSize(hints.setTableShardValue(1)));

        // By shardColValue
        Assert.assertEquals(3, getCount(shardId, 2));
        hints = copy(oldhints);
        res = dao.delete(whereClause, parameters, hints.setShardColValue("table", 2));
        res = assertInt(res, hints);
        assertResEquals(3, res);
        Assert.assertEquals(0, getSize(hints.setShardColValue("table", 2)));

        // By shardColValue
        Assert.assertEquals(4, getCount(shardId, 3));
        hints = copy(oldhints);
        res = dao.delete(whereClause, parameters, hints.setShardColValue("tableIndex", 3));
        res = assertInt(res, hints);
        assertResEquals(4, res);
        Assert.assertEquals(0, getSize(hints.setShardColValue("tableIndex", 3)));
    }

    private int getSize(DalHints oldhints) throws SQLException {
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 1);

        DalHints hints = copy(oldhints).callbackWith(new TestQueryResultCallback());
        return assertModels(dao.query(whereClause, parameters, hints.inTableShard(0)), hints).size();
    }


    /**
     * Test plain update with SQL
     * 
     * @throws SQLException
     */
    @Test
    public void testUpdatePlainFail() throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET address = 'CTRIP' WHERE id = 1";
        StatementParameters parameters = new StatementParameters();
        int res;
        try {
            DalHints hints = new DalHints();
            res = dao.update(sql, parameters, hints);
            Assert.fail();
        } catch (Exception e) {
        }
        // Async
        try {
            DalHints hints = asyncHints();
            res = dao.update(sql, parameters, hints);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }
        // Callback
        try {
            DalHints hints = intHints();
            res = dao.update(sql, parameters, hints);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {
        }

    }

    @Test
    public void testUpdatePlain() throws SQLException {
        testUpdatePlain(new DalHints());
        testUpdatePlain(asyncHints());
        testUpdatePlain(intHints());
    }

    private void testUpdatePlain(DalHints hints) throws SQLException {
        testUpdatePlainByShard(hints);
        testUpdatePlainByShardValue(hints);
        testUpdatePlainByShardCol(hints);
        testUpdatePlainByShardCol2(hints);
    }

    private void testUpdatePlainByShard(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shard
            testUpdatePlain(i, copy(hints).inShard(i));
        }
    }

    private void testUpdatePlainByShardValue(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardValue
            testUpdatePlain(i, copy(hints).setShardValue(i));
        }
    }

    private void testUpdatePlainByShardCol(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testUpdatePlain(i, copy(hints).setShardColValue("index", i));
        }
    }

    private void testUpdatePlainByShardCol2(DalHints hints) throws SQLException {
        reset();
        for (int i = 0; i < mod; i++) {
            // By shardColValue
            testUpdatePlain(i, copy(hints).setShardColValue("dbIndex", i));
        }
    }

    /**
     * Test plain update with SQL
     * 
     * @throws SQLException
     */
    private void testUpdatePlain(int shardId, DalHints oldhints) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET address = 'CTRIP' WHERE id = 1";
        StatementParameters parameters = new StatementParameters();
        DalHints hints;

        int res;
        try {
            hints = copy(oldhints);
            res = dao.update(sql, parameters, hints);
            res = assertInt(res, hints);
            Assert.fail();
        } catch (Exception e) {

        }

        // By tabelShard
        UpdateSqlBuilder usb = new UpdateSqlBuilder(TABLE_NAME, dao.getDatabaseCategory());
        usb.update("address", "CTRIP", Types.VARCHAR);
        usb.equal("id", "1", Types.INTEGER);
        // sql = "UPDATE " + TABLE_NAME
        // + " SET address = 'CTRIP' WHERE id = 1";
        hints = copy(oldhints);
        res = dao.update(usb, hints.inTableShard(0));
        res = assertInt(res, hints);
        assertResEquals(1, res);
        Assert.assertEquals("CTRIP", queryByPk(hints.inTableShard(0)).getAddress());

        // By tableShardValue
        Assert.assertEquals(2, getCount(shardId, 1));
        hints = copy(oldhints);
        res = dao.update(usb, hints.setTableShardValue(1));
        res = assertInt(res, hints);
        assertResEquals(1, res);
        Assert.assertEquals("CTRIP", queryByPk(hints.setTableShardValue(1)).getAddress());

        // By shardColValue
        Assert.assertEquals(3, getCount(shardId, 2));
        hints = copy(oldhints);
        res = dao.update(usb, hints.setShardColValue("table", 2));
        res = assertInt(res, hints);
        assertResEquals(1, res);
        Assert.assertEquals("CTRIP", queryByPk(hints.setShardColValue("table", 2)).getAddress());

        // By shardColValue
        Assert.assertEquals(4, getCount(shardId, 3));
        hints = copy(oldhints);
        res = dao.update(usb, hints.setShardColValue("tableIndex", 3));
        res = assertInt(res, hints);
        assertResEquals(1, res);
        Assert.assertEquals("CTRIP", queryByPk(hints.setShardColValue("tableIndex", 3)).getAddress());
    }

    private ClientTestModel queryByPk(DalHints oldhints) throws SQLException {
        DalHints hints = copy(oldhints).callbackWith(new TestQueryResultCallback());
        return assertModel(dao.queryByPk(1, hints), hints);
    }

    @Test
    public void testCrossShardCombinedInsert() throws SQLException {
        if (!diff.supportInsertValues)
            return;

        testCrossShardCombinedInsert(new DalHints());
        testCrossShardCombinedInsert(asyncHints());
        testCrossShardCombinedInsert(intHints());

        if (!INSERT_PK_BACK_ALLOWED)
            return;

        testCrossShardCombinedInsertSetPkBack(new DalHints());
        testCrossShardCombinedInsertSetPkBack(asyncHints());
        testCrossShardCombinedInsertSetPkBack(intHints());
    }

    private void testCrossShardCombinedInsert(DalHints oldhints) throws SQLException {
        try {
            deleteAllShardsByDbTable(dao, mod, tableMod);

            ClientTestModel[] pList = new ClientTestModel[mod * (1 + tableMod) * tableMod / 2];
            int x = 0;
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < tableMod; j++) {
                    for (int k = 0; k < j + 1; k++) {
                        ClientTestModel p = new ClientTestModel();

                        p = new ClientTestModel();
                        p.setId(1 + k);
                        p.setAddress("aaa");
                        p.setDbIndex(i);
                        p.setTableIndex(j);

                        pList[x++] = p;
                    }
                }
            }

            KeyHolder keyHolder = createKeyHolder();
            DalHints hints = copy(oldhints);
            // hints.set(DalHintEnum.sequentialExecution);
            int res = dao.combinedInsert(hints, keyHolder, Arrays.asList(pList));
            assertInt(res, hints);
            assertKeyHolderCrossShard(keyHolder);
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < tableMod; j++) {
                    Assert.assertEquals(j + 1, getCount(i, j));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private void testCrossShardCombinedInsertSetPkBack(DalHints oldhints) throws SQLException {
        try {
            deleteAllShardsByDbTable(dao, mod, tableMod);

            ClientTestModel[] pList = new ClientTestModel[mod * (1 + tableMod) * tableMod / 2];
            int x = 0;
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < tableMod; j++) {
                    for (int k = 0; k < j + 1; k++) {
                        ClientTestModel p = new ClientTestModel();

                        p = new ClientTestModel();
                        p.setId(1 + k);
                        p.setAddress("aaa");
                        p.setDbIndex(i);
                        p.setTableIndex(j);

                        pList[x++] = p;
                    }
                }
            }

            KeyHolder keyHolder = createKeyHolder();
            DalHints hints = copy(oldhints);
            IdentitySetBackHelper.clearId(Arrays.asList(pList));
            int res = dao.combinedInsert(hints.setIdentityBack(), keyHolder, Arrays.asList(pList));
            assertInt(res, hints);
            assertKeyHolderCrossShard(keyHolder);
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < tableMod; j++) {
                    Assert.assertEquals(j + 1, getCount(i, j));
                }
            }
            IdentitySetBackHelper.assertIdentity(dao, copy(oldhints), Arrays.asList(pList));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void assertKeyHolderCrossShard(KeyHolder holder) throws SQLException {
        if (!ASSERT_ALLOWED)
            return;
        /*
         * Shard 0 1\2 12\34 123\456 1234\5678 Shard 1 1\2 12\23 123\456 1234\5678
         */
        int x = 0;
        for (int i = 0; i < mod; i++) {
            for (int j = 0; j < tableMod; j++) {
                for (int k = 0; k < j + 1; k++) {
                    Assert.assertTrue(holder.getKey(x++).intValue() > 0);
                }
            }
        }
    }

    @Test
    public void testCrossShardBatchInsert() {
        testCrossShardBatchInsert(new DalHints());
        testCrossShardBatchInsert(asyncHints());
        testCrossShardBatchInsert(intHints());
    }

    public void testCrossShardBatchInsert(DalHints oldhints) {
        try {
            deleteAllShardsByDbTable(dao, mod, tableMod);

            ClientTestModel[] pList = new ClientTestModel[mod * (1 + tableMod) * tableMod / 2];
            int x = 0;
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < tableMod; j++) {
                    for (int k = 0; k < j + 1; k++) {
                        ClientTestModel p = new ClientTestModel();

                        p = new ClientTestModel();
                        p.setId(1 + k);
                        p.setAddress("aaa");
                        p.setDbIndex(i);
                        p.setTableIndex(j);

                        pList[x++] = p;
                    }
                }
            }

            DalHints hints = copy(oldhints);
            int[] res = dao.batchInsert(hints, Arrays.asList(pList));
            assertIntArray(res, hints);

            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < tableMod; j++) {
                    Assert.assertEquals(j + 1, getCount(i, j));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCrossShardDelete() {
        testCrossShardDelete(new DalHints());
        testCrossShardDelete(asyncHints());
        testCrossShardDelete(intHints());
    }

    public void testCrossShardDelete(DalHints oldhints) {
        try {
            reset();
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < tableMod; j++) {
                    Assert.assertEquals(j + 1, getCount(i, j));
                }
            }

            ClientTestModel[] pList = new ClientTestModel[mod * (1 + tableMod) * tableMod / 2];
            int x = 0;
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < tableMod; j++) {
                    for (int k = 0; k < j + 1; k++) {
                        ClientTestModel p = new ClientTestModel();

                        p = new ClientTestModel();
                        p.setId(1 + k);
                        p.setAddress("aaa");
                        p.setDbIndex(i);
                        p.setTableIndex(j);

                        pList[x++] = p;
                    }
                }
            }

            DalHints hints = copy(oldhints);
            int[] res = dao.batchDelete(hints, Arrays.asList(pList));
            assertIntArray(res, hints);

            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < tableMod; j++) {
                    Assert.assertEquals(0, getCount(i, j));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    // region select
    @Test
    public void testQueryListInAllDBAndAllTableShards() throws SQLException {
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" id = 1 ");

        try {
            List<ClientTestModel> list = dao.query(ssb1, new DalHints().inAllShards().inAllTableShards());
            Assert.assertEquals(8, list.size());
        } catch (Exception e) {
            Assert.fail();
        }

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME);

        try {
            List<ClientTestModel> list = dao.query(ssb2, new DalHints().inAllShards().inAllTableShards());
            Assert.assertEquals(20, list.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryListImplicitInAllDBAndAllTableShards() throws SQLException {
        //		set implicitAllShards on
        DalPropertiesManager.getInstance().tearDown();
        DalPropertiesManager.getInstance().setDalPropertiesProvider(new LocalDalPropertiesProvider());
        DalPropertiesManager.getInstance().setup();

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" id = 1 ");

        try {
            List<ClientTestModel> list = dao.query(ssb1, new DalHints());
            Assert.assertEquals(8, list.size());
        } catch (Exception e) {
            Assert.fail();
        }

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME);

        try {
            List<ClientTestModel> list = dao.query(ssb2, new DalHints());
            Assert.assertEquals(20, list.size());
        } catch (Exception e) {
            Assert.fail();
        }

        SelectSqlBuilder ssb3 = new SelectSqlBuilder();
        ssb3.selectAll().from(TABLE_NAME).equal("dbIndex",0,Types.INTEGER);
        try {
            List<ClientTestModel> list = dao.query(ssb3, new DalHints());
            Assert.assertEquals(10, list.size());
        } catch (Exception e) {
            Assert.fail();
        }

        SelectSqlBuilder ssb4= new SelectSqlBuilder();
        ssb4.selectAll().from(TABLE_NAME).equal("tableIndex",0,Types.INTEGER);
        try {
            List<ClientTestModel> list = dao.query(ssb4, new DalHints());
            Assert.assertEquals(2, list.size());
        } catch (Exception e) {
            Assert.fail();
        }

        //		set implicitAllShards off
        DalPropertiesManager.getInstance().tearDown();
        DalPropertiesManager.getInstance().setDalPropertiesProvider(DalElementFactory.DEFAULT.getDalPropertiesProvider());
        DalPropertiesManager.getInstance().setup();

        try {
            List<ClientTestModel> list = dao.query(ssb1, new DalHints());
            Assert.fail();
        } catch (Exception e) {
           assertTrue(e.getMessage().contains("Can not locate table shard"));
        }

        SelectSqlBuilder ssb5 = new SelectSqlBuilder();
        ssb5.selectAll().from(TABLE_NAME).equal("dbIndex",0,Types.INTEGER);
        try {
            List<ClientTestModel> list = dao.query(ssb5, new DalHints());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Can not locate table shard"));
        }

        SelectSqlBuilder ssb6= new SelectSqlBuilder();
        ssb6.selectAll().from(TABLE_NAME).equal("tableIndex",0,Types.INTEGER);
        try {
            List<ClientTestModel> list = dao.query(ssb6, new DalHints());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Can not locate shard"));
        }

    }

    @Test
    public void testQueryListInAllDbShardsAndInTableShards() throws SQLException {
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" id = 4 ");

        try {
            Set<String> set = new HashSet<>();
            set.add("0");
            set.add("1");
            set.add("2");
            List<ClientTestModel> list = dao.query(ssb1, new DalHints().inAllShards().inTableShards(set));
            Assert.assertEquals(0, list.size());
        } catch (Exception e) {
            Assert.fail();
        }

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" id = 3 ");

        try {
            Set<String> set = new HashSet<>();
            set.add("0");
            set.add("1");
            set.add("2");
            List<ClientTestModel> list = dao.query(ssb2, new DalHints().inAllShards().inTableShards(set));
            Assert.assertEquals(2, list.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryListInAllDbShardsAndTableShardBy() throws SQLException {
        int index = 1;

        List<String> list1 = new ArrayList<>();
        list1.add("0");
        StatementParameters parameters1 = new StatementParameters();
        parameters1.setInParameter(index, "tableIndex", Types.INTEGER, list1);

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters1);

        try {
            List<ClientTestModel> result = dao.query(ssb1, new DalHints().inAllShards().tableShardBy("tableIndex"));
            Assert.assertEquals(2, result.size());
        } catch (Exception e) {
            Assert.fail();
        }

        List<String> list2 = new ArrayList<>();
        list2.add("0");
        list2.add("1");
        StatementParameters parameters2 = new StatementParameters();
        parameters2.setInParameter(index, "tableIndex", Types.INTEGER, list2);

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters2);

        try {
            List<ClientTestModel> result = dao.query(ssb2, new DalHints().inAllShards().tableShardBy("tableIndex"));
            Assert.assertEquals(6, result.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryListInDbShardAndTableShardBy() throws SQLException {
        int index = 1;

        List<String> list1 = new ArrayList<>();
        list1.add("0");
        StatementParameters parameters1 = new StatementParameters();
        parameters1.setInParameter(index, "tableIndex", Types.INTEGER, list1);

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters1);

        try {
            List<ClientTestModel> result = dao.query(ssb1, new DalHints().inShard("0").tableShardBy("tableIndex"));
            Assert.assertEquals(1, result.size());
        } catch (Exception e) {
            Assert.fail();
        }

        List<String> list2 = new ArrayList<>();
        list2.add("0");
        list2.add("1");
        StatementParameters parameters2 = new StatementParameters();
        parameters2.setInParameter(index, "tableIndex", Types.INTEGER, list2);

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters2);

        try {
            List<ClientTestModel> result = dao.query(ssb2, new DalHints().inShard("0").tableShardBy("tableIndex"));
            Assert.assertEquals(3, result.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryListDbShardByAndTableShardBy() throws SQLException {
        int index = 1;

        List<String> list1 = new ArrayList<>();
        list1.add("1");

        list1.add("2");
        list1.add("4");

        list1.add("3");
        list1.add("6");
        list1.add("9");

        list1.add("8");
        list1.add("12");
        list1.add("16");

        list1.add("18");

        list1.add("24");
        list1.add("32");

        StatementParameters parameters1 = new StatementParameters();
        parameters1.setInParameter(index++, "quantity", Types.INTEGER, list1);

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" quantity in (?) ").with(parameters1);

        // By DalTableDao
        try {
            List<ClientTestModel> result = dao.query(ssb1, new DalHints().shardBy("quantity").tableShardBy("quantity"));
            Assert.assertEquals(1, result.size());

            ClientTestModel model = result.get(0);
            Assert.assertEquals(2, model.getId().intValue());
            Assert.assertEquals(6, model.getQuantity().intValue());
            Assert.assertEquals(0, model.getDbIndex().intValue());
            Assert.assertEquals(2, model.getTableIndex().intValue());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    // endregion

    // region update
    @Test
    public void testUpdateInAllDBAndAllTableShards() throws SQLException {
        UpdateSqlBuilder usb = new UpdateSqlBuilder(TABLE_NAME, dao.getDatabaseCategory());
        usb.update("address", "InAllDBAndAllTableShards", Types.VARCHAR);
        usb.equal("id", "1", Types.INTEGER);
        try {
            dao.update(usb, new DalHints().inAllShards().inAllTableShards());
        } catch (Exception e) {
            Assert.fail();
        }

        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        ClientTestModel model0 = dao.queryByPk(1, new DalHints().inShard("0").inTableShards(shard0));
        assertEquals("InAllDBAndAllTableShards", model0.getAddress());
        assertEquals(0, model0.getDbIndex().intValue());
        assertEquals(0, model0.getTableIndex().intValue());


        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        ClientTestModel model1 = dao.queryByPk(1, new DalHints().inShard("1").inTableShards(shard1));
        assertEquals("InAllDBAndAllTableShards", model1.getAddress());
        assertEquals(1, model1.getDbIndex().intValue());
        assertEquals(1, model1.getTableIndex().intValue());
    }

    @Test
    public void testUpdateInAllDBAndInTableShards() throws SQLException {
        UpdateSqlBuilder usb = new UpdateSqlBuilder(TABLE_NAME, dao.getDatabaseCategory());
        usb.update("address", "InAllDbShardsAndInTableShards", Types.VARCHAR);
        usb.equal("id", "1", Types.INTEGER);
        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("2");
        try {
            dao.update(usb, new DalHints().inAllShards().inTableShards(tableShards));
        } catch (Exception e) {
            Assert.fail();
        }

        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        ClientTestModel model0 = dao.queryByPk(1, new DalHints().inShard("0").inTableShards(shard0));
        assertEquals("InAllDbShardsAndInTableShards", model0.getAddress());
        assertEquals(0, model0.getDbIndex().intValue());
        assertEquals(0, model0.getTableIndex().intValue());

        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        ClientTestModel model1 = dao.queryByPk(1, new DalHints().inShard("1").inTableShards(shard1));
        assertNotEquals("InAllDbShardsAndInTableShards", model1.getAddress());
        assertEquals(1, model1.getDbIndex().intValue());
        assertEquals(1, model1.getTableIndex().intValue());
    }
    // endregion

    // region insert
    @Test
    public void testInsertInAllDBAndAllTableShards() throws SQLException {
        try {
            insertInAllDBAndAllTableShards();
        } catch (Exception e) {
            Assert.fail();
        }

        ClientTestModel pojo = new ClientTestModel();
        pojo.setAddress("InsertInAllDBAndAllTableShards");
        queryInAllDBAndAllTableShards(pojo);
    }

    private void insertInAllDBAndAllTableShards() throws SQLException {
        InsertSqlBuilder isb = new InsertSqlBuilder();
        isb.setDatabaseCategory(dao.getDatabaseCategory());
        isb.from(TABLE_NAME);
        isb.set("quantity", "-1", Types.INTEGER);
        isb.set("dbIndex", "-1", Types.INTEGER);
        isb.set("tableIndex", "-1", Types.INTEGER);
        isb.set("type", "-1", Types.INTEGER);
        isb.set("address", "InsertInAllDBAndAllTableShards", Types.VARCHAR);

        dao.insert(isb, new DalHints().inAllShards().inAllTableShards());
    }

    private void queryInAllDBAndAllTableShards(ClientTestModel pojo) throws SQLException {
        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        List<ClientTestModel> modelList0 = dao.queryBy(pojo, new DalHints().inShard("0").inTableShards(shard0));
        assertEquals(1, modelList0.size());

        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        List<ClientTestModel> modelList1 = dao.queryBy(pojo, new DalHints().inShard("1").inTableShards(shard1));
        assertEquals(1, modelList1.size());

        Set<String> shard2 = new HashSet<>();
        shard2.add("2");
        List<ClientTestModel> modelList2 = dao.queryBy(pojo, new DalHints().inShard("0").inTableShards(shard2));
        assertEquals(1, modelList2.size());

        Set<String> shard3 = new HashSet<>();
        shard3.add("3");
        List<ClientTestModel> modelList3 = dao.queryBy(pojo, new DalHints().inShard("1").inTableShards(shard3));
        assertEquals(1, modelList3.size());
    }
    // endregion

    // region delete
    @Test
    public void testDeleteInAllDbAndAllTableShards() throws SQLException {
        try {
            insertInAllDbAndAllTableShardsForDeleting();
        } catch (Exception e) {

        }

        ClientTestModel pojo = new ClientTestModel();
        pojo.setAddress("DeleteInAllDBAndAllTableShards");

        // before deleting
        queryInAllDBAndAllTableShardsForDeleting(pojo);
        // end

        DeleteSqlBuilder dsb = new DeleteSqlBuilder();
        dsb.setDatabaseCategory(dao.getDatabaseCategory());
        dsb.from(TABLE_NAME);
        dsb.where(" address = 'DeleteInAllDBAndAllTableShards' ");
        dao.delete(dsb, new DalHints().inAllShards().inAllTableShards());

        // after deleting
        queryInAllDBAndAllTableShardsWithoutAnyResult(pojo);
        // end

    }

    private void insertInAllDbAndAllTableShardsForDeleting() throws SQLException {
        InsertSqlBuilder isb = new InsertSqlBuilder();
        isb.setDatabaseCategory(dao.getDatabaseCategory());
        isb.from(TABLE_NAME);
        isb.set("quantity", "-1", Types.INTEGER);
        isb.set("dbIndex", "-1", Types.INTEGER);
        isb.set("tableIndex", "-1", Types.INTEGER);
        isb.set("type", "-1", Types.INTEGER);
        isb.set("address", "DeleteInAllDBAndAllTableShards", Types.VARCHAR);

        dao.insert(isb, new DalHints().inAllShards().inAllTableShards());
    }

    private void queryInAllDBAndAllTableShardsForDeleting(ClientTestModel pojo) throws SQLException {
        Set<String> dbShard0 = new HashSet<>();
        dbShard0.add("0");

        List<ClientTestModel> modelList0_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("0"));
        assertEquals(1, modelList0_0.size());

        List<ClientTestModel> modelList0_1 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("1"));
        assertEquals(1, modelList0_1.size());

        List<ClientTestModel> modelList0_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("2"));
        assertEquals(1, modelList0_2.size());

        List<ClientTestModel> modelList0_3 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("3"));
        assertEquals(1, modelList0_3.size());


        Set<String> dbShard1 = new HashSet<>();
        dbShard1.add("1");

        List<ClientTestModel> modelList1_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("0"));
        assertEquals(1, modelList1_0.size());

        List<ClientTestModel> modelList1_1 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("1"));
        assertEquals(1, modelList1_1.size());

        List<ClientTestModel> modelList1_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("2"));
        assertEquals(1, modelList1_2.size());

        List<ClientTestModel> modelList1_3 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("3"));
        assertEquals(1, modelList1_3.size());

    }

    private void queryInAllDBAndAllTableShardsWithoutAnyResult(ClientTestModel pojo) throws SQLException {
        Set<String> dbShard0 = new HashSet<>();
        dbShard0.add("0");

        List<ClientTestModel> modelList0_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("0"));
        assertEquals(0, modelList0_0.size());

        List<ClientTestModel> modelList0_1 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("1"));
        assertEquals(0, modelList0_1.size());

        List<ClientTestModel> modelList0_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("2"));
        assertEquals(0, modelList0_2.size());

        List<ClientTestModel> modelList0_3 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("3"));
        assertEquals(0, modelList0_3.size());


        Set<String> dbShard1 = new HashSet<>();
        dbShard1.add("1");

        List<ClientTestModel> modelList1_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("0"));
        assertEquals(0, modelList1_0.size());

        List<ClientTestModel> modelList1_1 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("1"));
        assertEquals(0, modelList1_1.size());

        List<ClientTestModel> modelList1_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("2"));
        assertEquals(0, modelList1_2.size());

        List<ClientTestModel> modelList1_3 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("3"));
        assertEquals(0, modelList1_3.size());

    }

    @Test
    public void testDeleteInTableShards() throws SQLException {
        try {
            insertInAllDBAndTableShardsForDeleting();
        } catch (Exception e) {
            Assert.fail();
        }

        ClientTestModel pojo = new ClientTestModel();
        pojo.setAddress("DeleteInAllDBAndInTableShards");

        // before deleting
        queryInAllDBAndInTableShards(pojo);
        // end

        DeleteSqlBuilder dsb = new DeleteSqlBuilder();
        dsb.setDatabaseCategory(dao.getDatabaseCategory());
        dsb.from(TABLE_NAME);
        dsb.where(" address = 'DeleteInAllDBAndInTableShards' ");

        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("2");

        dao.delete(dsb, new DalHints().inAllShards().inTableShards(tableShards));

        // after deleting
        queryInAllDBAndInTableShardsWithoutAnyResult(pojo);
        // end

    }

    private void insertInAllDBAndTableShardsForDeleting() throws SQLException {
        InsertSqlBuilder isb = new InsertSqlBuilder();
        isb.setDatabaseCategory(dao.getDatabaseCategory());
        isb.from(TABLE_NAME);
        isb.set("quantity", "-1", Types.INTEGER);
        isb.set("dbIndex", "-1", Types.INTEGER);
        isb.set("tableIndex", "-1", Types.INTEGER);
        isb.set("type", "-1", Types.INTEGER);
        isb.set("address", "DeleteInAllDBAndInTableShards", Types.VARCHAR);

        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("2");

        dao.insert(isb, new DalHints().inAllShards().inTableShards(tableShards));
    }

    private void queryInAllDBAndInTableShards(ClientTestModel pojo) throws SQLException {
        Set<String> dbShard0 = new HashSet<>();
        dbShard0.add("0");

        List<ClientTestModel> modelList0_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("0"));
        assertEquals(1, modelList0_0.size());

        List<ClientTestModel> modelList0_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("2"));
        assertEquals(1, modelList0_2.size());

        List<ClientTestModel> modelList0_1 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("1"));
        assertEquals(0, modelList0_1.size());

        List<ClientTestModel> modelList0_3 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("3"));
        assertEquals(0, modelList0_3.size());


        Set<String> dbShard1 = new HashSet<>();
        dbShard1.add("1");

        List<ClientTestModel> modelList1_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("0"));
        assertEquals(1, modelList1_0.size());

        List<ClientTestModel> modelList1_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("2"));
        assertEquals(1, modelList1_2.size());

        List<ClientTestModel> modelList1_1 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("1"));
        assertEquals(0, modelList1_1.size());

        List<ClientTestModel> modelList1_3 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("3"));
        assertEquals(0, modelList1_3.size());

    }

    private void queryInAllDBAndInTableShardsWithoutAnyResult(ClientTestModel pojo) throws SQLException {
        Set<String> dbShard0 = new HashSet<>();
        dbShard0.add("0");
        List<ClientTestModel> modelList0_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("0"));
        assertEquals(0, modelList0_0.size());

        List<ClientTestModel> modelList0_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("2"));
        assertEquals(0, modelList0_2.size());

        Set<String> dbShard1 = new HashSet<>();
        dbShard1.add("1");
        List<ClientTestModel> modelList1_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("0"));
        assertEquals(0, modelList1_0.size());

        List<ClientTestModel> modelList1_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("2"));
        assertEquals(0, modelList1_2.size());
    }

    @Test
    public void testDeleteInAllDBAndTableShardBy() throws SQLException {
        try {
            insertTableShardByForDeleting();
        } catch (Exception e) {
            Assert.fail();
        }

        ClientTestModel pojo = new ClientTestModel();
        pojo.setAddress("DeleteInAllDBShardsAndTableShardBy");

        // before deleting
        queryInAllDBShardsAndTableShardBy(pojo);
        // end

        int index = 0;
        StatementParameters parameters = new StatementParameters();
        List<String> list = new ArrayList<>();
        list.add("0");
        list.add("2");
        parameters.setInParameter(index++, "tableIndex", Types.INTEGER, list);

        DeleteSqlBuilder dsb = new DeleteSqlBuilder();
        dsb.setDatabaseCategory(dao.getDatabaseCategory());
        dsb.from(TABLE_NAME);
        dsb.where(" tableIndex in(?) and address = 'DeleteInAllDBShardsAndTableShardBy'");
        dsb.with(parameters);

        dao.delete(dsb, new DalHints().inAllShards().tableShardBy("tableIndex"));

        // after deleting
        queryInAllDBShardsAndTableShardByWithoutAnyResult(pojo);
        // end

    }

    private void insertTableShardByForDeleting() throws SQLException {
        InsertSqlBuilder isb = getTableShardByInsertSqlBuilder("0");
        dao.insert(isb, new DalHints().inAllShards().inTableShard("0"));

        InsertSqlBuilder isb2 = getTableShardByInsertSqlBuilder("2");
        dao.insert(isb2, new DalHints().inAllShards().inTableShard("2"));
    }

    private InsertSqlBuilder getTableShardByInsertSqlBuilder(String tableIndex) throws SQLException {
        InsertSqlBuilder isb = new InsertSqlBuilder();
        isb.setDatabaseCategory(dao.getDatabaseCategory());
        isb.from(TABLE_NAME);
        isb.set("quantity", "-1", Types.INTEGER);
        isb.set("dbIndex", "-1", Types.INTEGER);
        isb.set("tableIndex", tableIndex, Types.INTEGER);
        isb.set("type", "-1", Types.INTEGER);
        isb.set("address", "DeleteInAllDBShardsAndTableShardBy", Types.VARCHAR);
        return isb;
    }

    private void queryInAllDBShardsAndTableShardBy(ClientTestModel pojo) throws SQLException {
        Set<String> dbShard0 = new HashSet<>();
        dbShard0.add("0");

        List<ClientTestModel> modelList0_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("0"));
        assertEquals(1, modelList0_0.size());

        List<ClientTestModel> modelList0_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("2"));
        assertEquals(1, modelList0_2.size());

        List<ClientTestModel> modelList0_1 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("1"));
        assertEquals(0, modelList0_1.size());

        List<ClientTestModel> modelList0_3 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("3"));
        assertEquals(0, modelList0_3.size());


        Set<String> dbShard1 = new HashSet<>();
        dbShard1.add("1");

        List<ClientTestModel> modelList1_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("0"));
        assertEquals(1, modelList1_0.size());

        List<ClientTestModel> modelList1_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("2"));
        assertEquals(1, modelList1_2.size());

        List<ClientTestModel> modelList1_1 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("1"));
        assertEquals(0, modelList1_1.size());

        List<ClientTestModel> modelList1_3 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("3"));
        assertEquals(0, modelList1_3.size());

    }

    private void queryInAllDBShardsAndTableShardByWithoutAnyResult(ClientTestModel pojo) throws SQLException {
        Set<String> dbShard0 = new HashSet<>();
        dbShard0.add("0");

        List<ClientTestModel> modelList0_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("0"));
        assertEquals(0, modelList0_0.size());

        List<ClientTestModel> modelList0_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard0).inTableShard("2"));
        assertEquals(0, modelList0_2.size());

        Set<String> dbShard1 = new HashSet<>();
        dbShard1.add("1");

        List<ClientTestModel> modelList1_0 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("0"));
        assertEquals(0, modelList1_0.size());

        List<ClientTestModel> modelList1_2 = dao.queryBy(pojo, new DalHints().inShards(dbShard1).inTableShard("2"));
        assertEquals(0, modelList1_2.size());
    }

    // endregion

    // region FreeSelectSqlBuilder

    @Test
    public void testFreeSelectSqlBuilderInAllDBAndAllTableShards() throws SQLException {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        try {
            List<ClientTestModel> result = queryDao.query(builder, new DalHints().inAllShards().inAllTableShards());
            Assert.assertEquals(20, result.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testFreeSelectSqlBuilderInAllDBAndImplicitInAllTableShards() throws SQLException {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        try {
            List<ClientTestModel> result = queryDao.query(builder, new DalHints().inAllShards());
            Assert.assertEquals(20, result.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testFreeSelectSqlBuilderInAllDBAndTableShards() throws SQLException {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        try {
            Set<String> tableShards = new HashSet<>();
            tableShards.add("2");
            tableShards.add("3");
            List<ClientTestModel> result =
                    queryDao.query(builder, new DalHints().inAllShards().inTableShards(tableShards));
            Assert.assertEquals(14, result.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testFreeSelectSqlBuilderInAllDBAndTableShardBy() throws SQLException {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        try {
            List<ClientTestModel> result = queryDao.query(builder, new DalHints().inAllShards().tableShardBy("id"));
            Assert.assertEquals(6, result.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testFreeSelectSqlBuilderShardByAndTableShardBy() throws SQLException {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        try {
            List<ClientTestModel> result = queryDao.query(builder, new DalHints().shardBy("id").tableShardBy("id"));
            Assert.assertEquals(3, result.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    // endregion

}

package com.ctrip.platform.dal.dao.shard;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.ctrip.platform.dal.dao.sqlbuilder.AbstractFreeSqlBuilder;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public abstract class BaseDalTabelDaoShardByTableTest {
    private boolean ASSERT_ALLOWED = true;
    private boolean INSERT_PK_BACK_ALLOWED = false;
    private DatabaseDifference diff;
    private String databaseName;

    public BaseDalTabelDaoShardByTableTest(String databaseName, DatabaseDifference diff) {
        this.diff = diff;
        try {
            this.databaseName = databaseName;
            DalClientFactory.initClientFactory();
            DalParser<ClientTestModel> clientTestParser = new ClientTestDalParser(databaseName);
            dao = new DalTableDao<ClientTestModel>(clientTestParser);
            ASSERT_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;
            INSERT_PK_BACK_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;

            queryDao = new DalQueryDao(databaseName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final static String TABLE_NAME = "dal_client_test";
    private final static int mod = 4;

    private static DalTableDao<ClientTestModel> dao;
    private static DalQueryDao queryDao;

    public void assertResEquals(int exp, int res) {
        if (ASSERT_ALLOWED)
            assertEquals(exp, res);
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
            // By tabelShard
            if (i % 2 == 0)
                model = dao.queryByPk(1, new DalHints().inTableShard(String.valueOf(i)));
            else
                model = dao.queryByPk(1, new DalHints().inTableShard(i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By tableShardValue
            if (i % 2 == 0)
                model = dao.queryByPk(1, new DalHints().setTableShardValue(String.valueOf(i)));
            else
                model = dao.queryByPk(1, new DalHints().setTableShardValue(i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            if (i % 2 == 0)
                model = dao.queryByPk(1, new DalHints().setShardColValue("index", String.valueOf(i)));
            else
                model = dao.queryByPk(1, new DalHints().setShardColValue("index", i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            if (i % 2 == 0)
                model = dao.queryByPk(1, new DalHints().setShardColValue("tableIndex", String.valueOf(i)));
            else
                model = dao.queryByPk(1, new DalHints().setShardColValue("tableIndex", i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());
        }
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

    private ClientTestModel assertModel(Object model, DalHints hints) throws SQLException {
        assertNull(model);
        if (hints.is(DalHintEnum.resultCallback)) {
            TestQueryResultCallback callback = (TestQueryResultCallback) hints.get(DalHintEnum.resultCallback);
            return callback.get();
        }

        try {
            return (ClientTestModel) hints.getAsyncResult().get();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private List<ClientTestModel> assertModels(Object models, DalHints hints) throws SQLException {
        assertNull(models);
        if (hints.is(DalHintEnum.resultCallback)) {
            TestQueryResultCallback callback = (TestQueryResultCallback) hints.get(DalHintEnum.resultCallback);
            return callback.getModels();
        }
        try {
            return (List<ClientTestModel>) hints.getAsyncResult().get();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private int assertInt(int res, DalHints hints) throws SQLException {
        assertEquals(0, res);
        if (hints.is(DalHintEnum.resultCallback)) {
            IntCallback callback = (IntCallback) hints.get(DalHintEnum.resultCallback);
            return callback.getInt();
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
        assertNull(res);
        if (hints.is(DalHintEnum.resultCallback)) {
            IntCallback callback = (IntCallback) hints.get(DalHintEnum.resultCallback);
            return callback.getIntArray();
        }
        try {
            return (int[]) hints.getAsyncResult().get();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Test
    public void testQueryByPkAsyncCallback() throws SQLException {
        ClientTestModel model = null;
        DalHints hints;

        for (int i = 0; i < mod; i++) {
            // By tabelShard
            hints = new DalHints().asyncExecution();
            if (i % 2 == 0)
                model = dao.queryByPk(1, hints.inTableShard(String.valueOf(i)));
            else
                model = dao.queryByPk(1, hints.inTableShard(i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By tableShardValue
            hints = callbackHints();
            if (i % 2 == 0)
                model = dao.queryByPk(1, hints.setTableShardValue(String.valueOf(i)));
            else
                model = dao.queryByPk(1, hints.setTableShardValue(i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            hints = new DalHints().asyncExecution();
            if (i % 2 == 0)
                model = dao.queryByPk(1, hints.setShardColValue("index", String.valueOf(i)));
            else
                model = dao.queryByPk(1, hints.setShardColValue("index", i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            hints = callbackHints();
            if (i % 2 == 0)
                model = dao.queryByPk(1, hints.setShardColValue("tableIndex", String.valueOf(i)));
            else
                model = dao.queryByPk(1, hints.setShardColValue("tableIndex", i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());
        }
    }

    /**
     * Query by Entity with Primary key
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryByColumnNames() throws SQLException {
        ClientTestModel pk = null;
        ClientTestModel model = null;

        for (int i = 0; i < mod; i++) {
            pk = new ClientTestModel();
            pk.setId(1);

            // By tabelShard
            DalTableDao<ClientTestModel> dao = new DalTableDao(ClientTestModel.class, databaseName, "dal_client_test");
            model = dao.queryByPk(pk, new DalHints().inTableShard(i).selectByNames());
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            dao.queryLike(model, new DalHints().inTableShard(i).selectByNames());
            dao.count("id > 0", new StatementParameters(), new DalHints().inTableShard(i).selectByNames());
            Long L = dao.queryObject(new SelectSqlBuilder().select("id").requireFirst().where("id > 0"),
                    new DalHints().inTableShard(i).selectByNames(), Long.class);
        }
    }

    /**
     * Query by Entity with Primary key
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryByPkWithEntity() throws SQLException {
        ClientTestModel pk = null;
        ClientTestModel model = null;

        for (int i = 0; i < mod; i++) {
            pk = new ClientTestModel();
            pk.setId(1);

            // By tabelShard
            model = dao.queryByPk(pk, new DalHints().inTableShard(i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By tableShardValue
            model = dao.queryByPk(pk, new DalHints().setTableShardValue(i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            model = dao.queryByPk(pk, new DalHints().setShardColValue("index", i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            model = dao.queryByPk(pk, new DalHints().setShardColValue("tableIndex", i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By fields
            pk.setTableIndex(i);
            model = dao.queryByPk(pk, new DalHints());
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());
        }
    }

    @Test
    public void testQueryByPkWithEntityAsyncCallback() throws SQLException {
        ClientTestModel pk = null;
        ClientTestModel model = null;
        DalHints hints;

        for (int i = 0; i < mod; i++) {
            pk = new ClientTestModel();
            pk.setId(1);

            // By tabelShard
            hints = asyncHints();
            model = dao.queryByPk(pk, hints.inTableShard(i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By tableShardValue
            hints = callbackHints();
            model = dao.queryByPk(pk, hints.setTableShardValue(i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            hints = asyncHints();
            model = dao.queryByPk(pk, hints.setShardColValue("index", i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            hints = callbackHints();
            model = dao.queryByPk(pk, hints.setShardColValue("tableIndex", i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By fields
            hints = asyncHints();
            pk.setTableIndex(i);
            model = dao.queryByPk(pk, hints);
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());
        }
    }

    /**
     * Query by Entity without Primary key
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryByPkWithEntityNoId() throws SQLException {
        ClientTestModel pk = new ClientTestModel();
        ClientTestModel model = null;
        // By fields
        for (int i = 0; i < mod; i++) {
            pk = new ClientTestModel();
            pk.setTableIndex(i);
            if (i % 2 == 0)
                model = dao.queryByPk(pk, new DalHints());
            else
                model = dao.queryByPk(pk, new DalHints());
            assertNull(model);
        }
    }

    @Test
    public void testQueryByPkWithEntityNoIdAsyncCallback() throws SQLException {
        ClientTestModel pk = new ClientTestModel();
        ClientTestModel model = null;
        DalHints hints;

        // By fields
        for (int i = 0; i < mod; i++) {
            pk = new ClientTestModel();
            pk.setTableIndex(i);
            hints = new DalHints().asyncExecution();
            if (i % 2 == 0)
                model = dao.queryByPk(pk, hints);
            else
                model = dao.queryByPk(pk, hints);
            assertModel(model, hints);
        }

        // By fields
        for (int i = 0; i < mod; i++) {
            pk = new ClientTestModel();
            pk.setTableIndex(i);
            hints = callbackHints();
            if (i % 2 == 0)
                model = dao.queryByPk(pk, hints);
            else
                model = dao.queryByPk(pk, hints);
            assertModel(model, hints);
            assertNull(model);
            assertModel(model, hints);
        }
    }

    /**
     * Query against sample entity
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryLike() throws SQLException {
        List<ClientTestModel> models = null;

        ClientTestModel pk = null;

        for (int i = 0; i < mod; i++) {
            pk = new ClientTestModel();
            pk.setType((short) 1);

            // By tabelShard
            models = dao.queryLike(pk, new DalHints().inTableShard(i));
            assertEquals(i + 1, models.size());

            // By tableShardValue
            models = dao.queryLike(pk, new DalHints().setTableShardValue(i));
            assertEquals(i + 1, models.size());

            // By shardColValue
            models = dao.queryLike(pk, new DalHints().setShardColValue("index", i));
            assertEquals(i + 1, models.size());

            // By shardColValue
            models = dao.queryLike(pk, new DalHints().setShardColValue("tableIndex", i));
            assertEquals(i + 1, models.size());

            // By fields
            pk.setTableIndex(i);
            models = dao.queryLike(pk, new DalHints());
            assertEquals(i + 1, models.size());
        }
    }

    @Test
    public void testQueryLikeAsyncCallback() throws SQLException {
        List<ClientTestModel> models = null;

        ClientTestModel pk = null;
        DalHints hints;

        for (int i = 0; i < mod; i++) {
            pk = new ClientTestModel();
            pk.setType((short) 1);

            // By tabelShard
            hints = new DalHints().asyncExecution();
            models = dao.queryLike(pk, hints.inTableShard(i));
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By tableShardValue
            hints = callbackHints();
            models = dao.queryLike(pk, hints.setTableShardValue(i));
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By shardColValue
            hints = new DalHints().asyncExecution();
            models = dao.queryLike(pk, hints.setShardColValue("index", i));
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By shardColValue
            hints = callbackHints();
            models = dao.queryLike(pk, hints.setShardColValue("tableIndex", i));
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By fields
            hints = new DalHints().asyncExecution();
            pk.setTableIndex(i);
            models = dao.queryLike(pk, hints);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());
        }
    }

    /**
     * Query by Entity with where clause
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryWithWhereClause() throws SQLException {
        List<ClientTestModel> models = null;

        for (int i = 0; i < mod; i++) {
            String whereClause = "type=? and id=?";
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);
            parameters.set(2, Types.INTEGER, 1);

            // By tabelShard
            models = dao.query(whereClause, parameters, new DalHints().inTableShard(i));
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));

            // By tableShardValue
            models = dao.query(whereClause, parameters, new DalHints().setTableShardValue(i));
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));

            // By shardColValue
            models = dao.query(whereClause, parameters, new DalHints().setShardColValue("index", i));
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));

            // By shardColValue
            models = dao.query(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i));
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));

            // By parameters
            whereClause += " and tableIndex=?";
            parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "id", Types.SMALLINT, i + 1);
            parameters.set(3, "tableIndex", Types.SMALLINT, i);

            models = dao.query(whereClause, parameters, new DalHints());
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));
        }
    }

    @Test
    public void testQueryWithWhereClauseAsymcCallback() throws SQLException {
        List<ClientTestModel> models = null;
        DalHints hints;

        for (int i = 0; i < mod; i++) {
            String whereClause = "type=? and id=?";
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);
            parameters.set(2, Types.INTEGER, 1);

            // By tabelShard
            hints = new DalHints().asyncExecution();
            models = dao.query(whereClause, parameters, hints.inTableShard(i));
            models = assertModels(models, hints);
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));

            // By tableShardValue
            hints = callbackHints();
            models = dao.query(whereClause, parameters, hints.setTableShardValue(i));
            models = assertModels(models, hints);
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));

            // By shardColValue
            hints = new DalHints().asyncExecution();
            models = dao.query(whereClause, parameters, hints.setShardColValue("index", i));
            models = assertModels(models, hints);
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));

            // By shardColValue
            hints = callbackHints();
            models = dao.query(whereClause, parameters, hints.setShardColValue("tableIndex", i));
            models = assertModels(models, hints);
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));

            // By parameters
            hints = new DalHints().asyncExecution();
            whereClause += " and tableIndex=?";
            parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "id", Types.SMALLINT, i + 1);
            parameters.set(3, "tableIndex", Types.SMALLINT, i);

            models = dao.query(whereClause, parameters, hints);
            models = assertModels(models, hints);
            assertEquals(1, models.size());
            assertEquals("SH INFO", models.get(0).getAddress());
            assertEquals(models.get(0).getTableIndex(), new Integer(i));
        }
    }

    /**
     * Test Query the first row with where clause
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFirstWithWhereClause() throws SQLException {
        ClientTestModel model = null;
        for (int i = 0; i < mod; i++) {
            String whereClause = "type=?";

            // By tabelShard
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            model = dao.queryFirst(whereClause, parameters, new DalHints().inTableShard(i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By tableShardValue
            model = dao.queryFirst(whereClause, parameters, new DalHints().setTableShardValue(i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            model = dao.queryFirst(whereClause, parameters, new DalHints().setShardColValue("index", i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            model = dao.queryFirst(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i));
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By parameters
            whereClause += " and tableIndex=?";
            parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "tableIndex", Types.SMALLINT, i);
            model = dao.queryFirst(whereClause, parameters, new DalHints());
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());
        }
    }

    @Test
    public void testQueryFirstWithWhereClauseAsyncCallback() throws SQLException {
        DalHints hints;

        ClientTestModel model = null;
        for (int i = 0; i < mod; i++) {
            String whereClause = "type=?";

            // By tabelShard
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            hints = new DalHints().asyncExecution();
            model = dao.queryFirst(whereClause, parameters, hints.inTableShard(i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By tableShardValue
            hints = callbackHints();
            model = dao.queryFirst(whereClause, parameters, hints.setTableShardValue(i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            hints = new DalHints().asyncExecution();
            model = dao.queryFirst(whereClause, parameters, hints.setShardColValue("index", i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By shardColValue
            hints = callbackHints();
            model = dao.queryFirst(whereClause, parameters, hints.setShardColValue("tableIndex", i));
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());

            // By parameters
            whereClause += " and tableIndex=?";
            parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "tableIndex", Types.SMALLINT, i);
            model = dao.queryFirst(whereClause, parameters, hints);
            model = assertModel(model, hints);
            assertEquals(1, model.getId().intValue());
            assertEquals(i, model.getTableIndex().intValue());
        }
    }

    /**
     * Test Query the first row with where clause failed
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFirstWithWhereClauseFailed() throws SQLException {
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 10);
        try {
            dao.queryFirst(whereClause, parameters, new DalHints().inTableShard(1));
            fail();
        } catch (Throwable e) {
        }
    }

    @Test
    public void testQueryFirstWithWhereClauseFailedAsync() throws SQLException {
        DalHints hints;

        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 10);
        try {
            hints = new DalHints().asyncExecution();
            Object model = dao.queryFirst(whereClause, parameters, hints.inTableShard(1));
            assertModel(model, hints);
            fail();
        } catch (Throwable e) {
        }
    }

    @Test
    public void testQueryFirstWithWhereClauseFailedCallback() throws SQLException {
        DalHints hints;
        DefaultResultCallback callback;

        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 10);
        try {
            callback = new DefaultResultCallback();
            hints = new DalHints().callbackWith(callback);
            Object model = dao.queryFirst(whereClause, parameters, hints.inTableShard(1));
            callback.waitForDone();
            assertNull(callback.getResult());
        } catch (Throwable e) {
            fail();
        }
    }

    /**
     * Test Query the top rows with where clause
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryTopWithWhereClause() throws SQLException {
        List<ClientTestModel> models = null;

        for (int i = 0; i < mod; i++) {
            String whereClause = "type=?";
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            // By tabelShard
            models = dao.queryTop(whereClause, parameters, new DalHints().inTableShard(i), i + 1);
            assertEquals(i + 1, models.size());

            // By tableShardValue
            models = dao.queryTop(whereClause, parameters, new DalHints().setTableShardValue(i), i + 1);
            assertEquals(i + 1, models.size());

            // By shardColValue
            models = dao.queryTop(whereClause, parameters, new DalHints().setShardColValue("index", i), i + 1);
            assertEquals(i + 1, models.size());

            // By shardColValue
            models = dao.queryTop(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), i + 1);
            assertEquals(i + 1, models.size());

            whereClause += " and tableIndex=?";
            // By parameters
            parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "tableIndex", Types.SMALLINT, i);
            models = dao.queryTop(whereClause, parameters, new DalHints(), i + 1);
            assertEquals(i + 1, models.size());
        }
    }

    @Test
    public void testQueryTopWithWhereClauseAsyncCallback() throws SQLException {
        List<ClientTestModel> models = null;
        DalHints hints;

        for (int i = 0; i < mod; i++) {
            String whereClause = "type=?";
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            // By tabelShard
            hints = new DalHints().asyncExecution();
            models = dao.queryTop(whereClause, parameters, hints.inTableShard(i), i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By tableShardValue
            hints = callbackHints();
            models = dao.queryTop(whereClause, parameters, hints.setTableShardValue(i), i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By shardColValue
            hints = new DalHints().asyncExecution();
            models = dao.queryTop(whereClause, parameters, hints.setShardColValue("index", i), i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By shardColValue
            hints = callbackHints();
            models = dao.queryTop(whereClause, parameters, hints.setShardColValue("tableIndex", i), i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            whereClause += " and tableIndex=?";
            // By parameters
            hints = new DalHints().asyncExecution();
            parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "tableIndex", Types.SMALLINT, i);
            models = dao.queryTop(whereClause, parameters, hints, i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());
        }
    }

    /**
     * Test Query the top rows with where clause failed
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryTopWithWhereClauseFailed() throws SQLException {
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 10);

        List<ClientTestModel> models;
        try {
            models = dao.queryTop(whereClause, parameters, new DalHints(), 2);
            fail();
        } catch (Exception e) {
        }

        models = dao.queryTop(whereClause, parameters, new DalHints().inTableShard(1), 2);
        assertTrue(null != models);
        assertEquals(0, models.size());
    }

    @Test
    public void testQueryTopWithWhereClauseFailedAsync() throws SQLException {
        DalHints hints;

        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 10);

        List<ClientTestModel> models;
        try {
            hints = new DalHints().asyncExecution();
            models = dao.queryTop(whereClause, parameters, hints, 2);
            // There is DalException throws here
            Object o = hints.getAsyncResult().get();
            fail();
        } catch (Exception e) {
        }

        models = dao.queryTop(whereClause, parameters, new DalHints().inTableShard(1), 2);
        assertTrue(null != models);
        assertEquals(0, models.size());
    }

    @Test
    public void testQueryTopWithWhereClauseFailedCallback() throws SQLException {
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 10);

        List<ClientTestModel> models;
        DefaultResultCallback callback = new DefaultResultCallback();
        DalHints hints = new DalHints().callbackWith(callback);
        models = dao.queryTop(whereClause, parameters, hints, 2);
        try {
            callback.waitForDone();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(!callback.isSuccess());
        assertNull(callback.getResult());
        assertNotNull(callback.getError());

        models = dao.queryTop(whereClause, parameters, new DalHints().inTableShard(1), 2);
        assertTrue(null != models);
        assertEquals(0, models.size());
    }

    /**
     * Test Query range of result with where clause
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFromWithWhereClause() throws SQLException {
        List<ClientTestModel> models = null;
        String whereClause = "type=? order by id";

        for (int i = 0; i < mod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            // By tabelShard
            models = dao.queryFrom(whereClause, parameters, new DalHints().inTableShard(i), 0, i + 1);
            assertEquals(i + 1, models.size());

            // By tableShardValue
            models = dao.queryFrom(whereClause, parameters, new DalHints().setTableShardValue(i), 0, i + 1);
            assertEquals(i + 1, models.size());

            // By shardColValue
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("index", i), 0, i + 1);
            assertEquals(i + 1, models.size());

            // By shardColValue
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), 0, i + 1);
            assertEquals(i + 1, models.size());
        }

        whereClause = "type=? and tableIndex=? order by id";
        // By parameters
        for (int i = 0; i < mod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "tableIndex", Types.SMALLINT, i);

            models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, i + 1);
            assertEquals(i + 1, models.size());
        }
    }

    @Test
    public void testQueryFromWithWhereClauseAsyncCallback() throws SQLException {
        List<ClientTestModel> models = null;
        String whereClause = "type=? order by id";
        DalHints hints;

        for (int i = 0; i < mod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            // By tabelShard
            hints = asyncHints();
            models = dao.queryFrom(whereClause, parameters, hints.inTableShard(i), 0, i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By tableShardValue
            hints = callbackHints();
            models = dao.queryFrom(whereClause, parameters, hints.setTableShardValue(i), 0, i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By shardColValue
            hints = asyncHints();
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("index", i), 0, i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());

            // By shardColValue
            hints = callbackHints();
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("tableIndex", i), 0, i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());
        }

        whereClause = "type=? and tableIndex=? order by id";
        // By parameters
        for (int i = 0; i < mod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, "type", Types.SMALLINT, 1);
            parameters.set(2, "tableIndex", Types.SMALLINT, i);

            hints = asyncHints();
            models = dao.queryFrom(whereClause, parameters, hints, 0, i + 1);
            models = assertModels(models, hints);
            assertEquals(i + 1, models.size());
        }
    }

    /**
     * Test Query range of result with where clause failed when return not enough recodes
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFromWithWhereClauseFailed() throws SQLException {
        String whereClause = "type=? order by id";
        List<ClientTestModel> models = null;
        for (int i = 0; i < mod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            // By tabelShard
            models = dao.queryFrom(whereClause, parameters, new DalHints().inTableShard(i), 0, 10);
            assertTrue(null != models);
            assertEquals(i + 1, models.size());

            // By tableShardValue
            models = dao.queryFrom(whereClause, parameters, new DalHints().setTableShardValue(i), 0, 10);
            assertTrue(null != models);
            assertEquals(i + 1, models.size());

            // By shardColValue
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("index", i), 0, 10);
            assertTrue(null != models);
            assertEquals(i + 1, models.size());

            // By shardColValue
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), 0, 10);
            assertTrue(null != models);
            assertEquals(i + 1, models.size());
        }
    }

    @Test
    public void testQueryFromWithWhereClauseFailedAsyncCallback() throws SQLException {
        String whereClause = "type=? order by id";
        List<ClientTestModel> models = null;
        DalHints hints;

        for (int i = 0; i < mod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 1);

            // By tabelShard
            hints = asyncHints();
            models = dao.queryFrom(whereClause, parameters, hints.inTableShard(i), 0, 10);
            models = assertModels(models, hints);
            assertTrue(null != models);
            assertEquals(i + 1, models.size());

            // By tableShardValue
            hints = callbackHints();
            models = dao.queryFrom(whereClause, parameters, hints.setTableShardValue(i), 0, 10);
            models = assertModels(models, hints);
            assertTrue(null != models);
            assertEquals(i + 1, models.size());

            // By shardColValue
            hints = asyncHints();
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("index", i), 0, 10);
            models = assertModels(models, hints);
            assertTrue(null != models);
            assertEquals(i + 1, models.size());

            // By shardColValue
            hints = callbackHints();
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("tableIndex", i), 0, 10);
            models = assertModels(models, hints);
            assertTrue(null != models);
            assertEquals(i + 1, models.size());
        }
    }

    /**
     * Test Query range of result with where clause when return empty collection
     * 
     * @throws SQLException
     */
    @Test
    public void testQueryFromWithWhereClauseEmpty() throws SQLException {
        String whereClause = "type=? order by id";
        List<ClientTestModel> models = null;
        for (int i = 0; i < mod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 10);

            // By tabelShard
            models = dao.queryFrom(whereClause, parameters, new DalHints().inTableShard(i), 0, 10);
            assertTrue(null != models);
            assertEquals(0, models.size());

            // By tableShardValue
            models = dao.queryFrom(whereClause, parameters, new DalHints().setTableShardValue(i), 0, 10);
            assertTrue(null != models);
            assertEquals(0, models.size());

            // By shardColValue
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("index", i), 0, 10);
            assertTrue(null != models);
            assertEquals(0, models.size());

            // By shardColValue
            models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), 0, 10);
            assertTrue(null != models);
            assertEquals(0, models.size());
        }
    }

    @Test
    public void testQueryFromWithWhereClauseEmptyAsyncCallback() throws SQLException {
        String whereClause = "type=? order by id";
        List<ClientTestModel> models = null;
        DalHints hints;

        for (int i = 0; i < mod; i++) {
            StatementParameters parameters = new StatementParameters();
            parameters.set(1, Types.SMALLINT, 10);

            // By tabelShard
            hints = asyncHints();
            models = dao.queryFrom(whereClause, parameters, hints.inTableShard(i), 0, 10);
            models = assertModels(models, hints);
            assertTrue(null != models);
            assertEquals(0, models.size());

            // By tableShardValue
            hints = callbackHints();
            models = dao.queryFrom(whereClause, parameters, hints.setTableShardValue(i), 0, 10);
            models = assertModels(models, hints);
            assertTrue(null != models);
            assertEquals(0, models.size());

            // By shardColValue
            hints = asyncHints();
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("index", i), 0, 10);
            models = assertModels(models, hints);
            assertTrue(null != models);
            assertEquals(0, models.size());

            // By shardColValue
            hints = callbackHints();
            models = dao.queryFrom(whereClause, parameters, hints.setShardColValue("tableIndex", i), 0, 10);
            models = assertModels(models, hints);
            assertTrue(null != models);
            assertEquals(0, models.size());
        }
    }

    /**
     * Test Insert multiple entities one by one
     * 
     * @throws SQLException
     */
    @Test
    public void testInsertSingle() throws SQLException {
        ClientTestModel model = new ClientTestModel();
        model.setQuantity(10 + 1 % 3);
        model.setType(((Number) (1 % 3)).shortValue());
        model.setAddress("CTRIP");
        int res;
        try {
            res = dao.insert(new DalHints(), model);
            fail();
        } catch (Throwable e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            // By tabelShard
            res = dao.insert(new DalHints().inTableShard(i), model);
            assertEquals((i + 1) + j++ * 1, getCount(i));

            // By tableShardValue
            res = dao.insert(new DalHints().setTableShardValue(i), model);
            assertEquals((i + 1) + j++ * 1, getCount(i));

            // By shardColValue
            res = dao.insert(new DalHints().setShardColValue("index", i), model);
            assertEquals((i + 1) + j++ * 1, getCount(i));

            // By shardColValue
            res = dao.insert(new DalHints().setShardColValue("tableIndex", i), model);
            assertEquals((i + 1) + j++ * 1, getCount(i));

            // By fields
            model.setTableIndex(i);
            res = dao.insert(new DalHints(), model);
            assertEquals((i + 1) + j++ * 1, getCount(i));

            if (!INSERT_PK_BACK_ALLOWED)
                continue;

            // Test insert with keyholder or keyholder is null
            KeyHolder holder = new KeyHolder();
            res = dao.insert(new DalHints().inTableShard(i).setIdentityBack(), holder, model);
            assertEquals((i + 1) + j++ * 1, getCount(i));
            assertNotNull(holder.getKey());
            assertNotNull(model.getId());
            assertEquals(holder.getKey().intValue(), model.getId().intValue());

            // Test insert without keyholder or keyholder is null
            holder = null;
            res = dao.insert(new DalHints().inTableShard(i).setIdentityBack(), holder, model);
            assertEquals((i + 1) + j++ * 1, getCount(i));
            assertNotNull(model.getId());

            // Test insert without keyholder
            res = dao.insert(new DalHints().inTableShard(i).setIdentityBack(), model);
            assertEquals((i + 1) + j++ * 1, getCount(i));
            assertNotNull(model.getId());
        }
    }

    @Test
    public void testInsertSingleAsyncCallback() throws SQLException {
        ClientTestModel model = new ClientTestModel();
        model.setQuantity(10 + 1 % 3);
        model.setType(((Number) (1 % 3)).shortValue());
        model.setAddress("CTRIP");
        int res;
        DalHints hints;

        try {
            res = dao.insert(new DalHints(), model);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            // By tabelShard
            hints = asyncHints();
            res = dao.insert(hints.inTableShard(i), model);
            res = assertInt(res, hints);
            assertEquals((i + 1) + j++ * 1, getCount(i));

            // By tableShardValue
            hints = intHints();
            res = dao.insert(hints.setTableShardValue(i), model);
            res = assertInt(res, hints);
            assertEquals((i + 1) + j++ * 1, getCount(i));

            // By shardColValue
            hints = asyncHints();
            res = dao.insert(hints.setShardColValue("index", i), model);
            res = assertInt(res, hints);
            assertEquals((i + 1) + j++ * 1, getCount(i));

            // By shardColValue
            hints = intHints();
            res = dao.insert(hints.setShardColValue("tableIndex", i), model);
            res = assertInt(res, hints);
            assertEquals((i + 1) + j++ * 1, getCount(i));

            // By fields
            hints = asyncHints();
            model.setTableIndex(i);
            res = dao.insert(hints, model);
            res = assertInt(res, hints);
            assertEquals((i + 1) + j++ * 1, getCount(i));
        }
    }

    private void deleteAllShards() throws SQLException {
        for (int i = 0; i < mod; i++) {
            int j = 1;
            dao.delete("1=1", new StatementParameters(), new DalHints().inTableShard(i));
        }
    }

    /**
     * Test Insert multiple entities one by one
     * 
     * @throws SQLException
     */
    @Test
    public void testInsertMultipleAsList() throws SQLException {
        List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        int[] res;
        try {
            res = dao.insert(new DalHints(), entities);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            // By tabelShard
            res = dao.insert(new DalHints().inTableShard(i), entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By tableShardValue
            res = dao.insert(new DalHints().setTableShardValue(i), entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            res = dao.insert(new DalHints().setShardColValue("index", i), entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            res = dao.insert(new DalHints().setShardColValue("tableIndex", i), entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By fields same shard
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            res = dao.insert(new DalHints(), entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));
        }

        deleteAllShards();

        // By fields not same shard
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        dao.insert(new DalHints().continueOnError(), entities);
        assertEquals(1, getCount(0));
        assertEquals(1, getCount(1));
        assertEquals(1, getCount(2));
    }

    @Test
    public void testInsertMultipleAsListAsyncCallback() throws SQLException {
        List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
        DalHints hints;
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        int[] res;
        try {
            hints = new DalHints().asyncExecution();
            res = dao.insert(hints, entities);
            res = assertIntArray(res, hints);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            // By tabelShard
            hints = asyncHints();
            res = dao.insert(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By tableShardValue
            hints = intHints();
            res = dao.insert(hints.setTableShardValue(i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            hints = asyncHints();
            res = dao.insert(hints.setShardColValue("index", i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            hints = intHints();
            res = dao.insert(hints.setShardColValue("tableIndex", i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By fields same shard
            hints = asyncHints();
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            res = dao.insert(hints, entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));
        }

        deleteAllShards();

        // By fields not same shard
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        hints = intHints();
        res = dao.insert(hints.continueOnError(), entities);
        res = assertIntArray(res, hints);
        assertEquals(1, getCount(0));
        assertEquals(1, getCount(1));
        assertEquals(1, getCount(2));
    }

    private int getCount(int shardId) throws SQLException {
        return dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(shardId)).size();
    }

    /**
     * Test Test Insert multiple entities one by one with continueOnError hints
     * 
     * @throws SQLException
     */
    @Test
    public void testInsertMultipleAsListWithContinueOnErrorHints() throws SQLException {
        List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            if (i == 1) {
                model.setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP" + "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
                        + "CTRIPCTRIPCTRIPCTRIP");
            } else {
                model.setAddress("CTRIP");
            }
            entities.add(model);
        }

        int[] res;
        try {
            res = dao.insert(new DalHints(), entities);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            // By tabelShard
            dao.insert(new DalHints().continueOnError().inTableShard(i), entities);
            assertEquals((i + 1) + j++ * 2, getCount(i));

            // By tableShardValue
            res = dao.insert(new DalHints().continueOnError().setTableShardValue(i), entities);
            assertEquals((i + 1) + j++ * 2, getCount(i));

            // By shardColValue
            res = dao.insert(new DalHints().continueOnError().setShardColValue("index", i), entities);
            assertEquals((i + 1) + j++ * 2, getCount(i));

            // By shardColValue
            res = dao.insert(new DalHints().continueOnError().setShardColValue("tableIndex", i), entities);
            assertEquals((i + 1) + j++ * 2, getCount(i));

            // By fields same shard
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            res = dao.insert(new DalHints().continueOnError(), entities);
            assertEquals((i + 1) + j++ * 2, getCount(i));
        }

        deleteAllShards();

        // By fields not same shard
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        dao.insert(new DalHints().continueOnError(), entities);
        assertEquals(1, getCount(0));
        assertEquals(0, getCount(1));
        assertEquals(1, getCount(2));
    }

    @Test
    public void testInsertMultipleAsListWithContinueOnErrorHintsAsyncCallback() throws SQLException {
        List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
        DalHints hints;

        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            if (i == 1) {
                model.setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP" + "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
                        + "CTRIPCTRIPCTRIPCTRIP");
            } else {
                model.setAddress("CTRIP");
            }
            entities.add(model);
        }

        int[] res;
        try {
            hints = asyncHints();
            res = dao.insert(hints, entities);
            res = assertIntArray(res, hints);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            // By tabelShard
            hints = asyncHints();
            res = dao.insert(hints.continueOnError().inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 2, getCount(i));

            // By tableShardValue
            hints = intHints();
            res = dao.insert(hints.continueOnError().setTableShardValue(i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 2, getCount(i));

            // By shardColValue
            hints = asyncHints();
            res = dao.insert(hints.continueOnError().setShardColValue("index", i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 2, getCount(i));

            // By shardColValue
            hints = intHints();
            res = dao.insert(hints.continueOnError().setShardColValue("tableIndex", i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 2, getCount(i));

            // By fields same shard
            hints = asyncHints();
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            res = dao.insert(hints.continueOnError(), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 2, getCount(i));
        }

        deleteAllShards();

        // By fields not same shard
        hints = intHints();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        res = dao.insert(hints.continueOnError(), entities);
        res = assertIntArray(res, hints);
        assertEquals(1, getCount(0));
        assertEquals(0, getCount(1));
        assertEquals(1, getCount(2));
    }

    /**
     * Test Insert multiple entities with key-holder
     * 
     * @throws SQLException
     */
    @Test
    public void testInsertMultipleAsListWithKeyHolder() throws SQLException {
        List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        KeyHolder holder = new KeyHolder();
        int[] res;
        try {
            res = dao.insert(new DalHints(), holder, entities);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            holder = null;
            // By tabelShard
            // holder = new KeyHolder();
            res = dao.insert(new DalHints().inTableShard(i), holder, entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));
            // assertEquals(3, res);
            // assertEquals(3, holder.getKeyList().size());
            // assertTrue(holder.getKey(0).longValue() > 0);
            // assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));

            // By tableShardValue
            // holder = new KeyHolder();
            res = dao.insert(new DalHints().setTableShardValue(i), holder, entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            // holder = new KeyHolder();
            res = dao.insert(new DalHints().setShardColValue("index", i), holder, entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            // holder = new KeyHolder();
            res = dao.insert(new DalHints().setShardColValue("tableIndex", i), holder, entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By fields same shard
            // holder = new KeyHolder();
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            res = dao.insert(new DalHints(), holder, entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

        }

        deleteAllShards();

        // By fields not same shard
        holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        res = dao.insert(new DalHints(), null, entities);
        assertEquals(1, getCount(0));
        assertEquals(1, getCount(1));
        assertEquals(1, getCount(2));
        // assertEquals(3, res);
        // assertEquals(3, holder.getKeyList().size());
        // assertTrue(holder.getKey(0).longValue() > 0);
        // assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
    }

    @Test
    public void testInsertMultipleAsListWithKeyHolderWithPkInsertBack() throws SQLException {
        if (!INSERT_PK_BACK_ALLOWED)
            return;

        DalTableDao<ClientTestModel> dao =
                new DalTableDao<ClientTestModel>(ClientTestModel.class, databaseName, TABLE_NAME);

        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }


        KeyHolder holder = new KeyHolder();
        for (int i = 0; i < mod; i++) {
            int j = 1;
            IdentitySetBackHelper.clearId(entities);
            dao.insert(new DalHints().inTableShard(i).setIdentityBack(), holder, entities);
            assertEquals(3, holder.size());
            IdentitySetBackHelper.assertIdentityTableShard(dao, entities, i);
        }
        deleteAllShards();

        // Test without keyholder 1
        for (int i = 0; i < mod; i++) {
            int j = 1;
            IdentitySetBackHelper.clearId(entities);
            dao.insert(new DalHints().inTableShard(i).setIdentityBack(), null, entities);
            assertEquals(3, holder.size());
            IdentitySetBackHelper.assertIdentityTableShard(dao, entities, i);
        }

        deleteAllShards();

        // Test without keyholder 2
        for (int i = 0; i < mod; i++) {
            int j = 1;
            IdentitySetBackHelper.clearId(entities);
            dao.insert(new DalHints().inTableShard(i).setIdentityBack(), entities);
            assertEquals(3, holder.size());
            IdentitySetBackHelper.assertIdentityTableShard(dao, entities, i);
        }

        deleteAllShards();
        // By fields not same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        IdentitySetBackHelper.clearId(entities);
        dao.insert(new DalHints().setIdentityBack(), holder, entities);
        assertEquals(3, holder.size());
        assertEquals(1, getCount(0));
        assertEquals(1, getCount(1));
        assertEquals(1, getCount(2));
        IdentitySetBackHelper.assertIdentity(dao, entities);
        dao.insert(new DalHints().setIdentityBack(), holder, entities);
        assertEquals(3, holder.size());
    }

    @Test
    public void testInsertMultipleAsListWithKeyHolderAsyncCallback() throws SQLException {
        DalHints hints;

        List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        KeyHolder holder = new KeyHolder();
        int[] res;
        try {
            hints = asyncHints();
            res = dao.insert(hints, holder, entities);
            res = assertIntArray(res, hints);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            holder = null;
            // By tabelShard
            // holder = new KeyHolder();
            hints = asyncHints();
            res = dao.insert(hints.inTableShard(i), holder, entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));
            // assertEquals(3, res);
            // assertEquals(3, holder.getKeyList().size());
            // assertTrue(holder.getKey(0).longValue() > 0);
            // assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));

            // By tableShardValue
            // holder = new KeyHolder();
            hints = intHints();
            res = dao.insert(hints.setTableShardValue(i), holder, entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            // holder = new KeyHolder();
            hints = asyncHints();
            res = dao.insert(hints.setShardColValue("index", i), holder, entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            // holder = new KeyHolder();
            hints = intHints();
            res = dao.insert(hints.setShardColValue("tableIndex", i), holder, entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By fields same shard
            // holder = new KeyHolder();
            hints = asyncHints();
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            res = dao.insert(hints, holder, entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

        }

        deleteAllShards();

        // By fields not same shard
        holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        hints = intHints();
        res = dao.insert(hints, null, entities);
        res = assertIntArray(res, hints);
        assertEquals(1, getCount(0));
        assertEquals(1, getCount(1));
        assertEquals(1, getCount(2));
        // assertEquals(3, res);
        // assertEquals(3, holder.getKeyList().size());
        // assertTrue(holder.getKey(0).longValue() > 0);
        // assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
    }

    @Test
    public void testInsertMultipleAsListWithKeyHolderAsyncCallbackWithPkInsertBack() throws SQLException {
        if (!INSERT_PK_BACK_ALLOWED)
            return;

        DalHints hints;
        DalTableDao<ClientTestModel> dao =
                new DalTableDao<ClientTestModel>(ClientTestModel.class, databaseName, TABLE_NAME);
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP" + i);
            entities.add(model);
        }

        KeyHolder holder;
        int[] res;

        for (int i = 0; i < mod; i++) {
            int j = 1;
            holder = new KeyHolder();
            // By tabelShard
            // holder = new KeyHolder();
            hints = asyncHints();
            res = dao.insert(hints.inTableShard(i).setIdentityBack(), holder, entities);
            res = assertIntArray(res, hints);
            // for(ClientTestModel model: entities) {
            // assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());
            // }

            // By tableShardValue
            holder = new KeyHolder();
            hints = intHints();
            res = dao.insert(hints.setTableShardValue(i).setIdentityBack(), holder, entities);
            res = assertIntArray(res, hints);
            for (ClientTestModel model : entities) {
                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());
            }
        }

        deleteAllShards();

        // By fields not same shard
        holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        hints = intHints().setIdentityBack();
        res = dao.insert(hints, holder, entities);
        res = assertIntArray(res, hints);
        assertEquals(1, getCount(0));
        assertEquals(1, getCount(1));
        assertEquals(1, getCount(2));
        for (ClientTestModel model : entities) {
            assertEquals(dao.queryByPk(model, new DalHints()).getAddress(), model.getAddress());
        }
    }

    /**
     * Test Insert multiple entities with one SQL Statement
     * 
     * @throws SQLException
     */
    @Test
    public void testCombinedInsert() throws SQLException {
        if (!diff.supportInsertValues)
            return;

        ClientTestModel[] entities = new ClientTestModel[3];
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities[i] = model;
        }

        KeyHolder holder = new KeyHolder();
        int res;
        try {
            res = dao.combinedInsert(new DalHints(), holder, Arrays.asList(entities));
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            holder = null;
            // By tabelShard
            // holder = new KeyHolder();
            res = dao.combinedInsert(new DalHints().inTableShard(i), holder, Arrays.asList(entities));
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By tableShardValue
            // holder = new KeyHolder();
            res = dao.combinedInsert(new DalHints().setTableShardValue(i), holder, Arrays.asList(entities));
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            // holder = new KeyHolder();
            res = dao.combinedInsert(new DalHints().setShardColValue("index", i), holder, Arrays.asList(entities));
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            // holder = new KeyHolder();
            res = dao.combinedInsert(new DalHints().setShardColValue("tableIndex", i), holder, Arrays.asList(entities));
            assertEquals((i + 1) + j++ * 3, getCount(i));
        }

        // For combined insert, the shard id must be defined or change bd deduced.
    }

    @Test
    public void testCombinedInsertWithPkInsertBack() throws SQLException {
        if (!INSERT_PK_BACK_ALLOWED)
            return;

        DalTableDao<ClientTestModel> dao =
                new DalTableDao<ClientTestModel>(ClientTestModel.class, databaseName, TABLE_NAME);

        ClientTestModel[] entities = new ClientTestModel[3];
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP" + i);
            entities[i] = model;
        }

        for (int i = 0; i < mod; i++) {
            KeyHolder holder = new KeyHolder();
            dao.combinedInsert(new DalHints().inTableShard(i).setIdentityBack(), holder, Arrays.asList(entities));

            for (ClientTestModel model : entities) {
                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());
            }
        }

        // Test holder reuse case
        KeyHolder holder = new KeyHolder();
        for (int i = 0; i < mod; i++) {
            dao.combinedInsert(new DalHints().inTableShard(i).setIdentityBack(), holder, Arrays.asList(entities));

            for (ClientTestModel model : entities) {
                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());
            }
        }

        // Test with out kh
        for (int i = 0; i < mod; i++) {
            dao.combinedInsert(new DalHints().inTableShard(i).setIdentityBack(), Arrays.asList(entities));

            for (ClientTestModel model : entities) {
                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());
            }
        }

    }

    @Test
    public void testCombinedInsertAsyncCallback() throws SQLException {
        if (!diff.supportInsertValues)
            return;
        DalHints hints;

        ClientTestModel[] entities = new ClientTestModel[3];
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities[i] = model;
        }

        KeyHolder holder = new KeyHolder();
        int res;
        try {
            hints = asyncHints();
            res = dao.combinedInsert(hints, holder, Arrays.asList(entities));
            res = assertInt(res, hints);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            holder = null;
            // By tabelShard
            // holder = new KeyHolder();
            hints = asyncHints();
            res = dao.combinedInsert(hints.inTableShard(i), holder, Arrays.asList(entities));
            res = assertInt(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By tableShardValue
            // holder = new KeyHolder();
            hints = intHints();
            res = dao.combinedInsert(hints.setTableShardValue(i), holder, Arrays.asList(entities));
            res = assertInt(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            // holder = new KeyHolder();
            hints = asyncHints();
            res = dao.combinedInsert(hints.setShardColValue("index", i), holder, Arrays.asList(entities));
            res = assertInt(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            // holder = new KeyHolder();
            hints = intHints();
            res = dao.combinedInsert(hints.setShardColValue("tableIndex", i), holder, Arrays.asList(entities));
            res = assertInt(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));
        }

        // For combined insert, the shard id must be defined or change bd deduced.
    }

    @Test
    public void testCombinedInsertAsyncCallbackWithPkInsertBack() throws SQLException {
        if (!INSERT_PK_BACK_ALLOWED)
            return;

        DalTableDao<ClientTestModel> dao =
                new DalTableDao<ClientTestModel>(ClientTestModel.class, databaseName, TABLE_NAME);

        ClientTestModel[] entities = new ClientTestModel[3];
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP" + i);
            entities[i] = model;
        }

        int res;

        for (int i = 0; i < mod; i++) {
            int j = 1;
            KeyHolder holder = new KeyHolder();
            // By tabelShard
            // holder = new KeyHolder();
            DalHints hints = asyncHints();
            res = dao.combinedInsert(hints.inTableShard(i).setIdentityBack(), holder, Arrays.asList(entities));
            res = assertInt(res, hints);
            for (ClientTestModel model : entities) {
                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());
            }

            // By tableShardValue
            holder = new KeyHolder();
            hints = intHints();
            res = dao.combinedInsert(hints.setTableShardValue(i).setIdentityBack(), holder, Arrays.asList(entities));
            res = assertInt(res, hints);
            for (ClientTestModel model : entities) {
                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());
            }
        }
    }

    /**
     * Test Batch Insert multiple entities
     * 
     * @throws SQLException
     */
    @Test
    public void testBatchInsert() throws SQLException {
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        int[] res;
        try {
            res = dao.batchInsert(new DalHints(), entities);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            // By tabelShard
            res = dao.batchInsert(new DalHints().inTableShard(i), entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By tableShardValue
            res = dao.batchInsert(new DalHints().setTableShardValue(i), entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            res = dao.batchInsert(new DalHints().setShardColValue("index", i), entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            res = dao.batchInsert(new DalHints().setShardColValue("tableIndex", i), entities);
            assertEquals((i + 1) + j++ * 3, getCount(i));
        }

        // For combined insert, the shard id must be defined or change bd deduced.
    }

    @Test
    public void testBatchInsertAsyncCallback() throws SQLException {
        DalHints hints;
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        int[] res;
        try {
            hints = asyncHints();
            res = dao.batchInsert(hints, entities);
            res = assertIntArray(res, hints);
            fail();
        } catch (Exception e) {
        }

        for (int i = 0; i < mod; i++) {
            int j = 1;
            // By tabelShard
            hints = asyncHints();
            res = dao.batchInsert(hints.inTableShard(i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By tableShardValue
            hints = intHints();
            res = dao.batchInsert(hints.setTableShardValue(i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            hints = asyncHints();
            res = dao.batchInsert(hints.setShardColValue("index", i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));

            // By shardColValue
            hints = intHints();
            res = dao.batchInsert(hints.setShardColValue("tableIndex", i), entities);
            res = assertIntArray(res, hints);
            assertEquals((i + 1) + j++ * 3, getCount(i));
        }

        // For combined insert, the shard id must be defined or change bd deduced.
    }

    /**
     * Test delete multiple entities
     * 
     * @throws SQLException
     */
    @Test
    public void testDeleteMultiple() throws SQLException {
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i + 1);
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        int[] res;
        // By tabelShard
        assertEquals(1, getCount(0));
        res = dao.delete(new DalHints().inTableShard(0), entities);
        assertEquals(0, getCount(0));

        // By tableShardValue
        assertEquals(2, getCount(1));
        res = dao.delete(new DalHints().setTableShardValue(1), entities);
        assertEquals(0, getCount(1));

        // By shardColValue
        assertEquals(3, getCount(2));
        res = dao.delete(new DalHints().setShardColValue("index", 2), entities);
        assertEquals(0, getCount(2));

        // By shardColValue
        assertEquals(4, getCount(3));
        res = dao.delete(new DalHints().setShardColValue("tableIndex", 3), entities);
        assertEquals(1, getCount(3));

        // By fields same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        dao.insert(new DalHints(), entities);
        assertEquals(1, getCount(0));
        assertEquals(1, getCount(1));
        assertEquals(1, getCount(2));
        entities.set(0, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inTableShard(0)));
        entities.set(1, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inTableShard(1)));
        entities.set(2, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inTableShard(2)));
        res = dao.delete(new DalHints(), entities);
        assertEquals(0, getCount(0));
        assertEquals(0, getCount(1));
        assertEquals(0, getCount(2));
    }

    @Test
    public void testDeleteMultipleAsyncCallback() throws SQLException {
        DalHints hints;
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i + 1);
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        int[] res;
        // By tabelShard
        assertEquals(1, getCount(0));
        hints = asyncHints();
        res = dao.delete(hints.inTableShard(0), entities);
        res = assertIntArray(res, hints);
        assertEquals(0, getCount(0));

        // By tableShardValue
        assertEquals(2, getCount(1));
        hints = intHints();
        res = dao.delete(hints.setTableShardValue(1), entities);
        res = assertIntArray(res, hints);
        assertEquals(0, getCount(1));

        // By shardColValue
        assertEquals(3, getCount(2));
        hints = asyncHints();
        res = dao.delete(hints.setShardColValue("index", 2), entities);
        res = assertIntArray(res, hints);
        assertEquals(0, getCount(2));

        // By shardColValue
        assertEquals(4, getCount(3));
        hints = intHints();
        res = dao.delete(hints.setShardColValue("tableIndex", 3), entities);
        res = assertIntArray(res, hints);
        assertEquals(1, getCount(3));

        // By fields same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        dao.insert(new DalHints(), entities);
        assertEquals(1, getCount(0));
        assertEquals(1, getCount(1));
        assertEquals(1, getCount(2));

        entities.set(0, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inTableShard(0)));
        entities.set(1, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inTableShard(1)));
        entities.set(2, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inTableShard(2)));
        hints = intHints();
        res = dao.delete(hints, entities);
        res = assertIntArray(res, hints);
        assertEquals(0, getCount(0));
        assertEquals(0, getCount(1));
        assertEquals(0, getCount(2));
    }

    /**
     * Test batch delete multiple entities
     * 
     * @throws SQLException
     */
    @Test
    public void testBatchDelete() throws SQLException {
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i + 1);
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        int[] res;
        try {
            res = dao.batchDelete(new DalHints(), entities);
            fail();
        } catch (Exception e) {
        }

        // By tabelShard
        assertEquals(1, getCount(0));
        res = dao.batchDelete(new DalHints().inTableShard(0), entities);
        assertEquals(0, getCount(0));

        // By tableShardValue
        assertEquals(2, getCount(1));
        res = dao.batchDelete(new DalHints().setTableShardValue(1), entities);
        assertEquals(0, getCount(1));

        // By shardColValue
        assertEquals(3, getCount(2));
        res = dao.batchDelete(new DalHints().setShardColValue("index", 2), entities);
        assertEquals(0, getCount(2));

        // By shardColValue
        assertEquals(4, getCount(3));
        res = dao.batchDelete(new DalHints().setShardColValue("tableIndex", 3), entities);
        assertEquals(1, getCount(3));

        // By fields same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(0);
        entities.get(2).setTableIndex(0);
        dao.insert(new DalHints(), entities);
        assertEquals(3, getCount(0));
        List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
        res = dao.batchDelete(new DalHints().inTableShard(0), result);
        assertEquals(0, getCount(0));
    }

    @Test
    public void testBatchDeleteAsyncCallback() throws SQLException {
        DalHints hints;
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i + 1);
            model.setQuantity(10 + 1 % 3);
            model.setType(((Number) (1 % 3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        int[] res;
        try {
            hints = asyncHints();
            res = dao.batchDelete(hints, entities);
            res = assertIntArray(res, hints);
            fail();
        } catch (Exception e) {
        }

        // By tabelShard
        assertEquals(1, getCount(0));
        hints = asyncHints();
        res = dao.batchDelete(hints.inTableShard(0), entities);
        res = assertIntArray(res, hints);
        assertEquals(0, getCount(0));

        // By tableShardValue
        assertEquals(2, getCount(1));
        hints = intHints();
        res = dao.batchDelete(hints.setTableShardValue(1), entities);
        res = assertIntArray(res, hints);
        assertEquals(0, getCount(1));

        // By shardColValue
        assertEquals(3, getCount(2));
        hints = asyncHints();
        res = dao.batchDelete(hints.setShardColValue("index", 2), entities);
        res = assertIntArray(res, hints);
        assertEquals(0, getCount(2));

        // By shardColValue
        assertEquals(4, getCount(3));
        hints = intHints();
        res = dao.batchDelete(hints.setShardColValue("tableIndex", 3), entities);
        res = assertIntArray(res, hints);
        assertEquals(1, getCount(3));

        // By fields same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(0);
        entities.get(2).setTableIndex(0);
        dao.insert(new DalHints(), entities);
        assertEquals(3, getCount(0));
        List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
        hints = asyncHints();
        res = dao.batchDelete(hints.inTableShard(0), result);
        res = assertIntArray(res, hints);
        assertEquals(0, getCount(0));
    }

    /**
     * Test update multiple entities with primary key
     * 
     * @throws SQLException
     */
    @Test
    public void testUpdateMultiple() throws SQLException {
        DalHints hints = new DalHints();
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i + 1);
            model.setAddress("CTRIP");
            entities.add(model);
        }


        int[] res;
        try {
            res = dao.update(hints, entities);
            fail();
        } catch (Exception e) {
        }

        // By tabelShard
        entities.get(0).setAddress("test1");
        dao.update(new DalHints().inTableShard(0), entities.get(0));
        assertEquals("test1", dao.queryByPk(1, hints.inTableShard(0)).getAddress());

        // By tableShardValue
        entities.get(1).setQuantity(-11);
        dao.update(new DalHints().setTableShardValue(1), entities.get(1));
        assertEquals(-11, dao.queryByPk(2, hints.inTableShard(1)).getQuantity().intValue());

        // By shardColValue
        entities.get(2).setType((short) 3);
        dao.update(new DalHints().setShardColValue("index", 2), entities.get(2));
        assertEquals((short) 3, dao.queryByPk(3, hints.inTableShard(2)).getType().shortValue());

        // By shardColValue
        entities.get(3).setAddress("testa");
        res = dao.update(new DalHints().setShardColValue("tableIndex", 3), entities);
        assertEquals("testa", dao.queryByPk(4, hints.inTableShard(3)).getAddress());

        // By fields same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(0).setAddress("1234");
        entities.get(1).setTableIndex(0);
        entities.get(1).setAddress("1234");
        entities.get(2).setTableIndex(0);
        entities.get(2).setAddress("1234");
        entities.get(3).setTableIndex(0);
        entities.get(3).setAddress("1234");
        dao.update(new DalHints(), entities);
        List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
        for (ClientTestModel m : result)
            assertEquals("1234", m.getAddress());
    }

    @Test
    public void testUpdateMultipleAsyncCallback() throws SQLException {
        DalHints hints = new DalHints();
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i + 1);
            model.setAddress("CTRIP");
            entities.add(model);
        }


        int[] resx;
        try {
            hints = asyncHints();
            resx = dao.update(hints, entities);
            resx = assertIntArray(resx, hints);
            fail();
        } catch (Exception e) {
        }

        int res;
        // By tabelShard
        entities.get(0).setAddress("test1");
        hints = asyncHints();
        res = dao.update(hints.inTableShard(0), entities.get(0));
        res = assertInt(res, hints);
        assertEquals("test1", dao.queryByPk(1, new DalHints().inTableShard(0)).getAddress());

        // By tableShardValue
        entities.get(1).setQuantity(-11);
        hints = intHints();
        res = dao.update(hints.setTableShardValue(1), entities.get(1));
        res = assertInt(res, hints);
        assertEquals(-11, dao.queryByPk(2, new DalHints().inTableShard(1)).getQuantity().intValue());

        // By shardColValue
        entities.get(2).setType((short) 3);
        hints = asyncHints();
        res = dao.update(hints.setShardColValue("index", 2), entities.get(2));
        res = assertInt(res, hints);
        assertEquals((short) 3, dao.queryByPk(3, new DalHints().inTableShard(2)).getType().shortValue());

        // By shardColValue
        entities.get(3).setAddress("testa");
        hints = intHints();
        resx = dao.update(hints.setShardColValue("tableIndex", 3), entities);
        resx = assertIntArray(resx, hints);
        assertEquals("testa", dao.queryByPk(4, new DalHints().inTableShard(3)).getAddress());

        // By fields same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(0).setAddress("1234");
        entities.get(1).setTableIndex(0);
        entities.get(1).setAddress("1234");
        entities.get(2).setTableIndex(0);
        entities.get(2).setAddress("1234");
        entities.get(3).setTableIndex(0);
        entities.get(3).setAddress("1234");
        hints = asyncHints();
        resx = dao.update(hints, entities);
        resx = assertIntArray(resx, hints);
        List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
        for (ClientTestModel m : result)
            assertEquals("1234", m.getAddress());
    }

    /**
     * Test update multiple entities with primary key
     * 
     * @throws SQLException
     */
    @Test
    public void testBatchUpdate() throws SQLException {
        DalHints hints = new DalHints();
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i + 1);
            model.setAddress("CTRIP");
            entities.add(model);
        }


        int[] res;
        try {
            res = dao.batchUpdate(hints, entities);
            fail();
        } catch (Exception e) {

        }

        // By fields same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(0).setAddress("1234");
        entities.get(1).setTableIndex(0);
        entities.get(1).setAddress("1234");
        entities.get(2).setTableIndex(0);
        entities.get(2).setAddress("1234");
        entities.get(3).setTableIndex(0);
        entities.get(3).setAddress("1234");
        dao.batchUpdate(new DalHints(), entities);
        List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
        for (ClientTestModel m : result)
            assertEquals("1234", m.getAddress());
    }

    @Test
    public void testBatchUpdateAsync() throws SQLException {
        DalHints hints = new DalHints();
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i + 1);
            model.setAddress("CTRIP");
            entities.add(model);
        }


        int[] res;
        try {
            hints = asyncHints();
            res = dao.batchUpdate(hints, entities);
            res = assertIntArray(res, hints);
            fail();
        } catch (Exception e) {

        }

        // By fields same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(0).setAddress("1234");
        entities.get(1).setTableIndex(0);
        entities.get(1).setAddress("1234");
        entities.get(2).setTableIndex(0);
        entities.get(2).setAddress("1234");
        entities.get(3).setTableIndex(0);
        entities.get(3).setAddress("1234");
        hints = asyncHints();
        res = dao.batchUpdate(hints, entities);
        res = assertIntArray(res, hints);
        List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
        for (ClientTestModel m : result)
            assertEquals("1234", m.getAddress());
    }

    @Test
    public void testBatchUpdateCallback() throws SQLException {
        DefaultResultCallback callback = new DefaultResultCallback();
        DalHints hints = new DalHints().callbackWith(callback);

        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i + 1);
            model.setAddress("CTRIP");
            entities.add(model);
        }


        int[] res;
        try {
            res = dao.batchUpdate(hints, entities);
            callback.waitForDone();
            assertTrue(!callback.isSuccess());
        } catch (Exception e) {
            fail();
        }

        // By fields same shard
        // holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(0).setAddress("1234");
        entities.get(1).setTableIndex(0);
        entities.get(1).setAddress("1234");
        entities.get(2).setTableIndex(0);
        entities.get(2).setAddress("1234");
        entities.get(3).setTableIndex(0);
        entities.get(3).setAddress("1234");
        dao.batchUpdate(new DalHints(), entities);
        List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
        for (ClientTestModel m : result)
            assertEquals("1234", m.getAddress());
    }

    /**
     * Test delete entities with where clause and parameters
     * 
     * @throws SQLException
     */
    @Test
    public void testDeleteWithWhereClause() throws SQLException {
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 1);

        DalHints hints = new DalHints();
        int res;
        try {
            res = dao.delete(whereClause, parameters, hints);
            fail();
        } catch (Exception e) {

        }

        // By tabelShard
        res = dao.delete(whereClause, parameters, new DalHints().inTableShard(0));
        assertEquals(0, dao.query(whereClause, parameters, new DalHints().inTableShard(0)).size());

        // By tableShardValue
        assertEquals(2, getCount(1));
        res = dao.delete(whereClause, parameters, new DalHints().setTableShardValue(1));
        assertEquals(0, dao.query(whereClause, parameters, new DalHints().setTableShardValue(1)).size());

        // By shardColValue
        assertEquals(3, getCount(2));
        res = dao.delete(whereClause, parameters, new DalHints().setShardColValue("index", 2));
        assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardColValue("index", 2)).size());

        // By shardColValue
        assertEquals(4, getCount(3));
        res = dao.delete(whereClause, parameters, new DalHints().setShardColValue("tableIndex", 3));
        assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardColValue("tableIndex", 3)).size());
    }

    @Test
    public void testDeleteWithWhereClauseAsyncCallback() throws SQLException {
        DalHints hints;
        String whereClause = "type=?";
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.SMALLINT, 1);

        int res;
        try {
            hints = asyncHints();
            res = dao.delete(whereClause, parameters, hints);
            res = assertInt(res, hints);
            fail();
        } catch (Exception e) {

        }

        // By tabelShard
        hints = asyncHints();
        res = dao.delete(whereClause, parameters, hints.inTableShard(0));
        res = assertInt(res, hints);
        assertEquals(0, dao.query(whereClause, parameters, new DalHints().inTableShard(0)).size());

        // By tableShardValue
        assertEquals(2, getCount(1));
        hints = intHints();
        res = dao.delete(whereClause, parameters, hints.setTableShardValue(1));
        res = assertInt(res, hints);
        assertEquals(0, dao.query(whereClause, parameters, new DalHints().setTableShardValue(1)).size());

        // By shardColValue
        assertEquals(3, getCount(2));
        hints = asyncHints();
        res = dao.delete(whereClause, parameters, hints.setShardColValue("index", 2));
        res = assertInt(res, hints);
        assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardColValue("index", 2)).size());

        // By shardColValue
        assertEquals(4, getCount(3));
        hints = intHints();
        res = dao.delete(whereClause, parameters, hints.setShardColValue("tableIndex", 3));
        res = assertInt(res, hints);
        assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardColValue("tableIndex", 3)).size());
    }

    /**
     * Test plain update with SQL
     * 
     * @throws SQLException
     */
    @Test
    public void testUpdatePlain() throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET address = 'CTRIP' WHERE id = 1";
        StatementParameters parameters = new StatementParameters();
        DalHints hints = new DalHints();
        int res;
        try {
            res = dao.update(sql, parameters, hints);
            fail();
        } catch (Exception e) {

        }

        // By tabelShard
        UpdateSqlBuilder usb = new UpdateSqlBuilder(TABLE_NAME, dao.getDatabaseCategory());
        usb.update("address", "CTRIP", Types.VARCHAR);
        usb.equal("id", "1", Types.INTEGER);
        res = dao.update(usb, new DalHints().inTableShard(0));
        assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inTableShard(0)).getAddress());

        // By tableShardValue
        assertEquals(2, getCount(1));
        res = dao.update(usb, new DalHints().setTableShardValue(1));
        assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setTableShardValue(1)).getAddress());

        // By shardColValue
        assertEquals(3, getCount(2));
        res = dao.update(usb, new DalHints().setShardColValue("index", 2));
        assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("index", 2)).getAddress());

        // By shardColValue
        assertEquals(4, getCount(3));
        res = dao.update(usb, new DalHints().setShardColValue("tableIndex", 3));
        assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("tableIndex", 3)).getAddress());

    }

    @Test
    public void testUpdatePlainAsyncCallback() throws SQLException {
        try {
            DalClientFactory.initClientFactory();
        } catch (Exception e1) {
            fail();
        }
        String sql = "UPDATE " + TABLE_NAME + " SET address = 'CTRIP' WHERE id = 1";
        StatementParameters parameters = new StatementParameters();
        DalHints hints;
        int res;
        try {
            hints = asyncHints();
            res = dao.update(sql, parameters, hints);
            res = assertInt(res, hints);
            fail();
        } catch (Exception e) {

        }

        // By tabelShard
        UpdateSqlBuilder usb = new UpdateSqlBuilder(TABLE_NAME, dao.getDatabaseCategory());
        usb.update("address", "CTRIP", Types.VARCHAR);
        usb.equal("id", "1", Types.INTEGER);
        hints = asyncHints();
        res = dao.update(usb, hints.inTableShard(0));
        res = assertInt(res, hints);
        assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inTableShard(0)).getAddress());

        // By tableShardValue
        assertEquals(2, getCount(1));
        hints = intHints();
        res = dao.update(usb, hints.setTableShardValue(1));
        res = assertInt(res, hints);
        assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setTableShardValue(1)).getAddress());

        // By shardColValue
        assertEquals(3, getCount(2));
        hints = asyncHints();
        res = dao.update(usb, hints.setShardColValue("index", 2));
        res = assertInt(res, hints);
        assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("index", 2)).getAddress());

        // By shardColValue
        assertEquals(4, getCount(3));
        hints = intHints();
        res = dao.update(usb, hints.setShardColValue("tableIndex", 3));
        res = assertInt(res, hints);
        assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("tableIndex", 3)).getAddress());

    }

    @Test
    public void testCrossShardInsert() {
        if (!diff.supportInsertValues)
            return;
        try {
            deleteAllShards();

            ClientTestModel p = new ClientTestModel();

            ClientTestModel[] pList = new ClientTestModel[6];
            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(0);
            pList[0] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(1);
            pList[1] = p;

            p = new ClientTestModel();
            p.setId(3);
            p.setAddress("aaa");
            p.setTableIndex(2);
            pList[2] = p;

            p = new ClientTestModel();
            p.setId(4);
            p.setAddress("aaa");
            p.setTableIndex(3);
            pList[3] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(4);
            pList[4] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(5);
            pList[5] = p;

            KeyHolder keyHolder = null;// new KeyHolder();
            dao.combinedInsert(new DalHints(), keyHolder, Arrays.asList(pList));
            assertEquals(2, getCount(0));
            assertEquals(2, getCount(1));
            assertEquals(1, getCount(2));
            assertEquals(1, getCount(3));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCrossShardInsertAsync() {
        if (!diff.supportInsertValues)
            return;
        try {
            deleteAllShards();

            ClientTestModel p = new ClientTestModel();

            ClientTestModel[] pList = new ClientTestModel[6];
            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(0);
            pList[0] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(1);
            pList[1] = p;

            p = new ClientTestModel();
            p.setId(3);
            p.setAddress("aaa");
            p.setTableIndex(2);
            pList[2] = p;

            p = new ClientTestModel();
            p.setId(4);
            p.setAddress("aaa");
            p.setTableIndex(3);
            pList[3] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(4);
            pList[4] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(5);
            pList[5] = p;

            KeyHolder keyHolder = null;// new KeyHolder();
            DalHints hints = asyncHints();
            int res = dao.combinedInsert(hints, keyHolder, Arrays.asList(pList));
            res = assertInt(res, hints);
            assertEquals(2, getCount(0));
            assertEquals(2, getCount(1));
            assertEquals(1, getCount(2));
            assertEquals(1, getCount(3));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCrossShardInsertCallback() {
        if (!diff.supportInsertValues)
            return;
        try {
            deleteAllShards();

            ClientTestModel p = new ClientTestModel();

            ClientTestModel[] pList = new ClientTestModel[6];
            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(0);
            pList[0] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(1);
            pList[1] = p;

            p = new ClientTestModel();
            p.setId(3);
            p.setAddress("aaa");
            p.setTableIndex(2);
            pList[2] = p;

            p = new ClientTestModel();
            p.setId(4);
            p.setAddress("aaa");
            p.setTableIndex(3);
            pList[3] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(4);
            pList[4] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(5);
            pList[5] = p;

            KeyHolder keyHolder = null;// new KeyHolder();
            DalHints hints = intHints();
            int res = dao.combinedInsert(hints, keyHolder, Arrays.asList(pList));
            res = assertInt(res, hints);
            assertEquals(2, getCount(0));
            assertEquals(2, getCount(1));
            assertEquals(1, getCount(2));
            assertEquals(1, getCount(3));
        } catch (Exception e) {

            fail();
        }
    }

    @Test
    public void testCrossShardBatchInsert() {
        try {
            deleteAllShards();

            ClientTestModel p = new ClientTestModel();

            ClientTestModel[] pList = new ClientTestModel[6];
            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(0);
            pList[0] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(1);
            pList[1] = p;

            p = new ClientTestModel();
            p.setId(3);
            p.setAddress("aaa");
            p.setTableIndex(2);
            pList[2] = p;

            p = new ClientTestModel();
            p.setId(4);
            p.setAddress("aaa");
            p.setTableIndex(3);
            pList[3] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(4);
            pList[4] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(5);
            pList[5] = p;

            dao.batchInsert(new DalHints(), Arrays.asList(pList));
            assertEquals(2, getCount(0));
            assertEquals(2, getCount(1));
            assertEquals(1, getCount(2));
            assertEquals(1, getCount(3));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCrossShardBatchInsertAsync() {
        try {
            deleteAllShards();

            ClientTestModel p = new ClientTestModel();

            ClientTestModel[] pList = new ClientTestModel[6];
            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(0);
            pList[0] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(1);
            pList[1] = p;

            p = new ClientTestModel();
            p.setId(3);
            p.setAddress("aaa");
            p.setTableIndex(2);
            pList[2] = p;

            p = new ClientTestModel();
            p.setId(4);
            p.setAddress("aaa");
            p.setTableIndex(3);
            pList[3] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(4);
            pList[4] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(5);
            pList[5] = p;

            DalHints hints = asyncHints();
            int[] res = dao.batchInsert(hints, Arrays.asList(pList));
            assertIntArray(res, hints);
            assertEquals(2, getCount(0));
            assertEquals(2, getCount(1));
            assertEquals(1, getCount(2));
            assertEquals(1, getCount(3));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCrossShardBatchInsertCallback() {
        try {
            deleteAllShards();

            ClientTestModel p = new ClientTestModel();

            ClientTestModel[] pList = new ClientTestModel[6];
            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(0);
            pList[0] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(1);
            pList[1] = p;

            p = new ClientTestModel();
            p.setId(3);
            p.setAddress("aaa");
            p.setTableIndex(2);
            pList[2] = p;

            p = new ClientTestModel();
            p.setId(4);
            p.setAddress("aaa");
            p.setTableIndex(3);
            pList[3] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(4);
            pList[4] = p;

            p = new ClientTestModel();
            p.setId(5);
            p.setAddress("aaa");
            p.setTableIndex(5);
            pList[5] = p;

            DalHints hints = intHints();
            int[] res = dao.batchInsert(hints, Arrays.asList(pList));
            assertIntArray(res, hints);
            assertEquals(2, getCount(0));
            assertEquals(2, getCount(1));
            assertEquals(1, getCount(2));
            assertEquals(1, getCount(3));
        } catch (Exception e) {

            fail();
        }
    }

    @Test
    public void testCrossShardDelete() {
        try {
            ClientTestModel p = new ClientTestModel();

            ClientTestModel[] pList = new ClientTestModel[6];
            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(0);
            pList[0] = p;

            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(1);
            pList[1] = p;

            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(2);
            pList[2] = p;

            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(3);
            pList[3] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(5);
            pList[4] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(6);
            pList[5] = p;

            dao.batchDelete(new DalHints(), Arrays.asList(pList));
            assertEquals(0, getCount(0));
            assertEquals(0, getCount(1));
            assertEquals(1, getCount(2));
            assertEquals(3, getCount(3));

        } catch (Exception e) {

            fail();
        }
    }

    @Test
    public void testCrossShardDeleteAsync() {
        try {
            ClientTestModel p = new ClientTestModel();

            ClientTestModel[] pList = new ClientTestModel[6];
            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(0);
            pList[0] = p;

            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(1);
            pList[1] = p;

            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(2);
            pList[2] = p;

            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(3);
            pList[3] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(5);
            pList[4] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(6);
            pList[5] = p;

            DalHints hints = asyncHints();
            int[] res = dao.batchDelete(hints, Arrays.asList(pList));
            res = assertIntArray(res, hints);
            assertEquals(0, getCount(0));
            assertEquals(0, getCount(1));
            assertEquals(1, getCount(2));
            assertEquals(3, getCount(3));

        } catch (Exception e) {

            fail();
        }
    }

    @Test
    public void testCrossShardDeleteCallback() {
        try {
            ClientTestModel p = new ClientTestModel();

            ClientTestModel[] pList = new ClientTestModel[6];
            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(0);
            pList[0] = p;

            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(1);
            pList[1] = p;

            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(2);
            pList[2] = p;

            p = new ClientTestModel();
            p.setId(1);
            p.setAddress("aaa");
            p.setTableIndex(3);
            pList[3] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(5);
            pList[4] = p;

            p = new ClientTestModel();
            p.setId(2);
            p.setAddress("aaa");
            p.setTableIndex(6);
            pList[5] = p;

            DalHints hints = intHints();
            int[] res = dao.batchDelete(hints, Arrays.asList(pList));
            res = assertIntArray(res, hints);
            assertEquals(0, getCount(0));
            assertEquals(0, getCount(1));
            assertEquals(1, getCount(2));
            assertEquals(3, getCount(3));

        } catch (Exception e) {

            fail();
        }
    }


    // region select
    @Test
    public void testQueryByPkInAllTableShards() throws SQLException {
        try {
            ClientTestModel model = dao.queryByPk(1, new DalHints().inAllTableShards());
            Assert.fail();
        } catch (Exception e) {
            assertEquals("It is expected to return only 1 result. But the actually count is more than 1",
                    e.getMessage());
        }
    }

    @Test
    public void testQueryByPkInTableShard0() throws SQLException {
        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");

        // query id = 1 in table shard 0, 1 record expected
        try {
            ClientTestModel model = dao.queryByPk(1, new DalHints().inTableShards(tableShards));
            assertEquals(1, model.getId().intValue());
            assertEquals(0, model.getTableIndex().intValue());
        } catch (Exception e) {
            Assert.fail();
        }

        // query id = 2 in table shard 0, 0 record expected
        try {
            ClientTestModel model = dao.queryByPk(2, new DalHints().inTableShards(tableShards));
            assertEquals(null, model);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryListInAllTableShards() throws SQLException {
        // expected 4 records
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" id = 1 ");

        List<ClientTestModel> list1 = dao.query(ssb1, new DalHints().inAllTableShards());
        assertEquals(4, list1.size());

        // expected 3 records
        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" id = 2 ");

        List<ClientTestModel> list2 = dao.query(ssb2, new DalHints().inAllTableShards());
        assertEquals(3, list2.size());

        // expected 2 records
        SelectSqlBuilder ssb3 = new SelectSqlBuilder();
        ssb3.selectAll().from(TABLE_NAME).where(" id = 3 ");

        List<ClientTestModel> list3 = dao.query(ssb3, new DalHints().inAllTableShards());
        assertEquals(2, list3.size());

        // expected 1 record
        SelectSqlBuilder ssb4 = new SelectSqlBuilder();
        ssb4.selectAll().from(TABLE_NAME).where(" id = 4 ");

        List<ClientTestModel> list4 = dao.query(ssb4, new DalHints().inAllTableShards());
        assertEquals(1, list4.size());

    }

    @Test
    public void testQueryListInTableShards() throws SQLException {
        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");

        // expected 1 record
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" id = 1 ");
        List<ClientTestModel> list1 = dao.query(ssb1, new DalHints().inTableShards(tableShards));
        assertEquals(1, list1.size());

        // expected 2 records
        tableShards.add("1");
        List<ClientTestModel> list2 = dao.query(ssb1, new DalHints().inTableShards(tableShards));
        assertEquals(2, list2.size());

        // expected 3 records
        tableShards.add("2");
        List<ClientTestModel> list3 = dao.query(ssb1, new DalHints().inTableShards(tableShards));
        assertEquals(3, list3.size());

        // expected 4 records
        tableShards.add("3");
        List<ClientTestModel> list4 = dao.query(ssb1, new DalHints().inTableShards(tableShards));
        assertEquals(4, list4.size());

    }

    @Test
    public void testQueryListTableShardBy() throws SQLException {
        int index = 1;

        // expected 1 record
        List<String> list1 = new ArrayList<>();
        list1.add("0");
        StatementParameters parameters1 = new StatementParameters();
        parameters1.setInParameter(index, "tableIndex", Types.INTEGER, list1);

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters1);
        List<ClientTestModel> result1 = dao.query(ssb1, new DalHints().tableShardBy("tableIndex"));
        assertEquals(1, result1.size());

        // expected 3 record
        List<String> list2 = new ArrayList<>();
        list2.add("0");
        list2.add("1");
        StatementParameters parameters2 = new StatementParameters();
        parameters2.setInParameter(index, "tableIndex", Types.INTEGER, list2);

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters2);
        List<ClientTestModel> result2 = dao.query(ssb2, new DalHints().tableShardBy("tableIndex"));
        assertEquals(3, result2.size());

        // expected 6 records
        List<String> list3 = new ArrayList<>();
        list3.add("0");
        list3.add("1");
        list3.add("2");
        StatementParameters parameters3 = new StatementParameters();
        parameters3.setInParameter(index, "tableIndex", Types.INTEGER, list3);

        SelectSqlBuilder ssb3 = new SelectSqlBuilder();
        ssb3.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters3);
        List<ClientTestModel> result3 = dao.query(ssb3, new DalHints().tableShardBy("tableIndex"));
        assertEquals(6, result3.size());

        // expected 10 records
        List<String> list4 = new ArrayList<>();
        list4.add("0");
        list4.add("1");
        list4.add("2");
        list4.add("3");
        StatementParameters parameters4 = new StatementParameters();
        parameters4.setInParameter(index, "tableIndex", Types.INTEGER, list4);

        SelectSqlBuilder ssb4 = new SelectSqlBuilder();
        ssb4.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters4);
        List<ClientTestModel> result4 = dao.query(ssb4, new DalHints().tableShardBy("tableIndex"));
        assertEquals(10, result4.size());

    }

    @Test
    public void testQueryListBySimpleTypeInAllTableShards() throws SQLException {
        // expected 4 records
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.select("id").from(TABLE_NAME).where(" id = 1 ");

        List<Integer> list1 = dao.query(ssb1, new DalHints().inAllTableShards(), Integer.class);
        assertEquals(4, list1.size());

        // expected 3 records
        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.select("id").from(TABLE_NAME).where(" id = 2 ");

        List<Integer> list2 = dao.query(ssb2, new DalHints().inAllTableShards(), Integer.class);
        assertEquals(3, list2.size());

        // expected 2 records
        SelectSqlBuilder ssb3 = new SelectSqlBuilder();
        ssb3.select("id").from(TABLE_NAME).where(" id = 3 ");

        List<Integer> list3 = dao.query(ssb3, new DalHints().inAllTableShards(), Integer.class);
        assertEquals(2, list3.size());

        // expected 1 record
        SelectSqlBuilder ssb4 = new SelectSqlBuilder();
        ssb4.select("id").from(TABLE_NAME).where(" id = 4 ");

        List<Integer> list4 = dao.query(ssb4, new DalHints().inAllTableShards(), Integer.class);
        assertEquals(1, list4.size());
    }

    @Test
    public void testQueryListBySimpleTypeInTableShards() throws SQLException {
        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");

        // expected 1 record
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" id = 1 ");
        List<Integer> list1 = dao.query(ssb1, new DalHints().inTableShards(tableShards), Integer.class);
        assertEquals(1, list1.size());

        // expected 2 records
        tableShards.add("1");
        List<Integer> list2 = dao.query(ssb1, new DalHints().inTableShards(tableShards), Integer.class);
        assertEquals(2, list2.size());

        // expected 3 records
        tableShards.add("2");
        List<Integer> list3 = dao.query(ssb1, new DalHints().inTableShards(tableShards), Integer.class);
        assertEquals(3, list3.size());

        // expected 4 records
        tableShards.add("3");
        List<Integer> list4 = dao.query(ssb1, new DalHints().inTableShards(tableShards), Integer.class);
        assertEquals(4, list4.size());
    }

    @Test
    public void testQueryListBySimpleTypeTableShardBy() throws SQLException {
        int index = 1;

        // expected 1 record
        List<String> list1 = new ArrayList<>();
        list1.add("0");
        StatementParameters parameters1 = new StatementParameters();
        parameters1.setInParameter(index, "tableIndex", Types.INTEGER, list1);

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters1);
        List<Integer> result1 = dao.query(ssb1, new DalHints().tableShardBy("tableIndex"), Integer.class);
        assertEquals(1, result1.size());

        // expected 3 record
        List<String> list2 = new ArrayList<>();
        list2.add("0");
        list2.add("1");
        StatementParameters parameters2 = new StatementParameters();
        parameters2.setInParameter(index, "tableIndex", Types.INTEGER, list2);

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters2);
        List<Integer> result2 = dao.query(ssb2, new DalHints().tableShardBy("tableIndex"), Integer.class);
        assertEquals(3, result2.size());

        // expected 6 records
        List<String> list3 = new ArrayList<>();
        list3.add("0");
        list3.add("1");
        list3.add("2");
        StatementParameters parameters3 = new StatementParameters();
        parameters3.setInParameter(index, "tableIndex", Types.INTEGER, list3);

        SelectSqlBuilder ssb3 = new SelectSqlBuilder();
        ssb3.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters3);
        List<Integer> result3 = dao.query(ssb3, new DalHints().tableShardBy("tableIndex"), Integer.class);
        assertEquals(6, result3.size());

        // expected 10 records
        List<String> list4 = new ArrayList<>();
        list4.add("0");
        list4.add("1");
        list4.add("2");
        list4.add("3");
        StatementParameters parameters4 = new StatementParameters();
        parameters4.setInParameter(index, "tableIndex", Types.INTEGER, list4);

        SelectSqlBuilder ssb4 = new SelectSqlBuilder();
        ssb4.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters4);
        List<Integer> result4 = dao.query(ssb4, new DalHints().tableShardBy("tableIndex"), Integer.class);
        assertEquals(10, result4.size());
    }

    @Test
    public void testQueryObjectInAllTableShards() throws SQLException {
        // requireSingle result in exception
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" id = 1 ").requireSingle();

        try {
            ClientTestModel model = dao.queryObject(ssb1, new DalHints().inAllTableShards());
            Assert.fail();
        } catch (Exception e) {
            assertEquals("It is expected to return only 1 result. But the actually count is more than 1",
                    e.getMessage());
        }

        // requireFirst returns first result,which is not recommended when used in inAllTableShards
        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" id = 1 ").requireFirst();

        try {
            ClientTestModel model = dao.queryObject(ssb2, new DalHints().inAllTableShards());
            assertTrue(model != null);
        } catch (Exception e) {
            Assert.fail();
        }

    }

    @Test
    public void testQueryObjectInTableShards() throws SQLException {
        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("1");

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" id = 1 ").requireSingle();

        try {
            ClientTestModel model = dao.queryObject(ssb1, new DalHints().inTableShards(tableShards));
        } catch (Exception e) {
            assertEquals("It is expected to return only 1 result. But the actually count is more than 1",
                    e.getMessage());
        }

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" id = 1 ").requireFirst();

        try {
            ClientTestModel model = dao.queryObject(ssb2, new DalHints().inTableShards(tableShards));
            assertTrue(model != null);
        } catch (Exception e) {
            Assert.fail();
        }

    }

    @Test
    public void testQueryObjectTableShardBy() throws SQLException {
        int index = 1;

        List<String> list = new ArrayList<>();
        list.add("0");
        list.add("1");
        StatementParameters parameters = new StatementParameters();
        parameters.setInParameter(index, "tableIndex", Types.INTEGER, list);

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters).requireSingle();

        try {
            ClientTestModel model1 = dao.queryObject(ssb1, new DalHints().tableShardBy("tableIndex"));
        } catch (Exception e) {
            assertEquals("It is expected to return only 1 result. But the actually count is more than 1",
                    e.getMessage());
        }

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters).requireFirst();

        try {
            ClientTestModel model2 = dao.queryObject(ssb2, new DalHints().tableShardBy("tableIndex"));
            assertTrue(model2 != null);
        } catch (Exception e) {
            Assert.fail();
        }

    }

    @Test
    public void testQueryObjectBySimpleTypeInAllTableShards() throws SQLException {
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.select("id").from(TABLE_NAME).where(" id = 1 ").requireSingle();

        try {
            Integer id1 = dao.queryObject(ssb1, new DalHints().inAllTableShards(), Integer.class);
            Assert.fail();
        } catch (Exception e) {
            assertEquals("It is expected to return only 1 result. But the actually count is more than 1",
                    e.getMessage());
        }

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.select("id").from(TABLE_NAME).where(" id = 1 ").requireFirst();

        try {
            Integer id2 = dao.queryObject(ssb2, new DalHints().inAllTableShards(), Integer.class);
            assertTrue(id2 != null);
        } catch (Exception e) {
            Assert.fail();
        }

    }

    @Test
    public void testQueryObjectBySimpleTypeInTableShards() throws SQLException {
        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("1");

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.select("id").from(TABLE_NAME).where(" id = 1 ").requireSingle();

        try {
            Integer id1 = dao.queryObject(ssb1, new DalHints().inTableShards(tableShards), Integer.class);
        } catch (Exception e) {
            assertEquals("It is expected to return only 1 result. But the actually count is more than 1",
                    e.getMessage());
        }

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.select("id").from(TABLE_NAME).where(" id = 1 ").requireFirst();

        try {
            Integer id2 = dao.queryObject(ssb2, new DalHints().inTableShards(tableShards), Integer.class);
            assertTrue(id2 != null);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryObjectBySimpleTypeTableShardBy() throws SQLException {
        int index = 1;

        List<String> list = new ArrayList<>();
        list.add("0");
        list.add("1");
        StatementParameters parameters = new StatementParameters();
        parameters.setInParameter(index, "tableIndex", Types.INTEGER, list);

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.select("id").from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters).requireSingle();

        try {
            Integer id1 = dao.queryObject(ssb1, new DalHints().tableShardBy("tableIndex"), Integer.class);
        } catch (Exception e) {
            assertEquals("It is expected to return only 1 result. But the actually count is more than 1",
                    e.getMessage());
        }

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.select("id").from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters).requireFirst();

        try {
            Integer id2 = dao.queryObject(ssb2, new DalHints().tableShardBy("tableIndex"), Integer.class);
            assertTrue(id2 != null);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCountInAllTableShards() throws SQLException {
        // select all records
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).selectCount();

        Number count1 = dao.count(ssb1, new DalHints().inAllTableShards());
        assertEquals(10L, count1);

        // filter condition
        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" id = 1 ").selectCount();

        Number count2 = dao.count(ssb2, new DalHints().inAllTableShards());
        assertEquals(4L, count2);
    }

    @Test
    public void testCountInTableShards() throws SQLException {
        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("1");

        // select all records
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).selectCount();

        Number count1 = dao.count(ssb1, new DalHints().inTableShards(tableShards));
        assertEquals(3L, count1);

        // filter condition
        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" id = 1 ").selectCount();

        Number count2 = dao.count(ssb2, new DalHints().inTableShards(tableShards));
        assertEquals(2L, count2);

    }

    @Test
    public void testCountTableShardBy() throws SQLException {
        int index = 1;
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);

        StatementParameters parameters = new StatementParameters();
        parameters.setInParameter(index, "tableIndex", Types.BIGINT, list);

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters).selectCount();

        Number count1 = dao.count(ssb1, new DalHints().tableShardBy("tableIndex"));

        assertEquals(3L, count1);

    }

    @Test
    public void testCountTableShardBy2() throws SQLException {
        int index = 1;
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);

        StatementParameters parameters = new StatementParameters();
        parameters.setInParameter(index, "tableIndex", Types.BIGINT, list);

        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME).where(" tableIndex in (?) ").with(parameters).selectCount();

        Number count1 = dao.count(ssb1, new DalHints().tableShardBy("tableIndex"));

        assertEquals(3L, count1);

    }

    @Test
    public void testImplicitQueryListInAllTableShards() throws SQLException {
        SelectSqlBuilder ssb1 = new SelectSqlBuilder();
        ssb1.selectAll().from(TABLE_NAME);

        try {
            List<ClientTestModel> list1 = dao.query(ssb1, new DalHints().inAllShards());
            assertEquals(10, list1.size());
        } catch (Exception e) {
            Assert.fail();
        }

        SelectSqlBuilder ssb2 = new SelectSqlBuilder();
        ssb2.selectAll().from(TABLE_NAME).where(" id = 1 ");

        try {
            List<ClientTestModel> list2 = dao.query(ssb2, new DalHints().inAllShards());
            assertEquals(4, list2.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    // endregion

    // region update
    @Test
    public void testUpdateInAllTableShards() throws SQLException {
        UpdateSqlBuilder usb = new UpdateSqlBuilder(TABLE_NAME, dao.getDatabaseCategory());
        usb.update("address", "AllTableShards", Types.VARCHAR);
        usb.equal("id", "1", Types.INTEGER);
        try {
            dao.update(usb, new DalHints().inAllTableShards());
        } catch (Exception e) {
            Assert.fail();
        }

        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        ClientTestModel model0 = dao.queryByPk(1, new DalHints().inTableShards(shard0));
        assertEquals("AllTableShards", model0.getAddress());
        assertEquals(0, model0.getTableIndex().intValue());

        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        ClientTestModel model1 = dao.queryByPk(1, new DalHints().inTableShards(shard1));
        assertEquals("AllTableShards", model1.getAddress());
        assertEquals(1, model1.getTableIndex().intValue());

        Set<String> shard2 = new HashSet<>();
        shard2.add("2");
        ClientTestModel model2 = dao.queryByPk(1, new DalHints().inTableShards(shard2));
        assertEquals("AllTableShards", model2.getAddress());
        assertEquals(2, model2.getTableIndex().intValue());

        Set<String> shard3 = new HashSet<>();
        shard3.add("3");
        ClientTestModel model3 = dao.queryByPk(1, new DalHints().inTableShards(shard3));
        assertEquals("AllTableShards", model3.getAddress());
        assertEquals(3, model3.getTableIndex().intValue());

    }

    @Test
    public void testUpdateInTableShards() throws SQLException {
        UpdateSqlBuilder usb = new UpdateSqlBuilder(TABLE_NAME, dao.getDatabaseCategory());
        usb.update("address", "InTableShards", Types.VARCHAR);
        usb.equal("id", "1", Types.INTEGER);
        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("2");
        try {
            dao.update(usb, new DalHints().inTableShards(tableShards));
        } catch (Exception e) {
            Assert.fail();
        }

        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        ClientTestModel model0 = dao.queryByPk(1, new DalHints().inTableShards(shard0));
        assertEquals("InTableShards", model0.getAddress());
        assertEquals(0, model0.getTableIndex().intValue());

        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        ClientTestModel model1 = dao.queryByPk(1, new DalHints().inTableShards(shard1));
        assertNotEquals("InTableShards", model1.getAddress());
        assertEquals(1, model1.getTableIndex().intValue());

        Set<String> shard2 = new HashSet<>();
        shard2.add("2");
        ClientTestModel model2 = dao.queryByPk(1, new DalHints().inTableShards(shard2));
        assertEquals("InTableShards", model2.getAddress());
        assertEquals(2, model2.getTableIndex().intValue());

        Set<String> shard3 = new HashSet<>();
        shard3.add("3");
        ClientTestModel model3 = dao.queryByPk(1, new DalHints().inTableShards(shard3));
        assertNotEquals("InTableShards", model3.getAddress());
        assertEquals(3, model3.getTableIndex().intValue());

    }

    @Test
    public void testUpdateTableShardBy() throws SQLException {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);

        UpdateSqlBuilder usb = new UpdateSqlBuilder();
        usb.from(TABLE_NAME);
        usb.update("address", "TableShardBy", Types.VARCHAR);
        usb.in("id", list, Types.INTEGER);

        try {
            dao.update(usb, new DalHints().tableShardBy("id"));
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail();
        }

        ClientTestModel model2 = dao.queryByPk(2, new DalHints().inTableShard("2"));
        Assert.assertEquals(2, model2.getId().intValue());
        Assert.assertEquals(11, model2.getQuantity().intValue());
        Assert.assertEquals(1, model2.getDbIndex().intValue());
        Assert.assertEquals(2, model2.getTableIndex().intValue());
        Assert.assertEquals(1, model2.getType().shortValue());
        Assert.assertEquals("TableShardBy", model2.getAddress());

        ClientTestModel model3 = dao.queryByPk(3, new DalHints().inTableShard("3"));
        Assert.assertEquals(3, model3.getId().intValue());
        Assert.assertEquals(12, model3.getQuantity().intValue());
        Assert.assertEquals(1, model3.getDbIndex().intValue());
        Assert.assertEquals(3, model3.getTableIndex().intValue());
        Assert.assertEquals(1, model3.getType().shortValue());
        Assert.assertEquals("TableShardBy", model3.getAddress());

    }

    // endregion

    // region insert
    @Test
    public void testInsertInAllTableShards() throws SQLException {
        try {
            insertInAllTableShards();
        } catch (Exception e) {
            Assert.fail();
        }

        ClientTestModel pojo = new ClientTestModel();
        pojo.setAddress("InsertInAllTableShards");
        queryInAllTableShards(pojo);
    }

    private void insertInAllTableShards() throws SQLException {
        InsertSqlBuilder isb = new InsertSqlBuilder();
        isb.setDatabaseCategory(dao.getDatabaseCategory());
        isb.from(TABLE_NAME);
        isb.set("quantity", "0", Types.INTEGER);
        isb.set("dbIndex", "1", Types.INTEGER);
        isb.set("tableIndex", "-1", Types.INTEGER);
        isb.set("type", "1", Types.INTEGER);
        isb.set("address", "InsertInAllTableShards", Types.VARCHAR);

        dao.insert(isb, new DalHints().inAllTableShards());
    }

    private void queryInAllTableShards(ClientTestModel pojo) throws SQLException {
        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        List<ClientTestModel> modelList0 = dao.queryBy(pojo, new DalHints().inTableShards(shard0));
        assertEquals(1, modelList0.size());

        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        List<ClientTestModel> modelList1 = dao.queryBy(pojo, new DalHints().inTableShards(shard1));
        assertEquals(1, modelList1.size());

        Set<String> shard2 = new HashSet<>();
        shard2.add("2");
        List<ClientTestModel> modelList2 = dao.queryBy(pojo, new DalHints().inTableShards(shard2));
        assertEquals(1, modelList2.size());

        Set<String> shard3 = new HashSet<>();
        shard3.add("3");
        List<ClientTestModel> modelList3 = dao.queryBy(pojo, new DalHints().inTableShards(shard3));
        assertEquals(1, modelList3.size());
    }

    @Test
    public void testInsertInTableShards() throws SQLException {
        try {
            insertInTableShards();
        } catch (Exception e) {
            Assert.fail();
        }

        ClientTestModel pojo = new ClientTestModel();
        pojo.setAddress("InsertInTableShards");
        queryInTableShards(pojo);
    }

    private void insertInTableShards() throws SQLException {
        InsertSqlBuilder isb = new InsertSqlBuilder();
        isb.setDatabaseCategory(dao.getDatabaseCategory());
        isb.from(TABLE_NAME);
        isb.set("quantity", "0", Types.INTEGER);
        isb.set("dbIndex", "1", Types.INTEGER);
        isb.set("tableIndex", "-1", Types.INTEGER);
        isb.set("type", "1", Types.INTEGER);
        isb.set("address", "InsertInTableShards", Types.VARCHAR);

        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("2");

        dao.insert(isb, new DalHints().inTableShards(tableShards));
    }

    private void queryInTableShards(ClientTestModel pojo) throws SQLException {
        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        List<ClientTestModel> modelList0 = dao.queryBy(pojo, new DalHints().inTableShards(shard0));
        assertEquals(1, modelList0.size());

        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        List<ClientTestModel> modelList1 = dao.queryBy(pojo, new DalHints().inTableShards(shard1));
        assertEquals(0, modelList1.size());

        Set<String> shard2 = new HashSet<>();
        shard2.add("2");
        List<ClientTestModel> modelList2 = dao.queryBy(pojo, new DalHints().inTableShards(shard2));
        assertEquals(1, modelList2.size());

        Set<String> shard3 = new HashSet<>();
        shard3.add("3");
        List<ClientTestModel> modelList3 = dao.queryBy(pojo, new DalHints().inTableShards(shard3));
        assertEquals(0, modelList3.size());

    }
    // endregion

    // region delete
    @Test
    public void testDeleteInAllTableShards() throws SQLException {
        try {
            insertInAllTableShardsForDeleting();
        } catch (Exception e) {
            Assert.fail();
        }

        ClientTestModel pojo = new ClientTestModel();
        pojo.setAddress("DeleteInAllTableShards");

        // before deleting
        queryInAllTableShards(pojo);
        // end

        DeleteSqlBuilder dsb = new DeleteSqlBuilder();
        dsb.setDatabaseCategory(dao.getDatabaseCategory());
        dsb.from(TABLE_NAME);
        dsb.where(" address = 'DeleteInAllTableShards' ");

        dao.delete(dsb, new DalHints().inAllTableShards());

        // after deleting
        queryInAllTableShardsWithoutAnyResult(pojo);
        // end
    }

    private void insertInAllTableShardsForDeleting() throws SQLException {
        InsertSqlBuilder isb = new InsertSqlBuilder();
        isb.setDatabaseCategory(dao.getDatabaseCategory());
        isb.from(TABLE_NAME);
        isb.set("quantity", "0", Types.INTEGER);
        isb.set("dbIndex", "1", Types.INTEGER);
        isb.set("tableIndex", "-1", Types.INTEGER);
        isb.set("type", "1", Types.INTEGER);
        isb.set("address", "DeleteInAllTableShards", Types.VARCHAR);

        dao.insert(isb, new DalHints().inAllTableShards());
    }

    private void queryInAllTableShardsWithoutAnyResult(ClientTestModel pojo) throws SQLException {
        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        List<ClientTestModel> modelList0 = dao.queryBy(pojo, new DalHints().inTableShards(shard0));
        assertEquals(0, modelList0.size());

        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        List<ClientTestModel> modelList1 = dao.queryBy(pojo, new DalHints().inTableShards(shard1));
        assertEquals(0, modelList1.size());

        Set<String> shard2 = new HashSet<>();
        shard2.add("2");
        List<ClientTestModel> modelList2 = dao.queryBy(pojo, new DalHints().inTableShards(shard2));
        assertEquals(0, modelList2.size());

        Set<String> shard3 = new HashSet<>();
        shard3.add("3");
        List<ClientTestModel> modelList3 = dao.queryBy(pojo, new DalHints().inTableShards(shard3));
        assertEquals(0, modelList3.size());
    }

    @Test
    public void testDeleteInTableShards() throws SQLException {
        try {
            insertInTableShardsForDeleting();
        } catch (Exception e) {
            Assert.fail();
        }

        ClientTestModel pojo = new ClientTestModel();
        pojo.setAddress("DeleteInTableShards");

        // before deleting
        queryInTableShards(pojo);
        // end

        DeleteSqlBuilder dsb = new DeleteSqlBuilder();
        dsb.setDatabaseCategory(dao.getDatabaseCategory());
        dsb.from(TABLE_NAME);
        dsb.where(" address = 'DeleteInTableShards' ");

        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("2");

        dao.delete(dsb, new DalHints().inTableShards(tableShards));

        // after deleting
        queryInAllTableShardsWithoutAnyResult(pojo);
        // end
    }

    private void insertInTableShardsForDeleting() throws SQLException {
        InsertSqlBuilder isb = new InsertSqlBuilder();
        isb.setDatabaseCategory(dao.getDatabaseCategory());
        isb.from(TABLE_NAME);
        isb.set("quantity", "0", Types.INTEGER);
        isb.set("dbIndex", "1", Types.INTEGER);
        isb.set("tableIndex", "-1", Types.INTEGER);
        isb.set("type", "1", Types.INTEGER);
        isb.set("address", "DeleteInTableShards", Types.VARCHAR);

        Set<String> tableShards = new HashSet<>();
        tableShards.add("0");
        tableShards.add("2");

        dao.insert(isb, new DalHints().inTableShards(tableShards));
    }

    @Test
    public void testDeleteTableShardBy() throws SQLException {
        try {
            insertTableShardByForDeleting();
        } catch (Exception e) {
            Assert.fail();
        }

        ClientTestModel pojo = new ClientTestModel();
        pojo.setAddress("DeleteTableShardBy");

        // before deleting
        queryTableShardBy(pojo);
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
        dsb.where(" tableIndex in(?) and address = 'DeleteTableShardBy'");
        dsb.with(parameters);

        dao.delete(dsb, new DalHints().tableShardBy("tableIndex"));

        // after deleting
        queryTableShardByWithoutAnyResult(pojo);
        // end

    }

    private void insertTableShardByForDeleting() throws SQLException {
        InsertSqlBuilder isb = getTableShardByInsertSqlBuilder("0");
        dao.insert(isb, new DalHints().inTableShard("0"));

        InsertSqlBuilder isb2 = getTableShardByInsertSqlBuilder("2");
        dao.insert(isb2, new DalHints().inTableShard("2"));
    }

    private InsertSqlBuilder getTableShardByInsertSqlBuilder(String tableIndex) throws SQLException {
        InsertSqlBuilder isb = new InsertSqlBuilder();
        isb.setDatabaseCategory(dao.getDatabaseCategory());
        isb.from(TABLE_NAME);
        isb.set("quantity", "0", Types.INTEGER);
        isb.set("dbIndex", "1", Types.INTEGER);
        isb.set("tableIndex", tableIndex, Types.INTEGER);
        isb.set("type", "1", Types.INTEGER);
        isb.set("address", "DeleteTableShardBy", Types.VARCHAR);
        return isb;
    }

    private void queryTableShardBy(ClientTestModel pojo) throws SQLException {
        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        List<ClientTestModel> modelList0 = dao.queryBy(pojo, new DalHints().inTableShards(shard0));
        assertEquals(1, modelList0.size());

        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        List<ClientTestModel> modelList1 = dao.queryBy(pojo, new DalHints().inTableShards(shard1));
        assertEquals(0, modelList1.size());

        Set<String> shard2 = new HashSet<>();
        shard2.add("2");
        List<ClientTestModel> modelList2 = dao.queryBy(pojo, new DalHints().inTableShards(shard2));
        assertEquals(1, modelList2.size());

        Set<String> shard3 = new HashSet<>();
        shard3.add("3");
        List<ClientTestModel> modelList3 = dao.queryBy(pojo, new DalHints().inTableShards(shard3));
        assertEquals(0, modelList3.size());
    }

    private void queryTableShardByWithoutAnyResult(ClientTestModel pojo) throws SQLException {
        Set<String> shard0 = new HashSet<>();
        shard0.add("0");
        List<ClientTestModel> modelList0 = dao.queryBy(pojo, new DalHints().inTableShards(shard0));
        assertEquals(0, modelList0.size());

        Set<String> shard1 = new HashSet<>();
        shard1.add("1");
        List<ClientTestModel> modelList1 = dao.queryBy(pojo, new DalHints().inTableShards(shard1));
        assertEquals(0, modelList1.size());

        Set<String> shard2 = new HashSet<>();
        shard2.add("2");
        List<ClientTestModel> modelList2 = dao.queryBy(pojo, new DalHints().inTableShards(shard2));
        assertEquals(0, modelList2.size());

        Set<String> shard3 = new HashSet<>();
        shard3.add("3");
        List<ClientTestModel> modelList3 = dao.queryBy(pojo, new DalHints().inTableShards(shard3));
        assertEquals(0, modelList3.size());
    }

    // endregion

    // region FreeSelectSqlBuilder

    @Test
    public void testFreeSelectSqlBuilderInTableShardWithTableName() throws SQLException {
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result = null;
        try {
            result = queryDao.query(builder, new DalHints().inTableShard(3));
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testFreeSelectSqlBuilderInAllTableShardsWithTableName() throws SQLException {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(4);

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result = null;
        try {
            result = queryDao.query(builder, new DalHints().inAllTableShards());
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(6, result.size());
    }

    @Test
    public void testFreeSelectSqlBuilderInTableShardsWithTableName() throws SQLException {
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result = null;
        try {
            Set<String> tableShards = new HashSet<>();
            tableShards.add("2");
            tableShards.add("3");
            result = queryDao.query(builder, new DalHints().inTableShards(tableShards));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertEquals(3, result.size());
    }

    @Test
    public void testFreeSelectSqlBuilderInOneTableShardWithTableName() throws SQLException {
        List<Integer> list = new ArrayList<>();
        list.add(4);

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result = null;
        try {
            Set<String> tableShards = new HashSet<>();
            tableShards.add("3");
            result = queryDao.query(builder, new DalHints().inTableShards(tableShards));
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(1, result.size());

        ClientTestModel model = result.get(0);
        Assert.assertEquals(4, model.getId().intValue());
        Assert.assertEquals(13, model.getQuantity().intValue());
        Assert.assertEquals(1, model.getDbIndex().intValue());
        Assert.assertEquals(3, model.getTableIndex().intValue());
        Assert.assertEquals("SH INFO", model.getAddress());
    }

    @Test
    public void testFreeSelectSqlBuilderTableShardByWithTableName() throws SQLException {
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);

        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result = null;
        try {
            result = queryDao.query(builder, new DalHints().tableShardBy("id"));
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(1, result.size());

        ClientTestModel model = result.get(0);
        Assert.assertEquals(3, model.getId().intValue());
        Assert.assertEquals(12, model.getQuantity().intValue());
        Assert.assertEquals(1, model.getDbIndex().intValue());
        Assert.assertEquals(3, model.getTableIndex().intValue());
        Assert.assertEquals(1, model.getType().shortValue());
        Assert.assertEquals("SH INFO", model.getAddress());


        // multiple
        List<Integer> list2 = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        FreeSelectSqlBuilder<List<ClientTestModel>> builder2 = new FreeSelectSqlBuilder<>();
        builder2.selectAll().from(TABLE_NAME).where(Expressions.in("id", list, Types.INTEGER));
        builder2.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result2 = null;
        try {
            result2 = queryDao.query(builder2, new DalHints().tableShardBy("id"));
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(3, result2.size());
    }

    @Test
    public void testFreeSelectSqlBuilderWithFreeSql() throws SQLException {
        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        builder.setTemplate("select * from dal_client_test_0");
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result = null;
        try {
            result = queryDao.query(builder, new DalHints().inTableShard(3)); // inTableShard actually invalid here
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(1, result.size());

        ClientTestModel model = result.get(0);
        Assert.assertEquals(model.getId().intValue(), 1);
        Assert.assertEquals(model.getQuantity().intValue(), 10);
        Assert.assertEquals(model.getDbIndex().intValue(), 1);
        Assert.assertEquals(model.getTableIndex().intValue(), 0);
        Assert.assertEquals(model.getType().shortValue(), 1);
        Assert.assertEquals(model.getAddress(), "SH INFO");

    }

    @Test
    public void testFreeSelectBuilderWithTableEntity() throws SQLException {
        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        AbstractFreeSqlBuilder.Table table = new AbstractFreeSqlBuilder.Table("dal_client_test");
        builder.selectAll().from(table);
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result = null;
        try {
            result = queryDao.query(builder, new DalHints().inTableShard(3)); // inTableShard actually invalid here
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(4, result.size());

        FreeSelectSqlBuilder<List<ClientTestModel>> builder2 = new FreeSelectSqlBuilder();
        AbstractFreeSqlBuilder.Table table2 = new AbstractFreeSqlBuilder.Table("dal_client_test");
        table2.inShard("0");
        builder2.selectAll().from(table2);
        builder2.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result2 = null;
        try {
            result2 = queryDao.query(builder2, new DalHints().inTableShard(3)); // inTableShard actually invalid here
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(1, result2.size());

    }

    @Test
    public void testFreeSelectBuilderWithTableEntityImplicitInAllTableShards() throws SQLException {
        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        AbstractFreeSqlBuilder.Table table = new AbstractFreeSqlBuilder.Table("dal_client_test");
        builder.selectAll().from(table);
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        List<ClientTestModel> result = null;
        try {
            result = queryDao.query(builder, new DalHints().inAllShards());
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(10, result.size());
    }

    @Test
    public void testFreeSelectBuilderWithTableEntityThrowsException() throws SQLException {
        FreeSelectSqlBuilder<List<ClientTestModel>> builder = new FreeSelectSqlBuilder();
        AbstractFreeSqlBuilder.Table table = new AbstractFreeSqlBuilder.Table("dal_client_test");
        builder.selectAll().from(table);
        builder.mapWith(new DalDefaultJpaMapper<>(ClientTestModel.class));

        try {
            List<ClientTestModel> result = queryDao.query(builder, new DalHints());
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("java.sql.SQLException: Can not locate table shard for dao_test_mysql_tableShard",
                    e.getCause().getMessage());
        }
    }

    // endregion

}

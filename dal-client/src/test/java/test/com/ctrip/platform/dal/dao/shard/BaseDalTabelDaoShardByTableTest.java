package test.com.ctrip.platform.dal.dao.shard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub.DatabaseDifference;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.ctrip.platform.dal.dao.sqlbuilder.UpdateSqlBuilder;

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
            INSERT_PK_BACK_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 4;

	private static DalTableDao<ClientTestModel> dao;
	
	public void assertResEquals(int exp, int res) {
		if(ASSERT_ALLOWED)
			assertEquals(exp, res);
	}
	
	/**
	 * Test Query by Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPk() throws SQLException {
		ClientTestModel model = null;
		
		for(int i = 0; i < mod; i++) {
			// By tabelShard
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().inTableShard(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().inTableShard(i));
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By tableShardValue
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().setTableShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().setTableShardValue(i));
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().setShardColValue("index", String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().setShardColValue("index", i));
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			if(i%2 == 0)
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
			return (ClientTestModel)getResult();
		}

		public List<ClientTestModel> getModels() {
			try {
				waitForDone();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return (List<ClientTestModel>)getResult();
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
		if(hints.is(DalHintEnum.resultCallback)){
			TestQueryResultCallback callback = (TestQueryResultCallback)hints.get(DalHintEnum.resultCallback);
			return callback.get();
		}

		try {
			return (ClientTestModel)hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	private List<ClientTestModel> assertModels(Object models, DalHints hints) throws SQLException {
		assertNull(models);
		if(hints.is(DalHintEnum.resultCallback)){
			TestQueryResultCallback callback = (TestQueryResultCallback)hints.get(DalHintEnum.resultCallback);
			return callback.getModels();
		}
		try {
			return (List<ClientTestModel>)hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	private int assertInt(int res, DalHints hints) throws SQLException {
		assertEquals(0, res);
		if(hints.is(DalHintEnum.resultCallback)){
			IntCallback callback = (IntCallback)hints.get(DalHintEnum.resultCallback);
			return callback.getInt();
		}
		try {
			return ((int[])hints.getAsyncResult().get())[0];
		} catch (Exception e) {
			try {
				return (Integer)hints.getAsyncResult().get();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
	}

	private int[] assertIntArray(int[] res, DalHints hints) throws SQLException {
		assertNull(res);
		if(hints.is(DalHintEnum.resultCallback)){
			IntCallback callback = (IntCallback)hints.get(DalHintEnum.resultCallback);
			return callback.getIntArray();
		}
		try {
			return (int[])hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Test
	public void testQueryByPkAsyncCallback() throws SQLException {
		ClientTestModel model = null;
		DalHints hints;

		for(int i = 0; i < mod; i++) {
			// By tabelShard
			hints = new DalHints().asyncExecution();
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.inTableShard(String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.inTableShard(i));
			model = assertModel(model, hints);
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By tableShardValue
			hints = callbackHints();
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.setTableShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.setTableShardValue(i));
			model = assertModel(model, hints);
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			hints = new DalHints().asyncExecution();
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.setShardColValue("index", String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.setShardColValue("index", i));
			model = assertModel(model, hints);
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			hints = callbackHints();
			if(i%2 == 0)
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
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPkWithEntity() throws SQLException{
		ClientTestModel pk = null;
		ClientTestModel model = null;
		
		for(int i = 0; i < mod; i++) {
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
	public void testQueryByPkWithEntityAsyncCallback() throws SQLException{
		ClientTestModel pk = null;
		ClientTestModel model = null;
		DalHints hints;

		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPkWithEntityNoId() throws SQLException{
		ClientTestModel pk = new ClientTestModel();
		ClientTestModel model = null;
		// By fields
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setTableIndex(i);
			if(i%2 == 0)
				model = dao.queryByPk(pk, new DalHints());
			else
				model = dao.queryByPk(pk, new DalHints());
			assertNull(model);
		}
	}

	@Test
	public void testQueryByPkWithEntityNoIdAsyncCallback() throws SQLException{
		ClientTestModel pk = new ClientTestModel();
		ClientTestModel model = null;
		DalHints hints;

		// By fields
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setTableIndex(i);
			hints = new DalHints().asyncExecution();
			if(i%2 == 0)
				model = dao.queryByPk(pk, hints);
			else
				model = dao.queryByPk(pk, hints);
			assertModel(model, hints);
		}

		// By fields
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setTableIndex(i);
			hints = callbackHints();
			if(i%2 == 0)
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
	 * @throws SQLException
	 */
	@Test
	public void testQueryLike() throws SQLException{
		List<ClientTestModel> models = null;

		ClientTestModel pk = null;
		
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setType((short)1);

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
	public void testQueryLikeAsyncCallback() throws SQLException{
		List<ClientTestModel> models = null;

		ClientTestModel pk = null;
		DalHints hints;

		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setType((short)1);

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
	 * @throws SQLException
	 */
	@Test
	public void testQueryWithWhereClause() throws SQLException{
		List<ClientTestModel> models = null;
		
		for(int i = 0; i < mod; i++) {
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
	public void testQueryWithWhereClauseAsymcCallback() throws SQLException{
		List<ClientTestModel> models = null;
		DalHints hints;

		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException 
	 */
	@Test
	public void testQueryFirstWithWhereClause() throws SQLException{
		ClientTestModel model = null;
		for(int i = 0; i < mod; i++) {
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
	public void testQueryFirstWithWhereClauseAsyncCallback() throws SQLException{
		DalHints hints;

		ClientTestModel model = null;
		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException
	 */
	@Test
	public void testQueryFirstWithWhereClauseFailed() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 10);
		try{
			dao.queryFirst(whereClause, parameters, new DalHints().inTableShard(1));
			fail();
		}catch(Throwable e) {
		}
	}
	
	@Test
	public void testQueryFirstWithWhereClauseFailedAsync() throws SQLException{
		DalHints hints;

		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 10);
		try{
			hints = new DalHints().asyncExecution();
			Object model = dao.queryFirst(whereClause, parameters, hints.inTableShard(1));
			assertModel(model, hints);
			fail();
		}catch(Throwable e) {
		}
	}
	
	@Test
	public void testQueryFirstWithWhereClauseFailedCallback() throws SQLException{
		DalHints hints;
		DefaultResultCallback callback;

		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 10);
		try{
			callback = new DefaultResultCallback();
			hints = new DalHints().callbackWith(callback);
			Object model = dao.queryFirst(whereClause, parameters, hints.inTableShard(1));
			callback.waitForDone();
			assertNull(callback.getResult());
		}catch(Throwable e) {
			fail();
		}
	}
	
	/**
	 * Test Query the top rows with where clause
	 * @throws SQLException
	 */
	@Test
	public void testQueryTopWithWhereClause() throws SQLException{
		List<ClientTestModel> models = null;
		
		for(int i = 0; i < mod; i++) {
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
	public void testQueryTopWithWhereClauseAsyncCallback() throws SQLException{
		List<ClientTestModel> models = null;
		DalHints hints;

		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException
	 */
	@Test
	public void testQueryTopWithWhereClauseFailed() throws SQLException{
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
	public void testQueryTopWithWhereClauseFailedAsync() throws SQLException{
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
	public void testQueryTopWithWhereClauseFailedCallback() throws SQLException{
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
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClause() throws SQLException{
		List<ClientTestModel> models = null;
		String whereClause = "type=?";
		
		for(int i = 0; i < mod; i++) {
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

		whereClause += " and tableIndex=?";
		// By parameters
		for(int i = 0; i < mod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);

			models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, i + 1);
			assertEquals(i + 1, models.size());
		}
	}
	
	@Test
	public void testQueryFromWithWhereClauseAsyncCallback() throws SQLException{
		List<ClientTestModel> models = null;
		String whereClause = "type=?";
		DalHints hints;
		
		for(int i = 0; i < mod; i++) {
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

		whereClause += " and tableIndex=?";
		// By parameters
		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClauseFailed() throws SQLException{
		String whereClause = "type=?";
		List<ClientTestModel> models = null;
		for(int i = 0; i < mod; i++) {
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
	public void testQueryFromWithWhereClauseFailedAsyncCallback() throws SQLException{
		String whereClause = "type=?";
		List<ClientTestModel> models = null;
		DalHints hints;

		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClauseEmpty() throws SQLException{
		String whereClause = "type=?";
		List<ClientTestModel> models = null;
		for(int i = 0; i < mod; i++) {
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
	public void testQueryFromWithWhereClauseEmptyAsyncCallback() throws SQLException{
		String whereClause = "type=?";
		List<ClientTestModel> models = null;
		DalHints hints;

		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException
	 */
	@Test
	public void testInsertSingle() throws SQLException{
		ClientTestModel model = new ClientTestModel();
		model.setQuantity(10 + 1%3);
		model.setType(((Number)(1%3)).shortValue());
		model.setAddress("CTRIP");
		int res;
		try {
			res = dao.insert(new DalHints(), model);
			fail();
		} catch (Throwable e) {
		}
		
		for(int i = 0; i < mod; i++) {
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
		}
	}
	
	@Test
	public void testInsertSingleAsyncCallback() throws SQLException{
		ClientTestModel model = new ClientTestModel();
		model.setQuantity(10 + 1%3);
		model.setType(((Number)(1%3)).shortValue());
		model.setAddress("CTRIP");
		int res;
		DalHints hints;

		try {
			res = dao.insert(new DalHints(), model);
			fail();
		} catch (Exception e) {
		}
		
		for(int i = 0; i < mod; i++) {
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
		for(int i = 0; i < mod; i++) {
			int j = 1;
			dao.delete("1=1", new StatementParameters(), new DalHints().inTableShard(i));
		}
	}
	
	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleAsList() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}

		int[] res;
		try {
			res = dao.insert(new DalHints(), entities);
			fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
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
	public void testInsertMultipleAsListAsyncCallback() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		DalHints hints;
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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

		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleAsListWithContinueOnErrorHints() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			if(i==1){
				model.setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
						+ "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
						+ "CTRIPCTRIPCTRIPCTRIP");
			}
			else{
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

		for(int i = 0; i < mod; i++) {
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
	public void testInsertMultipleAsListWithContinueOnErrorHintsAsyncCallback() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		DalHints hints;

		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			if(i==1){
				model.setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
						+ "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
						+ "CTRIPCTRIPCTRIPCTRIP");
			}
			else{
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

		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleAsListWithKeyHolder() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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

		for(int i = 0; i < mod; i++) {
			int j = 1;
			holder = null;
			// By tabelShard
			// holder = new KeyHolder();
			res = dao.insert(new DalHints().inTableShard(i), holder, entities);
			assertEquals((i + 1) + j++ * 3, getCount(i));
//			assertEquals(3, res);
//			assertEquals(3, holder.getKeyList().size());		 
//			assertTrue(holder.getKey(0).longValue() > 0);
//			assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));

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
//		assertEquals(3, res);
//		assertEquals(3, holder.getKeyList().size());		 
//		assertTrue(holder.getKey(0).longValue() > 0);
//		assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
	}
	
    @Test
    public void testInsertMultipleAsListWithKeyHolderWithPkInsertBack() throws SQLException{
        if(!INSERT_PK_BACK_ALLOWED)
            return;
        
        DalTableDao<ClientTestModel> dao = new DalTableDao<ClientTestModel>(ClientTestModel.class, databaseName, TABLE_NAME);
        
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1%3);
            model.setType(((Number)(1%3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }


        for(int i = 0; i < mod; i++) {
            int j = 1;
            KeyHolder holder = new KeyHolder();
            IdentitySetBackHelper.clearId(entities);
            dao.insert(new DalHints().inTableShard(i).setIdentityBack(), holder, entities);
            IdentitySetBackHelper.assertIdentityTableShard(dao, entities, i);
        }
        
        deleteAllShards();
        
        // By fields not same shard
        KeyHolder holder = new KeyHolder();
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        IdentitySetBackHelper.clearId(entities);
        dao.insert(new DalHints().setIdentityBack(), holder, entities);
        assertEquals(1, getCount(0));
        assertEquals(1, getCount(1));
        assertEquals(1, getCount(2));
        IdentitySetBackHelper.assertIdentity(dao, entities);
    }
    
	@Test
	public void testInsertMultipleAsListWithKeyHolderAsyncCallback() throws SQLException{
		DalHints hints;

		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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

		for(int i = 0; i < mod; i++) {
			int j = 1;
			holder = null;
			// By tabelShard
			// holder = new KeyHolder();
			hints = asyncHints();
			res = dao.insert(hints.inTableShard(i), holder, entities);
			res = assertIntArray(res, hints);
			assertEquals((i + 1) + j++ * 3, getCount(i));
//			assertEquals(3, res);
//			assertEquals(3, holder.getKeyList().size());		 
//			assertTrue(holder.getKey(0).longValue() > 0);
//			assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));

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
//		assertEquals(3, res);
//		assertEquals(3, holder.getKeyList().size());		 
//		assertTrue(holder.getKey(0).longValue() > 0);
//		assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
	}
	
    @Test
    public void testInsertMultipleAsListWithKeyHolderAsyncCallbackWithPkInsertBack() throws SQLException{
        if(!INSERT_PK_BACK_ALLOWED)
            return;
        
        DalHints hints;
        DalTableDao<ClientTestModel> dao = new DalTableDao<ClientTestModel>(ClientTestModel.class, databaseName, TABLE_NAME);
        List<ClientTestModel> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1%3);
            model.setType(((Number)(1%3)).shortValue());
            model.setAddress("CTRIP" + i);
            entities.add(model);
        }

        KeyHolder holder;
        int[] res;

        for(int i = 0; i < mod; i++) {
            int j = 1;
            holder = new KeyHolder();
            // By tabelShard
            // holder = new KeyHolder();
            hints = asyncHints();
            res = dao.insert(hints.inTableShard(i).setIdentityBack(), holder, entities);
            res = assertIntArray(res, hints);
//            for(ClientTestModel model: entities) {
//                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());    
//            }

            // By tableShardValue
            holder = new KeyHolder();
            hints = intHints();
            res = dao.insert(hints.setTableShardValue(i).setIdentityBack(), holder, entities);
            res = assertIntArray(res, hints);
            for(ClientTestModel model: entities) {
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
        for(ClientTestModel model: entities) {
            assertEquals(dao.queryByPk(model, new DalHints()).getAddress(), model.getAddress());    
        }
    }
    	
	/**
	 * Test Insert multiple entities with one SQL Statement
	 * @throws SQLException
	 */
	@Test
	public void testCombinedInsert() throws SQLException{
		if(!diff.supportInsertValues)return;
		
		ClientTestModel[] entities = new ClientTestModel[3];
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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
		
		for(int i = 0; i < mod; i++) {
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
    public void testCombinedInsertWithPkInsertBack() throws SQLException{
        if(!INSERT_PK_BACK_ALLOWED)return;
        
        DalTableDao<ClientTestModel> dao = new DalTableDao<ClientTestModel>(ClientTestModel.class, databaseName, TABLE_NAME);
        
        ClientTestModel[] entities = new ClientTestModel[3];
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1%3);
            model.setType(((Number)(1%3)).shortValue());
            model.setAddress("CTRIP" + i);
            entities[i] = model;
        }
        
        for(int i = 0; i < mod; i++) {
            KeyHolder holder = new KeyHolder();
            dao.combinedInsert(new DalHints().inTableShard(i).setIdentityBack(), holder, Arrays.asList(entities));
            
            for(ClientTestModel model: entities) {
                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());    
            }            
        }
    }

    @Test
	public void testCombinedInsertAsyncCallback() throws SQLException{
		if(!diff.supportInsertValues)return;
		DalHints hints;

		ClientTestModel[] entities = new ClientTestModel[3];
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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
		
		for(int i = 0; i < mod; i++) {
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
    public void testCombinedInsertAsyncCallbackWithPkInsertBack() throws SQLException{
        if(!INSERT_PK_BACK_ALLOWED)
            return;
        
        DalTableDao<ClientTestModel> dao = new DalTableDao<ClientTestModel>(ClientTestModel.class, databaseName, TABLE_NAME);
        
        ClientTestModel[] entities = new ClientTestModel[3];
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + 1%3);
            model.setType(((Number)(1%3)).shortValue());
            model.setAddress("CTRIP" + i);
            entities[i] = model;
        }
        
        int res;
        
        for(int i = 0; i < mod; i++) {
            int j = 1;
            KeyHolder holder = new KeyHolder();            
            // By tabelShard
            // holder = new KeyHolder();
            DalHints hints = asyncHints();
            res = dao.combinedInsert(hints.inTableShard(i).setIdentityBack(), holder, Arrays.asList(entities));
            res = assertInt(res, hints);
            for(ClientTestModel model: entities) {
                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());    
            }            
            
            // By tableShardValue
            holder = new KeyHolder();
            hints = intHints();
            res = dao.combinedInsert(hints.setTableShardValue(i).setIdentityBack(), holder, Arrays.asList(entities));
            res = assertInt(res, hints);
            for(ClientTestModel model: entities) {
                assertEquals(dao.queryByPk(model, new DalHints().inTableShard(i)).getAddress(), model.getAddress());    
            }            
        }
    }
    
	/**
	 * Test Batch Insert multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testBatchInsert() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}

		int[] res;
		try {
			res = dao.batchInsert(new DalHints(), entities);
			fail();
		} catch (Exception e) {
		}
		
		for(int i = 0; i < mod; i++) {
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
	public void testBatchInsertAsyncCallback() throws SQLException{
		DalHints hints;
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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
		
		for(int i = 0; i < mod; i++) {
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
	 * @throws SQLException
	 */
	@Test
	public void testDeleteMultiple() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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
	public void testDeleteMultipleAsyncCallback() throws SQLException{
		DalHints hints;
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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
	 * @throws SQLException
	 */
	@Test
	public void testBatchDelete() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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
	public void testBatchDeleteAsyncCallback() throws SQLException{
		DalHints hints;
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
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
	 * @throws SQLException
	 */
	@Test
	public void testUpdateMultiple() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
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
		entities.get(2).setType((short)3);
		dao.update(new DalHints().setShardColValue("index", 2), entities.get(2));
		assertEquals((short)3, dao.queryByPk(3, hints.inTableShard(2)).getType().shortValue());

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
		for(ClientTestModel m: result)
			assertEquals("1234", m.getAddress());
	}
	
	@Test
	public void testUpdateMultipleAsyncCallback() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
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
		entities.get(2).setType((short)3);
		hints = asyncHints();
		res = dao.update(hints.setShardColValue("index", 2), entities.get(2));
		res = assertInt(res, hints);
		assertEquals((short)3, dao.queryByPk(3, new DalHints().inTableShard(2)).getType().shortValue());

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
		for(ClientTestModel m: result)
			assertEquals("1234", m.getAddress());
	}
	
	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	@Test
	public void testBatchUpdate() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
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
		for(ClientTestModel m: result)
			assertEquals("1234", m.getAddress());
	}
	
	@Test
	public void testBatchUpdateAsync() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
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
		for(ClientTestModel m: result)
			assertEquals("1234", m.getAddress());
	}
	
	@Test
	public void testBatchUpdateCallback() throws SQLException{
		DefaultResultCallback callback = new DefaultResultCallback();
		DalHints hints = new DalHints().callbackWith(callback);

		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
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
		for(ClientTestModel m: result)
			assertEquals("1234", m.getAddress());
	}
	
	/**
	 * Test delete entities with where clause and parameters
	 * @throws SQLException
	 */
	@Test
	public void testDeleteWithWhereClause() throws SQLException{
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
	public void testDeleteWithWhereClauseAsyncCallback() throws SQLException{
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
	 * @throws SQLException
	 */
	@Test
	public void testUpdatePlain() throws SQLException{
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
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
	public void testUpdatePlainAsyncCallback() throws SQLException{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e1) {
			fail();
		}
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
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
		if(!diff.supportInsertValues)return;
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
			
			KeyHolder keyHolder = null;//new KeyHolder();
			dao.combinedInsert(new DalHints(), keyHolder, Arrays.asList(pList));
			assertEquals(2, getCount(0));
			assertEquals(2, getCount(1));
			assertEquals(1, getCount(2));
			assertEquals(1, getCount(3));
		} catch (Exception e) {
			
			fail();
		}
	}

	@Test
	public void testCrossShardInsertAsync() {
		if(!diff.supportInsertValues)return;
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
			
			KeyHolder keyHolder = null;//new KeyHolder();
			DalHints hints = asyncHints();
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
	public void testCrossShardInsertCallback() {
		if(!diff.supportInsertValues)return;
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
			
			KeyHolder keyHolder = null;//new KeyHolder();
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
}
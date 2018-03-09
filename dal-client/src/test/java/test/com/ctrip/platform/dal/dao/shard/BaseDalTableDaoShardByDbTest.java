package test.com.ctrip.platform.dal.dao.shard;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static test.com.ctrip.platform.dal.dao.unittests.DalTestHelper.deleteAllShardsByDb;
import static test.com.ctrip.platform.dal.dao.unittests.DalTestHelper.getCountByDb;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub.DatabaseDifference;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalResultCallback;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

public abstract class BaseDalTableDaoShardByDbTest {
	private boolean ASSERT_ALLOWED = false;
	private boolean INSERT_PK_BACK_ALLOWED = false;
	private DatabaseDifference diff;
	private String databaseName;
	public BaseDalTableDaoShardByDbTest(String databaseName, String generatedKey, DatabaseDifference diff) {
		this.diff = diff;
		this.databaseName = databaseName;
		try {
			DalClientFactory.initClientFactory();
			DalParser<ClientTestModel> clientTestParser = new ClientTestDalParser(databaseName);
			dao = new DalTableDao<ClientTestModel>(clientTestParser);
//			ASSERT_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;
			INSERT_PK_BACK_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;;
			GENERATED_KEY = generatedKey;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	private String GENERATED_KEY;
	
	private static DalTableDao<ClientTestModel> dao;

	// Only for sql server
	public KeyHolder createKeyHolder() {
		return ASSERT_ALLOWED ? new KeyHolder() : null;
	}
	

	public void assertResEquals(int exp, int res) {
		if(ASSERT_ALLOWED)
			assertEquals(exp, res);
	}
	
	public void assertResEquals(int[] exp, int[] res) {
		assertEquals(exp.length, res.length);
		
		if(ASSERT_ALLOWED)
			assertArrayEquals(exp, res);
	}

	public void assertResEquals(int exp, int[] res) {
		if(ASSERT_ALLOWED) {
			int total = 0;
			for(int t: res)total+=t;
			assertEquals(exp, total);
		}
	}
	
	/**
	 * Test Query by Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPk() throws SQLException {
		ClientTestModel model = null;
		
		for(int i = 0; i < mod; i++) {
			// By shard
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().inShard(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().inShard(i));
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardValue
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().setShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().setShardValue(i));
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
	
	private ClientTestModel getModel(DalHints hints) throws SQLException {
		try {
			return (ClientTestModel)hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private ClientTestModel getModel(DefaultResultCallback callback) throws SQLException { 
		while(callback.getResult() == null)
			try {
				Thread.yield();
			} catch (Exception e) {
				throw new SQLException(e);
			}
		
		return (ClientTestModel)callback.getResult();
	}

	@Test
	public void testQueryByPkAsync() throws SQLException {
		ClientTestModel model = null;
		DalHints hints;
		for(int i = 0; i < mod; i++) {
			// By shard
			hints = new DalHints().asyncExecution();
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.inShard(String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.inShard(i));
			
			assertNull(model);
			model = getModel(hints);
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardValue
			hints = new DalHints().asyncExecution();
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.setShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.setShardValue(i));

			assertNull(model);
			model = getModel(hints);
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			hints = new DalHints().asyncExecution();
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.setShardColValue("index", String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.setShardColValue("index", i));
			
			assertNull(model);
			model = getModel(hints);
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			hints = new DalHints().asyncExecution();
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.setShardColValue("tableIndex", String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.setShardColValue("tableIndex", i));

			assertNull(model);
			model = getModel(hints);
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());
		}
	}

	private class TestQueryResultCallback implements DalResultCallback {
		private AtomicReference<ClientTestModel> model = new AtomicReference<>();
		
		@Override
		public <T> void onResult(T result) {
			model.set((ClientTestModel)result);
		}
		
		public ClientTestModel get() {
			while(model.get() == null)
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					return null;
				}
			return model.get();
		}

		@Override
		public void onError(Throwable e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	@Test
	public void testQueryByPkCallback() throws SQLException {
		ClientTestModel model = null;
		DalHints hints;
		TestQueryResultCallback callback;
		for(int i = 0; i < mod; i++) {
			// By shard
			callback = new TestQueryResultCallback();
			hints = new DalHints().callbackWith(callback);
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.inShard(String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.inShard(i));
			
			assertNull(model);
			model = callback.get();
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardValue
			callback = new TestQueryResultCallback();
			hints = new DalHints().callbackWith(callback);
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.setShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.setShardValue(i));

			assertNull(model);
			model = callback.get();
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			callback = new TestQueryResultCallback();
			hints = new DalHints().callbackWith(callback);
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.setShardColValue("index", String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.setShardColValue("index", i));
			
			assertNull(model);
			model = callback.get();
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			callback = new TestQueryResultCallback();
			hints = new DalHints().callbackWith(callback);
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.setShardColValue("tableIndex", String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.setShardColValue("tableIndex", i));

			assertNull(model);
			model = callback.get();
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

			// By shard
			model = dao.queryByPk(pk, new DalHints().inShard(i));
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardValue
			model = dao.queryByPk(pk, new DalHints().setShardValue(i));
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
	
	/**
	 * Query by Entity without Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPkWithEntityNoId() throws SQLException{
		ClientTestModel pk = new ClientTestModel();
		ClientTestModel model = null;
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setTableIndex(i);
			
			// By shard
			model = dao.queryByPk(pk, new DalHints().inShard(i));
			assertNull(model);

			// By shardValue
			model = dao.queryByPk(pk, new DalHints().setShardValue(i));
			assertNull(model);

			// By shardColValue
			model = dao.queryByPk(pk, new DalHints().setShardColValue("index", i));
			assertNull(model);
			
			// By shardColValue
			model = dao.queryByPk(pk, new DalHints().setShardColValue("tableIndex", i));
			assertNull(model);

			// By fields
			model = dao.queryByPk(pk, new DalHints());
			assertNull(model);
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

			// By shard
			models = dao.queryLike(pk, new DalHints().inShard(i));
			assertEquals(3, models.size());

			// By shardValue
			models = dao.queryLike(pk, new DalHints().setShardValue(i));
			assertEquals(3, models.size());

			// By shardColValue
			models = dao.queryLike(pk, new DalHints().setShardColValue("index", i));
			assertEquals(3, models.size());

			// By shardColValue
			models = dao.queryLike(pk, new DalHints().setShardColValue("tableIndex", i));
			assertEquals(3, models.size());

			// By fields
			pk.setTableIndex(i);
			models = dao.queryLike(pk, new DalHints());
			assertEquals(3, models.size());
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
			
			// By shard
			models = dao.query(whereClause, parameters, new DalHints().inShard(i));
			assertEquals(1, models.size());
			assertEquals("SH INFO", models.get(0).getAddress());
			assertEquals(models.get(0).getTableIndex(), new Integer(i));

			// By shardValue
			models = dao.query(whereClause, parameters, new DalHints().setShardValue(i));
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

			// By shard
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);

			model = dao.queryFirst(whereClause, parameters, new DalHints().inShard(i));
			assertEquals(1, model.getId().intValue());
			assertEquals(i, model.getTableIndex().intValue());

			// By shardValue
			model = dao.queryFirst(whereClause, parameters, new DalHints().setShardValue(i));
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
			dao.queryFirst(whereClause, parameters, new DalHints().inShard(1));
			fail();
		}catch(Throwable e) {
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

			// By shard
			models = dao.queryTop(whereClause, parameters, new DalHints().inShard(i), i + 1);
			assertEquals(i + 1, models.size());
			
			// By shardValue
			models = dao.queryTop(whereClause, parameters, new DalHints().setShardValue(i), i + 1);
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
		
		models = dao.queryTop(whereClause, parameters, new DalHints().inShard(1), 2);
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

			// By shard
			models = dao.queryFrom(whereClause, parameters, new DalHints().inShard(i), 0, i + 1);
			assertEquals(i + 1, models.size());
		
			// By shardValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue(i), 0, i + 1);
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
			
			// By shard
			models = dao.queryFrom(whereClause, parameters, new DalHints().inShard(i), 0, 10);
			assertTrue(null != models);
			assertEquals(3, models.size());

			// By shardValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue(i), 0, 10);
			assertTrue(null != models);
			assertEquals(3, models.size());

			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("index", i), 0, 10);
			assertTrue(null != models);
			assertEquals(3, models.size());
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), 0, 10);
			assertTrue(null != models);
			assertEquals(3, models.size());
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
			
			// By shard
			models = dao.queryFrom(whereClause, parameters, new DalHints().inShard(i), 0, 10);
			assertTrue(null != models);
			assertEquals(0, models.size());

			// By shardValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue(i), 0, 10);
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
    public void testQueryListPartial() throws SQLException{
        List<ClientTestModel> models = null;

        DalTableDao<ClientTestModel> dao = new DalTableDao<>(ClientTestModel.class, databaseName, TABLE_NAME); 
        for(int i = 0; i < mod; i++) {
            SelectSqlBuilder builder = new SelectSqlBuilder();
            builder.equal("type", 1, Types.SMALLINT);
            builder.select("id", "tableIndex");
            DalHints hints = new DalHints();
            models = dao.query(builder, hints.inShard(i));
            Assert.assertTrue(null != models);
            Assert.assertEquals(3, models.size());
            ClientTestModel model = models.get(0);
            
            Assert.assertNull(model.getAddress());
            Assert.assertNull(model.getLastChanged());
            Assert.assertNull(model.getQuantity());
            
            Assert.assertNull(hints.get(DalHintEnum.partialQuery));
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
		} catch (Exception e) {
		}
		
		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			res = dao.insert(new DalHints().inShard(i), model);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));

			// By shardValue
			res = dao.insert(new DalHints().setShardValue(i), model);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), model);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), model);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));
			
			// By fields
			model.setTableIndex(i);
			res = dao.insert(new DalHints(), model);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));
		}
	}
	
	   /**
     * Test Insert multiple entities one by one
     * @throws SQLException
     */
    @Test
    public void testInsertSingleWithPkInsertBack() throws SQLException{
        if(!INSERT_PK_BACK_ALLOWED)
            return;

        ClientTestModel model = new ClientTestModel();
        model.setQuantity(10 + 1%3);
        model.setType(((Number)(1%3)).shortValue());
        model.setAddress("CTRIP");
        
        int res;
        for(int i = 0; i < mod; i++) {
            int j = 1;
            // By shard
            model.setId(-1);
            model.setAddress("CTRIP" + j++);
            res = dao.insert(new DalHints().inShard(i).setIdentityBack().setKeyHolder(new KeyHolder()), model);
            assertTrue(model.getId() > 0);
            assertEquals(dao.queryByPk(model, new DalHints().inShard(i)).getAddress(), model.getAddress());

            // By shardValue
            model.setId(-1);
            model.setAddress("CTRIP" + j++);
            res = dao.insert(new DalHints().setShardValue(i).setIdentityBack().setKeyHolder(new KeyHolder()), model);
            assertTrue(model.getId() > 0);
            assertEquals(dao.queryByPk(model, new DalHints().setShardValue(i)).getAddress(), model.getAddress());

            // By shardColValue
            model.setId(-1);
            model.setAddress("CTRIP" + j++);
            res = dao.insert(new DalHints().setShardColValue("index", i).setIdentityBack().setKeyHolder(new KeyHolder()), model);
            assertTrue(model.getId() > 0);
            assertEquals(dao.queryByPk(model, new DalHints().setShardColValue("index", i)).getAddress(), model.getAddress());
            
            // By shardColValue
            model.setId(-1);
            model.setAddress("CTRIP" + j++);
            dao.insert(new DalHints().setShardColValue("tableIndex", i).setIdentityBack().setKeyHolder(new KeyHolder()), model);
            assertTrue(model.getId() > 0);
            assertEquals(dao.queryByPk(model, new DalHints().setShardColValue("tableIndex", i)).getAddress(), model.getAddress());
            
            // By fields
            model.setId(-1);
            model.setAddress("CTRIP" + j++);
            model.setTableIndex(i);
            res = dao.insert(new DalHints().setIdentityBack().setKeyHolder(new KeyHolder()), model);
            assertTrue(model.getId() > 0);
        }
    }

	private int getInt(DalHints hints) throws SQLException {
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

	private int[] getIntArray(DalHints hints) throws SQLException {
		try {
			return (int[])hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Test
	public void testInsertSingleAsync() throws SQLException{
		ClientTestModel model = new ClientTestModel();
		model.setQuantity(10 + 1%3);
		model.setType(((Number)(1%3)).shortValue());
		model.setAddress("CTRIP");
		int res;
		try {
			res = dao.insert(new DalHints(), model);
			fail();
		} catch (Exception e) {
		}
		
		DalHints hints;
		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			hints = new DalHints().asyncExecution();
			res = dao.insert(hints.inShard(i), model);
			assertEquals(0, res);
			res = getInt(hints);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));

			// By shardValue
			hints = new DalHints().asyncExecution();
			res = dao.insert(hints.setShardValue(i), model);
			assertEquals(0, res);
			res = getInt(hints);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));

			// By shardColValue
			hints = new DalHints().asyncExecution();
			res = dao.insert(hints.setShardColValue("index", i), model);
			assertEquals(0, res);
			res = getInt(hints);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));
			
			// By shardColValue
			hints = new DalHints().asyncExecution();
			res = dao.insert(hints.setShardColValue("tableIndex", i), model);
			assertEquals(0, res);
			res = getInt(hints);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));
			
			// By fields
			hints = new DalHints().asyncExecution();
			model.setTableIndex(i);
			res = dao.insert(hints, model);
			assertEquals(0, res);
			res = getInt(hints);
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));
		}
	}
	
	@Test
	public void testInsertSingleCallback() throws SQLException{
		ClientTestModel model = new ClientTestModel();
		model.setQuantity(10 + 1%3);
		model.setType(((Number)(1%3)).shortValue());
		model.setAddress("CTRIP");
		int res;
		try {
			res = dao.insert(new DalHints(), model);
			fail();
		} catch (Exception e) {
		}
		
		DalHints hints;
		IntCallback callback;
		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.insert(hints.inShard(i), model);
			assertEquals(0, res);
			res = callback.getInt();
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));

			// By shardValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.insert(hints.setShardValue(i), model);
			assertEquals(0, res);
			res = callback.getInt();
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));

			// By shardColValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.insert(hints.setShardColValue("index", i), model);
			assertEquals(0, res);
			res = callback.getInt();
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));
			
			// By shardColValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.insert(hints.setShardColValue("tableIndex", i), model);
			assertEquals(0, res);
			res = callback.getInt();
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));
			
			// By fields
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			model.setTableIndex(i);
			res = dao.insert(hints, model);
			assertEquals(0, res);
			res = callback.getInt();
			assertResEquals(1, res);
			assertEquals(3 + j++ * 1, getCountByDb(dao, i));
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
			// By shard
			res = dao.insert(new DalHints().inShard(i), entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));

			// By shardValue
			res = dao.insert(new DalHints().setShardValue(i), entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints(), entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
		}
		
		deleteAllShardsByDb(dao, mod);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(new DalHints().continueOnError(), entities);
		assertResEquals(3, res);
		assertEquals(2, getCountByDb(dao, 0));
		assertEquals(1, getCountByDb(dao, 1));
	}
	
    /**
     * Test Insert multiple entities one by one
     * @throws SQLException
     */
    @Test
    public void testInsertMultipleAsListWithPkInsertBack() throws SQLException{
        if(!INSERT_PK_BACK_ALLOWED)
            return;

        List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
        for (int i = 0; i < 3; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setQuantity(10 + i%3);
            model.setType(((Number)(i%3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }

        int[] res;

        for(int i = 0; i < mod; i++) {
            int j = 1;
            // By shard
            IdentitySetBackHelper.clearId(entities);
            res = dao.insert(new DalHints().inShard(i).setIdentityBack().setKeyHolder(new KeyHolder()), entities);
            IdentitySetBackHelper.assertIdentity(dao, entities, i);

            // By fields same shard
            IdentitySetBackHelper.clearId(entities);
            entities.get(0).setTableIndex(i);
            entities.get(1).setTableIndex(i);
            entities.get(2).setTableIndex(i);
            res = dao.insert(new DalHints().setIdentityBack().setKeyHolder(new KeyHolder()), entities);
            IdentitySetBackHelper.assertIdentity(dao, entities, i);
        }
        
        deleteAllShardsByDb(dao, mod);
        
        // By fields not same shard
        entities.get(0).setTableIndex(0);
        entities.get(1).setTableIndex(1);
        entities.get(2).setTableIndex(2);
        res = dao.insert(new DalHints().continueOnError().setIdentityBack().setKeyHolder(new KeyHolder()), entities);
        for(ClientTestModel model: entities) {
            assertEquals(dao.queryByPk(model, new DalHints()).getAddress(), model.getAddress());    
        }
    }
    
	@Test
	public void testInsertMultipleAsListAsyncCallback() throws SQLException{
		DalHints hints;
		IntCallback callback;

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
			// By shard
			hints = new DalHints().asyncExecution();
			res = dao.insert(hints.inShard(i), entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));

			// By shardValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.insert(hints.setShardValue(i), entities);
			assertNull(res);
			res = callback.getIntArray();
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));

			// By shardColValue
			hints = new DalHints().asyncExecution();
			res = dao.insert(hints.setShardColValue("index", i), entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By shardColValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.insert(hints.setShardColValue("tableIndex", i), entities);
			assertNull(res);
			res = callback.getIntArray();
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By fields same shard
			hints = new DalHints().asyncExecution();
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(hints, entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
		}
		
		deleteAllShardsByDb(dao, mod);
		
		// By fields not same shard
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(hints.continueOnError(), entities);
		assertNull(res);
		res = callback.getIntArray();
		assertResEquals(3, res);
		assertEquals(2, getCountByDb(dao, 0));
		assertEquals(1, getCountByDb(dao, 1));
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
			// By shard
			res = dao.insert(new DalHints().continueOnError().inShard(i), entities);
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));

			// By shardValue
			res = dao.insert(new DalHints().continueOnError().setShardValue(i), entities);
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));

			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("index", i), entities);
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));
			
			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("tableIndex", i), entities);
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints().continueOnError(), entities);
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));
		}
		
		deleteAllShardsByDb(dao, mod);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(new DalHints().continueOnError(), entities);
		assertResEquals(2, res);
		assertEquals(2, getCountByDb(dao, 0));
		assertEquals(0, getCountByDb(dao, 1));
	}

	@Test
	public void testInsertMultipleAsListWithContinueOnErrorHintsAsyncCallback() throws SQLException{
		DalHints hints;
		IntCallback callback;

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
			// By shard
			hints = new DalHints().asyncExecution();
			res = dao.insert(hints.continueOnError().inShard(i), entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));

			// By shardValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.insert(hints.continueOnError().setShardValue(i), entities);
			assertNull(res);
			res = callback.getIntArray();
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));

			// By shardColValue
			hints = new DalHints().asyncExecution();
			res = dao.insert(hints.continueOnError().setShardColValue("index", i), entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(2, res);
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));
			
			// By shardColValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.insert(hints.continueOnError().setShardColValue("tableIndex", i), entities);
			assertNull(res);
			res = callback.getIntArray();
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));
			
			// By fields same shard
			hints = new DalHints().asyncExecution();
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(hints.continueOnError(), entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(2, res);
			assertResEquals(2, res);
			assertEquals(3 + j++ * 2, getCountByDb(dao, i));
			
			if(INSERT_PK_BACK_ALLOWED) {
	            // By fields same shard
	            hints = new DalHints().asyncExecution().setIdentityBack().setKeyHolder(new KeyHolder());
	            entities.get(0).setTableIndex(i);
	            entities.get(1).setTableIndex(i);
	            entities.get(2).setTableIndex(i);
	            res = dao.insert(hints.continueOnError(), entities);
	            assertNull(res);
	            res = getIntArray(hints);
	            assertResEquals(2, res);
	            assertResEquals(2, res);
	            assertEquals(3 + j++ * 2, getCountByDb(dao, i));
	            IdentitySetBackHelper.assertIdentityWithError(dao, entities);
			}
		}
		
		deleteAllShardsByDb(dao, mod);
		
		// By fields not same shard
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(hints.continueOnError(), entities);
		assertNull(res);
		res = callback.getIntArray();
		assertResEquals(2, res);
		assertEquals(2, getCountByDb(dao, 0));
		assertEquals(0, getCountByDb(dao, 1));
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

		KeyHolder holder = createKeyHolder();
		int[] res;
		try {
			res = dao.insert(new DalHints(), holder, entities);
			fail();
		} catch (Exception e) {
			
		}

		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			holder = createKeyHolder();
			res = dao.insert(new DalHints().inShard(i), holder, entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);

			// By shardValue
			holder = createKeyHolder();
			res = dao.insert(new DalHints().setShardValue(i), holder, entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
			
			// By shardColValue
			holder = createKeyHolder();
			res = dao.insert(new DalHints().setShardColValue("index", i), holder, entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
			
			// By shardColValue
			holder = createKeyHolder();
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), holder, entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
			
			// By fields same shard
			holder = createKeyHolder();
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints(), holder, entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
		}
		
		deleteAllShardsByDb(dao, mod);
		
		// By fields not same shard
		holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(new DalHints(), holder, entities);
		assertResEquals(3, res);
		assertEquals(2, getCountByDb(dao, 0));
		assertEquals(1, getCountByDb(dao, 1));
		assertResEquals(3, res);
		assertKeyHolder(holder);
	}
	
	@Test
	public void testInsertMultipleAsListWithKeyHolderAsyncCallback() throws SQLException{
		DalHints hints;
		IntCallback callback;

		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}

		KeyHolder holder = createKeyHolder();
		int[] res;
		try {
			res = dao.insert(new DalHints(), holder, entities);
			fail();
		} catch (Exception e) {
			
		}

		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			hints = new DalHints().asyncExecution();
			holder = createKeyHolder();
			res = dao.insert(hints.inShard(i), holder, entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);

			// By shardValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			holder = createKeyHolder();
			res = dao.insert(hints.setShardValue(i), holder, entities);
			assertNull(res);
			res = callback.getIntArray();
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
			
			// By shardColValue
			hints = new DalHints().asyncExecution();
			holder = createKeyHolder();
			res = dao.insert(hints.setShardColValue("index", i), holder, entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
			
			// By shardColValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			holder = createKeyHolder();
			res = dao.insert(hints.setShardColValue("tableIndex", i), holder, entities);
			assertNull(res);
			res = callback.getIntArray();
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
			
			// By fields same shard
			hints = new DalHints().asyncExecution();
			holder = createKeyHolder();
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(hints, holder, entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
		}
		
		deleteAllShardsByDb(dao, mod);
		
		// By fields not same shard
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(hints, holder, entities);
		assertNull(res);
		res = callback.getIntArray();
		assertResEquals(3, res);
		assertEquals(2, getCountByDb(dao, 0));
		assertEquals(1, getCountByDb(dao, 1));
		assertResEquals(3, res);
		assertKeyHolder(holder);
	}

	private void assertKeyHolder(KeyHolder holder) throws SQLException {
		if(!ASSERT_ALLOWED)
			return;
		assertEquals(3, holder.size());		 
		assertTrue(holder.getKey(0).longValue() > 0);
		assertTrue(holder.getKeyList().get(0).containsKey(GENERATED_KEY));
	}
	
	/**
	 * Test Insert multiple entities with one SQL Statement
	 * @throws SQLException
	 */
	@Test
	public void testCombinedInsert() throws SQLException{
		if(!diff.supportInsertValues)return;

		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		KeyHolder holder = createKeyHolder();
		int res;
		try {
			res = dao.combinedInsert(new DalHints(), holder, entities);
			fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < mod; i++) {
			int j = 1;
			holder = null;
			// By shard
			holder = createKeyHolder();
			res = dao.combinedInsert(new DalHints().inShard(i), holder, entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);

			// By shardValue
			holder = createKeyHolder();
			res = dao.combinedInsert(new DalHints().setShardValue(i), holder, entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);

			// By shardColValue
			holder = createKeyHolder();
			res = dao.combinedInsert(new DalHints().setShardColValue("index", i), holder, entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);

			// By shardColValue
			holder = createKeyHolder();
			res = dao.combinedInsert(new DalHints().setShardColValue("tableIndex", i), holder, entities);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);
		}
		
		// For combined insert, the shard id must be defined or change bd deduced.
	}
	
	@Test
	public void testCombinedInsertAsyncCallback() throws SQLException{
		if(!diff.supportInsertValues)return;

		DalHints hints;
		IntCallback callback;

		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		KeyHolder holder = createKeyHolder();
		int res;
		try {
			res = dao.combinedInsert(new DalHints(), holder, entities);
			fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < mod; i++) {
			int j = 1;

			// By shard
			hints = new DalHints().asyncExecution();
			holder = createKeyHolder();
			res = dao.combinedInsert(hints.inShard(i), holder, entities);
			assertEquals(0, res);
			res = getInt(hints);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);

			// By shardValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			holder = createKeyHolder();
			res = dao.combinedInsert(hints.setShardValue(i), holder, entities);
			assertEquals(0, res);
			res = callback.getInt();
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);

			// By shardColValue
			hints = new DalHints().asyncExecution();
			holder = createKeyHolder();
			res = dao.combinedInsert(hints.setShardColValue("index", i), holder, entities);
			assertEquals(0, res);
			res = getInt(hints);
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);

			// By shardColValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			holder = createKeyHolder();
			res = dao.combinedInsert(hints.setShardColValue("tableIndex", i), holder, entities);
			assertEquals(0, res);
			res = callback.getInt();
			assertResEquals(3, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);
		}
		
		// For combined insert, the shard id must be defined or change bd deduced.
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
			// By shard
			res = dao.batchInsert(new DalHints().inShard(i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By shardValue
			res = dao.batchInsert(new DalHints().setShardValue(i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));

			// By shardColValue
			res = dao.batchInsert(new DalHints().setShardColValue("index", i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By shardColValue
			res = dao.batchInsert(new DalHints().setShardColValue("tableIndex", i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
		}
		
		// For combined insert, the shard id must be defined or change bd deduced.
	}
	
	@Test
	public void testBatchInsertAsyncCallback() throws SQLException{
		DalHints hints;
		IntCallback callback;

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
			// By shard
			hints = new DalHints().asyncExecution();
			res = dao.batchInsert(hints.inShard(i), entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(new int[]{1,1,1}, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By shardValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.batchInsert(hints.setShardValue(i), entities);
			assertNull(res);
			res = callback.getIntArray();
			assertResEquals(new int[]{1,1,1}, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));

			// By shardColValue
			hints = new DalHints().asyncExecution();
			res = dao.batchInsert(hints.setShardColValue("index", i), entities);
			assertNull(res);
			res = getIntArray(hints);
			assertResEquals(new int[]{1,1,1}, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By shardColValue
			callback = new IntCallback();
			hints = new DalHints().callbackWith(callback);
			res = dao.batchInsert(hints.setShardColValue("tableIndex", i), entities);
			assertNull(res);
			res = callback.getIntArray();
			assertResEquals(new int[]{1,1,1}, res);
			assertEquals(3 + j++ * 3, getCountByDb(dao, i));
		}
		
		// For combined insert, the shard id must be defined or change bd deduced.
	}
		
	public abstract void insertBack();
	
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
		// By shard
		assertEquals(3, getCountByDb(dao, 0));
		res = dao.delete(new DalHints().inShard(0), entities);
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 0));

		// By shardValue
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(new DalHints().setShardValue(1), entities);
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 1));

		insertBack();
		
		// By shardColValue
		assertEquals(3, getCountByDb(dao, 0));
		res = dao.delete(new DalHints().setShardColValue("index", 2), entities);
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 0));
		
		// By shardColValue
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(new DalHints().setShardColValue("tableIndex", 3), entities);
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 1));
		
		// By fields same shard
		// holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		dao.insert(new DalHints(), entities);
		assertEquals(2, getCountByDb(dao, 0));
		assertEquals(1, getCountByDb(dao, 1));
		entities.set(0, dao.queryTop("1=1", new StatementParameters(), new DalHints().inShard(0), 2).get(0));
		entities.set(1, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inShard(1)));
		entities.set(2, dao.queryTop("1=1", new StatementParameters(), new DalHints().inShard(0), 2).get(1));
		res = dao.delete(new DalHints(), entities);
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 0));
		assertEquals(0, getCountByDb(dao, 1));
	}
	
	@Test
	public void testDeleteMultipleAsyncCallback() throws SQLException{
		DalHints hints;
		IntCallback callback;

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
		// By shard
		hints = new DalHints().asyncExecution();
		assertEquals(3, getCountByDb(dao, 0));
		res = dao.delete(hints.inShard(0), entities);
		assertNull(res);
		res = getIntArray(hints);
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 0));

		// By shardValue
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(hints.setShardValue(1), entities);
		assertNull(res);
		res = callback.getIntArray();
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 1));

		insertBack();
		
		// By shardColValue
		hints = new DalHints().asyncExecution();
		assertEquals(3, getCountByDb(dao, 0));
		res = dao.delete(hints.setShardColValue("index", 2), entities);
		assertNull(res);
		res = getIntArray(hints);
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 0));
		
		// By shardColValue
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(hints.setShardColValue("tableIndex", 3), entities);
		assertNull(res);
		res = callback.getIntArray();
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 1));
		
		// By fields same shard
		// holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		dao.insert(new DalHints(), entities);
		assertEquals(2, getCountByDb(dao, 0));
		assertEquals(1, getCountByDb(dao, 1));
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		entities.set(0, dao.queryTop("1=1", new StatementParameters(), new DalHints().inShard(0), 2).get(0));
		entities.set(1, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inShard(1)));
		entities.set(2, dao.queryTop("1=1", new StatementParameters(), new DalHints().inShard(0), 2).get(1));
		res = dao.delete(hints, entities);
		assertNull(res);
		res = callback.getIntArray();
		assertResEquals(3, res);
		assertEquals(0, getCountByDb(dao, 0));
		assertEquals(0, getCountByDb(dao, 1));
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
		
		// By shard
		assertEquals(3, getCountByDb(dao, 0));
		res = dao.batchDelete(new DalHints().inShard(0), entities);
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 0));

		// By shardValue
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.batchDelete(new DalHints().setShardValue(1), entities);
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 1));
		
		insertBack();

		// By shardColValue
		assertEquals(3, getCountByDb(dao, 0));
		res = dao.batchDelete(new DalHints().setShardColValue("index", 2), entities);
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 0));
		
		// By shardColValue
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.batchDelete(new DalHints().setShardColValue("tableIndex", 3), entities);
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 1));
		
		// By fields same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(0);
		entities.get(2).setTableIndex(0);
		dao.insert(new DalHints(), entities);
		assertEquals(3, getCountByDb(dao, 0));
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));
		res = dao.batchDelete(new DalHints().inShard(0), result);
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 0));
	}
	
	@Test
	public void testBatchDeleteAsyncCallback() throws SQLException{
		DalHints hints;
		IntCallback callback;

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
		
		// By shard
		hints = new DalHints().asyncExecution();
		assertEquals(3, getCountByDb(dao, 0));
		res = dao.batchDelete(hints.inShard(0), entities);
		assertNull(res);
		res = getIntArray(hints);
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 0));

		// By shardValue
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.batchDelete(hints.setShardValue(1), entities);
		assertNull(res);
		res = callback.getIntArray();
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 1));
		
		insertBack();

		// By shardColValue
		hints = new DalHints().asyncExecution();
		assertEquals(3, getCountByDb(dao, 0));
		res = dao.batchDelete(hints.setShardColValue("index", 2), entities);
		assertNull(res);
		res = getIntArray(hints);
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 0));
		
		// By shardColValue
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.batchDelete(hints.setShardColValue("tableIndex", 3), entities);
		assertNull(res);
		res = callback.getIntArray();
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 1));
		
		// By fields same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(0);
		entities.get(2).setTableIndex(0);
		dao.insert(new DalHints(), entities);
		assertEquals(3, getCountByDb(dao, 0));
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));

		hints = new DalHints().asyncExecution();
		res = dao.batchDelete(hints.inShard(0), result);
		assertNull(res);
		res = getIntArray(hints);
		assertResEquals(new int[]{1,1,1}, res);
		assertEquals(0, getCountByDb(dao, 0));
	}
	
	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	@Test
	public void testUpdateMultiple() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		
		int[] ress;
		try {
			ress = dao.update(hints, entities);
			fail();
		} catch (Exception e) {
			
		}
		
		int res;
		// By shard
		entities.get(0).setAddress("test1");
		res = dao.update(new DalHints().inShard(0), entities.get(0));
		assertResEquals(1, res);
		assertEquals("test1", dao.queryByPk(1, hints.inShard(0)).getAddress());

		// By shardValue
		entities.get(1).setQuantity(-11);
		res = dao.update(new DalHints().setShardValue(1), entities.get(1));
		assertResEquals(1, res);
		assertEquals(-11, dao.queryByPk(2, hints.inShard(1)).getQuantity().intValue());
		
		// By shardColValue
		entities.get(2).setType((short)3);
		res = dao.update(new DalHints().setShardColValue("index", 2), entities.get(2));
		assertResEquals(1, res);
		assertEquals((short)3, dao.queryByPk(3, hints.inShard(0)).getType().shortValue());

		// By shardColValue
		entities.get(0).setAddress("testa");
		res = dao.update(new DalHints().setShardColValue("tableIndex", 3), entities.get(0));
		assertResEquals(1, res);
		assertEquals("testa", dao.queryByPk(1, hints.inShard(1)).getAddress());
		
		// By fields same shard
		// holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(0).setAddress("1234");
		entities.get(1).setTableIndex(0);
		entities.get(1).setAddress("1234");
		entities.get(2).setTableIndex(0);
		entities.get(2).setAddress("1234");
		ress = dao.update(new DalHints(), entities);
		assertResEquals(3, res);
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));
		for(ClientTestModel m: result)
			assertEquals("1234", m.getAddress());
	}
	
	@Test
	public void testUpdateMultipleAsyncCallback() throws SQLException{
		DalHints hints;
		IntCallback callback;

		hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		
		int[] ress;
		try {
			ress = dao.update(hints, entities);
			fail();
		} catch (Exception e) {
			
		}
		
		int res;
		// By shard
		hints = new DalHints().asyncExecution();
		entities.get(0).setAddress("test1");
		res = dao.update(hints.inShard(0), entities.get(0));
		assertEquals(0, res);
		res = getInt(hints);
		assertResEquals(1, res);
		assertEquals("test1", dao.queryByPk(1, new DalHints().inShard(0)).getAddress());

		// By shardValue
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		entities.get(1).setQuantity(-11);
		res = dao.update(hints.setShardValue(1), entities.get(1));
		assertEquals(0, res);
		res = callback.getInt();
		assertResEquals(1, res);
		assertEquals(-11, dao.queryByPk(2, new DalHints().inShard(1)).getQuantity().intValue());
		
		// By shardColValue
		hints = new DalHints().asyncExecution();
		entities.get(2).setType((short)3);
		res = dao.update(hints.setShardColValue("index", 2), entities.get(2));
		assertEquals(0, res);
		res = getInt(hints);
		assertResEquals(1, res);
		assertEquals((short)3, dao.queryByPk(3, new DalHints().inShard(0)).getType().shortValue());

		// By shardColValue
		callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		entities.get(0).setAddress("testa");
		res = dao.update(hints.setShardColValue("tableIndex", 3), entities.get(0));
		assertEquals(0, res);
		res = callback.getInt();
		assertResEquals(1, res);
		assertEquals("testa", dao.queryByPk(1, new DalHints().inShard(1)).getAddress());
		
		// By fields same shard
		// holder = createKeyHolder();
		hints = new DalHints().asyncExecution();
		entities.get(0).setTableIndex(0);
		entities.get(0).setAddress("1234");
		entities.get(1).setTableIndex(0);
		entities.get(1).setAddress("1234");
		entities.get(2).setTableIndex(0);
		entities.get(2).setAddress("1234");
		ress = dao.update(hints, entities);
		assertNull(ress);
		res = getInt(hints);
		assertResEquals(3, res);
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));
		for(ClientTestModel m: result)
			assertEquals("1234", m.getAddress());
	}
	
	@Test
	public void testBatchUpdateMultiple() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		
		int[] ress;
		try {
			ress = dao.batchUpdate(hints, entities);
			fail();
		} catch (Exception e) {
			
		}
		
		// By fields same shard
		// holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(0).setAddress("1234");
		entities.get(1).setTableIndex(0);
		entities.get(1).setAddress("1234");
		entities.get(2).setTableIndex(0);
		entities.get(2).setAddress("1234");
		ress = dao.batchUpdate(new DalHints(), entities);
		assertResEquals(3, ress);
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));
		for(ClientTestModel m: result)
			assertEquals("1234", m.getAddress());
	}
	
	public void testBatchUpdateMultipleAsync() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		
		int[] ress;
		try {
			ress = dao.batchUpdate(hints, entities);
			fail();
		} catch (Exception e) {
			
		}
		
		// By fields same shard
		// holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(0).setAddress("1234");
		entities.get(1).setTableIndex(0);
		entities.get(1).setAddress("1234");
		entities.get(2).setTableIndex(0);
		entities.get(2).setAddress("1234");
		hints = new DalHints().asyncExecution();
		ress = dao.batchUpdate(hints, entities);
		assertNull(ress);
		ress = getIntArray(hints);
		assertResEquals(3, ress);
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));
		for(ClientTestModel m: result)
			assertEquals("1234", m.getAddress());
	}
	
	public void testBatchUpdateMultipleCallback() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		
		int[] ress;
		try {
			ress = dao.batchUpdate(hints, entities);
			fail();
		} catch (Exception e) {
			
		}
		
		// By fields same shard
		// holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(0).setAddress("1234");
		entities.get(1).setTableIndex(0);
		entities.get(1).setAddress("1234");
		entities.get(2).setTableIndex(0);
		entities.get(2).setAddress("1234");
		IntCallback callback = new IntCallback();
		hints = new DalHints().callbackWith(callback);
		ress = dao.batchUpdate(hints, entities);
		assertNull(ress);
		ress = callback.getIntArray();
		assertResEquals(3, ress);
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));
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
		
		// By shard
		res = dao.delete(whereClause, parameters, new DalHints().inShard(0));
		assertResEquals(3, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().inShard(0)).size());

		// By shardValue
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, new DalHints().setShardValue(1));
		assertResEquals(3, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardValue(1)).size());
		
		insertBack();

		// By shardColValue
		assertEquals(3, getCountByDb(dao, 0));
		res = dao.delete(whereClause, parameters, new DalHints().setShardColValue("index", 2));
		assertResEquals(3, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardColValue("index", 2)).size());
		
		// By shardColValue
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, new DalHints().setShardColValue("tableIndex", 3));
		assertResEquals(3, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardColValue("tableIndex", 3)).size());
		
		insertBack();
	}
	
	/**
	 * Test delete entities with where clause and parameters
	 * @throws SQLException
	 */
	@Test
	public void testDeleteWithWhereClauseAllShards() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);

		DalHints hints = new DalHints();
		int res;
		
		// By allShards
		assertEquals(3, getCountByDb(dao, 0));
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, new DalHints().inAllShards());
		assertResEquals(6, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().inAllShards()).size());
	}
	
	/**
	 * Test delete entities with where clause and parameters
	 * @throws SQLException
	 */
	@Test
	public void testDeleteWithWhereClauseAllShardsAsync() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);

		DalHints hints = new DalHints();
		int res;
		
		// By allShards
		assertEquals(3, getCountByDb(dao, 0));
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, hints.inAllShards().asyncExecution());
		try {
			res = (Integer)hints.getAsyncResult().get();
		} catch (Exception e) {
			fail();
		}
		assertResEquals(6, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().inAllShards()).size());
	}
	
	@Test
	public void testDeleteWithWhereClauseAllShardsCallback() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);

		IntCallback callback = new IntCallback();
		DalHints hints = new DalHints().callbackWith(callback);
		int res;
		
		// By allShards
		assertEquals(3, getCountByDb(dao, 0));
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, hints.inAllShards().asyncExecution());
		assertEquals(0, res);
		res = callback.getInt();
		assertResEquals(6, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().inAllShards()).size());
	}
	
	/**
	 * Test delete entities with where clause and parameters
	 * @throws SQLException
	 */
	@Test
	public void testDeleteWithWhereClauseShards() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);

		DalHints hints = new DalHints();
		int res;
		
		// By shards
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		assertEquals(3, getCountByDb(dao, 0));
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, new DalHints().inShards(shards));
		assertResEquals(6, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().inShards(shards)).size());
	}
	
	@Test
	public void testDeleteWithWhereClauseShardsAsync() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);

		DalHints hints = new DalHints().asyncExecution();
		int res;
		
		// By shards
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		assertEquals(3, getCountByDb(dao, 0));
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, hints.inShards(shards));
		assertEquals(0, res);
		res = getInt(hints);
		assertResEquals(6, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().inShards(shards)).size());
	}
	
	@Test
	public void testDeleteWithWhereClauseShardsCallback() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);

		DalHints hints = new DalHints();
		IntCallback callback = new IntCallback();
		hints.callbackWith(callback);
		
		int res;
		
		// By shards
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		assertEquals(3, getCountByDb(dao, 0));
		assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, hints.inShards(shards));
		assertEquals(0, res);
		res = callback.getInt();
		assertResEquals(6, res);
		assertEquals(0, dao.query(whereClause, parameters, new DalHints().inShards(shards)).size());
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
		
		// By shard
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().inShard(0));
		assertResEquals(1, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(0)).getAddress());

		// By shardValue
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().setShardValue(1));
		assertResEquals(1, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardValue(1)).getAddress());
		
		// By shardColValue
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().setShardColValue("index", 2));
		assertResEquals(1, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("index", 2)).getAddress());
		
		// By shardColValue
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().setShardColValue("tableIndex", 3));
		assertResEquals(1, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("tableIndex", 3)).getAddress());
	}
	
	/**
	 * Test plain update with SQL
	 * @throws SQLException
	 */
	@Test
	public void testUpdatePlainAllShards() throws SQLException{
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int res;
		
		// By allShards
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().inAllShards());
		assertResEquals(2, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(0)).getAddress());
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(1)).getAddress());
	}
	
	@Test
	public void testUpdatePlainAllShardsAsync() throws SQLException{
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints().asyncExecution();
		int res;
		
		// By allShards
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, hints.inAllShards());
		assertEquals(0, res);
		res = getInt(hints);
		assertResEquals(2, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(0)).getAddress());
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(1)).getAddress());
	}
	
	@Test
	public void testUpdatePlainAllShardsCallback() throws SQLException{
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		StatementParameters parameters = new StatementParameters();
		IntCallback callback = new IntCallback();
		DalHints hints = new DalHints().callbackWith(callback);
		
		int res;
		
		// By allShards
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, hints.inAllShards());
		assertEquals(0, res);
		res = callback.getInt();
		assertResEquals(2, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(0)).getAddress());
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(1)).getAddress());
	}
	
	/**
	 * Test plain update with SQL
	 * @throws SQLException
	 */
	@Test
	public void testUpdatePlainShards() throws SQLException{
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int res;
		
		// By shards
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().inShards(shards));
		assertResEquals(2, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(0)).getAddress());
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(1)).getAddress());
	}
	
	@Test
	public void testUpdatePlainShardsAsync() throws SQLException{
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints().asyncExecution();
		int res;
		
		// By shards
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, hints.inShards(shards));
		assertEquals(0, res);
		res = getInt(hints);
		assertResEquals(2, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(0)).getAddress());
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(1)).getAddress());
	}

	@Test
	public void testUpdatePlainShardsCallback() throws SQLException{
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		StatementParameters parameters = new StatementParameters();
		IntCallback callback = new IntCallback();
		DalHints hints = new DalHints().callbackWith(callback);
		int res;
		
		// By shards
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, hints.inShards(shards));
		assertEquals(0, res);
		res = callback.getInt();
		assertResEquals(2, res);
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(0)).getAddress());
		assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(1)).getAddress());
	}

    private List<ClientTestModel> create6Entities() {
        List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
        for (int i = 0; i < 6; i++) {
            ClientTestModel model = new ClientTestModel();
            model.setId(i+1);
            model.setAddress("aaa");
            model.setTableIndex(i);
            entities.add(model);
        }
        return entities;
    }
    
	@Test
	public void testCrossShardInsert() {
		if(!diff.supportInsertValues)return;
		
		try {
			int res = 0;
			deleteAllShardsByDb(dao, mod);
			
			ClientTestModel p = new ClientTestModel();
			
			List<ClientTestModel> pList = create6Entities();
			
			assertEquals(0, getCountByDb(dao, 0));
			assertEquals(0, getCountByDb(dao, 1));

			KeyHolder keyholder = createKeyHolder();
			DalHints hints = new DalHints();
			res = dao.combinedInsert(hints, keyholder, pList);
			assertResEquals(6, res);
			assertEquals(3, getCountByDb(dao, 0));
			assertEquals(3, getCountByDb(dao, 1));
			assertKeyHolderCrossShard(keyholder);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

    @Test
    public void testCrossShardInsertPkInsertBack() {
        if(!diff.supportInsertValues || !INSERT_PK_BACK_ALLOWED )return;
        
        try {
            int res = 0;
            deleteAllShardsByDb(dao, mod);
            
            List<ClientTestModel> pList = create6Entities();
            int i = 0;
            IdentitySetBackHelper.clearId(pList);
            
            assertEquals(0, getCountByDb(dao, 0));
            assertEquals(0, getCountByDb(dao, 1));

            KeyHolder keyholder = new KeyHolder();
            DalHints hints = new DalHints();
            res = dao.combinedInsert(hints.setIdentityBack(), keyholder, pList);
            assertResEquals(6, res);
            assertEquals(3, getCountByDb(dao, 0));
            assertEquals(3, getCountByDb(dao, 1));
            assertKeyHolderCrossShard(keyholder);
            
            IdentitySetBackHelper.assertIdentity(dao, pList);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

	@Test
	public void testCrossShardInsertAsync() {
		if(!diff.supportInsertValues)return;

		try {
			int res = 0;
			deleteAllShardsByDb(dao, mod);
			
			ClientTestModel p = new ClientTestModel();
			
			List<ClientTestModel> pList = create6Entities();
			
			assertEquals(0, getCountByDb(dao, 0));
			assertEquals(0, getCountByDb(dao, 1));

			KeyHolder keyholder = createKeyHolder();
			DalHints hints = new DalHints().asyncExecution();
			res = dao.combinedInsert(hints, keyholder, pList);
			assertEquals(0, res);
			res = getInt(hints);
			assertResEquals(6, res);
			assertEquals(3, getCountByDb(dao, 0));
			assertEquals(3, getCountByDb(dao, 1));
			assertKeyHolderCrossShard(keyholder);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCrossShardInsertCallback() {
		if(!diff.supportInsertValues)return;

		try {
			int res = 0;
			deleteAllShardsByDb(dao, mod);
			
			ClientTestModel p = new ClientTestModel();
			
			List<ClientTestModel> pList = create6Entities();
			
			assertEquals(0, getCountByDb(dao, 0));
			assertEquals(0, getCountByDb(dao, 1));

			KeyHolder keyholder = createKeyHolder();
			IntCallback callback = new IntCallback();
			DalHints hints = new DalHints().callbackWith(callback);
			res = dao.combinedInsert(hints, keyholder, pList);
			assertEquals(0,  res);
			res = callback.getInt();
			assertResEquals(6, res);
			assertEquals(3, getCountByDb(dao, 0));
			assertEquals(3, getCountByDb(dao, 1));
			assertKeyHolderCrossShard(keyholder);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void assertKeyHolderCrossShard(KeyHolder holder) throws SQLException {
		if(!ASSERT_ALLOWED)
			return;

		assertEquals(6, holder.size());
//		for(int i = 0; i < mod; i++) {
//			assertEquals(3, detailResults.getResultByDb(String.valueOf(i)).size());
//		}
//		for(int i = 0; i < 6; i++) {
//			assertTrue(holder.getKey(i).longValue() > 0);
//			assertTrue(holder.getKeyList().get(i).containsKey(GENERATED_KEY));
//		}
	}
		
	@Test
	public void testCrossShardBatchInsert() {
		try {
			Map<String, int[]>  res;
			deleteAllShardsByDb(dao, mod);
			
			ClientTestModel p = new ClientTestModel();
			
			List<ClientTestModel> pList = create6Entities();
			
			DalHints hints = new DalHints();
			int[] resx = dao.batchInsert(hints, pList);
			
			assertEquals(6, resx.length);
			assertEquals(3, getCountByDb(dao, 0));
			assertEquals(3, getCountByDb(dao, 1));
			
			assertResEquals(new int[]{1, 1, 1, 1, 1, 1}, resx);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCrossShardBatchInsertAsync() {
		try {
			Map<String, int[]>  res;
			deleteAllShardsByDb(dao, mod);
			
			ClientTestModel p = new ClientTestModel();
			
			List<ClientTestModel> pList = create6Entities();
			
			DalHints hints = new DalHints().asyncExecution();
			int[] resx = dao.batchInsert(hints, pList);
			assertNull(resx);
			resx = getIntArray(hints);
			
			assertEquals(6, resx.length);
			assertEquals(3, getCountByDb(dao, 0));
			assertEquals(3, getCountByDb(dao, 1));
			
			assertResEquals(new int[]{1, 1, 1, 1, 1, 1}, resx);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCrossShardBatchInsertCallback() {
		try {
			Map<String, int[]>  res;
			deleteAllShardsByDb(dao, mod);
			
			ClientTestModel p = new ClientTestModel();
			
			List<ClientTestModel> pList = create6Entities();
			
			IntCallback callback = new IntCallback();
			DalHints hints = new DalHints().callbackWith(callback);
			int[] resx = dao.batchInsert(hints, pList);
			assertNull(resx);
			resx = callback.getIntArray();
			
			assertEquals(6, resx.length);
			assertEquals(3, getCountByDb(dao, 0));
			assertEquals(3, getCountByDb(dao, 1));
			
			assertResEquals(new int[]{1, 1, 1, 1, 1, 1}, resx);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCrossShardDelete() {
		try {
			int[] res;
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
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(2);
			pList[2] = p;
			
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(3);
			pList[3] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(5);
			pList[4] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(6);
			pList[5] = p;
			
			DalHints hints = new DalHints();
			res = dao.batchDelete(hints, Arrays.asList(pList));
			assertEquals(0, getCountByDb(dao, 0));
			assertEquals(0, getCountByDb(dao, 1));
			
			assertResEquals(new int[]{1, 1, 1, 1, 1, 1}, res);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCrossShardDeleteAsync() {
		try {
			int[] res;
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
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(2);
			pList[2] = p;
			
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(3);
			pList[3] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(5);
			pList[4] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(6);
			pList[5] = p;
			
			DalHints hints = new DalHints().asyncExecution();
			res = dao.batchDelete(hints, Arrays.asList(pList));
			assertNull(res);
			res = getIntArray(hints);
			assertEquals(0, getCountByDb(dao, 0));
			assertEquals(0, getCountByDb(dao, 1));
			
			assertResEquals(new int[]{1, 1, 1, 1, 1, 1}, res);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCrossShardDeleteCallback() {
		try {
			int[] res;
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
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(2);
			pList[2] = p;
			
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(3);
			pList[3] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(5);
			pList[4] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(6);
			pList[5] = p;
			
			IntCallback callback = new IntCallback();
			DalHints hints = new DalHints().callbackWith(callback);
			res = dao.batchDelete(hints, Arrays.asList(pList));
			assertNull(res);
			res = callback.getIntArray();
			assertEquals(0, getCountByDb(dao, 0));
			assertEquals(0, getCountByDb(dao, 1));
			
			assertResEquals(new int[]{1, 1, 1, 1, 1, 1}, res);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}

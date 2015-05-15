package com.ctrip.platform.dal.tester.shard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.QueryCallback;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.ShortRowMapper;

public class DalQueryDaoTest {
	String DATABASE_NAME = DATABASE_NAME_SQLSVR;
	
	private final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr_dbShard";
	private final static String DATABASE_NAME_MOD = DATABASE_NAME_SQLSVR;
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	
	//Create the the table
	private final static String DROP_TABLE_SQL_SQLSVR_TPL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQLSVR_TPL = "CREATE TABLE " + TABLE_NAME +"("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	private static DalClient clientSqlSvr;
	
	static {
		try {
			DalClientFactory.initClientFactory();
			clientSqlSvr = DalClientFactory.getClient(DATABASE_NAME_SQLSVR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		// For SQL server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		for(int i = 0; i < mod; i++) {
			sqls = new String[] {DROP_TABLE_SQL_SQLSVR_TPL, CREATE_TABLE_SQL_SQLSVR_TPL};
			for (int j = 0; j < sqls.length; j++) {
				clientSqlSvr.update(sqls[j], parameters, hints.inShard(i));
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		//For Sql Server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		for(int i = 0; i < mod; i++) {
			clientSqlSvr.update(DROP_TABLE_SQL_SQLSVR_TPL, parameters, hints.inShard(i));
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		//For Sql Server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[2 + 3];
			insertSqls[0] = "SET IDENTITY_INSERT "+ TABLE_NAME + " ON";
			for(int j = 0; j < 3; j ++) {
				int id = j + 1;
				int quantity = 10 + j;
				insertSqls[j + 1] = "INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
							+ " VALUES(" + id + ", " + quantity + "," + i + ", 'SH INFO')";
			}
					
			insertSqls[4] = "SET IDENTITY_INSERT "+ TABLE_NAME +" OFF";
			clientSqlSvr.batchUpdate(insertSqls, hints.inShard(i));
		}
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		sql = "DELETE FROM " + TABLE_NAME;
		parameters = new StatementParameters();
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			clientSqlSvr.update(sql, parameters, hints.inShard(i));
		}
	}

	public void insertBack() {
		try {
			setUp();
		} catch (Exception e) {
			fail();
		}
	}

	
	private StatementParameters parameters = new StatementParameters();
	private String sqlList = "select * from " + TABLE_NAME;
	private String sqlListQuantity = "select quantity from " + TABLE_NAME;
	private String sqlObject = "select * from " + TABLE_NAME + " where id = ? and type=0";
	private String sqlFirst = "select * from " + TABLE_NAME + " where id = ?";
	private String sqlNoResult = "select * from " + TABLE_NAME + " where id = -1";
	
	private class TestComparator implements Comparator<Short>{
		@Override
		public int compare(Short o1, Short o2) {
			return o1.compareTo(o2);
		}
	}
	
	private class TestResultMerger implements ResultMerger<List<Short>> {
		List<Short> result = new ArrayList<>();

		public void addPartial(String shard, List<Short> partial) throws SQLException {
			result.addAll(partial);
		}

		public List<Short> merge() throws SQLException {
			Collections.sort(result);
			Collections.reverse(result);
			return result;
		}
	 }
	
	private class TestQueryCallback implements QueryCallback {

		@Override
		public <T> void onResult(T result) {
			assertNotNull(result);
			List value = (List)result;
			assertEquals(6, value.size());
		}
	}
	
	//Factors: sync/async; merger/no merger; sorter/no sorter, callback/no callback
	
	/**
	 * The template method under test
	 * @throws SQLException 
	 */
	 private List<Short> queryListInAllShard(DalHints hints) throws SQLException {
		return new DalQueryDao(DATABASE_NAME).query(
				sqlList, parameters, 
				hints.inAllShards(), 
				new ShortRowMapper());
	}
	
	@Test
	public void testQueryListAllShardsSync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListInAllShard(hints);
			assertEquals(6, result.size());
			Short t = result.get(0);
			assertEquals(new Short((short)1), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListInAllShard(hints.asyncExecution());
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			Short s = fr.get().get(0);
			assertEquals(6, fr.get().size());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsWithMerger() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListInAllShard(hints.mergeBy(new TestResultMerger()));
			Short t = result.get(0);
			assertEquals(new Short((short)3), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsWithSorter() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListInAllShard(hints.sortBy(new TestComparator()));
			assertEquals(6, result.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testQueryListAllShardsWithCallback() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListInAllShard(hints.callbackWith(new TestQueryCallback()));
			assertNull(result);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsSyncWithClass() {
		try {
			DalHints hints = new DalHints();
			List<Integer> result = new DalQueryDao(DATABASE_NAME).query(
					sqlListQuantity, parameters, 
					hints.inAllShards(), 
					Integer.class);
			assertEquals(6, result.size());
			Integer t = result.get(0);
			assertEquals(new Integer(10), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsSyncWithRowCallback() {
		try {
			DalHints hints = new DalHints();
			TestDalRowCallback callback = new TestDalRowCallback();
			new DalQueryDao(DATABASE_NAME).query(
					sqlListQuantity, parameters, 
					hints.inAllShards(), 
					callback);
			// 66 = (10 + 11 + 12)*2
			assertEquals(66, callback.result.get());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsSyncWithRowCallbackSequential() {
		try {
			DalHints hints = new DalHints();
			TestDalRowCallback callback = new TestDalRowCallback();
			new DalQueryDao(DATABASE_NAME).query(
					sqlListQuantity, parameters, 
					hints.inAllShards().sequentialExecute(), 
					callback);
			// 66 = (10 + 11 + 12)*2
			assertEquals(66, callback.result.get());
		} catch (Exception e) {
			fail();
		}
	}
	
	private static class TestDalRowCallback implements DalRowCallback {
		AtomicInteger result = new AtomicInteger();
		public void process(ResultSet rs) throws SQLException {
			result.addAndGet(rs.getShort("quantity"));
		}
	}
	
	private static class TestDalRowCallback2 implements DalRowCallback {
		int result;
		public void process(ResultSet rs) throws SQLException {
			result+=rs.getShort("quantity");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// QueryForObject Tests
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The template method under test
	 * @throws SQLException 
	 */
	private ClientTestModel queryForObjectInAllShard(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, 1);
		return new DalQueryDao(DATABASE_NAME).queryForObject(
				sqlObject, parameters, 
				hints.inAllShards(), 
				new ClientTestDalRowMapper());
	}
	
	@Test
	public void testQueryForObjectAllShardsSync() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = queryForObjectInAllShard(hints);
			assertNotNull(result);
			assertEquals(1, result.id);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsSyncFail() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, 1);
			ClientTestModel result = new DalQueryDao(DATABASE_NAME).queryForObject(
					sqlFirst, parameters, 
					hints.inAllShards(), 
					new ClientTestDalRowMapper());
			fail();
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsAsync() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = queryForObjectInAllShard(hints.asyncExecution());
			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			assertNotNull(fr.get());
			assertEquals(1, fr.get().id);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsAsyncFail() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = new DalQueryDao(DATABASE_NAME).queryForObject(
					sqlFirst, parameters, 
					hints.inAllShards().asyncExecution(), 
					new ClientTestDalRowMapper());
			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			fr.get();
			fail();
		} catch (Exception e) {
		}
	}
	
	private class TestSingleResultMerger<T> implements ResultMerger<T>{
		private T result;
		
		@Override
		public void addPartial(String shard, T partial) throws SQLException {
			result = partial;
		}

		@Override
		public T merge() {
			return result;
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsWithMerger() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result =queryForObjectInAllShard(hints.mergeBy(new TestSingleResultMerger()));
			assertNotNull(result);
			assertEquals(1, result.id);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsNullable() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			ClientTestModel result = new DalQueryDao(DATABASE_NAME).queryForObjectNullable(
					sqlNoResult, parameters, 
					hints.inAllShards(), 
					new ClientTestDalRowMapper());
			assertNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	private class TestQueryCallback2 implements QueryCallback {
		Object result;
		@Override
		public <T> void onResult(T result) {
			this.result = result;
		}
	}
	


	@Test
	public void testQueryForObjectAllShardsWithCallback() {
		try {
			TestQueryCallback2 callback = new TestQueryCallback2();
			DalHints hints = new DalHints();
			ClientTestModel result = queryForObjectInAllShard(hints.callbackWith(callback));
			assertNull(result);
			assertEquals(result, callback.result);
		} catch (Exception e) {
			fail();
		}
	}
	
	DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
	
	@Test
	public void testQueryForObjectAllShardsSyncWithClass() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, 1);
			Integer result = dao.queryForObject(
					sqlObject, parameters, 
					hints.inAllShards(), 
					Integer.class);
			assertEquals(1, result.intValue());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testQueryForObjectAllShardsSyncWithClassNullable() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			Integer result = dao.queryForObjectNullable(
					sqlNoResult, parameters, 
					hints.inAllShards(), 
					Integer.class);
			assertNull(result);
		} catch (Exception e) {
			fail();
		}
	}
	

	//////////////////////////////////////////////////////////////////////////////
	// QueryFirst Tests
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The template method under test
	 * @throws SQLException 
	 */
	private ClientTestModel queryFirstInAllShard(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, 1);
		return new DalQueryDao(DATABASE_NAME).queryFirst(
				sqlFirst, parameters, 
				hints.inAllShards(), 
				new ClientTestDalRowMapper());
	}
	
	@Test
	public void testQueryFirstAllShardsSync() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = queryFirstInAllShard(hints);
			assertNotNull(result);
			assertEquals(1, result.id);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryFirstAllShardsSyncFail() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, 1);
			ClientTestModel result = new DalQueryDao(DATABASE_NAME).queryFirst(
					sqlNoResult, parameters, 
					hints.inAllShards(), 
					new ClientTestDalRowMapper());
			fail();
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testQueryFirstAllShardsAsync() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = queryFirstInAllShard(hints.asyncExecution());
			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			assertNotNull(fr.get());
			assertEquals(1, fr.get().id);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryFirstAllShardsAsyncFail() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = new DalQueryDao(DATABASE_NAME).queryForObject(
					sqlNoResult, parameters, 
					hints.inAllShards().asyncExecution(), 
					new ClientTestDalRowMapper());
			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			fr.get();
			fail();
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testQueryFirstAllShardsWithMerger() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result =queryFirstInAllShard(hints.mergeBy(new TestSingleResultMerger()));
			assertNotNull(result);
			assertEquals(1, result.id);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryFirstAllShardsNullable() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			ClientTestModel result = new DalQueryDao(DATABASE_NAME).queryFirstNullable(
					sqlNoResult, parameters, 
					hints.inAllShards(), 
					new ClientTestDalRowMapper());
			assertNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryFirstAllShardsWithCallback() {
		try {
			TestQueryCallback2 callback = new TestQueryCallback2();
			DalHints hints = new DalHints();
			ClientTestModel result = queryFirstInAllShard(hints.callbackWith(callback));
			assertNull(result);
			assertEquals(result, callback.result);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryFirstAllShardsSyncWithClass() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, 1);
			Integer result = dao.queryFirst(
					sqlFirst, parameters, 
					hints.inAllShards(), 
					Integer.class);
			assertEquals(1, result.intValue());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testQueryFirstAllShardsSyncWithClassNullable() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			Integer result = dao.queryFirstNullable(
					sqlNoResult, parameters, 
					hints.inAllShards(), 
					Integer.class);
			assertNull(result);
		} catch (Exception e) {
			fail();
		}
	}
	
//	@Test
//	public void testQueryClassForList() {
//		try {
//			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
//			List<Integer> result = dao.query(sqlList, parameters, hints, Integer.class);
//			assertEquals(3, result.size());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	@Test
//	public void testQueryCallbackForList() {
//		try {
//			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
//			int a = 0;
//			DalRowCallbackTest test = new DalRowCallbackTest();
//			dao.query(sqlList, parameters, hints, test);
//			assertEquals(33, test.result);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	
//	@Test
//	public void testQueryForObject() {
//		DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
//		
//		Integer id;
//		// This will fail
//		try {
//			id = dao.queryForObject(sqlList, parameters, hints, new FixedValueRowMapper<Integer>());
//			fail();
//		} catch (SQLException e) {
//		}
//		
//		// This will pass
//		try {
//			StatementParameters parameters = new StatementParameters();
//			parameters.set(1, Types.INTEGER, 1);
//
//			id = dao.queryForObject(sqlObject, parameters, hints, new FixedValueRowMapper<Integer>(1));
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//		// This will fail
//		try {
//			dao.queryForObject(sqlList, parameters, hints, Integer.class);
//			fail();
//		} catch (SQLException e) {
//		}
//		
//		// This will pass
//		try {
//			StatementParameters parameters = new StatementParameters();
//			parameters.set(1, Types.INTEGER, 1);
//
//			dao.queryForObject(sqlObject, parameters, hints, Integer.class);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//	
//	@Test
//	public void testQueryForObjectNullable() {
//		DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
//		
//		Integer id;
//		try {
//			id = dao.queryForObjectNullable(sqlList, parameters, hints, new FixedValueRowMapper<Integer>());
//			fail();
//		} catch (SQLException e) {
//		}
//		
//		// This will pass
//		try {
//			StatementParameters parameters = new StatementParameters();
//			parameters.set(1, Types.INTEGER, 1);
//
//			id = dao.queryForObjectNullable(sqlObject, parameters, hints, new FixedValueRowMapper<Integer>(1));
//			
//			parameters = new StatementParameters();
//			parameters.set(1, Types.INTEGER, -1);
//
//			assertNull(dao.queryForObjectNullable(sqlObject, parameters, hints, new FixedValueRowMapper<Integer>(1)));
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//		// This will fail
//		try {
//			dao.queryForObjectNullable(sqlList, parameters, hints, Integer.class);
//			fail();
//		} catch (SQLException e) {
//		}
//		
//		// This will pass
//		try {
//			StatementParameters parameters = new StatementParameters();
//			parameters.set(1, Types.INTEGER, 1);
//
//			dao.queryForObjectNullable(sqlObject, parameters, hints, Integer.class);
//			
//			parameters = new StatementParameters();
//			parameters.set(1, Types.INTEGER, -1);
//			
//			assertNull(dao.queryForObjectNullable(sqlObject, parameters, hints, Integer.class));
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//	
//	@Test
//	public void testRange() {
//		try {
//			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
//			DalRowMapper<Integer> mapper = new DalRowMapper<Integer>() {
//				@Override
//				public Integer map(ResultSet rs, int rowNum) throws SQLException {
//					return rs.getInt("Id");
//				}
//			};
//			
//			Integer id = dao.queryFirst(sqlList, parameters, hints, mapper);
//			assertNotNull(id);
//			
//			id = dao.queryFirst(sqlList, parameters, hints, Integer.class);
//			assertNotNull(id);
//			
//			id = dao.queryFirstNullable(sqlList, parameters, hints, mapper);
//			assertNotNull(id);
//			
//			id = dao.queryFirstNullable(sqlList, parameters, hints, Integer.class);
//			assertNotNull(id);
//			
//			id = dao.queryFirstNullable(sqlNoResult, parameters, hints, mapper);
//			assertNull(id);
//			
//			id = dao.queryFirstNullable(sqlNoResult, parameters, hints, Integer.class);
//			assertNull(id);
//
//			List<Integer> result = dao.queryTop(sqlList, parameters, hints, mapper, 5);
//			assertEquals(3, result.size());
//			
//			result = dao.queryTop(sqlListQuantity, parameters, hints, Integer.class, 5);
//			assertEquals(3, result.size());
//
//			result = dao.queryFrom(sqlList, parameters, hints, mapper, 3, 5);
//			assertEquals(0, result.size());
//			
//			result = dao.queryFrom(sqlListQuantity, parameters, hints, mapper, 3, 5);
//			assertEquals(0, result.size());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	private static class ClientTestModel {
		private int id;
		private int quantity;
		private short type;
		private String address;
		private Timestamp lastChanged;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public short getType() {
			return type;
		}

		public void setType(short type) {
			this.type = type;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Timestamp getLastChanged() {
			return lastChanged;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.lastChanged = lastChanged;
		}
	}

	private static class ClientTestDalRowMapper implements
			DalRowMapper<ClientTestModel> {

		@Override
		public ClientTestModel map(ResultSet rs, int rowNum)
				throws SQLException {
			ClientTestModel model = new ClientTestModel();
			model.setId(rs.getInt(1));
			model.setQuantity(rs.getInt(2));
			model.setType(rs.getShort(3));
			model.setAddress(rs.getString(4));
			model.setLastChanged(rs.getTimestamp(5));
			return model;
		}
	}
}

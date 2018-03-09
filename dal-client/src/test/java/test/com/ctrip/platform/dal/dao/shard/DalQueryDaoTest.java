package test.com.ctrip.platform.dal.dao.shard;

import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalResultCallback;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalListMerger;
import com.ctrip.platform.dal.dao.helper.ShortRowMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.MultipleSqlBuilder;

public abstract class DalQueryDaoTest {
    private String logicDbName;
	private DatabaseCategory dbCategory;
	private DalQueryDao dao;
	private final static String TABLE_NAME = "dal_client_test";
	
	public DalQueryDaoTest(String DATABASE_NAME, DatabaseCategory dbCategory){
		this.dbCategory = dbCategory;
		dao = new DalQueryDao(DATABASE_NAME);
		logicDbName = DATABASE_NAME;
	}
	
	public abstract void insertBack();
	
	private String sqlList = "select * from " + TABLE_NAME + " order by id";
	private String sqlListQuantity = "select quantity from " + TABLE_NAME;
	private String sqlObject = "select * from " + TABLE_NAME + " where id = ? and type=0";
	private String sqlFirst = "select * from " + TABLE_NAME + " where id = ?";
	private String sqlNoResult = "select * from " + TABLE_NAME + " where id = -1";
	private String sqlInParam = "select * from " + TABLE_NAME + " where type in (?)";
	private String sqlIdInParam = "select * from " + TABLE_NAME + " where id in (?)";
	
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
	
	private class TestQueryCallback implements DalResultCallback {

		@Override
		public <T> void onResult(T result) {
			assertNotNull(result);
			List value = (List)result;
			assertEquals(6, value.size());
		}

		@Override
		public void onError(Throwable e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	//Factors: sync/async; merger/no merger; sorter/no sorter, callback/no callback
	
	private StatementParameters parameters() {return new StatementParameters();}
	
	/**
	 * The template method under test
	 * @throws SQLException 
	 */
	 private List<Short> queryListInAllShard(DalHints hints) throws SQLException {
		return dao.query(
				sqlList, parameters(), 
				hints.inAllShards(), 
				new ShortRowMapper());
	}
	
	@Test
	public void testQueryListAllShards() {
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
    public void testQueryListAllShardsBuilder() throws SQLException {
        String address = null;
        Integer id = 1;
        
        FreeSelectSqlBuilder<List<ClientTestModel>> query = new FreeSelectSqlBuilder<>();

        query.selectAll().from(TABLE_NAME).where(like("address ").nullable(address), AND, equal("id").nullable(id));

        
        query.mapWith(ClientTestModel.class);

        DalQueryDao dao = new DalQueryDao(logicDbName);
        
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, 1);
        List<ClientTestModel> l = dao.query(query, parameters, new DalHints().inAllShards());
        Assert.assertTrue(l.size() > 0);
    }

    @Test
    public void testQueryListAllShardsBuilderWhen() throws SQLException {
        String address = null;
        Integer id = 1;
        
        FreeSelectSqlBuilder<List<ClientTestModel>> query = new FreeSelectSqlBuilder<>();

        query.selectAll().from(TABLE_NAME).where(like("address ").when(address!=null), AND, equal("id").when(id!=null));

        
        query.mapWith(ClientTestModel.class);

        DalQueryDao dao = new DalQueryDao(logicDbName);
        
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, 1);
        List<ClientTestModel> l = dao.query(query, parameters, new DalHints().inAllShards());
        Assert.assertTrue(l.size() > 0);
    }

	
	private DalDefaultJpaMapper<ClientTestModel> jpaMapper() throws SQLException{
		return new DalDefaultJpaMapper<>(ClientTestModel.class);
	}
	
	@Test
	public void testQueryPartialAllShards() {
		try {
			assertListPartial(dao.query(sqlList, parameters(),new DalHints().inAllShards().partialQuery("quantity", "address"),jpaMapper()));

			assertPartial(dao.queryFirst(sqlList, parameters(),new DalHints().inAllShards().partialQuery("quantity", "address"),jpaMapper()));
			
			assertPartial(dao.queryFirstNullable(sqlList, parameters(),new DalHints().inAllShards().partialQuery("quantity", "address"),jpaMapper()));
			
			assertPartial(dao.queryForObject(sqlObject, parameters().set(1, Types.INTEGER, 1),new DalHints().inAllShards().partialQuery("quantity", "address"),jpaMapper()));
			
			assertPartial(dao.queryForObjectNullable(sqlObject, parameters().set(1, Types.INTEGER, 1),new DalHints().inAllShards().partialQuery("quantity", "address"),jpaMapper()));
			
			assertListPartial(dao.queryFrom(sqlList, parameters(),new DalHints().inAllShards().partialQuery("quantity", "address"),jpaMapper(), 1 ,10));
			
			assertListPartial(dao.queryTop(sqlList, parameters(),new DalHints().inAllShards().partialQuery("quantity", "address"),jpaMapper(), 10));
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListInAllShard(hints.asyncExecution());
			
			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();
			
			Short s = result.get(0);
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
	public void testQueryListAllShardsWithMergerAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListInAllShard(hints.mergeBy(new TestResultMerger()).asyncExecution());
			
			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();
			
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

			assertEquals(new Short((short)1), result.get(0));
			assertEquals(new Short((short)1), result.get(1));
			assertEquals(new Short((short)2), result.get(2));
			assertEquals(new Short((short)2), result.get(3));
			assertEquals(new Short((short)3), result.get(4));
			assertEquals(new Short((short)3), result.get(5));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testQueryListAllShardsWithSorterAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListInAllShard(hints.sortBy(new TestComparator()).asyncExecution());

			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();
			
			assertEquals(6, result.size());
			
			assertEquals(new Short((short)1), result.get(0));
			assertEquals(new Short((short)1), result.get(1));
			assertEquals(new Short((short)2), result.get(2));
			assertEquals(new Short((short)2), result.get(3));
			assertEquals(new Short((short)3), result.get(4));
			assertEquals(new Short((short)3), result.get(5));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	// Test in parameters
	 private List<Short> queryListForInParamOld(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		
		List<Integer> inParam = new ArrayList<>();
		inParam.add(0);
		inParam.add(1);
		inParam.add(2);
		inParam.add(3);
		inParam.add(4);
		
		parameters.setInParameter(1, "type", Types.INTEGER, inParam);
		
		String sql = SQLParser.parse(sqlInParam, inParam);
		return dao.query(
				sql, parameters, 
				hints.inAllShards(), 
				new ShortRowMapper());
	}
	 
		// Test in parameters
	 private List<Short> queryListForInParamBuilder(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		
		List<Integer> inParam = new ArrayList<>();
		inParam.add(0);
		inParam.add(1);
		inParam.add(2);
		inParam.add(3);
		inParam.add(4);
		
		parameters.setInParameter(1, "type", Types.INTEGER, inParam);
		FreeSelectSqlBuilder<List<Short>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate(sqlInParam);
		builder.mapWith(new ShortRowMapper());
		return dao.query(
				builder, parameters, 
				hints.shardBy("type"));
	}
	 
     // Test in parameters
	 private List<Short> queryListForInParamBuilderNew(DalHints hints) throws SQLException {
	     StatementParameters parameters = new StatementParameters();
         FreeSelectSqlBuilder<List<Short>> builder = new FreeSelectSqlBuilder<>(dbCategory);
         builder.with(parameters);

	     List<Integer> inParam = new ArrayList<>();
	     inParam.add(0);
	     inParam.add(1);
	     inParam.add(2);
	     inParam.add(3);
	     inParam.add(4);
	     
	     builder.setIn("type", inParam, Types.INTEGER);
	     builder.setTemplate(sqlInParam);
	     builder.mapWith(new ShortRowMapper());
	     return dao.query(
	             builder, parameters,
	             hints.shardBy("type"));
	 }
  
     // Test in parameters
     private List<Short> queryListForInParamBuilderNew1(DalHints hints) throws SQLException {
         StatementParameters parameters = new StatementParameters();
         FreeSelectSqlBuilder<List<Short>> builder = new FreeSelectSqlBuilder<>();
         builder.with(parameters);

         List<Integer> inParam = new ArrayList<>();
         inParam.add(0);
         inParam.add(1);
         inParam.add(2);
         inParam.add(3);
         inParam.add(4);
         
         builder.selectAll().from(TABLE_NAME).where(in("type", inParam, Types.INTEGER));
         builder.mapWith(new ShortRowMapper());
         return dao.query(
                 builder, parameters,
                 hints.shardBy("type"));
     }

	 @Test
	public void testQueryListInParams() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamOld(hints);
			assertEquals(6, result.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryListInParamsAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamOld(hints.asyncExecution());
			
			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();
			
			Short s = result.get(0);
			assertEquals(6, fr.get().size());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryListInParamsWithMerger() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamOld(hints.mergeBy(new TestResultMerger()));
			Short t = result.get(0);
			assertEquals(6, result.size());
			assertEquals(new Short((short)3), t);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryListInParamsWithMergerAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamOld(hints.mergeBy(new TestResultMerger()).asyncExecution());

			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();
			
			Short t = result.get(0);
			assertEquals(new Short((short)3), t);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryListInParamsWithSorter() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamOld(hints.sortBy(new TestComparator()));
			assertEquals(6, result.size());
			
			assertEquals(new Short((short)1), result.get(0));
			assertEquals(new Short((short)1), result.get(1));
			assertEquals(new Short((short)2), result.get(2));
			assertEquals(new Short((short)2), result.get(3));
			assertEquals(new Short((short)3), result.get(4));
			assertEquals(new Short((short)3), result.get(5));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testQueryListInParamsWithSorterAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamOld(hints.sortBy(new TestComparator()).asyncExecution());

			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();
			
			assertEquals(new Short((short)1), result.get(0));
			assertEquals(new Short((short)1), result.get(1));
			assertEquals(new Short((short)2), result.get(2));
			assertEquals(new Short((short)2), result.get(3));
			assertEquals(new Short((short)3), result.get(4));
			assertEquals(new Short((short)3), result.get(5));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
		
	@Test
	public void testQueryListInParamsBuilder() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamBuilder(hints);
			assertEquals(6, result.size());

            hints = new DalHints();
            result = queryListForInParamBuilderNew(hints);
            assertEquals(6, result.size());
		
            hints = new DalHints();
            result = queryListForInParamBuilderNew1(hints);
            assertEquals(6, result.size());
        
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryListInParamsBuilderAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamBuilder(hints.asyncExecution());
			
			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();
			
			Short s = result.get(0);
			assertEquals(6, fr.get().size());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryListInParamsBuilderWithMerger() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamBuilder(hints.mergeBy(new TestResultMerger()));
			Short t = result.get(0);
			assertEquals(6, result.size());
			assertEquals(new Short((short)3), t);
			
			hints = new DalHints();
            result = queryListForInParamBuilderNew(hints.mergeBy(new TestResultMerger()));
            t = result.get(0);
            assertEquals(6, result.size());
            assertEquals(new Short((short)3), t);
            
            hints = new DalHints();
            result = queryListForInParamBuilderNew1(hints.mergeBy(new TestResultMerger()));
            t = result.get(0);
            assertEquals(6, result.size());
            assertEquals(new Short((short)3), t);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryListInParamsBuilderWithMergerAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamBuilder(hints.mergeBy(new TestResultMerger()).asyncExecution());

			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();
			
			Short t = result.get(0);
			assertEquals(new Short((short)3), t);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryListInParamsBuilderWithSorter() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamBuilder(hints.sortBy(new TestComparator()));
			assertEquals(6, result.size());
			
			assertEquals(new Short((short)1), result.get(0));
			assertEquals(new Short((short)1), result.get(1));
			assertEquals(new Short((short)2), result.get(2));
			assertEquals(new Short((short)2), result.get(3));
			assertEquals(new Short((short)3), result.get(4));
			assertEquals(new Short((short)3), result.get(5));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testQueryListInParamsBuilderWithSorterAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryListForInParamBuilder(hints.sortBy(new TestComparator()).asyncExecution());

			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();
			
			assertEquals(new Short((short)1), result.get(0));
			assertEquals(new Short((short)1), result.get(1));
			assertEquals(new Short((short)2), result.get(2));
			assertEquals(new Short((short)2), result.get(3));
			assertEquals(new Short((short)3), result.get(4));
			assertEquals(new Short((short)3), result.get(5));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	private class ClientTestModelComparator implements Comparator<ClientTestModel>{
		@Override
		public int compare(ClientTestModel o1, ClientTestModel o2) {
			return new Integer(o1.getId()).compareTo(o2.getId());
		}
	}

	private class InteregrComparator implements Comparator<Integer>{
		@Override
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
	}
	
	private class TestDalRowCallback3 implements DalRowCallback {

		@Override
		public void process(ResultSet rs) throws SQLException {
		}
	}

	private List queryMultipleAllShards(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.INTEGER, 1);
		
		MultipleSqlBuilder builder = new MultipleSqlBuilder();
		// TODO add all add method
		builder.addQuery(sqlList, new StatementParameters(), new ClientTestDalRowMapper());//mapper
		builder.addQuery(sqlList, new StatementParameters(), new ClientTestDalRowMapper(), new DalListMerger<ClientTestModel>());//merger
		builder.addQuery(sqlList, new StatementParameters(), ClientTestModel.class, new ClientTestModelComparator());//sorter
		builder.addQuery(sqlListQuantity, new StatementParameters(), Integer.class, new DalListMerger<Integer>());//merger
		builder.addQuery(sqlObject, parameters, Integer.class, new InteregrComparator());//soter
		builder.addQuery(sqlNoResult, new StatementParameters(), new TestDalRowCallback3());//callback
		
		parameters = new StatementParameters();
		List<Integer> inParam = new ArrayList<>();
		inParam.add(0);
		inParam.add(1);
		inParam.add(2);
		inParam.add(3);
		inParam.add(4);
		parameters.setInParameter(1, "type", Types.INTEGER, inParam);
		builder.addQuery(sqlIdInParam, parameters, ClientTestModel.class);

		return dao.query(builder, hints.inAllShards());
	}
	
	@Test
	public void testQueryMultipleAllShards() {
		if(dbCategory == DatabaseCategory.Oracle)
			return;
		
		try {
			List list = queryMultipleAllShards(new DalHints());
			
			assertMultipleResult(list);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryMultipleAllShardsAsync() {
		if(dbCategory == DatabaseCategory.Oracle)
			return;
		
		try {
			DalHints hints = new DalHints();
			List list = queryMultipleAllShards(hints.asyncExecution());
			
			assertNull(list);
			list = (List)hints.getAsyncResult().get();
			assertMultipleResult(list);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private void assertMultipleResult(List list) {
		assertEquals(7, list.size());
		assertEquals(6, ((List)list.get(0)).size());
		assertEquals(6, ((List)list.get(1)).size());
		assertEquals(6, ((List)list.get(2)).size());
		assertEquals(6, ((List)list.get(3)).size());
		assertEquals(1, ((List)list.get(4)).size());
		assertEquals(0, ((List)list.get(5)).size());
		assertEquals(6, ((List)list.get(6)).size());
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
	public void testQueryListAllShardsWithClass() {
		try {
			DalHints hints = new DalHints();
			List<Integer> result = dao.query(
					sqlListQuantity, parameters(), 
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
	public void testQueryListAllShardsAsyncWithClass() {
		try {
			DalHints hints = new DalHints();
			List<Integer> result = dao.query(
					sqlListQuantity, parameters(), 
					hints.inAllShards().asyncExecution(), 
					Integer.class);

			assertNull(result);
			Future<List<Integer>> fr = (Future<List<Integer>>)hints.getAsyncResult();
			result = fr.get();
			
			assertEquals(6, result.size());
			Integer t = result.get(0);
			assertEquals(new Integer(10), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsWithRowCallback() {
		try {
			DalHints hints = new DalHints();
			TestDalRowCallback callback = new TestDalRowCallback();
			dao.query(
					sqlListQuantity, parameters(), 
					hints.inAllShards(), 
					callback);
			// 66 = (10 + 11 + 12)*2
			assertEquals(66, callback.result.get());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsWithRowCallbackAsync() {
		try {
			DalHints hints = new DalHints();
			TestDalRowCallback callback = new TestDalRowCallback();
			dao.query(
					sqlListQuantity, parameters(), 
					hints.inAllShards().asyncExecution(), 
					callback);

			// Make sure the execution is completed
			hints.getAsyncResult().get();
			
			// 66 = (10 + 11 + 12)*2
			assertEquals(66, callback.result.get());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsWithRowCallbackSequential() {
		try {
			DalHints hints = new DalHints();
			TestDalRowCallback callback = new TestDalRowCallback();
			dao.query(
					sqlListQuantity, parameters(), 
					hints.inAllShards().sequentialExecute(), 
					callback);
			// 66 = (10 + 11 + 12)*2
			assertEquals(66, callback.result.get());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryListAllShardsWithRowCallbackSequentialAsync() {
		try {
			DalHints hints = new DalHints();
			TestDalRowCallback callback = new TestDalRowCallback();
			dao.query(
					sqlListQuantity, parameters(), 
					hints.inAllShards().sequentialExecute().asyncExecution(), 
					callback);
			
			// Make sure the execution is completed
			hints.getAsyncResult().get();

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
		return dao.queryForObject(
				sqlObject, parameters, 
				hints.inAllShards(), 
				new ClientTestDalRowMapper());
	}
	
	/**
	 * The template method under test
	 * @throws SQLException 
	 */
	private ClientTestModel queryForObjectInAllShardPartial(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, 1);
		return dao.queryForObject(
				sqlObject, parameters, 
				hints.inAllShards(), 
				new DalDefaultJpaMapper<>(ClientTestModel.class));
	}
	
	private void assertListPartial(List<ClientTestModel> result) {
		for(ClientTestModel model: result)
			assertPartial(model);
	}
	
	private void assertPartial(ClientTestModel result) {
		assertEquals(0, result.getId());
		assertEquals(0, result.getType());
		assertNull(result.getLastChanged());
		assertNotNull(result.getQuantity());
		assertEquals("SH INFO", result.getAddress());
	}
	
	@Test
	public void testQueryForObjectAllShards() {
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
	public void testQueryForObjectAllShardsPartial() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = queryForObjectInAllShardPartial(hints.partialQuery("quantity", "address"));
			assertPartial(result);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testQueryForObjectAllShardsFail() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, 1);
			ClientTestModel result = dao.queryForObject(
					sqlFirst, parameters, 
					hints.inAllShards(), 
					new ClientTestDalRowMapper());
			fail();
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsFailPartial() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, 1);
			ClientTestModel result = dao.queryForObject(
					sqlFirst, parameters, 
					hints.inAllShards().partialQuery("quantity", "address"), 
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
	public void testQueryForObjectAllShardsAsyncPartial() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = queryForObjectInAllShardPartial(hints.asyncExecution().partialQuery("quantity", "address"));
			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			result = fr.get();
			assertPartial(result);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsAsyncFail() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = dao.queryForObject(
					sqlFirst, parameters(), 
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
	public void testQueryForObjectAllShardsAsyncFailPartial() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = dao.queryForObject(
					sqlFirst, parameters(), 
					hints.inAllShards().asyncExecution().partialQuery("quantity", "address"), 
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
			ClientTestModel result = queryForObjectInAllShard(hints.mergeBy(new TestSingleResultMerger()));
			assertNotNull(result);
			assertEquals(1, result.id);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsWithMergerPartial() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = queryForObjectInAllShard(hints.mergeBy(new TestSingleResultMerger()).partialQuery("quantity", "address"));
			assertNotNull(result);
			assertEquals(1, result.id);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsWithMergerAsync() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = queryForObjectInAllShard(hints.mergeBy(new TestSingleResultMerger()).asyncExecution());
			
			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			result = fr.get();
			
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
			ClientTestModel result = dao.queryForObjectNullable(
					sqlNoResult, parameters(), 
					hints.inAllShards(), 
					new ClientTestDalRowMapper());
			assertNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsNullablePartial() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			ClientTestModel result = dao.queryForObjectNullable(
					sqlNoResult, parameters(), 
					hints.inAllShards().partialQuery("quantity", "address"), 
					new ClientTestDalRowMapper());
			assertNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsNullableAsync() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			ClientTestModel result = dao.queryForObjectNullable(
					sqlNoResult, parameters(), 
					hints.inAllShards().asyncExecution(), 
					new ClientTestDalRowMapper());
			
			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			result = fr.get();

			assertNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	private class TestQueryCallback2 implements DalResultCallback {
		Object result;
		@Override
		public <T> void onResult(T result) {
			this.result = result;
		}
		@Override
		public void onError(Throwable e) {
			// TODO Auto-generated method stub
			
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
		
		try {
			TestQueryCallback2 callback = new TestQueryCallback2();
			DalHints hints = new DalHints();
			ClientTestModel result = queryForObjectInAllShard(hints.callbackWith(callback).partialQuery("quantity", "address"));
			assertNull(result);
			assertEquals(result, callback.result);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsWithClass() {
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
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testQueryForObjectAllShardsWithClassAsync() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, 1);
			Integer result = dao.queryForObject(
					sqlObject, parameters, 
					hints.inAllShards().asyncExecution(), 
					Integer.class);

			assertNull(result);
			Future<Integer> fr = (Future<Integer>)hints.getAsyncResult();
			result = fr.get();
			
			assertEquals(1, result.intValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testQueryForObjectAllShardsWithClassNullable() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			Integer result = dao.queryForObjectNullable(
					sqlNoResult, parameters(), 
					hints.inAllShards(), 
					Integer.class);
			assertNull(result);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectAllShardsWithClassNullableAsync() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			Integer result = dao.queryForObjectNullable(
					sqlNoResult, parameters(), 
					hints.inAllShards().asyncExecution(), 
					Integer.class);

			assertNull(result);
			Future<Integer> fr = (Future<Integer>)hints.getAsyncResult();
			result = fr.get();

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
		return dao.queryFirst(
				sqlFirst, parameters, 
				hints.inAllShards(), 
				new ClientTestDalRowMapper());
	}
	
	private ClientTestModel queryFirstInAllShardPartial(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, 1);
		return dao.queryFirst(
				sqlFirst, parameters, 
				hints.inAllShards().partialQuery("quantity", "address"), 
				new DalDefaultJpaMapper<>(ClientTestModel.class));
	}
	
	@Test
	public void testQueryFirstAllShards() {
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
	public void testQueryFirstAllShardsPartial() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result = queryFirstInAllShardPartial(hints);
			assertPartial(result);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryFirstAllShardsFail() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, 1);
			ClientTestModel result = dao.queryFirst(
					sqlNoResult, parameters(), 
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
			ClientTestModel result = dao.queryForObject(
					sqlNoResult, parameters(), 
					hints.inAllShards().asyncExecution(), 
					new ClientTestDalRowMapper());
			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			result = fr.get();
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
	public void testQueryFirstAllShardsWithMergerAsync() {
		try {
			DalHints hints = new DalHints();
			ClientTestModel result =queryFirstInAllShard(hints.mergeBy(new TestSingleResultMerger()).asyncExecution());

			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			result = fr.get();

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
			ClientTestModel result = dao.queryFirstNullable(
					sqlNoResult, parameters(), 
					hints.inAllShards(), 
					new ClientTestDalRowMapper());
			assertNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryFirstAllShardsNullableAsync() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			ClientTestModel result = dao.queryFirstNullable(
					sqlNoResult, parameters(), 
					hints.inAllShards().asyncExecution(), 
					new ClientTestDalRowMapper());

			assertNull(result);
			Future<ClientTestModel> fr = (Future<ClientTestModel>)hints.getAsyncResult();
			result = fr.get();

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
	public void testQueryFirstAllShardsWithClass() {
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
	public void testQueryFirstAllShardsWithClassAsync() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, 1);
			Integer result = dao.queryFirst(
					sqlFirst, parameters, 
					hints.inAllShards().asyncExecution(), 
					Integer.class);

			assertNull(result);
			Future<Integer> fr = (Future<Integer>)hints.getAsyncResult();
			result = fr.get();

			assertEquals(1, result.intValue());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testQueryFirstAllShardsWithClassNullable() {
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
	
	@Test
	public void testQueryFirstAllShardsWithClassNullableAsync() {
		try {
			DalHints hints = new DalHints();
			StatementParameters parameters = new StatementParameters();
			Integer result = dao.queryFirstNullable(
					sqlNoResult, parameters, 
					hints.inAllShards().asyncExecution(), 
					Integer.class);

			assertNull(result);
			Future<Integer> fr = (Future<Integer>)hints.getAsyncResult();
			result = fr.get();

			assertNull(result);
		} catch (Exception e) {
			fail();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// QueryTop Tests
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * The template method under test
	 * @throws SQLException 
	 */
	 private List<Short> queryTopInAllShard(DalHints hints) throws SQLException {
		return dao.queryTop(
				sqlList, parameters(), 
				hints.inAllShards(), 
				new ShortRowMapper(), 4);
	}
	
	@Test
	public void testQueryTopAllShards() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryTopInAllShard(hints);
			assertEquals(4, result.size());
			Short t = result.get(0);
			assertEquals(new Short((short)1), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryTopAllShardsAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryTopInAllShard(hints.asyncExecution());
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			Short s = fr.get().get(0);
			assertEquals(new Short((short)1), s);
			assertEquals(4, fr.get().size());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryTopAllShardsWithMerger() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryTopInAllShard(hints.mergeBy(new TestResultMerger()));
			Short t = result.get(0);
			assertEquals(new Short((short)3), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryTopAllShardsWithMergerAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryTopInAllShard(hints.mergeBy(new TestResultMerger()).asyncExecution());

			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();

			Short t = result.get(0);
			assertEquals(new Short((short)3), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryTopAllShardsWithSorter() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryTopInAllShard(hints.sortBy(new TestComparator()));
			assertEquals(4, result.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryTopAllShardsWithSorterAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryTopInAllShard(hints.sortBy(new TestComparator()).asyncExecution());

			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();

			assertEquals(4, result.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testQueryTopAllShardsWithCallback() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryTopInAllShard(hints.callbackWith(new TestQueryCallback()));
			assertNull(result);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryTopAllShardsWithClass() {
		try {
			DalHints hints = new DalHints();
			List<Integer> result = dao.queryTop(
					sqlListQuantity, parameters(), 
					hints.inAllShards(), 
					Integer.class, 4);
			assertEquals(4, result.size());
			Integer t = result.get(0);
			assertEquals(new Integer(10), t);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testQueryTopAllShardsWithClassAsync() {
		try {
			DalHints hints = new DalHints();
			List<Integer> result = dao.queryTop(
					sqlListQuantity, parameters(), 
					hints.inAllShards().asyncExecution(), 
					Integer.class, 4);

			assertNull(result);
			Future<List<Integer>> fr = (Future<List<Integer>>)hints.getAsyncResult();
			result = fr.get();

			assertEquals(4, result.size());
			Integer t = result.get(0);
			assertEquals(new Integer(10), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// QueryFrom Tests
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * The template method under test
	 * @throws SQLException 
	 */
	 private List<Short> queryFromInAllShard(DalHints hints) throws SQLException {
		return dao.queryFrom(
				sqlList, parameters(), 
				hints.inAllShards(), 
				new ShortRowMapper(), 2, 4);
	}
	
	@Test
	public void testQueryFromAllShards() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryFromInAllShard(hints);
			assertEquals(4, result.size());

			//[3, 1, 2, 3]
			assertEquals(new Short((short)3), result.get(0));
			assertEquals(new Short((short)1), result.get(1));
			assertEquals(new Short((short)2), result.get(2));
			assertEquals(new Short((short)3), result.get(3));
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryFromAllShardsAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryFromInAllShard(hints.asyncExecution());
			
			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();

			//[3, 1, 2, 3]
			assertEquals(new Short((short)3), result.get(0));
			assertEquals(new Short((short)1), result.get(1));
			assertEquals(new Short((short)2), result.get(2));
			assertEquals(new Short((short)3), result.get(3));
			assertEquals(4, fr.get().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryFromAllShardsWithMerger() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryFromInAllShard(hints.mergeBy(new TestResultMerger()));
			Short t = result.get(0);
			assertEquals(new Short((short)3), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryFromAllShardsWithMergerAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryFromInAllShard(hints.mergeBy(new TestResultMerger()).asyncExecution());

			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();

			Short t = result.get(0);
			assertEquals(new Short((short)3), t);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryFromAllShardsWithSorter() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryFromInAllShard(hints.sortBy(new TestComparator()));
			assertEquals(4, result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testQueryFromAllShardsWithSorterAsync() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryFromInAllShard(hints.sortBy(new TestComparator()).asyncExecution());

			assertNull(result);
			Future<List<Short>> fr = (Future<List<Short>>)hints.getAsyncResult();
			result = fr.get();

			assertEquals(4, result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testQueryFromAllShardsWithCallback() {
		try {
			DalHints hints = new DalHints();
			List<Short> result = queryFromInAllShard(hints.callbackWith(new TestQueryCallback()));
			assertNull(result);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testQueryFromAllShardsWithClass() {
		try {
			DalHints hints = new DalHints();
			List<Integer> result = dao.queryFrom(
					sqlListQuantity, parameters(), 
					hints.inAllShards(), 
					Integer.class, 2, 4);
			assertEquals(4, result.size());
			assertEquals(new Integer(12), result.get(0));
			assertEquals(new Integer(10), result.get(1));
			assertEquals(new Integer(11), result.get(2));
			assertEquals(new Integer(12), result.get(3));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryFromAllShardsWithClassAsync() {
		try {
			DalHints hints = new DalHints();
			List<Integer> result = dao.queryFrom(
					sqlListQuantity, parameters(), 
					hints.inAllShards().asyncExecution(), 
					Integer.class, 2, 4);

			assertNull(result);
			Future<List<Integer>> fr = (Future<List<Integer>>)hints.getAsyncResult();
			result = fr.get();

			assertEquals(4, result.size());
			assertEquals(new Integer(12), result.get(0));
			assertEquals(new Integer(10), result.get(1));
			assertEquals(new Integer(11), result.get(2));
			assertEquals(new Integer(12), result.get(3));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Entity
	@Database(name="SqlServerSimpleShard")
	@Table(name="People")
	public static class ClientTestModel {
		@Id
		@Column(name="id")
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Type(value=Types.BIGINT)
		private int id;

		@Column(name="quantity")
		@Type(value=Types.INTEGER)
		private int quantity;

		@Column(name="type")
		@Type(value=Types.SMALLINT)
		private short type;
		
		@Column(name="address")
		@Type(value=Types.VARCHAR)
		private String address;
		
		@Column(name="last_changed")
		@Type(value=Types.TIMESTAMP)
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

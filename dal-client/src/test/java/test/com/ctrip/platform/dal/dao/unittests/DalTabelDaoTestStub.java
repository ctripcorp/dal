package test.com.ctrip.platform.dal.dao.unittests;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.com.ctrip.platform.dal.dao.unitbase.ClientTestDalParser;
import test.com.ctrip.platform.dal.dao.unitbase.ClientTestModel;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTabelDaoTestStub {
	private boolean validateBatchUpdateCount;
	private boolean supportGetGeneratedKeys;
	private boolean supportInsertValues;
	
	public DalTabelDaoTestStub(String dbName, boolean validateBatchUpdateCount, boolean supportGetGeneratedKeys, boolean supportInsertValues) {
		this.validateBatchUpdateCount = validateBatchUpdateCount;
		this.supportGetGeneratedKeys = supportGetGeneratedKeys;
		this.supportInsertValues = supportInsertValues;
		try {
			dao = new DalTableDao<ClientTestModel>(new ClientTestDalParser(dbName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final static String TABLE_NAME = "dal_client_test";
	private DalTableDao<ClientTestModel> dao = null;

	/**
	 * Test Query by Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPk() throws SQLException {
		ClientTestModel model = dao.queryByPk(1, new DalHints());
		Assert.assertTrue(null != model);
		Assert.assertEquals(10, model.getQuantity().intValue());
	}

	/**
	 * Query by Entity with Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPkWithEntity() throws SQLException{
		ClientTestModel pk = new ClientTestModel();
		pk.setId(1);
		ClientTestModel model = dao.queryByPk(pk, new DalHints());
		Assert.assertTrue(null != model);
		Assert.assertEquals(10, model.getQuantity().intValue());
	}
	
	/**
	 * Query by Entity without Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPkWithEntityNoId() throws SQLException{
		ClientTestModel pk = new ClientTestModel();
		try {
			Assert.assertNull(dao.queryByPk(pk, new DalHints()));
		} catch (SQLException e) { 
			Assert.fail();
		}
	}
	
	/**
	 * Query against sample entity
	 * @throws SQLException
	 */
	@Test
	public void testQueryLike() throws SQLException{
		ClientTestModel pk = new ClientTestModel();
		pk.setType((short)1);
		List<ClientTestModel> models = dao.queryLike(pk, new DalHints());
		Assert.assertTrue(null != models);
		Assert.assertEquals(3, models.size());
	}
	
	/**
	 * Query by Entity with where clause
	 * @throws SQLException
	 */
	@Test
	public void testQueryWithWhereClause() throws SQLException{
		String whereClause = "type=? and id=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);
		parameters.set(2, Types.INTEGER, 1);
		
		List<ClientTestModel> models = dao.query(whereClause, parameters, new DalHints());
		Assert.assertTrue(null != models);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("SH INFO", models.get(0).getAddress());
	}
	
	/**
	 * Test Query the first row with where clause
	 * @throws SQLException 
	 */
	@Test
	public void testQueryFirstWithWhereClause() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);
		
		ClientTestModel model = dao.queryFirst(whereClause, parameters, new DalHints());
		Assert.assertTrue(null != model);
		Assert.assertEquals(1, model.getId().intValue());
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
			dao.queryFirst(whereClause, parameters, new DalHints());
			Assert.fail();
		}catch(Throwable e) {
		}
	}
	
	/**
	 * Test Query the top rows with where clause
	 * @throws SQLException
	 */
	@Test
	public void testQueryTopWithWhereClause() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);
		
		List<ClientTestModel> models = dao.queryTop(whereClause, parameters, new DalHints(), 2);
		Assert.assertTrue(null != models);
		Assert.assertEquals(2, models.size());
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
		
		List<ClientTestModel> models = dao.queryTop(whereClause, parameters, new DalHints(), 2);
		Assert.assertTrue(null != models);
		Assert.assertEquals(0, models.size());
	}
	
	/**
	 * Test Query range of result with where clause
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClause() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);
		
		List<ClientTestModel> models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, 1);
		Assert.assertTrue(null != models);
		Assert.assertEquals(1, models.size());
	}
	
	/**
	 * Test Query range of result with where clause failed when return not enough recodes
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClauseFailed() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);
		
		List<ClientTestModel> models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, 10);
		Assert.assertTrue(null != models);
		Assert.assertEquals(3, models.size());
	}
	
	/**
	 * Test Query range of result with where clause when return empty collection
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClauseEmpty() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 10);
		
		List<ClientTestModel> models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, 10);
		Assert.assertTrue(null != models);
		Assert.assertEquals(0, models.size());
	}
	
	/**
	 * Test Insert with null or empty parameters
	 * @throws SQLException
	 */
	@Test
	public void testInsertCheckForData() throws SQLException{
		ClientTestModel model = null;
		try {
			dao.insert(new DalHints(), model);
			Assert.fail();
		} catch (Exception e) {
		}
		
		List<ClientTestModel> models = null;
		try {
			dao.insert(new DalHints(), models);
			Assert.fail();
		} catch (Exception e) {
		}

		models = new ArrayList<>();
		int[] res = dao.insert(new DalHints(), models);
		assertEquals(0, res);
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
		int res = dao.insert(new DalHints(), model);
		Assert.assertEquals(1, res);
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
		int[] res = dao.insert(new DalHints(), entities);
		assertEquals(3, res);
		Assert.assertEquals(3, DalTestHelper.getCount(dao, "address='CTRIP'"));
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
		
		DalHints hints = new DalHints(DalHintEnum.continueOnError);
		int[] res = dao.insert(hints, entities);
		assertEquals(2, res);
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
		KeyHolder holder = supportGetGeneratedKeys ? new KeyHolder() : null;
		int[] res = dao.insert(new DalHints(),holder, entities);
		assertEquals(3, res);
		
		if(supportGetGeneratedKeys) {
			Assert.assertEquals(3, holder.size());
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEY"));
		}
	}
	
	@Test
	public void testCombinedInsertCheckForData() throws SQLException{
		int res;
		
		List<ClientTestModel> modelList = null;
		try {
			res = dao.combinedInsert(new DalHints(), null, modelList);
			Assert.fail();
		} catch (Exception e) {
		}
		
		modelList = new ArrayList<>();
		res = dao.combinedInsert(new DalHints(), null, modelList);
		Assert.assertEquals(0, res);
	}

	/**
	 * Test Insert multiple entities with one SQL Statement
	 * @throws SQLException
	 */
	@Test
	public void testCombinedInsert() throws SQLException{
		if(!supportInsertValues)
			return;
		
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}
		KeyHolder holder = new KeyHolder();
		DalHints hints = new DalHints();
		int res = dao.combinedInsert(hints, holder, entities);
		Assert.assertEquals(3, res);
		Assert.assertEquals(3, holder.size());
		Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEY"));		
	}
	
	@Test
	public void testBatchInsertCheckForData() throws SQLException{
		int[] res;
		
		List<ClientTestModel> modelList = null;
		try {
			res = dao.batchInsert(new DalHints(), modelList);
			Assert.fail();
		} catch (Exception e) {
		}
		
		modelList = new ArrayList<>();
		res = dao.batchInsert(new DalHints(), modelList);
		Assert.assertArrayEquals(new int[0], res);
	}
	
	/**
	 * Test Batch Insert multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testBatchInsert() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}
		int[] res = dao.batchInsert(new DalHints(), entities);
		Assert.assertTrue(res.length == 3);
		Assert.assertEquals(3, dao.query("id>4", new StatementParameters(), new DalHints()).size());

//		for (int i = 0; i < 3; i++){
//			Assert.assertTrue(res[i] > 0);
//		}
	}
	
	/**
	 * Test delete multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException{
		ClientTestModel model = new ClientTestModel();
		model.setId(1);

		int res = dao.delete(new DalHints(), model);
		Assert.assertEquals(1, res);
	}
	
	@Test
	public void testBatchDeleteCheckForData() throws SQLException{
		int[] res;
		
		List<ClientTestModel> modelList = null;
		try {
			res = dao.batchDelete(new DalHints(), modelList);
			Assert.fail();
		} catch (Exception e) {
		}
		
		modelList = new ArrayList<>();
		res = dao.batchDelete(new DalHints(), modelList);
		Assert.assertArrayEquals(new int[0], res);
	}
	
	/**
	 * Test batch delete multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testBatchDelete() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			entities.add(model);
		}
		int[] res = dao.batchDelete(new DalHints(), entities);
		Assert.assertEquals(3, res.length);
		if(validateBatchUpdateCount)
			Assert.assertArrayEquals(new int[]{1,1,1}, res);
	}
	
	@Test
	public void testUpdateCheckForData() throws SQLException{
		int[] res;

		List<ClientTestModel> modelList = null;
		try {
			res = dao.update(new DalHints(), modelList);
			Assert.fail();
		} catch (Exception e) {
		}
		
		modelList = new ArrayList<>();
		res = dao.update(new DalHints(), modelList);
		assertEquals(0, res);
	}
	
	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	@Test
	public void testUpdateMultiple() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> models = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			models.add(model);
		}
		int[] res = dao.update(hints, models);
		assertEquals(3, res);
		
		ClientTestModel model = dao.queryByPk(1, hints);
		Assert.assertTrue(null != model);
		Assert.assertEquals("CTRIP", model.getAddress());
	}
	
	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	@Test
	public void testBatchUpdate() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> modelList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			modelList.add(model);
		}
		dao.batchUpdate(hints, modelList);
		Assert.assertEquals(3, DalTestHelper.getCount(dao, "address='CTRIP'"));
		
		ClientTestModel model = dao.queryByPk(1, hints);
		Assert.assertTrue(null != model);
		Assert.assertEquals("CTRIP", model.getAddress());
	}
	
	private void assertEquals(int expected, int[] res) {
		int total = 0;
		for(int t: res)total+=t;
		Assert.assertEquals(expected, total);
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
		int res = dao.delete(whereClause, parameters, hints);
		Assert.assertEquals(3, res);
		
		List<ClientTestModel> models = dao.query(whereClause, parameters, hints);
		Assert.assertEquals(0, models.size());
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
		int res = dao.update(sql, parameters, hints);
		Assert.assertEquals(1, res);
		
		ClientTestModel model = dao.queryByPk(1, hints);
		Assert.assertTrue(null != model);
		Assert.assertEquals("CTRIP", model.getAddress());
	}
}

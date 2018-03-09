package test.com.ctrip.platform.dal.dao.unittests;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub;
import test.com.ctrip.platform.dal.dao.unitbase.ClientTestModel;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

public class DalTableDaoTestStub extends BaseTestStub {
	public DalTableDaoTestStub(String dbName, DatabaseDifference diff) {
		super(dbName, diff);
	}
	
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
	
    @Test
    public void testQueryList() throws SQLException{
        SelectSqlBuilder builder = new SelectSqlBuilder();
        
        builder.equal("type", 1, Types.SMALLINT);
        
        List<ClientTestModel> models = dao.query(builder, new DalHints());
        Assert.assertTrue(null != models);
        Assert.assertEquals(3, models.size());
        
        
        builder = new SelectSqlBuilder();
        builder.equal("type", 1, Types.SMALLINT);
        models = dao.query(builder.atPage(1, 1), new DalHints());
        Assert.assertTrue(null != models);
        Assert.assertEquals(1, models.size());
        
        builder = new SelectSqlBuilder();
        builder.equal("type", 10, Types.SMALLINT);
        models = dao.query(builder.atPage(1, 10), new DalHints());
        Assert.assertTrue(null != models);
        Assert.assertEquals(0, models.size());        
    }
    
    @Test
    public void testQueryListAllColumns() throws SQLException{
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAllColumns();
        
        builder.equal("type", 1, Types.SMALLINT);
        
        List<ClientTestModel> models = dao.query(builder, new DalHints());
        Assert.assertTrue(null != models);
        Assert.assertEquals(3, models.size());
        
        
        builder = new SelectSqlBuilder().selectAllColumns();
        builder.equal("type", 1, Types.SMALLINT);
        models = dao.query(builder.atPage(1, 1), new DalHints());
        Assert.assertTrue(null != models);
        Assert.assertEquals(1, models.size());
        
        builder = new SelectSqlBuilder().selectAllColumns();
        builder.equal("type", 10, Types.SMALLINT);
        models = dao.query(builder.atPage(1, 10), new DalHints());
        Assert.assertTrue(null != models);
        Assert.assertEquals(0, models.size());        
    }
    
    @Test
    public void testQueryObjectList() throws SQLException{
        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.equal("type", 1, Types.SMALLINT);
        List<Short> models = dao.query(builder, new DalHints(), Short.class);
        Assert.assertTrue(null != models);
        Assert.assertEquals(3, models.size());
        
        builder = new SelectSqlBuilder();
        builder.equal("type", 1, Types.SMALLINT);
        models = dao.query(builder.atPage(1, 1), new DalHints(), Short.class);
        Assert.assertTrue(null != models);
        Assert.assertEquals(1, models.size());
        
        builder = new SelectSqlBuilder();
        builder.equal("type", 10, Types.SMALLINT);
        models = dao.query(builder.atPage(1, 10), new DalHints(), Short.class);
        Assert.assertTrue(null != models);
        Assert.assertEquals(0, models.size());
        
    }
    
    @Test
    public void testQueryObject() throws SQLException{
        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.equal("type", 1, Types.SMALLINT);
        builder.requireFirst();
        Short models = dao.queryObject(builder, new DalHints(), Short.class);
        Assert.assertNotNull(models);
        
        builder = new SelectSqlBuilder();
        builder.equal("type", 1, Types.SMALLINT);
        builder.requireFirst();
        models = dao.queryObject(builder.atPage(1, 1), new DalHints(), Short.class);
        Assert.assertNotNull(models);
        
        builder = new SelectSqlBuilder();
        builder.equal("type", 10, Types.SMALLINT);
        builder.requireFirst();
        models = dao.queryObject(builder.atPage(1, 10), new DalHints(), Short.class);
        Assert.assertNull(models);
    }
    
    @Test
    public void testQueryObjectAllColumns() throws SQLException{
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAllColumns();
        builder.equal("type", 1, Types.SMALLINT);
        builder.requireFirst();
        ClientTestModel models = dao.queryObject(builder, new DalHints());
        Assert.assertNotNull(models);
        
        builder = new SelectSqlBuilder().selectAllColumns();
        builder.equal("type", 1, Types.SMALLINT);
        builder.requireFirst();
        models = dao.queryObject(builder.atPage(1, 1), new DalHints());
        Assert.assertNotNull(models);
        
        builder = new SelectSqlBuilder().selectAllColumns();
        builder.equal("type", 10, Types.SMALLINT);
        builder.requireFirst();
        models = dao.queryObject(builder.atPage(1, 10), new DalHints());
        Assert.assertNull(models);
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
		Assert.assertEquals(0, res.length);
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
		assertEquals(1, res, 4+1);
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
		assertEquals(new int[]{1,1,1}, res, 3, "address='CTRIP'");
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
		assertEquals(new int[]{1,0,1}, res, 4+2);
	}
	
    @Test
    public void testInsertMultipleAsListWithContinueOnErrorHintsWithKeholder() throws SQLException{
        if(!diff.supportGetGeneratedKeys)
            return;
        
        DalTableDao<ClientTestModelJpa> dao = new DalTableDao(new DalDefaultJpaParser<>(ClientTestModelJpa.class, dbName));
        List<ClientTestModelJpa> entities = new ArrayList<ClientTestModelJpa>();
        for (int i = 0; i < 3; i++) {
            ClientTestModelJpa model = new ClientTestModelJpa();
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
        
        DalHints hints = new DalHints(DalHintEnum.continueOnError).setKeyHolder(new KeyHolder()).setIdentityBack();
        int[] res = dao.insert(hints, entities);
        assertEquals(new int[]{1,0,1}, res, 4+2);
        for(ClientTestModelJpa model: entities) {
            if(model.getId()!=null)
                Assert.assertEquals(dao.queryByPk(model, new DalHints()).getAddress(), model.getAddress());    
        }        
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
		KeyHolder holder = diff.supportGetGeneratedKeys ? new KeyHolder() : null;
		int[] res = dao.insert(new DalHints(),holder, entities);
		assertEquals(new int[]{1, 1, 1}, res, 4+3);
		
		if(diff.supportGetGeneratedKeys) {
			Assert.assertEquals(3, holder.size());
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEY"));
		}
	}
	
    /**
     * Test Insert multiple entities with key-holder
     * @throws SQLException
     */
    @Test
    public void testInsertMultipleAsListWithKeyInsertBack() throws SQLException{
        if(!diff.supportGetGeneratedKeys)
            return;
        
        DalTableDao<ClientTestModelJpa> dao = new DalTableDao(new DalDefaultJpaParser<>(ClientTestModelJpa.class, dbName));
        
        List<ClientTestModelJpa> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModelJpa model = new ClientTestModelJpa();
            model.setQuantity(10 + 1%3);
            model.setType(((Number)(1%3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }
        KeyHolder holder = new KeyHolder();
        int[] res = dao.insert(new DalHints().setIdentityBack(), holder, entities);
        assertEquals(new int[]{1, 1, 1}, res, 4+3);
        Assert.assertEquals(3, holder.size());
        Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEY"));
        
        int i = 0;
        for(ClientTestModelJpa pojo: entities)
            Assert.assertEquals(holder.getKey(i++).intValue(), pojo.getId().intValue());

    }
    
	@Test
	public void testCombinedInsertCheckForData() throws SQLException{
		int res;
		
		List<ClientTestModel> modelList = null;
		try {
			res = dao.combinedInsert(new DalHints(), null, modelList);
			Assert.fail();
		} catch (Throwable e) {
		}
		
		modelList = new ArrayList<>();
		if(diff.category == DatabaseCategory.Oracle)
			return;
		
		res = dao.combinedInsert(new DalHints(), null, modelList);
		Assert.assertEquals(0, res);
	}

	/**
	 * Test Insert multiple entities with one SQL Statement
	 * @throws SQLException
	 */
	@Test
	public void testCombinedInsert() throws SQLException{
		if(!diff.supportInsertValues)
			return;
		
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}
		KeyHolder holder = diff.supportGetGeneratedKeys ? new KeyHolder() : null;
		DalHints hints = new DalHints();
		int res = dao.combinedInsert(hints, holder, entities);
		assertEquals(3, res, 4+3);
		if(diff.supportGetGeneratedKeys ){
			Assert.assertEquals(3, holder.size());
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEY"));
		}
	}
	
    /**
     * Test Insert multiple entities with one SQL Statement
     * @throws SQLException
     */
    @Test
    public void testCombinedInsertWithKeyInsertBack() throws SQLException{
        if(!diff.supportInsertValues || !diff.supportGetGeneratedKeys)
            return;
        
        DalTableDao<ClientTestModelJpa> dao = new DalTableDao(new DalDefaultJpaParser<>(ClientTestModelJpa.class, dbName));
        
        List<ClientTestModelJpa> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientTestModelJpa model = new ClientTestModelJpa();
            model.setQuantity(10 + 1%3);
            model.setType(((Number)(1%3)).shortValue());
            model.setAddress("CTRIP");
            entities.add(model);
        }
        KeyHolder holder = diff.supportGetGeneratedKeys ? new KeyHolder() : null;
        DalHints hints = new DalHints();
        int res = dao.combinedInsert(hints.setIdentityBack(), holder, entities);
        assertEquals(3, res, 4+3);
        Assert.assertEquals(3, holder.size());
        Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEY"));

        int i = 0;
        for(ClientTestModelJpa pojo: entities)
            Assert.assertEquals(holder.getKey(i++).intValue(), pojo.getId().intValue());
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
		assertEqualsBatchInsert(new int[]{1, 1, 1}, res, 7);
	}
	
	/**
	 * Test delete single entities
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException{
		ClientTestModel model = new ClientTestModel();
		model.setId(1);

		int res = dao.delete(new DalHints(), model);
		Assert.assertEquals(1, res, 4-1);
	}
	
	/**
	 * Test delete multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testDeleteMultiple() throws SQLException{
		List<ClientTestModel> modelList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			modelList.add(model);
		}
		int[] res = dao.delete(new DalHints(), modelList);
		assertEquals(new int[]{1, 1, 1}, res, 4-3);
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
		assertEqualsBatch(new int[]{1,1,1}, res, 4-3);
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
		Assert.assertEquals(0, res.length);
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
		assertEquals(new int[]{1,1,1}, res, 3, "address='CTRIP'");
		
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
		int[] res = dao.batchUpdate(hints, modelList);
		assertEqualsBatch(new int[]{1,1,1}, res, 3, "address='CTRIP'");
		
		ClientTestModel model = dao.queryByPk(1, hints);
		Assert.assertTrue(null != model);
		Assert.assertEquals("CTRIP", model.getAddress());
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
		assertEquals(3, res, 1);
		
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
		assertEquals(1, res, 1, "address = 'CTRIP'");
		
		ClientTestModel model = dao.queryByPk(1, hints);
		Assert.assertTrue(null != model);
		Assert.assertEquals("CTRIP", model.getAddress());
	}
}

package test.com.ctrip.platform.dal.dao.unittests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub;
import test.com.ctrip.platform.dal.dao.unitbase.ClientTestModel;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalCustomRowMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.AbstractFreeSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;

public class DalQueryDaoTestStub extends BaseTestStub {
	private DalQueryDao client = null;
	public DalQueryDaoTestStub(String dbName, DatabaseDifference diff) {
		super(dbName, diff);
		client = new DalQueryDao(dbName);
	}

	private final static int ROW_COUNT = 1000;
	
	public static void prepareData(String dbName) throws Exception {
		String insertSql = "INSERT INTO " + TABLE_NAME
				+ "(quantity,type,address)" + " VALUES(?, ?, ?)";
		StatementParameters[] parameterList = new StatementParameters[ROW_COUNT];

		Random random = new Random();
		for (int i = 0; i < ROW_COUNT; i++) {
			StatementParameters param = new StatementParameters();
			int quantity = random.nextInt(5);
			int type = random.nextInt(3);

			param.set(1, Types.INTEGER, 10 + quantity);
			param.set(2, Types.SMALLINT, type);
			param.set(3, Types.VARCHAR, "SZ INFO" + "_" + i % 100 + "#");

			parameterList[i] = param;
		}
		DalClientFactory.getClient(dbName).batchUpdate(insertSql, parameterList, new DalHints());
	}

	
	/**
	 * Test the basic query function with mapper
	 * @throws SQLException
	 */
	@Test
	public void testQueryWithMapper() throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME;
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();

		List<ClientTestModel> models = client.query(sql, param, hints, mapper);
		Assert.assertEquals(ROW_COUNT, models.size());
	}
	
	@Test
	public void testQueryWithClazz() throws SQLException {
		String sql = "SELECT quantity FROM " + TABLE_NAME;
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();

		List<Integer> models = client.query(sql, param, hints, Integer.class);
		Assert.assertEquals(ROW_COUNT, models.size());
	}
	
	/**
	 * Test the query function with callback
	 * @throws SQLException
	 */
	@Test
	public void testQueryWithCallback() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		final ClientTestModel model = new ClientTestModel();
		DalRowCallback callback = new DalRowCallback(){
			@Override
			public void process(ResultSet rs) throws SQLException {
				model.setId(rs.getInt(1));
				model.setQuantity(rs.getInt(2));
				model.setType(rs.getShort(3));
				model.setAddress(rs.getString(4));
				model.setLastChanged(rs.getTimestamp(5));	
			}
		};
		
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		client.query(sql, param, hints, callback);
		
		Assert.assertEquals(1, model.getId().intValue());
	}
	
	/**
	 * Test query for object success
	 * @throws SQLException
	 */
	@Test
	public void testQueryForObjectSuccess() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		ClientTestModel model = client.queryForObject(sql, param, hints, mapper);
		Assert.assertEquals(1, model.getId().intValue());

		FreeSelectSqlBuilder<List<Map<String, Object>>> builder = new FreeSelectSqlBuilder<>();
		builder.append("select count(*) as c1, 111 as c2");
		builder.mapWith(new DalCustomRowMapper("c1", "c2"));
        builder.with(new StatementParameters());
		List<Map<String, Object>> l = client.query(builder, hints);
	}
	
	/**
	 * Test query for object failed
	 */
	@Test
	public void testQueryForObjectFailed(){
		String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT 2";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.queryForObject(sql, param, hints, mapper);
			Assert.fail();
		} catch (SQLException e) {
		}
	}
	
	/**
	 * Test query for object success
	 * @throws SQLException
	 */
	@Test
	public void testQueryForObjectNullableSuccess() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		ClientTestModel model = client.queryForObjectNullable(sql, param, hints, mapper);
		Assert.assertNotNull(model);
		Assert.assertEquals(1, model.getId().intValue());
		
		sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = -1";
		Assert.assertNull(client.queryForObjectNullable(sql, param, hints, mapper));
	}
	
	/**
	 * Test query for object failed
	 */
	@Test
	public void testQueryForObjectNullableFailed(){
		String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT 2";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.queryForObjectNullable(sql, param, hints, mapper);
			Assert.fail();
		} catch (SQLException e) {
		}
	}
	
	/**
	 * Test query for object success with scalar
	 * @throws SQLException
	 */
	@Test
	public void testQueryForObjectScalarSuccess() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		Long id = client.queryForObject(sql, param, hints, Long.class);
		Assert.assertEquals(1, id.intValue());
	}
	
	/**
	 * Test query for object failed with scalar
	 */
	@Test
	public void testQueryForObjectScalarFailed(){
		String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT 2";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.queryForObject(sql, param, hints, Long.class);
			Assert.fail();
		} catch (SQLException e) {
		}
	}
	

	/**
	 * Test query for object success with scalar
	 * @throws SQLException
	 */
	@Test
	public void testQueryForObjectScalarNullableSuccess() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		Number id = client.queryForObjectNullable(sql, param, hints, Number.class);
		Assert.assertEquals(1, id.intValue());
		
		sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = -1";
		Assert.assertNull(client.queryForObjectNullable(sql, param, hints, Long.class));
	}
	
	/**
	 * Test query for object failed with scalar
	 */
	@Test
	public void testQueryForObjectScalarNullableFailed(){
		String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT 2";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.queryForObjectNullable(sql, param, hints, Long.class);
			Assert.fail();
		} catch (SQLException e) {
		}
	}
	
	/**
	 * Test query for first success
	 * @throws SQLException
	 */
	@Test
	public void testQueryFirstSuccess() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		ClientTestModel model = client.queryFirst(sql, param, hints, mapper);
		Assert.assertEquals(1, model.getId().intValue());
	}
	
	/**
	 *  Test query for first failed
	 */
	@Test
	public void testQueryFirstFaield(){
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = -1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.queryFirst(sql, param, hints, mapper);
			Assert.fail();
		} catch (SQLException e) {
		}
	}
	
	/**
	 * Test query for first success
	 * @throws SQLException
	 */
	@Test
	public void testQueryFirstNullableSuccess() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		ClientTestModel model = client.queryFirstNullable(sql, param, hints, mapper);
		Assert.assertEquals(1, model.getId().intValue());
	}
	
	/**
	 *  Test query for first failed
	 */
	@Test
	public void testQueryFirstNullableFaield(){
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = -1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			Assert.assertNull(client.queryFirstNullable(sql, param, hints, mapper));
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Test query for first success
	 * @throws SQLException
	 */
	@Test
	public void testTypedQueryFirstSuccess() throws SQLException{
		String sql = "SELECT quantity FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		Integer result = client.queryFirst(sql, param, hints, Integer.class);
		Assert.assertNotNull(result);
	}
	
	/**
	 *  Test query for first failed
	 */
	@Test
	public void testTypedQueryFirstFaield(){
		String sql = "SELECT quantity FROM " + TABLE_NAME + " WHERE id = -1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			Integer result = client.queryFirst(sql, param, hints, Integer.class);
			Assert.fail();
		} catch (SQLException e) {
		}
	}
	
	/**
	 * Test query for first success
	 * @throws SQLException
	 */
	@Test
	public void testTypedQueryFirstNullableSuccess() throws SQLException{
		String sql = "SELECT quantity FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		Integer result = client.queryFirstNullable(sql, param, hints, Integer.class);
		Assert.assertNotNull(result);
	}
	
	/**
	 *  Test query for first failed
	 */
	@Test
	public void testTypedQueryFirstNullableFaield(){
		String sql = "SELECT quantity FROM " + TABLE_NAME + " WHERE id = -1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			Assert.assertNull(client.queryFirstNullable(sql, param, hints, Integer.class));
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	/**
	 * Test query for Top success
	 * @throws SQLException
	 */
	@Test
	public void testQueryTopSuccess() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE quantity = 10";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		List<ClientTestModel> models = client.queryTop(sql, param, hints, mapper, 10);
		Assert.assertEquals(10, models.size());
		Assert.assertEquals(10, models.get(0).getQuantity().intValue());
	}
	
	/**
	 * Test query for Top failed
	 * @throws SQLException
	 */
	@Test
	public void testQueryTopFailed() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE iid = -1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		
		try {
			List<ClientTestModel> models = client.queryTop(sql, param, hints, mapper, 1000);
			Assert.fail();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Test query for Top success
	 * @throws SQLException
	 */
	@Test
	public void testQueryObjectTopSuccess() throws SQLException{
		String sql = "SELECT quantity FROM " + TABLE_NAME + " WHERE quantity = 10";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		List<Integer> models = client.queryTop(sql, param, hints, Integer.class, 10);
		Assert.assertEquals(10, models.size());
		Assert.assertEquals(10, models.get(0).intValue());
	}
	
	/**
	 * Test query for Top failed
	 * @throws SQLException
	 */
	@Test
	public void testQueryObjectTopFailed() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE iid = -1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();

		try {
			client.queryTop(sql, param, hints, Integer.class, 1000);
			Assert.fail();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Test query for From success
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromSuccess() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME;
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		List<ClientTestModel> models = client.queryFrom(sql, param, hints, mapper, 0, 10);
		Assert.assertEquals(10, models.size());
		Assert.assertEquals(1, models.get(0).getId().intValue());
		
		List<ClientTestModel> models_2 = client.queryFrom(sql, param, hints, mapper, 100, 10);
		Assert.assertEquals(10, models_2.size());
		Assert.assertEquals(101, models_2.get(0).getId().intValue());
	}
	
	/**
	 * Test query for From Failed
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromFailed() throws SQLException{
		String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT x 5";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		List<ClientTestModel> models;
		try {
			models = client.queryFrom(sql, param, hints, mapper, 0, 10);
			Assert.fail();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Test query for From success
	 * @throws SQLException
	 */
	@Test
	public void testQueryObjectFromSuccess() throws SQLException{
		String sql = "SELECT quantity FROM " + TABLE_NAME;
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		List<Integer> models = client.queryFrom(sql, param, hints, Integer.class, 0, 10);
		Assert.assertEquals(10, models.size());
		
		List<Integer> models_2 = client.queryFrom(sql, param, hints, Integer.class, 100, 10);
		Assert.assertEquals(10, models_2.size());
	}
	
	/**
	 * Test query for From Failed
	 * @throws SQLException
	 */
	@Test
	public void testQueryObjectFromFailed() throws SQLException{
		String sql = "SELECT quantity FROM " + TABLE_NAME + " LIMIT x 5";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();

		try {
			client.queryFrom(sql, param, hints, Integer.class, 0, 10);
			Assert.fail();
		} catch (Exception e) {
		}
	}
}
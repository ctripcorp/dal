package com.ctrip.platform.dal.dao.unittests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalQueryDaoSqlServerTest {
	private final static int ROW_COUNT = 1000;
	private final static String DATABASE_NAME = "HotelPubDB";
	private final static String TABLE_NAME = "dal_client_test";

	private final static String DROP_TABLE_SQL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '" + TABLE_NAME + "') " + "DROP TABLE  "
			+ TABLE_NAME;

	// Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME
			+ "(" + "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,type smallint, " + "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";

	private static DalClient baseClient = null;
	private static DalQueryDao client = null;
	private static DalRowMapper<ClientTestModel> mapper = null;
	static {
		try {
			DalClientFactory.initPrivateFactory();
			baseClient = DalClientFactory.getClient(DATABASE_NAME);
			client = new DalQueryDao(DATABASE_NAME);
			mapper = new ClientTestDalRowMapper();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL };
		for (int i = 0; i < sqls.length; i++) {
			baseClient.update(sqls[i], parameters, hints);
		}

		/*baseClient.update("SET IDENTITY_INSERT " + TABLE_NAME + " ON",
				parameters, hints);*/

		String insertSql = "INSERT INTO " + TABLE_NAME
				+ "(quantity,type,address)" + " VALUES(?, ?, ?)";
		StatementParameters[] parameterList = new StatementParameters[ROW_COUNT];

		Random random = new Random();
		for (int i = 0; i < ROW_COUNT; i++) {
			StatementParameters param = new StatementParameters();
			int quantity = random.nextInt(5);
			int type = random.nextInt(3);
			//param.set(1, Types.INTEGER, i + 1);
			param.set(1, Types.INTEGER, 10 + quantity);
			param.set(2, Types.SMALLINT, type);
			param.set(3, Types.VARCHAR, "SZ INFO" + "_" + i % 100 + "#");

			parameterList[i] = param;
		}
		baseClient.batchUpdate(insertSql, parameterList, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL };
		for (int i = 0; i < sqls.length; i++) {
			baseClient.update(sqls[i], parameters, hints);
		}
	}

	/**
	 * Test the basic query function with mapper
	 * 
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

	/**
	 * Test the query function with callback
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testQueryWithCallback() throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		final ClientTestModel model = new ClientTestModel();
		DalRowCallback callback = new DalRowCallback() {
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

		Assert.assertEquals(1, model.getId());
	}

	/**
	 * Test query for object success
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testQueryForObjectSuccess() throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		ClientTestModel model = client
				.queryForObject(sql, param, hints, mapper);
		Assert.assertEquals(1, model.getId());
	}

	/**
	 * Test query for object failed
	 */
	@Test
	public void testQueryForObjectFailed() {
		String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT 2";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		ClientTestModel model = null;
		try {
			model = client.queryForObject(sql, param, hints, mapper);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(null == model);
	}

	/**
	 * Test query for object success with scalar
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testQueryForObjectScalarSuccess() throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		Integer id = client.queryForObject(sql, param, hints, Integer.class);
		Assert.assertEquals(1, id.intValue());
	}

	/**
	 * Test query for object failed with scalar
	 */
	@Test
	public void testQueryForObjectScalarFailed() {
		String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT 2";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		Long id = null;
		try {
			id = client.queryForObject(sql, param, hints, Long.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(null, id);
	}

	/**
	 * Test query for first success
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testQueryFirstSuccess() throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = 1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		ClientTestModel model = client.queryFirst(sql, param, hints, mapper);
		Assert.assertEquals(1, model.getId());
	}

	/**
	 * Test query for first failed
	 */
	@Test
	public void testQueryFirstFaield() {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = -1";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		ClientTestModel model = null;
		try {
			model = client.queryFirst(sql, param, hints, mapper);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(null, model);
	}

	/**
	 * Test query for Top success
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testQueryTopSuccess() throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE quantity = 10";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		List<ClientTestModel> models = client.queryTop(sql, param, hints,
				mapper, 10);
		Assert.assertEquals(10, models.size());
		Assert.assertEquals(10, models.get(0).getQuantity());
	}

	/**
	 * Test query for Top failed
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testQueryTopFailed() throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE quantity = 10";
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		List<ClientTestModel> models = client.queryTop(sql, param, hints,
				mapper, 1000);
		Assert.assertTrue(1000 > models.size());
		Assert.assertEquals(10, models.get(0).getQuantity());
	}

	/**
	 * Test query for From success
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromSuccess() throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME;
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		List<ClientTestModel> models = client.queryFrom(sql, param, hints,
				mapper, 0, 10);
		Assert.assertEquals(10, models.size());
		Assert.assertEquals(1, models.get(0).getId());

		List<ClientTestModel> models_2 = client.queryFrom(sql, param, hints,
				mapper, 100, 10);
		Assert.assertEquals(10, models_2.size());
		Assert.assertEquals(101, models_2.get(0).getId());
	}

	/**
	 * Test query for From Failed
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromFailed() throws SQLException {
		String sql = "SELECT TOP(5) * FROM " + TABLE_NAME;
		StatementParameters param = new StatementParameters();
		DalHints hints = new DalHints();
		List<ClientTestModel> models = client.queryFrom(sql, param, hints,
				mapper, 0, 10);
		Assert.assertEquals(5, models.size());
		Assert.assertEquals(1, models.get(0).getId());
	}

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

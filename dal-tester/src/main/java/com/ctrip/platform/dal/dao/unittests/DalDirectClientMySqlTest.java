package com.ctrip.platform.dal.dao.unittests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public class DalDirectClientMySqlTest {
	private final static String DATABASE_NAME = "dao_test";
	private final static String TABLE_NAME = "dal_client_test";
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;
	private final static String CREATE_TABLE_SQL = "CREATE TABLE dal_client_test("
			+ "ID int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	private static DalClient client = null;

	static {
		try {
			DalClientFactory.initPrivateFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL };
		client.batchUpdate(sqls, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		client.update(DROP_TABLE_SQL, parameters, hints);
	}

	/**
	 * Test the basic query function without parameters
	 * 
	 * @throws SQLException
	 */
	@Test
	public void quryTestWithoutParameters() throws SQLException {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 12, 2, 'SZ INFO', NULL)" };
		int[] counts = client.batchUpdate(insertSqls, hints);
		Assert.assertArrayEquals(new int[] { 1, 1, 1 }, counts);

		String querySql = "SELECT * FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		ClientTestDalRowMapper mapper = new ClientTestDalRowMapper();
		List<ClientTestModel> res = client.query(querySql, parameters, hints,
				new DalRowMapperExtractor<ClientTestModel>(mapper));
		Assert.assertTrue(null != res && res.size() == 3);
		
		ClientTestModel model = res.get(0);
		Assert.assertTrue(model.getQuantity() == 10 &&
				model.getType() == 1 &&
				model.getAddress().equals("SH INFO"));

		String[] deleteSqls = new String[] { "DELETE FROM " + TABLE_NAME };

		counts = client.batchUpdate(deleteSqls, hints);
		Assert.assertArrayEquals(new int[] { 3 }, counts);
	}

	/**
	 * Test the basic query function without parameters
	 * 
	 * @throws SQLException
	 */
	@Test
	public void quryTestWithParameters() throws SQLException {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 12, 2, 'SZ INFO', NULL)" };
		int[] counts = client.batchUpdate(insertSqls, hints);
		Assert.assertArrayEquals(new int[] { 1, 1, 1 }, counts);

		String querySql = "SELECT * FROM " + TABLE_NAME + " WHERE type = ?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);
		ClientTestDalRowMapper mapper = new ClientTestDalRowMapper();
		List<ClientTestModel> res = client.query(querySql, parameters, hints,
				new DalRowMapperExtractor<ClientTestModel>(mapper));
		Assert.assertTrue(null != res && res.size() == 2);

		String[] deleteSqls = new String[] { "DELETE FROM " + TABLE_NAME };

		counts = client.batchUpdate(deleteSqls, hints);
		Assert.assertArrayEquals(new int[] { 3 }, counts);
	}

	/**
	 * Test the basic qury function with maxRows limit
	 * @throws SQLException 
	 */
	@Test
	public void queryTestWithFetchSizeLimit() throws SQLException{
		DalHints hints = new DalHints();
		
		String[] insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 12, 2, 'SZ INFO', NULL)" };
		int[] counts = client.batchUpdate(insertSqls, hints);
		Assert.assertArrayEquals(new int[] { 1, 1, 1 }, counts);

		String querySql = "SELECT * FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		ClientTestDalRowMapper mapper = new ClientTestDalRowMapper();
		hints.set(DalHintEnum.maxRows, 1); //Set fecth size here
		List<ClientTestModel> res = client.query(querySql, parameters, hints,
				new DalRowMapperExtractor<ClientTestModel>(mapper));
		Assert.assertTrue(null != res && res.size() == 1);
		
		String[] deleteSqls = new String[] { "DELETE FROM " + TABLE_NAME };

		counts = client.batchUpdate(deleteSqls, hints);
		Assert.assertArrayEquals(new int[] { 3 }, counts);
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

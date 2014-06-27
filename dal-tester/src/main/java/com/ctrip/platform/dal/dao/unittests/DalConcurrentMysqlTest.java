package com.ctrip.platform.dal.dao.unittests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public class DalConcurrentMysqlTest {
	
	private final static String DATABASE_NAME = "dao_test";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static String SP_I_NAME = "dal_client_test_i";
	private final static String SP_D_NAME="dal_client_test_d";
	private final static String SP_U_NAME = "dal_client_test_u";
	
	private final static int INSERT_COUNT = 1000;
	private final static Random random = new Random();
	
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE dal_client_test("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	//Only has normal parameters
	private static final String CREATE_I_SP_SQL = "CREATE PROCEDURE dal_client_test_i("
			+ "dal_id int,"
			+ "quantity int,"
			+ "type smallint,"
			+ "address VARCHAR(64)) "
			+ "BEGIN INSERT INTO dal_client_test"
			+ "(id, quantity, type, address) "
			+ "VALUES(dal_id, quantity, type, address);"
			+ "SELECT ROW_COUNT() AS result;"
			+ "END";
	//Has out parameters store procedure
	private static final String CREATE_D_SP_SQL = "CREATE PROCEDURE dal_client_test_d("
			+ "dal_id int,"
			+ "out count int)"
			+ "BEGIN DELETE FROM dal_client_test WHERE id=dal_id;"
			+ "SELECT ROW_COUNT() AS result;"
			+ "SELECT COUNT(*) INTO count from dal_client_test;"
			+ "END";
	//Has in-out parameters store procedure
	private static final String CREATE_U_SP_SQL = "CREATE PROCEDURE dal_client_test_u("
			+ "dal_id int,"
			+ "quantity int,"
			+ "type smallint,"
			+ "INOUT address VARCHAR(64))"
			+ "BEGIN UPDATE dal_client_test "
			+ "SET quantity = quantity, type=type, address=address "
			+ "WHERE id=dal_id;"
			+ "SELECT ROW_COUNT() AS result;"
			+ "END";
	
	private static final String DROP_I_SP_SQL = "DROP PROCEDURE  IF  EXISTS dal_client_test_i";
	private static final String DROP_D_SP_SQL = "DROP PROCEDURE  IF  EXISTS dal_client_test_d";
	private static final String DROP_U_SP_SQL = "DROP PROCEDURE  IF  EXISTS dal_client_test_u";
	
	private static DalClient client = null;
	private static ClientTestDalRowMapper mapper = null;

	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
			mapper = new ClientTestDalRowMapper();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL, 
				DROP_I_SP_SQL, CREATE_I_SP_SQL, 
				DROP_D_SP_SQL, CREATE_D_SP_SQL,
				DROP_U_SP_SQL, CREATE_U_SP_SQL};
		client.batchUpdate(sqls, hints);
		
		String[] insertSqls = new String[INSERT_COUNT];
		for (int i = 0; i < INSERT_COUNT; i++) {
			insertSqls[i] = String.format("INSERT INTO " + TABLE_NAME
						+ " VALUES(%s, %s, %s, '%s', NULL)", i+1, i%100, i%10, "SH INFO " + i);
		}
		client.batchUpdate(insertSqls, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL, DROP_I_SP_SQL,
				DROP_D_SP_SQL, DROP_U_SP_SQL};
		client.batchUpdate(sqls, hints);
	}

	@Test
	public void testQueryAll() throws SQLException {
		String querySql = "SELECT * FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		ClientTestDalRowMapper mapper = new ClientTestDalRowMapper();
		DalHints hints = new DalHints();
		List<ClientTestModel> res = client.query(querySql, parameters, hints,
				new DalRowMapperExtractor<ClientTestModel>(mapper));
		Assert.assertTrue(null != res && res.size() == INSERT_COUNT);
	}
	
	@Test
	public void testUpdate() throws SQLException{
		int id = random.nextInt(1000);
		List<ClientTestModel> models = this.queryModelsByIds(id);
		ClientTestModel model = null;
		if(models.size() > 0)
			model = models.get(0);
		else{
			System.out.println(id);
			return;
		}
		String updateSql = String.format(
				"UPDATE %s SET address=? WHERE id=?", TABLE_NAME);
		StatementParameters parameters = new StatementParameters();
		if(model.getAddress().contains("SH"))
			parameters.set(1, Types.VARCHAR, model.getAddress().replace("SH", "BJ"));
		else
			parameters.set(1, Types.VARCHAR, model.getAddress().replace("BJ", "SH"));
		parameters.set(2, Types.INTEGER,  model.getId());
		DalHints hints = new DalHints();
		int count = client.update(updateSql, parameters, hints);
		Assert.assertEquals(1, count);
	}

	/**
	 * Get the models all in dal_client_test by specified IDs
	 * 
	 * @param ids
	 * @return The list of ClientTestModel
	 */
	private List<ClientTestModel> queryModelsByIds(int... ids) {
		List<ClientTestModel> models = new ArrayList<ClientTestModel>();
		String querySql = "";
		if (null != ids && ids.length > 0) {
			Integer[] idds = new Integer[ids.length];
			for (int i = 0; i < idds.length; i++) {
				idds[i] = ids[i];
			}
			querySql = "SELECT * FROM %s WHERE id in(%s)";
			String inClause = StringUtils.join(idds, ",");
			querySql = String.format(querySql, TABLE_NAME, inClause);
		} else {
			querySql = "SELECT * FROM " + TABLE_NAME;
		}
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			models = client.query(querySql, parameters, hints,
					new DalRowMapperExtractor<ClientTestModel>(mapper));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return models;
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

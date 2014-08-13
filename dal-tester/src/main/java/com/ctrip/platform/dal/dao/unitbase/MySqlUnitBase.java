package com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public class MySqlUnitBase {
	
	protected static DalClient client = null;
	protected static ClientTestDalRowMapper mapper = null;
	
	protected static final String DATABASE = "dao_test";
	protected final static String TABLE_NAME = "dal_client_test";
	protected final static String SP_I_NAME = "dal_client_test_i";
	protected final static String SP_D_NAME="dal_client_test_d";
	protected final static String SP_U_NAME = "dal_client_test_u";
	
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
		
	static{
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE);
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
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL, DROP_I_SP_SQL,
				DROP_D_SP_SQL, DROP_U_SP_SQL};
		client.batchUpdate(sqls, hints);
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(1, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(2, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(3, 12, 2, 'SZ INFO', NULL)" };
		int[] counts = client.batchUpdate(insertSqls, hints);
		Assert.assertArrayEquals(new int[] { 1, 1, 1 }, counts);
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.update(sql, parameters, hints);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the models all in dal_client_test by specified IDs
	 * 
	 * @param ids
	 * @return The list of ClientTestModel
	 */
	protected List<ClientTestModel> queryModelsByIds(int... ids) {
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
}

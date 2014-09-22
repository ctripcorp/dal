package com.ctrip.platform.dal.dao.unittests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

/**
 * Unit tests for Dal direct client
 * 
 * @author wcyuan
 * @version 2014-05-04
 */
public class DalDirectClientMySqlTest {
	private final static String DATABASE_NAME = "dao_test";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static String SP_I_NAME = "dal_client_test_i";
	private final static String SP_D_NAME="dal_client_test_d";
	private final static String SP_U_NAME = "dal_client_test_u";
	
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
	 * Test the basic query function without parameters
	 * 
	 * @throws SQLException
	 */
	@Test
	public void quryTestWithoutParameters() throws SQLException {
		String querySql = "SELECT * FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		ClientTestDalRowMapper mapper = new ClientTestDalRowMapper();
		DalHints hints = new DalHints();
		List<ClientTestModel> res = client.query(querySql, parameters, hints,
				new DalRowMapperExtractor<ClientTestModel>(mapper));
		Assert.assertTrue(null != res && res.size() == 3);

		ClientTestModel model = res.get(0);
		Assert.assertTrue(model.getQuantity() == 10 && model.getType() == 1
				&& model.getAddress().equals("SH INFO"));
	}

	/**
	 * Test the basic query function without parameters
	 * 
	 * @throws SQLException
	 */
	@Test
	public void quryTestWithParameters() throws SQLException {
		String querySql = "SELECT * FROM " + TABLE_NAME + " WHERE type = ?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);
		ClientTestDalRowMapper mapper = new ClientTestDalRowMapper();
		DalHints hints = new DalHints();
		List<ClientTestModel> res = client.query(querySql, parameters, hints,
				new DalRowMapperExtractor<ClientTestModel>(mapper));
		Assert.assertTrue(null != res);
		Assert.assertEquals(2, res.size());
	}

	/**
	 * Test the basic query function with maxRows limit
	 * 
	 * @throws SQLException
	 */
	@Test
	public void queryTestWithFetchSizeLimit() throws SQLException {
		String querySql = "SELECT * FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		ClientTestDalRowMapper mapper = new ClientTestDalRowMapper();
		DalHints hints = new DalHints();
		hints.set(DalHintEnum.maxRows, 1); // Set fetch size limit
		List<ClientTestModel> res = client.query(querySql, parameters, hints,
				new DalRowMapperExtractor<ClientTestModel>(mapper));
		Assert.assertTrue(null != res);
		Assert.assertEquals(1, res.size());
	}

	/**
	 * Test the update function without parameters
	 * 
	 * @throws SQLException
	 */
	@Test
	public void updateTestWithoutParameters() throws SQLException {
		String updateSql = String.format(
				"UPDATE %s SET address=%s WHERE id=%s", TABLE_NAME,
				"'BJ INFO'", 1);
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int count = client.update(updateSql, parameters, hints);
		Assert.assertEquals(1, count);

		List<ClientTestModel> po_models = this.queryModelsByIds(1);
		Assert.assertTrue(null != po_models);
		Assert.assertEquals(1, po_models.size());
		Assert.assertEquals("BJ INFO", po_models.get(0).getAddress());
	}

	/**
	 * Test the update function with parameters
	 * 
	 * @throws SQLException
	 */
	@Test
	public void updateTestWithParameters() throws SQLException {
		String updateSql = String.format("UPDATE %s SET address=? WHERE id=?",
				TABLE_NAME);
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.VARCHAR, "BJ INFO");
		parameters.set(2, Types.INTEGER, 1);
		DalHints hints = new DalHints();
		int count = client.update(updateSql, parameters, hints);
		Assert.assertTrue(count == 1);

		List<ClientTestModel> po_models = this.queryModelsByIds(1);
		Assert.assertTrue(null != po_models);
		Assert.assertEquals(1, po_models.size());
		Assert.assertEquals("BJ INFO", po_models.get(0).getAddress());
	}

	/**
	 * Test the update function with delete SQL statement
	 * 
	 * @throws SQLException
	 */
	@Test
	public void updateTestWithDelete() throws SQLException {
		String deleteSql = String.format("DELETE FROM %s WHERE id=?",
				TABLE_NAME);
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.INTEGER, 1);
		DalHints hints = new DalHints();
		int count = client.update(deleteSql, parameters, hints);
		Assert.assertEquals(1, count);

		List<ClientTestModel> po_models = this.queryModelsByIds(1);
		Assert.assertTrue(null != po_models);
		Assert.assertEquals(0, po_models.size());
	}

	/**
	 * Test the update function with key-holder
	 * 
	 * @throws SQLException
	 */
	@Test
	public void updateTestInsertWithKeyHolder() throws SQLException {
		String insertSql = String.format(
				"INSERT INTO %s VALUES(NULL, 10, 1, 'SH INFO', NULL)",
				TABLE_NAME);
		StatementParameters parameters = new StatementParameters();
		KeyHolder holder = new KeyHolder();
		DalHints hints = new DalHints();
		int count = client.update(insertSql, parameters, hints, holder);
		Assert.assertEquals(1, count);
		Assert.assertEquals(1, holder.getKeyList().size());
		Assert.assertTrue(holder.getKeyList().get(0)
				.containsKey("GENERATED_KEY"));
	}

	/**
	 * Test the batch update function without parameters all success.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void batchUpdateTestWithoutParametersAllSuccessed()
			throws SQLException {
		String[] sqls = new String[] {
				"DELETE FROM " + TABLE_NAME + " WHERE ID = 1",
				"UPDATE " + TABLE_NAME
						+ " SET address = 'HK INFO' WHERE id = 2",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 10, 1, 'SH INFO', NULL)" };

		DalHints hints = new DalHints();
		int[] counts = client.batchUpdate(sqls, hints);
		Assert.assertEquals(3, counts.length);
		Assert.assertArrayEquals(new int[] { 1, 1, 1 }, counts);
	}

	@Test
	public void batchUpdateTestWithRollback() throws SQLException{
		String[] sqls = new String[] {
				"DELETE FROM " + TABLE_NAME + " WHERE ID = 1",
				"DELETE FROM " + TABLE_NAME + " WHERE _ID = 2",
				"DELETE FROM " + TABLE_NAME + " WHERE ID = 3" };
		DalHints hints = new DalHints();
		//hints.set(DalHintEnum.forceAutoCommit);
		try{
			client.batchUpdate(sqls, hints);
			Assert.fail();
		}catch(Exception e){ }
		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(3, models.size());
	}
	
	@Test
	public void batchUpdateTestWithoutRollback() throws SQLException{
		String[] sqls = new String[] {
				"DELETE FROM " + TABLE_NAME + " WHERE ID = 1",
				"DELETE FROM " + TABLE_NAME + " WHERE _ID = 2",
				"DELETE FROM " + TABLE_NAME + " WHERE ID = 3" };
		DalHints hints = new DalHints();
		hints.set(DalHintEnum.forceAutoCommit);
		try{
			client.batchUpdate(sqls, hints);
			Assert.fail();
		}catch(Exception e){ }
		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(1, models.size());
	}
	
	/**
	 * Test the batch update function without parameters not all success.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void batchUpdateTestWithoutParametersNotAllSuccessed()
			throws SQLException {
		String[] sqls = new String[] {
				"DELETE FROM " + TABLE_NAME + " WHERE ID = 100",
				"UPDATE " + TABLE_NAME
						+ " SET address = 'HK INFO' WHERE id = 2",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(NULL, 10, 1, 'SH INFO', NULL)" };

		DalHints hints = new DalHints();
		int[] counts = client.batchUpdate(sqls, hints);
		Assert.assertEquals(3, counts.length);
		Assert.assertArrayEquals(new int[] { 0, 1, 1 }, counts);
	}

	/**
	 * Test the batch update function with parameters all success.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void batchUpdateTestWithParametersAllSuccessed() throws SQLException {
		String sql = "INSERT INTO " + TABLE_NAME + " VALUES(?, ?, ?, ?, ?)";
		StatementParameters[] parameterList = new StatementParameters[2];
		parameterList[0] = new StatementParameters();
		parameterList[0].set(1, Types.INTEGER, null);
		parameterList[0].set(2, Types.INTEGER, 11);
		parameterList[0].set(3, Types.SMALLINT, 2);
		parameterList[0].set(4, Types.VARCHAR, "SZ INFO");
		parameterList[0].set(5, Types.TIMESTAMP,
				new Timestamp(System.currentTimeMillis()));

		parameterList[1] = new StatementParameters();
		parameterList[1].set(1, Types.INTEGER, null);
		parameterList[1].set(2, Types.INTEGER, 11);
		parameterList[1].set(3, Types.SMALLINT, 2);
		parameterList[1].set(4, Types.VARCHAR, "HK INFO");
		parameterList[1].set(5, Types.TIMESTAMP,
				new Timestamp(System.currentTimeMillis()));

		DalHints hints = new DalHints();
		int[] counts = client.batchUpdate(sql, parameterList, hints);
		Assert.assertEquals(2, counts.length);
		Assert.assertArrayEquals(new int[] { 1, 1 }, counts);

		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(5, models.size());
	}

	/**
	 * Test the batch update function with parameters not all success.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void batchUpdateTestWithParametersNotAllSuccessed()
			throws SQLException {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
		StatementParameters[] parameterList = new StatementParameters[2];
		parameterList[0] = new StatementParameters();
		parameterList[0].set(1, Types.INTEGER, 1);

		parameterList[1] = new StatementParameters();
		parameterList[1].set(1, Types.INTEGER, 100);

		DalHints hints = new DalHints();
		int[] counts = client.batchUpdate(sql, parameterList, hints);
		Assert.assertEquals(2, counts.length);
		Assert.assertArrayEquals(new int[] { 1, 0 }, counts);

		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(2, models.size());
	}

	/**
	 * Test execute command function
	 * @throws SQLException
	 */
	@Test
	public void executeTestWithOneCommand() throws SQLException {
		final DalHints hints = new DalHints();
		DalCommand command = new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = 1";
				StatementParameters parameters = new StatementParameters();
				return client.update(sql, parameters, hints) == 1;
			}
		};
		client.execute(command, hints);

		List<ClientTestModel> models = this.queryModelsByIds(1);
		Assert.assertEquals(0, models.size());
	}

	/**
	 * Test execute multiple commands and all successes.
	 * @throws SQLException
	 */
	@Test
	public void executeTestWithMultipleCommandsAllSuccessed() throws SQLException {
		final DalHints hints = new DalHints();
		List<DalCommand> commands = new ArrayList<DalCommand>();
		commands.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = 1";
				StatementParameters parameters = new StatementParameters();
				return client.update(sql, parameters, hints) == 1;
			}
		});
		
		commands.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = 2";
				StatementParameters parameters = new StatementParameters();
				return client.update(sql, parameters, hints) == 1;
			}
		});
		
		commands.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = 3";
				StatementParameters parameters = new StatementParameters();
				return client.update(sql, parameters, hints) == 1;
			}
		});
		client.execute(commands, hints);
		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(0, models.size());
	}
	
	/**
	 * Test execute multiple commands and not all successes.
	 * If one failed, the after commands will be dropped.
	 * @throws SQLException
	 */
	@Test
	public void executeTestWithMultipleCommandsNotAllSuccessed() throws SQLException {
		final DalHints hints = new DalHints();
		List<DalCommand> commands = new ArrayList<DalCommand>();
		commands.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = 1";
				StatementParameters parameters = new StatementParameters();
				return client.update(sql, parameters, hints) == 1;
			}
		});
		
		commands.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = 100";
				StatementParameters parameters = new StatementParameters();
				return client.update(sql, parameters, hints) == 1;
			}
		});
		
		commands.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = 3";
				StatementParameters parameters = new StatementParameters();
				return client.update(sql, parameters, hints) == 1;
			}
		});
		client.execute(commands, hints);
		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(2, models.size());
	}

	/**
	 * Test call without parameters and has no resultsParameter
	 * @throws SQLException
	 */
	@Test
	public void callTestWithoutParametersNoResultsParameter() throws SQLException{
		String callSql = "call " + SP_I_NAME + "(4,12,1,'SZ INFO')";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(0, res.size());
		
		List<ClientTestModel> models = this.queryModelsByIds(4);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("SZ INFO", models.get(0).getAddress());
	}
	
	/**
	 * Test the call function with out parameters
	 * @throws SQLException
	 */
	@Test
	public void callTestWithoutParametersAndOutParameter() throws SQLException{
		String callSql = "call " + SP_D_NAME + "(1,?)";
		StatementParameters parameters = new StatementParameters();
		parameters.registerOut("count", Types.INTEGER);
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(2, ((Number)res.get("count")).intValue());
		
		List<ClientTestModel> models = this.queryModelsByIds(1);
		Assert.assertEquals(0, models.size());
		
		List<ClientTestModel> models_d = this.queryModelsByIds();
		Assert.assertEquals(2, models_d.size());
	}
	
	/**
	 * Test call without parameters but has resultsParameter
	 * @throws SQLException
	 */
	@Test
	public void callTestWithoutParametersAndResultsParameter() throws SQLException{
		String callSql = "call " + SP_I_NAME + "(4,12,1,'SZ INFO')";
		StatementParameters parameters = new StatementParameters();
		DalScalarExtractor extractor = new DalScalarExtractor();
		parameters.setResultsParameter("result", extractor);
		parameters.setResultsParameter("update_count");
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertEquals(2, res.size());
		Assert.assertTrue(res.containsKey("result"));
		Assert.assertTrue(res.containsKey("update_count"));
		Assert.assertEquals((long)1, res.get("result"));
		
		List<ClientTestModel> models = this.queryModelsByIds(4);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("SZ INFO", models.get(0).getAddress());
	}
	
	/**
	 * Test call with parameters but has no resultsParameter
	 * @throws SQLException
	 */
	@Test
	public void callTestWithParametersNoResultsParameter() throws SQLException {
		String callSql = "call " + SP_I_NAME + "(?,?,?,?)";
		StatementParameters parameters = new StatementParameters();
		parameters.set("dal_id", Types.INTEGER, 4);
		parameters.set("quantity", Types.INTEGER, 10);
		parameters.set("type", Types.SMALLINT, 3);
		parameters.set("address", Types.VARCHAR, "SZ INFO");
		
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(0, res.size());
		
		List<ClientTestModel> models = this.queryModelsByIds(4);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("SZ INFO", models.get(0).getAddress());
	}
	
	/**
	 * Test the call function with in-out parameters
	 * @throws SQLException
	 */
	@Test
	public void callTestWithParametersAndInOutParameters() throws SQLException{
		String callSql = "call " + SP_U_NAME + "(?,?,?,?)";
		StatementParameters parameters = new StatementParameters();
		parameters.set("dal_id", Types.INTEGER, 1);
		parameters.set("quantity", Types.INTEGER, 10);
		parameters.set("type", Types.SMALLINT, 3);
		//parameters.set("address", Types.VARCHAR, "SZ INFO");
		parameters.registerInOut("address", Types.VARCHAR, "SZ INFO");
		
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(1, res.size());
		Assert.assertTrue(res.containsKey("address"));
		
		List<ClientTestModel> models = this.queryModelsByIds(1);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("SZ INFO", models.get(0).getAddress());
	}
	
	/**
	 * Test call with parameters and has resultsParameter
	 * @throws SQLException
	 */
	@Test
	public void callTestWithParametersAndResultsParameter() throws SQLException {
		String callSql = "call " + SP_I_NAME + "(?,?,?,?)";
		StatementParameters parameters = new StatementParameters();
		parameters.set("dal_id", Types.INTEGER, 4);
		parameters.set("quantity", Types.INTEGER, 10);
		parameters.set("type", Types.SMALLINT, 3);
		parameters.set("address", Types.VARCHAR, "SZ INFO");
		
		DalScalarExtractor extractor = new DalScalarExtractor();
		parameters.setResultsParameter("result", extractor);
		parameters.setResultsParameter("update_count");
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertEquals(2, res.size());
		Assert.assertTrue(res.containsKey("result"));
		Assert.assertTrue(res.containsKey("update_count"));
		Assert.assertEquals((long)1, res.get("result"));
		
		List<ClientTestModel> models = this.queryModelsByIds(4);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("SZ INFO", models.get(0).getAddress());
	}
	
	/**
	 * Test batch call with parameters but has no ResultParameters
	 * @throws SQLException 
	 */
	@Test
	public void testBatchCallWithParametersNoResultParameters() throws SQLException{
		String callSql = "call " + SP_I_NAME + "(?,?,?,?)";
		StatementParameters[] parametersList = new StatementParameters[7];
		DalHints hints = new DalHints();
		for(int i = 0; i < 7; i++){
			StatementParameters param = new StatementParameters();
			param.set("dal_id", Types.INTEGER, null);
			param.set("quantity", Types.INTEGER, 10 + i);
			param.set("type", Types.SMALLINT, 3);
			param.set("address", Types.VARCHAR, "SZ INFO" + "_" + i);
			parametersList[i] = param;
		}
		int[] res = client.batchCall(callSql, parametersList, hints);
		Assert.assertEquals(7, res.length);
		
		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(10, models.size());
	}
	
	/**
	 * Test batch call with parameters and has ResultParameters
	 * @throws SQLException 
	 */
	@Test
	public void testBatchCallWithParametersAndResultParameters() throws SQLException{
		String callSql = "call " + SP_D_NAME + "(?,?)";
		StatementParameters[] parametersList = new StatementParameters[3];
		for(int i = 0; i < 3; i++){
			StatementParameters parameters = new StatementParameters();
			parameters.set("dal_id", Types.INTEGER, i + 1);
			parameters.registerOut("count", Types.INTEGER);
			parametersList[i] = parameters;
		}
		DalHints hints = new DalHints();
		int[] res = client.batchCall(callSql, parametersList, hints);
		Assert.assertEquals(3, res.length);
		
		List<ClientTestModel> models = this.queryModelsByIds(1,2,3);
		Assert.assertEquals(0, models.size());
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

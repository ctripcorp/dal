package test.com.ctrip.platform.dal.dao.unittests;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub;
import test.com.ctrip.platform.dal.dao.unitbase.ClientTestDalRowMapper;
import test.com.ctrip.platform.dal.dao.unitbase.ClientTestModel;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class DalDirectClientTestStub extends BaseTestStub {
	public DalDirectClientTestStub(String dbName, DatabaseDifference diff) {
		super(dbName, diff);
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
		assertEquals(1, count, 3);

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
		assertEquals(1, count, 2, "address='BJ INFO'");

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
		assertEquals(1, count, 2);

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
		if(!diff.supportGetGeneratedKeys)
			return;
		
		String insertSql = String.format(
				"INSERT INTO %s VALUES(NULL, 10, 1, 'SH INFO', NULL)",
				TABLE_NAME);
		StatementParameters parameters = new StatementParameters();
		KeyHolder holder = new KeyHolder();
		DalHints hints = new DalHints();
		int count = client.update(insertSql, parameters, hints.setKeyHolder(holder));
		Assert.assertEquals(1, count);
		Assert.assertEquals(1, holder.size());
		Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEY"));
		Assert.assertEquals(4, queryModelsByIds().size());
	}

	/**
	 * Test the update function with key-holder
	 * 
	 * @throws SQLException
	 */
	@Test
	public void updateTestInsertWithOutKeyHolder() throws SQLException {
		String insertSql = String.format(
				"INSERT INTO %s(quantity,type,address) VALUES(10, 1, 'SH INFO')",
				TABLE_NAME);
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int count = client.update(insertSql, parameters, hints);
		assertEquals(1, count, 4);
		Assert.assertEquals(4, queryModelsByIds().size());
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
				"INSERT INTO " + TABLE_NAME + "(quantity,type,address)"
						+ " VALUES(10, 1, 'SH INFO')" };

		DalHints hints = new DalHints();
		int[] counts = client.batchUpdate(sqls, hints);
		assertEquals(new int[] { 1, 1, 1 }, counts, 3);
		Assert.assertEquals("HK INFO", this.queryModelsByIds(2).get(0).getAddress());
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

	// The SQL server does not support auto commit for batch update
	@Test
	public void batchUpdateTestWithoutRollback() throws SQLException{
		if(diff.category == DatabaseCategory.SqlServer)
			return;
		
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
		Assert.assertEquals(3-1, models.size());
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
				"INSERT INTO " + TABLE_NAME + "(quantity,type,address)"
						+ " VALUES(10, 1, 'SH INFO')" };
//				Sql Server does not support insert NULL into PK
//				java.sql.BatchUpdateException: An explicit value for the identity column in table 'dal_client_test' can only be specified when a column list is used and IDENTITY_INSERT is ON.
//				"INSERT INTO " + TABLE_NAME
//						+ " VALUES(NULL, 10, 1, 'SH INFO', NULL)" };

		DalHints hints = new DalHints();
		int[] counts = client.batchUpdate(sqls, hints);
		assertEquals(new int[] { 0, 1, 1 }, counts, 4);
		Assert.assertEquals("HK INFO", this.queryModelsByIds(2).get(0).getAddress());
	}

	/**
	 * Test the batch update function with parameters all success.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void batchUpdateTestWithParametersAllSuccessed() throws SQLException {
		String sql = "INSERT INTO " + TABLE_NAME + "(quantity,type,address,last_changed) VALUES(?, ?, ?,?)";

		StatementParameters[] parameterList = new StatementParameters[2];
		parameterList[0] = new StatementParameters();
		parameterList[0].set(1, Types.INTEGER, 11);
		parameterList[0].set(2, Types.SMALLINT, 2);
		parameterList[0].set(3, Types.VARCHAR, "SZ INFO");
		parameterList[0].set(4, Types.TIMESTAMP,
				new Timestamp(System.currentTimeMillis()));

		parameterList[1] = new StatementParameters();
		parameterList[1].set(1, Types.INTEGER, 11);
		parameterList[1].set(2, Types.SMALLINT, 2);
		parameterList[1].set(3, Types.VARCHAR, "HK INFO");
		parameterList[1].set(4, Types.TIMESTAMP,
				new Timestamp(System.currentTimeMillis()));

		DalHints hints = new DalHints();
		int[] counts = client.batchUpdate(sql, parameterList, hints);
		assertEqualsBatchInsert(new int[]{1, 1}, counts, 3+2);

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
		assertEqualsBatch(new int[] { 1, 0 }, counts, 3-1);

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
				client.update(sql, parameters, hints);
				return true;
			}
		});
		
		commands.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = 2";
				StatementParameters parameters = new StatementParameters();
				client.update(sql, parameters, hints);
				return true;
			}
		});
		
		commands.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = 3";
				StatementParameters parameters = new StatementParameters();
				client.update(sql, parameters, hints);
				return true;
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
				client.update(sql, parameters, hints);
				return false;
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
	public void callTestWithoutParametersForSpWithoutOutParameter() throws SQLException{
		String callSql = "{call " + SP_WITHOUT_OUT_PARAM + "(4, 12,1,'SZ INFO')}";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(0, res.size());
		
		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(4, models.size());
	}

	/**
	 * Test call with parameters but has no resultsParameter
	 * @throws SQLException
	 */
	@Test
	public void callTestWithParametersForSpWithoutOutParameter() throws SQLException {
		String callSql = "{call " + SP_WITHOUT_OUT_PARAM + "(?,?,?,?)}";
		StatementParameters parameters = new StatementParameters();
		parameters.set("v_id", Types.INTEGER, 4);
		parameters.set("v_quantity", Types.INTEGER, 10);
		parameters.set("v_type", Types.SMALLINT, 3);
		parameters.set("v_address", Types.VARCHAR, "SZ INFO");
		
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(0, res.size());
		
		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(4, models.size());
	}
	
	/**
	 * Test batch call with parameters but has no ResultParameters
	 * @throws SQLException 
	 */
	@Test
	public void batchCallTestWithParametersForSpWithoutOutParameter() throws SQLException{
		String callSql = "{call " + SP_WITHOUT_OUT_PARAM + "(?,?,?,?)}";
		StatementParameters[] parametersList = new StatementParameters[7];
		DalHints hints = new DalHints();
		for(int i = 0; i < 7; i++){
			StatementParameters parameters = new StatementParameters();
			parameters.set("v_id", Types.INTEGER, null);
			parameters.set("v_quantity", Types.INTEGER, 10 + i);
			parameters.set("v_type", Types.SMALLINT, 3);
			parameters.set("v_address", Types.VARCHAR, "SZ INFO" + "_" + i);
			parametersList[i] = parameters;
		}
		int[] res = client.batchCall(callSql, parametersList, hints);
		Assert.assertEquals(7, res.length);
		
		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(10, models.size());
	}
	
	/**
	 * Test the call function with out parameters
	 * @throws SQLException
	 */
	@Test
	public void callTestForSpWithOutParameter() throws SQLException{
		String callSql = diff.category == DatabaseCategory.SqlServer ?
				"{call " + SP_WITH_OUT_PARAM + "(?,?)}":
					"call " + SP_WITH_OUT_PARAM + "(?,?)";
		StatementParameters parameters = new StatementParameters();
		parameters.set("v_id", Types.INTEGER, 1);
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
	 * Test batch call with parameters and has ResultParameters
	 * @throws SQLException 
	 */
	@Test
	public void batchCallTestForSpWithOutParameter() throws SQLException{
		// Oracle do not support out parameter for batch store procedure update
		if(!diff.supportBatchSpWithOutParameter) 
			return;
		
		String callSql = "{call " + SP_WITH_OUT_PARAM + "(?,?)}";
		StatementParameters[] parametersList = new StatementParameters[3];
		for(int i = 0; i < 3; i++){
			StatementParameters parameters = new StatementParameters();
			parameters.set("v_id", Types.INTEGER, i + 1);
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
	 * Test the call function with in-out parameters
	 * @throws SQLException
	 */
	@Test
	public void callTestForSpWithInOutParameters() throws SQLException{
		String callSql = "{call " + SP_WITH_IN_OUT_PARAM + "(?,?,?,?)}";
		StatementParameters parameters = new StatementParameters();
		parameters.set("v_id", Types.INTEGER, 1);
		parameters.set("v_quantity", Types.INTEGER, 10);
		parameters.set("v_type", Types.SMALLINT, 3);
		parameters.registerInOut("v_address", Types.VARCHAR, "SZ INFO");
		
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(1, res.size());
		Assert.assertTrue(res.containsKey("v_address"));
		Assert.assertEquals("output", parameters.get("v_address", ParameterDirection.InputOutput).getValue());
		
		List<ClientTestModel> models = this.queryModelsByIds(1);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("SZ INFO", models.get(0).getAddress());
	}

	/**
	 * Test the call function with retrieveAllResultsFromSp parameters
	 * @throws SQLException
	 */
	@Test
	public void callTestForSpWithIntermediateParameters() throws SQLException{
		String callSql = "{call " + SP_WITH_INTERMEDIATE_RESULT + "(?,?,?,?)}";
		StatementParameters parameters = new StatementParameters();
		parameters.set("v_id", Types.INTEGER, 1);
		parameters.set("v_quantity", Types.INTEGER, 10);
		parameters.set("v_type", Types.SMALLINT, 3);
		parameters.registerInOut("v_address", Types.VARCHAR, "SZ INFO");
		
		DalHints hints = new DalHints().retrieveAllResultsFromSp();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		System.out.println(res);
		Assert.assertTrue(null != res);
		
		if(diff.supportSpIntermediateResult)
			Assert.assertTrue(res.size()>=5);//mysql will return update count as well, while sqlserver will not return update count
		else
			Assert.assertEquals(1, res.size());
		
		Assert.assertTrue(res.containsKey("v_address"));
		Assert.assertEquals("output", res.get("v_address"));
		Assert.assertEquals("output", parameters.get("v_address", ParameterDirection.InputOutput).getValue());
		
		List<ClientTestModel> models = this.queryModelsByIds(1);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("aaa", models.get(0).getAddress());
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
}

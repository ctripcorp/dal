package com.ctrip.platform.dal.dao.unittests;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.unitbase.ClientTestModel;
import com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class DalDirectClientSqlServerTest extends DalDirectClientTestStub {
	private static SqlServerDatabaseInitializer initializer = new SqlServerDatabaseInitializer();
	public DalDirectClientSqlServerTest() {
		super(initializer.DATABASE_NAME, initializer.diff);
	}
	private final static String DATABASE_NAME = initializer.DATABASE_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		initializer.setUpBeforeClass();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		initializer.tearDownAfterClass();
	}

	@Before
	public void setUp() throws Exception {
		initializer.setUp();
	}

	@After
	public void tearDown() throws Exception {
		initializer.tearDown();
	}

	@Test
	public void TSQLTestForSpWithOutParameter() throws SQLException{
		String callSql = "exec " + SP_WITH_OUT_PARAM + " @v_id=?, @count=?";

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
	public void testBatchCallWithParametersAndResultParameters() throws SQLException{
		String callSql = "{call " + SP_WITH_OUT_PARAM + "(?,NULL)}";
		StatementParameters[] parametersList = new StatementParameters[3];
		for(int i = 0; i < 3; i++){
			StatementParameters parameters = new StatementParameters();
			parameters.set("v_id", Types.INTEGER, i + 1);
			//parameters.registerOut("count", Types.INTEGER);
			parametersList[i] = parameters;
		}
		DalHints hints = new DalHints();
		int[] res = client.batchCall(callSql, parametersList, hints);
		Assert.assertEquals(3, res.length);
		
		List<ClientTestModel> models = this.queryModelsByIds(1,2,3);
		Assert.assertEquals(0, models.size());
	}

	@Test
	public void testTSQLBatchCallWithParametersAndResultParameters() throws SQLException{
		String callSql = "exec " + SP_WITH_OUT_PARAM + " @v_id=?, @count=Null";
		StatementParameters[] parametersList = new StatementParameters[3];
		for(int i = 0; i < 3; i++){
			StatementParameters parameters = new StatementParameters();
			parameters.set("v_id",Types.INTEGER, i + 1);
//			parameters.registerOut("count", Types.INTEGER);
			parametersList[i] = parameters;
		}
		DalHints hints = new DalHints();
		int[] res = client.batchCall(callSql, parametersList, hints);
		Assert.assertEquals(3, res.length);

		List<ClientTestModel> models = this.queryModelsByIds(1,2,3);
		Assert.assertEquals(0, models.size());
	}

	@Test
	public void execTestWithoutParametersForSpWithoutOutParameter() throws SQLException{
		String callSql = "exec " + SP_WITHOUT_OUT_PARAM + " @v_id=4, @v_quantity=12, @v_type=1, @v_address='SZ INFO'";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(0, res.size());

		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(4, models.size());
	}

	@Test
	public void execTestWithParametersForSpWithoutOutParameter() throws SQLException {
		String callSql = "exec " + SP_WITHOUT_OUT_PARAM + " @v_id=?, @v_quantity=?, @v_type=?, @v_address=?";

//		set disordered parameter by name
		StatementParameters parameters = new StatementParameters();
		parameters.set("v_quantity", Types.INTEGER, 10);
		parameters.set("v_id", Types.INTEGER, 4);
		parameters.set("v_type", Types.SMALLINT, 3);
		parameters.set("v_address", Types.VARCHAR, "SZ INFO");

		DalHints hints = new DalHints();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(0, res.size());

		List<ClientTestModel> models = this.queryModelsByIds();
		Assert.assertEquals(4, models.size());

//		set disordered parameter by index and name
		StatementParameters parameters2 = new StatementParameters();
		int index=1;
		parameters2.set(index++,"v_quantity", Types.INTEGER, 10);
		parameters2.set(index++,"v_id", Types.INTEGER, 4);
		parameters2.set(index++,"v_type", Types.SMALLINT, 3);
		parameters2.set(index++,"v_address", Types.VARCHAR, "SZ INFO");

		try{
			client.call(callSql, parameters2, new DalHints());
		}catch (Exception e){
			Assert.assertTrue(e.getMessage().contains("doesn't match with the index"));
		}

//		set disordered parameter by index
		StatementParameters parameters3 = new StatementParameters();
		index=1;
		parameters3.set(index++,Types.VARCHAR, "SZ INFO");
		parameters3.set(index++,Types.INTEGER, 10);
		parameters3.set(index++,Types.INTEGER, 4);
		parameters3.set(index++,Types.SMALLINT, 3);
		try{
			client.call(callSql, parameters3, new DalHints());
		}catch (Exception e){
			Assert.assertTrue(e.getMessage().contains("Error converting data type varchar to int"));
		}

		//		set ordered parameter by index
		StatementParameters parameters4 = new StatementParameters();
		index=1;
		parameters4.set(index++,Types.INTEGER, 5);
		parameters4.set(index++,Types.INTEGER, 10);
		parameters4.set(index++,Types.SMALLINT, 3);
		parameters4.set(index++,Types.VARCHAR, "SZ INFO");
		try{
			client.call(callSql, parameters4, new DalHints());
		}catch (Exception e){
			Assert.fail();
		}
		Assert.assertTrue(null != res);
		Assert.assertEquals(0, res.size());

		models = this.queryModelsByIds();
		Assert.assertEquals(5, models.size());


		//		set ordered parameter by index and name
		StatementParameters parameters5 = new StatementParameters();
		index=1;
		parameters5.set(index++,"v_id", Types.INTEGER, 6);
		parameters5.set(index++,"v_quantity", Types.INTEGER, 10);
		parameters5.set(index++,"v_type", Types.SMALLINT, 3);
		parameters5.set(index++,"v_address", Types.VARCHAR, "SZ INFO");

		try{
			client.call(callSql, parameters5, new DalHints());
		}catch (Exception e){
			Assert.fail();
		}

		Assert.assertTrue(null != res);
		Assert.assertEquals(0, res.size());

		models = this.queryModelsByIds();
		Assert.assertEquals(6, models.size());
	}


	@Test
	public void batchExecTestWithParametersForSpWithoutOutParameter() throws SQLException{
		String callSql = "exec " + SP_WITHOUT_OUT_PARAM + " @v_id=?, @v_quantity=?, @v_type=?, @v_address=?";
//		set parameter by name
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

//		set parameter by index
		StatementParameters[] parametersList2 = new StatementParameters[7];
		for(int i = 0; i < 7; i++){
			StatementParameters parameters = new StatementParameters();
			int index=1;
			parameters.set(index++, Types.INTEGER, null);
			parameters.set(index++, Types.INTEGER, 10 + i);
			parameters.set(index++, Types.SMALLINT, 3);
			parameters.set(index++, Types.VARCHAR, "SZ INFO" + "_" + i);
			parametersList2[i] = parameters;
		}
		res = client.batchCall(callSql, parametersList, hints);
		Assert.assertEquals(7, res.length);

		models = this.queryModelsByIds();
		Assert.assertEquals(17, models.size());

		//		set parameter by index and name
		StatementParameters[] parametersList3 = new StatementParameters[7];
		for(int i = 0; i < 7; i++){
			StatementParameters parameters = new StatementParameters();
			int index=1;
			parameters.set(index++, "v_id", Types.INTEGER, null);
			parameters.set(index++, "v_quantity",Types.INTEGER, 10 + i);
			parameters.set(index++, "v_type",Types.SMALLINT, 3);
			parameters.set(index++, "v_address",Types.VARCHAR, "SZ INFO" + "_" + i);
			parametersList3[i] = parameters;
		}
		res = client.batchCall(callSql, parametersList, hints);
		Assert.assertEquals(7, res.length);

		models = this.queryModelsByIds();
		Assert.assertEquals(24, models.size());
	}


	@Test
	public void execTestForSpWithInOutParameters() throws SQLException{
		String callSql = "exec " + SP_WITH_IN_OUT_PARAM + " @v_id=?, @v_quantity=?, @v_type=?, @v_address=?";
//		set parameter by name
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
		Assert.assertEquals("output", res.get("v_address"));

		List<ClientTestModel> models = this.queryModelsByIds(1);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("SZ INFO", models.get(0).getAddress());


		//		set parameter by index
		StatementParameters parameters2 = new StatementParameters();
		int index=1;
		parameters2.set(index++, Types.INTEGER, 2);
		parameters2.set(index++, Types.INTEGER, 10);
		parameters2.set(index++, Types.SMALLINT, 3);
		parameters2.registerInOut(index++, Types.VARCHAR, "SZ INFO");

		res = client.call(callSql, parameters2, hints);
		Assert.assertTrue(null != res);
		Assert.assertEquals(1, res.size());
		Assert.assertEquals("output",parameters.get(3).getValue());

//		set parameter with disordered index
		StatementParameters parameters3 = new StatementParameters();
		index=1;
		parameters3.registerInOut(index++, Types.VARCHAR, "SZ INFO");
		parameters3.set(index++, Types.INTEGER, 3);
		parameters3.set(index++, Types.INTEGER, 10);
		parameters3.set(index++, Types.SMALLINT, 3);
		try{
			client.call(callSql, parameters3, hints);
			Assert.fail();
		}catch (Exception e){
			Assert.assertTrue(e.getMessage().contains("was not declared as an OUTPUT parameter"));
		}

		//		set parameter by disordered name
		StatementParameters parameters4 = new StatementParameters();
		parameters4.registerInOut("v_address", Types.VARCHAR, "SZ INFO");
		parameters4.set("v_id", Types.INTEGER, 3);
		parameters4.set("v_quantity", Types.INTEGER, 10);
		parameters4.set("v_type", Types.SMALLINT, 3);

		try {
			res = client.call(callSql, parameters4, hints);
		}catch (Exception e){
			Assert.fail();
		}
		Assert.assertTrue(null != res);
		Assert.assertEquals(1, res.size());
		Assert.assertTrue(res.containsKey("v_address"));
		Assert.assertEquals("output", res.get("v_address"));
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

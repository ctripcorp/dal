package test.com.ctrip.platform.dal.dao.unittests;

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

import test.com.ctrip.platform.dal.dao.unitbase.ClientTestDalRowMapper;
import test.com.ctrip.platform.dal.dao.unitbase.ClientTestModel;
import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

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

	/**
	 * Test the call function with out parameters
	 * @throws SQLException
	 */
	@Test
	public void callTestWithoutParametersAndOutParameter() throws SQLException{
		String callSql = "{call " + SP_D_NAME + "(?,?)}";
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
	 * Test the call function with retrieveAllResultsFromSp parameters
	 * @throws SQLException
	 */
	@Test
	public void callTestWithAutoParameters() throws SQLException{
		String callSql = "{call " + MULTIPLE_RESULT_SP_SQL + "(?,?,?,?)}";
		StatementParameters parameters = new StatementParameters();
		parameters.set("dal_id", Types.INTEGER, 1);
		parameters.set("quantity", Types.INTEGER, 10);
		parameters.set("type", Types.SMALLINT, 3);
		parameters.registerInOut("address", Types.VARCHAR, "SZ INFO");
		
		DalHints hints = new DalHints().retrieveAllResultsFromSp();
		Map<String, ?> res = client.call(callSql, parameters, hints);
		System.out.println(res);
		Assert.assertTrue(null != res);
		Assert.assertEquals(5, res.size());
		Assert.assertTrue(res.containsKey("address"));
		Assert.assertEquals("output", res.get("address"));
		Assert.assertEquals("output", parameters.get("address", ParameterDirection.InputOutput).getValue());
		
		List<ClientTestModel> models = this.queryModelsByIds(1);
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("aaa", models.get(0).getAddress());
	}
	

	/**
	 * Test batch call with parameters and has ResultParameters
	 * @throws SQLException 
	 */
	@Test
	public void testBatchCallWithParametersAndResultParameters() throws SQLException{
		String callSql = "{call " + SP_D_NAME + "(?,NULL)}";
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

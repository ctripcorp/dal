package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalCustomRowMapperTest {
	
	private static DalClient client = null;
	
	private static final String DATABASE = "dao_test";
	private final static String TABLE_NAME = "dal_client_test";
	
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE dal_client_test("
				+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
				+ "quantity int,"
				+ "type smallint, "
				+ "address VARCHAR(64) not null, "
				+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	static{
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL};
		client.batchUpdate(sqls, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL};
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
	
	@Test
	public void testArray() throws SQLException {
		DalCustomRowMapper mapper = new DalCustomRowMapper("id", "quantity", "type");
		String sql = "select id, quantity, type from " + TABLE_NAME;
		
		DalRowMapperExtractor<Map<String, Object>> rse =
				new DalRowMapperExtractor<Map<String, Object>>(mapper);
		List<Map<String, Object>> rest = client.query(sql, new StatementParameters(), new DalHints(), rse);
		Assert.assertEquals(3, rest.size());
		Assert.assertEquals("1", rest.get(0).get("id").toString());
	}
	
	@Test
	public void testList() throws SQLException {
		List<String> columns = new ArrayList<String>();
		columns.add("id");
		DalCustomRowMapper mapper = new DalCustomRowMapper(columns);
		String sql = "select id from " + TABLE_NAME;
		
		DalRowMapperExtractor<Map<String, Object>> rse =
				new DalRowMapperExtractor<Map<String, Object>>(mapper);
		List<Map<String, Object>> rest = client.query(sql, new StatementParameters(), new DalHints(), rse);
		Assert.assertEquals(3, rest.size());
		Assert.assertEquals("1", rest.get(0).get("id").toString());
	}
}

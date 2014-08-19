package com.ctrip.platform.dal.dao.helpers;

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

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalCustomRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.unitbase.Database;

public class DalCustomRowMapperTest{
	
	private static Database database = null;
	
	static{
		database = new Database("dao_test", "dal_client_test", DatabaseCategory.MySql);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		database.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		database.drop();
	}

	@Before
	public void setUp() throws Exception {
		database.mock();
	}

	@After
	public void tearDown() throws Exception {
		database.clear();

	}
	
	@Test
	public void testArray() throws SQLException {
		DalCustomRowMapper mapper = new DalCustomRowMapper("id", "quantity", "type");
		String sql = "select id, quantity, type from " + database.getTableName();
		
		DalRowMapperExtractor<Map<String, Object>> rse =
				new DalRowMapperExtractor<Map<String, Object>>(mapper);
		List<Map<String, Object>> rest = database.getClient().query(sql, new StatementParameters(), new DalHints(), rse);
		Assert.assertEquals(3, rest.size());
		Assert.assertEquals("1", rest.get(0).get("id").toString());
		
	}
	
	@Test
	public void testList() throws SQLException {
		List<String> columns = new ArrayList<String>();
		columns.add("id");
		DalCustomRowMapper mapper = new DalCustomRowMapper(columns);
		String sql = "select id from " + database.getTableName();
		
		DalRowMapperExtractor<Map<String, Object>> rse =
				new DalRowMapperExtractor<Map<String, Object>>(mapper);
		List<Map<String, Object>> rest = database.getClient().query(sql, new StatementParameters(), new DalHints(), rse);
		Assert.assertEquals(3, rest.size());
		Assert.assertEquals("1", rest.get(0).get("id").toString());
	}
}

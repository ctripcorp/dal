package test.com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.com.ctrip.platform.dal.dao.unitbase.Database;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalCustomRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

	@Test
	public void testDalColumnMapRowMapperAlias() {
		try {
			StatementParameters parameters = new StatementParameters();
			String sqlAlias="select quantity as q, type as t from "+ database.getTableName() +" where id=1";
			DalQueryDao dao = new DalQueryDao(database.getDatabaseName());
			List<Map<String, Object>> result = dao.query(sqlAlias, parameters, new DalHints(), new DalColumnMapRowMapper());
			assertEquals(1, result.size());
			assertEquals(10,result.get(0).get("q"));
			assertEquals(1,result.get(0).get("t"));
		} catch (Exception e) {
			fail();
		}
	}
}

package test.com.ctrip.platform.dal.dao.helper;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public class DalColumnMapRowMapperTest {
	private final static String DATABASE_NAME = "dao_test_sqlsvr";

	private final static String TABLE_NAME = "dal_client_test";
	
	private final static String DROP_TABLE_SQL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME +"("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	private static DalClient client = null;

	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL};
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL};
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}
	
	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"SET IDENTITY_INSERT "+ TABLE_NAME +" ON",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(1, 10, 1, 'SH INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(2, 11, 1, 'BJ INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(3, 12, 2, 'SZ INFO')",
				"SET IDENTITY_INSERT "+ TABLE_NAME +" OFF"};
		client.batchUpdate(insertSqls, hints);
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
	
	private DalHints hints = new DalHints();
	private String sqlList = "select * from " + TABLE_NAME;
	private String sqlObject = "select * from " + TABLE_NAME + " where id = ?";
	private String sqlNoResult = "select * from " + TABLE_NAME + " where id = -1";
	private String sqlAlias="select quantity as q, type as t from "+ TABLE_NAME +" where id=1";
	@Test
	public void testDalColumnMapRowMapperList() {
		try {
			StatementParameters parameters = new StatementParameters();
			
			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
			List<Map<String, Object>> result = dao.query(sqlList, parameters, hints, new DalColumnMapRowMapper());
			assertEquals(3, result.size());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testDalColumnMapRowMapperEmpty() {
		try {
			StatementParameters parameters = new StatementParameters();
			DalClient client = DalClientFactory.getClient(DATABASE_NAME);
			List<Map<String, Object>> result1 = client.query(sqlNoResult, parameters, new DalHints(), new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper()));
			assertEquals(0, result1.size());

			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
			List<Map<String, Object>> result = dao.query(sqlNoResult, parameters, hints, new DalColumnMapRowMapper());
			assertEquals(0, result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testDalColumnMapRowMapperOne() {
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 3);
			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
			List<Map<String, Object>> result = dao.query(sqlObject, parameters, hints, new DalColumnMapRowMapper());
			assertEquals(1, result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testDalColumnMapRowMapperAlias() {
		try {
			StatementParameters parameters = new StatementParameters();

			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
			List<Map<String, Object>> result = dao.query(sqlAlias, parameters, hints, new DalColumnMapRowMapper());
			assertEquals(1, result.size());
			assertEquals(10,result.get(0).get("q"));
			assertEquals(new Short("1"),result.get(0).get("t"));
		} catch (Exception e) {
			fail();
		}
	}
}
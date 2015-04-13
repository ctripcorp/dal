package com.ctrip.platform.dal.tester.baseDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTableDaoTest {
	private final static String DATABASE_NAME = "dao_test";
	
	private final static String TABLE_NAME = "dal_client_test";
	
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME +"("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	private static DalClient client = null;
	private static DalParser<ClientTestModel> clientTestParser = new ClientTestDalParser();
	private static DalTableDao<ClientTestModel> dao;

	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
			dao = new DalTableDao<ClientTestModel>(clientTestParser);
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
		assertArrayEquals(new int[] { 1, 1, 1 }, counts);
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
			fail();
		}
	}

	private StatementParameters parameters = new StatementParameters();
	private DalHints hints = new DalHints();

	@Test
	public void testQueryByPkNumber() {
		try {
			ClientTestModel p = dao.queryByPk(1, hints);
			assertEquals("SH INFO", p.getAddress());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testQueryByPk() {
		try {
			ClientTestModel p = new ClientTestModel();
			p.setId(1);
			p = dao.queryByPk(p, hints);
			assertEquals("SH INFO", p.getAddress());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryLike() {
		try {
			ClientTestModel p = new ClientTestModel();
			p.setAddress("SH INFO");

			List<ClientTestModel> pList = dao.queryLike(p, hints);
			assertEquals(1, pList.size());
			assertEquals("SH INFO", pList.get(0).getAddress());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	
	@Test
	public void testQuery() {
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 1);

			List<ClientTestModel> pList = dao.query("ID = ?", parameters, hints);
			assertEquals(1, pList.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRange() {
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 1);
			
			ClientTestModel p = dao.queryFirst("ID > ?", parameters, hints);
			assertEquals(2, p.getId().intValue());
			
			List<ClientTestModel> result = dao.queryTop("ID > ?", parameters, hints, 5);
			assertEquals(2, result.size());
			
			result = dao.queryFrom("ID > ?", parameters, hints, 0, 3);
			assertEquals(2, result.size());
			
			result = dao.queryFrom("ID > ?", parameters, hints, 1, 3);
			assertEquals(1, result.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testInsert() {
		try {
			ClientTestModel p = new ClientTestModel();
			p.setAddress("insert test 1");
			assertEquals(1, dao.insert(hints, p));
			p.setAddress("insert test 2");
			assertEquals(1, dao.insert(hints, p));
			p.setAddress("insert test 3");
			assertEquals(1, dao.insert(hints, p));
			
			ClientTestModel[] pList = new ClientTestModel[3];
			p = new ClientTestModel();
			p.setAddress("insert test 4");
			pList[0] = p;
			p = new ClientTestModel();
			p.setAddress("insert test 5");
			pList[1] = p;
			p = new ClientTestModel();
			p.setAddress("insert test 6");
			pList[2] = p;
			
			assertEquals(3, dao.insert(hints, pList));			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdate() {
		try {
			
			ClientTestModel p = new ClientTestModel();
			try {
				dao.update(hints, p);
				fail();
			} catch (Exception e) {
			}

			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.VARCHAR, "SH%");
			
			p = dao.queryFirst("Address Like ?", parameters, hints);
			System.out.println(p.getId());
			p.setAddress("Never mind it");
			dao.update(hints, p);
			p = dao.queryByPk(p.getId(), hints);
			assertEquals("Never mind it", p.getAddress());
			
			parameters = new StatementParameters();
			parameters.set(1, Types.VARCHAR, "update test");
			parameters.set(2, Types.INTEGER, p.getId());
			dao.update("update " + TABLE_NAME + " set Address = ? where ID >= ?", parameters, hints);
			p = dao.queryByPk(p.getId(), hints);
			assertEquals("update test", p.getAddress());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDelete() {
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.VARCHAR, "SH INFO");
			
			ClientTestModel p = dao.queryFirst("Address Like ?", parameters, hints);
			assertNotNull(p);
			dao.delete(hints, p);
			try{
				assertNull(dao.queryByPk(p.getId(), hints));
				
			} catch (Exception e) {
				fail();
			}
			
			parameters = new StatementParameters();
			parameters.set(1, Types.VARCHAR, "SH INFO");
			dao.delete("Address LIKE ?", parameters, hints);
			List<ClientTestModel> result = dao.query("Address LIKE ?", parameters, hints);
			assertEquals(0, result.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testInsertWithKeyHolder() {
		try {
			dao.delete("Address = 'testInsertKH'", parameters, hints);
			
			ClientTestModel p = new ClientTestModel();
			
			ClientTestModel[] pList = new ClientTestModel[3];
			p = new ClientTestModel();
			p.setAddress("testInsertKH");
			pList[0] = p;
			p = new ClientTestModel();
			p.setAddress("testInsertKH");
			pList[1] = p;
			p = new ClientTestModel();
			p.setAddress("testInsertKH");
			pList[2] = p;
			
			KeyHolder keyHolder = new KeyHolder();
			dao.insert(hints, keyHolder, pList);
			assertEquals(3, keyHolder.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCombinedInsert() {
		try {
			dao.delete("Address LIKE 'testInsertCombined%'", parameters, hints);
			
			ClientTestModel p = new ClientTestModel();
			
			ClientTestModel[] pList = new ClientTestModel[3];
			p = new ClientTestModel();
			p.setAddress("testInsertCombined1");
			pList[0] = p;
			p = new ClientTestModel();
			p.setAddress("testInsertCombined2");
			pList[1] = p;
			p = new ClientTestModel();
			p.setAddress("testInsertCombined3");
			pList[2] = p;
			
			KeyHolder keyHolder = new KeyHolder();
			dao.combinedInsert(hints, keyHolder, pList);
			assertEquals(3, keyHolder.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testContinueOnError() {
		try {
			ClientTestModel p = new ClientTestModel();
			ClientTestModel[] pList = new ClientTestModel[3];
			p = new ClientTestModel();
			p.setAddress("ContinueOnError");
			pList[0] = p;
			p = new ClientTestModel();
			p.setAddress("ContinueOnErrorContinueOnErrorContinueOnErrorContinueOnErrorContinueOnError");
			pList[1] = p;
			p = new ClientTestModel();
			p.setAddress("ContinueOnError");
			pList[2] = p;
			
			hints = new DalHints(DalHintEnum.continueOnError);
			int count = dao.insert(hints, pList);
			assertEquals(2, count);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testBatchInsert() {
		try {
			ClientTestModel p;
			ClientTestModel[] pList = new ClientTestModel[3];
			p = new ClientTestModel();
			p.setAddress("insert test 4");
			pList[0] = p;
			p = new ClientTestModel();
			p.setAddress("insert test 5");
			pList[1] = p;
			p = new ClientTestModel();
			p.setAddress("insert test 6");
			pList[2] = p;
			
			int[] results = dao.batchInsert(hints, pList);
			assertEquals(3, results.length);
			for(int i = 0; i< 3; i++){
				assertTrue(results[i] > 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static class ClientTestDalParser implements DalParser<ClientTestModel>{
		private static final String databaseName="dao_test";
		private static final String tableName= "dal_client_test";
		private static final String[] columnNames = new String[]{
			"id","quantity","type","address","last_changed"
		};
		private static final String[] primaryKeyNames = new String[]{"id"};
		private static final int[] columnTypes = new int[]{
			Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR, Types.TIMESTAMP
		};
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

		@Override
		public String getDatabaseName() {
			return databaseName;
		}

		@Override
		public String getTableName() {
			return tableName;
		}

		@Override
		public String[] getColumnNames() {
			return columnNames;
		}

		@Override
		public String[] getPrimaryKeyNames() {
			return primaryKeyNames;
		}

		@Override
		public int[] getColumnTypes() {
			return columnTypes;
		}

		@Override
		public boolean isAutoIncrement() {
			return true;
		}

		@Override
		public Number getIdentityValue(ClientTestModel pojo) {
			return pojo.getId();
		}

		@Override
		public Map<String, ?> getPrimaryKeys(ClientTestModel pojo) {
			Map<String, Object> keys = new LinkedHashMap<String, Object>();
			keys.put("id", pojo.getId());
			return keys;
		}

		@Override
		public Map<String, ?> getFields(ClientTestModel pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("id", pojo.getId());
			map.put("quantity", pojo.getQuantity());
			map.put("type", pojo.getType());
			map.put("address", pojo.getAddress());
			map.put("last_changed", pojo.getLastChanged());
			return map;
		}
		
	}
	
	private static class ClientTestModel {
		private Integer id;
		private Integer quantity;
		private Short type;
		private String address;
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public Short getType() {
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
}
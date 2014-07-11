package com.ctrip.platform.dal.tester.baseDao;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.FixedValueRowMapper;

public class DalQueryDaoTest {
	private final static String DATABASE_NAME = "HotelPubDB";

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
	
	private StatementParameters parameters = new StatementParameters();
	private DalHints hints = new DalHints();
	private String sqlList = "select * from " + TABLE_NAME;
	private String sqlListQuantity = "select quantity from " + TABLE_NAME;
	private String sqlObject = "select * from " + TABLE_NAME + " where id = ?";
	private String sqlNoResult = "select * from " + TABLE_NAME + " where id = -1";
	
	@Test
	public void testQueryMapperForList() {
		try {
			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
			List<Short> result = dao.query(sqlList, parameters, hints, new DalRowMapper<Short>() {
				@Override
				public Short map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getShort("quantity");
				}
			});
			assertEquals(3, result.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testQueryClassForList() {
		try {
			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
			List<Integer> result = dao.query(sqlList, parameters, hints, Integer.class);
			assertEquals(3, result.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class DalRowCallbackTest implements DalRowCallback {
		int result = 0;
		public void process(ResultSet rs) throws SQLException {
			result+=rs.getShort("quantity");
		}
	}
	
	@Test
	public void testQueryCallbackForList() {
		try {
			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
			int a = 0;
			DalRowCallbackTest test = new DalRowCallbackTest();
			dao.query(sqlList, parameters, hints, test);
			assertEquals(33, test.result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Test
	public void testQueryForObject() {
		DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
		
		Integer id;
		// This will fail
		try {
			id = dao.queryForObject(sqlList, parameters, hints, new FixedValueRowMapper<Integer>());
			fail();
		} catch (SQLException e) {
		}
		
		// This will pass
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 1);

			id = dao.queryForObject(sqlObject, parameters, hints, new FixedValueRowMapper<Integer>(1));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		// This will fail
		try {
			dao.queryForObject(sqlList, parameters, hints, Integer.class);
			fail();
		} catch (SQLException e) {
		}
		
		// This will pass
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 1);

			dao.queryForObject(sqlObject, parameters, hints, Integer.class);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryForObjectNullable() {
		DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
		
		Integer id;
		try {
			id = dao.queryForObjectNullable(sqlList, parameters, hints, new FixedValueRowMapper<Integer>());
			fail();
		} catch (SQLException e) {
		}
		
		// This will pass
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 1);

			id = dao.queryForObjectNullable(sqlObject, parameters, hints, new FixedValueRowMapper<Integer>(1));
			
			parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, -1);

			assertNull(dao.queryForObjectNullable(sqlObject, parameters, hints, new FixedValueRowMapper<Integer>(1)));

		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		// This will fail
		try {
			dao.queryForObjectNullable(sqlList, parameters, hints, Integer.class);
			fail();
		} catch (SQLException e) {
		}
		
		// This will pass
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 1);

			dao.queryForObjectNullable(sqlObject, parameters, hints, Integer.class);
			
			parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, -1);
			
			assertNull(dao.queryForObjectNullable(sqlObject, parameters, hints, Integer.class));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRange() {
		try {
			DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
			DalRowMapper<Integer> mapper = new DalRowMapper<Integer>() {
				@Override
				public Integer map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt("Id");
				}
			};
			
			Integer id = dao.queryFirst(sqlList, parameters, hints, mapper);
			assertNotNull(id);
			
			id = dao.queryFirst(sqlList, parameters, hints, Integer.class);
			assertNotNull(id);
			
			id = dao.queryFirstNullable(sqlList, parameters, hints, mapper);
			assertNotNull(id);
			
			id = dao.queryFirstNullable(sqlList, parameters, hints, Integer.class);
			assertNotNull(id);
			
			id = dao.queryFirstNullable(sqlNoResult, parameters, hints, mapper);
			assertNull(id);
			
			id = dao.queryFirstNullable(sqlNoResult, parameters, hints, Integer.class);
			assertNull(id);

			List<Integer> result = dao.queryTop(sqlList, parameters, hints, mapper, 5);
			assertEquals(3, result.size());
			
			result = dao.queryTop(sqlListQuantity, parameters, hints, Integer.class, 5);
			assertEquals(3, result.size());

			result = dao.queryFrom(sqlList, parameters, hints, mapper, 3, 5);
			assertEquals(0, result.size());
			
			result = dao.queryFrom(sqlListQuantity, parameters, hints, mapper, 3, 5);
			assertEquals(0, result.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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

package com.ctrip.platform.dal.dao.unittests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;


public class DalConcurrentMysqlTest {
	
	private final static String DATABASE_NAME = "dao_test";
	private final static String TABLE_NAME = "dal_client_test";
	
	private static List<Number> generateIds;
	private static Random random = new Random();
	private final static int INSERT_COUNT = 1000;
	
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	private final static String CREATE_TABLE_SQL = "CREATE TABLE dal_client_test("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	private static DalClient client = null;
	private static DalTableDao<ClientTestModel> dao = null;
	private static DalParser<ClientTestModel> parser = null;
	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
			parser = new ClientTestDalParser();
			dao = new DalTableDao<ClientTestModel>(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL};
		client.batchUpdate(sqls, hints);
		
		ClientTestModel[] models = new ClientTestModel[INSERT_COUNT];
		for (int i = 0; i < INSERT_COUNT; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setAddress("SH-" + i);
			model.setLastChanged(new Timestamp(System.currentTimeMillis()));
			model.setQuantity(i%10);
			model.setType((short)(i%3));
			
			models[i] = model;
		}
		
		KeyHolder holder = new KeyHolder();
		
		int count = dao.insert(hints,holder, models);
		System.out.println("The insert ClientTestModel count: " + count);	
		generateIds = holder.getIdList();
		System.out.println("Ids: " + generateIds.size());
		
		Assert.assertEquals(INSERT_COUNT, generateIds.size());
	}

	@Test
	public void testQueryById() throws SQLException{
		int maxId = random.nextInt(INSERT_COUNT);
		List<ClientTestModel> models = dao.query("Id > " + generateIds.get(maxId).intValue(), new StatementParameters(), new DalHints());
		System.out.println(models.size());
	}
	
	@Test
	public void testCount() throws SQLException{
		String sql = "SELECT count(1) from " + TABLE_NAME;
		Number count = (Number)client.query(sql, new StatementParameters(), new DalHints(), new DalScalarExtractor());
		System.out.println("count = " + count);
		Assert.assertEquals(INSERT_COUNT, count.intValue());
	}
	
	@Test
	public void testQueryByIdRange() throws SQLException{
		int maxId = random.nextInt(INSERT_COUNT);
		int minId = random.nextInt(maxId);
		List<ClientTestModel> models = dao.query("Id > " + generateIds.get(minId).intValue() + " and Id <= " + generateIds.get(maxId).intValue(), 
				new StatementParameters(), new DalHints());
		System.out.println(models.size());
		Assert.assertEquals(generateIds.get(maxId).intValue() - generateIds.get(minId).intValue(), models.size());
	}

	@Test
	public void testUpdate() throws SQLException{
		int id = random.nextInt(INSERT_COUNT);
		String updateSql = "UPDATE " + TABLE_NAME + " SET address = ? WHERE id = ?";
		StatementParameters param = new StatementParameters();
		if(id % 2 == 0)
			param.set(1, Types.VARCHAR, "BJ-" + id);
		else
			param.set(1, Types.VARCHAR, "SH-" + id);
		param.set(2, Types.INTEGER, id);
		int count = dao.update(updateSql, param, new DalHints());
		System.out.println("update souccess: " + id);
		Assert.assertEquals(1, count);
	}
	
	@Test
	public void testExecute() throws SQLException{
		List<DalCommand> commands = new ArrayList<DalCommand>();
		commands.add(new DalCommand(){
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "INSERT INTO " + TABLE_NAME + "(quantity,type,address)" + " VALUES(10, 1, 'SH INFO')";
				int ret = client.update(sql, new StatementParameters(), new DalHints());
				if(ret > 0) {
					System.out.println("insert success.");
				} else{
					System.out.println("insert failed.");
				}
				return ret > 0;
			}});
		commands.add(new DalCommand(){
			@Override
			public boolean execute(DalClient client) throws SQLException {
				String sql = "DELETE FROM " + TABLE_NAME + " WHERE address = '" + "SH INFO'";
				int ret = client.update(sql, new StatementParameters(), new DalHints());
				if(ret > 0){
					System.out.println("delete success");
				}else{
					System.out.println("delete failed");
				}
				return ret > 0;
			}});
		client.execute(commands, new DalHints());
		
		String sql = "SELECT count(1) from " + TABLE_NAME;
		Number count = (Number)client.query(sql, new StatementParameters(), new DalHints(), new DalScalarExtractor());
		Assert.assertEquals(INSERT_COUNT, count.intValue());
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL };
		client.batchUpdate(sqls, hints);
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

package test.com.ctrip.platform.dal.dao.parser;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class DalDefaultJpaParserMySqlTest {
	private final static int ROW_COUNT = 100;
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s";
	private final static String CREATE_TABLE_SQL = "CREATE TABLE %s("
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
			String dbName="dao_test";
			String tableName="dal_client_test";

			parser = new DalDefaultJpaParser(ClientTestModel.class, dbName, tableName);
			
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(parser.getDatabaseName());	
			dao = new DalTableDao<ClientTestModel>(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { 
				String.format(DROP_TABLE_SQL, parser.getTableName()), 
				String.format(CREATE_TABLE_SQL, parser.getTableName())};
		client.batchUpdate(sqls, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { 
				String.format(DROP_TABLE_SQL, parser.getTableName()) };
		client.batchUpdate(sqls, hints);
	}

	@Test
	public void test() throws SQLException {
		ClientTestModel[] models = new ClientTestModel[ROW_COUNT];
		for (int i = 0; i < ROW_COUNT; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			models[i] = model;
		}
		DalHints hints = new DalHints();
		int[] res = dao.insert(hints, Arrays.asList(models));
		assertEquals(ROW_COUNT, res);
		
		StatementParameters parameters = new StatementParameters();
		List<ClientTestModel> db_models = dao.query("true", parameters, hints);
		Assert.assertEquals(ROW_COUNT, db_models.size());
		res = dao.delete(hints, db_models);
		assertEquals(ROW_COUNT, res);
	}
	
	private void assertEquals(int expected, int[] res) {
		int total = 0;
		for(int t: res)total+=t;
		Assert.assertEquals(expected, total);
	}

	@Test
	public void testBatch() throws SQLException {
		ClientTestModel[] models = new ClientTestModel[ROW_COUNT];
		for (int i = 0; i < ROW_COUNT; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			models[i] = model;
		}
		DalHints hints = new DalHints();
		int[] res = dao.batchInsert(hints, Arrays.asList(models));
		Assert.assertEquals(ROW_COUNT, res.length);
		
		StatementParameters parameters = new StatementParameters();
		List<ClientTestModel> db_models = dao.query("true", parameters, hints);
		Assert.assertEquals(ROW_COUNT, db_models.size());
		
		res = dao.delete(hints, db_models);
		assertEquals(ROW_COUNT, res);
	}


	@Database(name="123")
	@Entity(name="123")
	public static class ClientTestModel {
		@Id
		@Column(name="id")
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Type(value=Types.INTEGER)
		private Integer id;
		
		@Column(name="quantity")
		@Type(value=Types.INTEGER)
		private Integer quan;
		
		@Column
		@Type(value=Types.SMALLINT)
		private Short type;
		
		@Column(length=50)
		@Type(value=Types.VARCHAR)
		private String address;
		
		@Column(nullable =false, insertable=false, name="last_changed")
		@Type(value=Types.TIMESTAMP)
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quan;
		}

		public void setQuantity(int quantity) {
			this.quan = quantity;
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

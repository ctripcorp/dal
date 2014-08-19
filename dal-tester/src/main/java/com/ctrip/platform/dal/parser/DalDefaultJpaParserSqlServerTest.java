package com.ctrip.platform.dal.parser;

import java.sql.SQLException;
import java.sql.Timestamp;
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

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.unittests.DalTestHelper;
import com.ctrip.platform.dal.ext.parser.DalDefaultJpaParser;
import com.ctrip.platform.dal.ext.parser.DefaultLoader;

/**
 * Test the default Jpa Parser with sql server database
 * @author wcyuan
 * @version 2014-05-08
 */
public class DalDefaultJpaParserSqlServerTest {
	private final static int ROW_COUNT = 100;
	private final static String DROP_TABLE_SQL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '%s') DROP TABLE  %s";

	// Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE %s"
			+ "(" + "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,type smallint, " + "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	
	private static DalClient client = null;
	private static DalTableDao<ClientTestModel> dao = null;
	private static DalParser<ClientTestModel> parser = null;
	static {
		try {
			parser = DalDefaultJpaParser.create(ClientTestModel.class, 
					new DefaultLoader(DatabaseCategory.SqlServer), "HotelPubDB");
			
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
				String.format(DROP_TABLE_SQL, parser.getTableName(), parser.getTableName()), 
				String.format(CREATE_TABLE_SQL, parser.getTableName())};
		client.batchUpdate(sqls, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { 
				String.format(DROP_TABLE_SQL, parser.getTableName(), parser.getTableName())};
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
		int res = dao.insert(hints, models);
		Assert.assertEquals(ROW_COUNT, DalTestHelper.getCount(dao));
		
		StatementParameters parameters = new StatementParameters();
		List<ClientTestModel> db_models = dao.query("1=1", parameters, hints);
		Assert.assertEquals(ROW_COUNT, db_models.size());
		db_models.toArray(models);
		res = dao.delete(hints, models);
		Assert.assertEquals(0, DalTestHelper.getCount(dao));
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
		int[] res = dao.batchInsert(hints, models);
		Assert.assertEquals(ROW_COUNT, res.length);
		
		StatementParameters parameters = new StatementParameters();
		List<ClientTestModel> db_models = dao.query("1=1", parameters, hints);
		Assert.assertEquals(ROW_COUNT, db_models.size());
		
		db_models.toArray(models);
		int ress = dao.delete(hints, models);
		Assert.assertEquals(0, DalTestHelper.getCount(dao));
	}

	
	@Entity(name="dal_client_test")
	public static class ClientTestModel {
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Integer id;
		
		@Column(name="quantity")
		private Integer quan;
		
		@Column
		private Short type;
		
		@Column(length=50)
		private String address;
		
		@Column(nullable =false, insertable=false, name="last_changed")
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

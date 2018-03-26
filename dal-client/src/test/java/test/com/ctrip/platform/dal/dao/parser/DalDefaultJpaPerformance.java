package test.com.ctrip.platform.dal.dao.parser;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class DalDefaultJpaPerformance {
	private static Logger log = LoggerFactory.getLogger("performance");
	
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s";
	private final static String CREATE_TABLE_SQL = "CREATE TABLE %s("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "score float, "
			+ "orderid bigint, "
			+ "address1 VARCHAR(64) not null, "
			+ "address2 VARCHAR(64) not null, "
			+ "address3 VARCHAR(64) not null, "
			+ "address4 VARCHAR(64) not null, "
			+ "address5 VARCHAR(64) not null, "
			+ "address6 VARCHAR(64) not null, "
			+ "address7 VARCHAR(64) not null, "
			+ "address8 VARCHAR(64) not null, "
			+ "address9 VARCHAR(64) not null, "
			+ "address10 VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	private DalParser<ClientTestModel> parser = null;
	private DalTableDao<ClientTestModel> dao = null;
	private static DalClient client = null;
	
	public DalDefaultJpaPerformance(DatabaseCategory dbCategory){
		try {
			parser = new DalDefaultJpaParser(ClientTestModel.class);
			client = DalClientFactory.getClient(parser.getDatabaseName());
			dao = new DalTableDao<ClientTestModel>(parser);
		} catch (Exception e) {
			log.error("fail", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * If the table exists, drop it, then create a new table
	 * @throws SQLException
	 */
	public void dropAndCreateTable() throws SQLException{
		DalHints hints = new DalHints();
		String[] sqls = new String[] { 
				String.format(DROP_TABLE_SQL, this.parser.getTableName()),
				String.format(CREATE_TABLE_SQL, this.parser.getTableName())};
		client.batchUpdate(sqls, hints);
	}
	
	/**
	 * Insert the specified count jpa_performance_test records
	 * @param count
	 * 		the specified count
	 * @return 
	 * 		The actual insert success count
	 * @throws SQLException
	 */
	public int[] randomInsert(int count) throws SQLException{
		if (0 == count)
			return new int[0];
		ClientTestModel[] entities = new ClientTestModel[count];
		Random random = new Random();
		for (int i = 0; i < count; i++) {
			ClientTestModel model = new ClientTestModel();
			int seed = random.nextInt(5);
			model.setId(null);
			model.setQuantity(seed + 10);
			model.setType(((Number) (i % 3)).shortValue());
			model.setScore(((Number) (i % 3)).floatValue());
			model.setOrderid(((Number) (i % 3)).longValue());
			model.setAddress1("CTRIP");
			model.setAddress2("CTRIP");
			model.setAddress3("CTRIP");
			model.setAddress4("CTRIP");
			model.setAddress5("CTRIP");
			model.setAddress6("CTRIP");
			model.setAddress7("CTRIP");
			model.setAddress8("CTRIP");
			model.setAddress9("CTRIP");
			model.setAddress10("CTRIP");
			model.setLastChanged(new Timestamp(System.currentTimeMillis()));
			entities[i] = model;
		}
		return dao.batchInsert(new DalHints(), Arrays.asList(entities));
	}
	
	/**
	 * Update the lastChange column multiple entities to now 
	 * according to the specified where Clause
	 * @param whereClause
	 * 		Example: "type==3", or "true" for all
	 * @return
	 * 		The actual update success count
	 * @throws SQLException
	 */
	public int[] updateLastChangedTime(String whereClause) throws SQLException{
		List<ClientTestModel> entities = this.query(whereClause);
		for (ClientTestModel model : entities) {
			model.setLastChanged(new Timestamp(System.currentTimeMillis()));
		}
		return this.update(entities);
	}
	
	/**
	 * Update the multiple entities according to their ID and the current fields
	 * @param entities
	 * 		The entities need to be updated
	 * @return
	 * 		The actual update success count
	 * @throws SQLException
	 */
	public int[] update(List<ClientTestModel> entities) throws SQLException{
		DalHints hints = new DalHints();
		return dao.batchUpdate(hints, entities);
	}
	
	/**
	 * Query the entities according to the specified where Clause
	 * @param whereClause
	 * 		Example: "type==3", or "true" for all
	 * @return
	 * 		The list of result set
	 * @throws SQLException
	 */
	public List<ClientTestModel> query(String whereClause) throws SQLException{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		return dao.query(whereClause, parameters, hints);
	}
	
	
	 
	@Entity(name="jpa_performance_test")
	@Database(name="dao_test")
	public  static class ClientTestModel{
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Type(value = Types.INTEGER)
		private Integer id;
		
		@Column
		@Type(value = Types.INTEGER)
		private Integer quantity;
		
		@Column
		@Type(value = Types.SMALLINT)
		private Short type;
		
		@Column
		@Type(value = Types.FLOAT)
		private float score;
		
		@Column
		@Type(value = Types.BIGINT)
		private long orderid;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address1;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address2;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address3;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address4;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address5;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address6;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address7;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address8;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address9;
		
		@Column
		@Type(value = Types.VARCHAR)
		private String address10;
		
		@Column(name="last_changed")
		@Type(value = Types.TIMESTAMP)
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public Short getType() {
			return type;
		}

		public void setType(Short type) {
			this.type = type;
		}

		public String getAddress1() {
			return address1;
		}

		public long getOrderid() {
			return orderid;
		}

		public void setOrderid(long orderid) {
			this.orderid = orderid;
		}

		public void setAddress1(String address1) {
			this.address1 = address1;
		}

		public String getAddress2() {
			return address2;
		}

		public void setAddress2(String address2) {
			this.address2 = address2;
		}

		public String getAddress3() {
			return address3;
		}

		public void setAddress3(String address3) {
			this.address3 = address3;
		}

		public String getAddress4() {
			return address4;
		}

		public void setAddress4(String address4) {
			this.address4 = address4;
		}

		public String getAddress5() {
			return address5;
		}

		public void setAddress5(String address5) {
			this.address5 = address5;
		}

		public String getAddress6() {
			return address6;
		}

		public void setAddress6(String address6) {
			this.address6 = address6;
		}

		public String getAddress7() {
			return address7;
		}

		public void setAddress7(String address7) {
			this.address7 = address7;
		}

		public String getAddress8() {
			return address8;
		}

		public void setAddress8(String address8) {
			this.address8 = address8;
		}

		public String getAddress9() {
			return address9;
		}

		public void setAddress9(String address9) {
			this.address9 = address9;
		}

		public String getAddress10() {
			return address10;
		}

		public void setAddress10(String address10) {
			this.address10 = address10;
		}

		public Timestamp getLastChanged() {
			return lastChanged;
		}

		public float getScore() {
			return score;
		}

		public void setScore(float score) {
			this.score = score;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.lastChanged = lastChanged;
		} 
	}
}

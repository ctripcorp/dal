package test.com.ctrip.platform.dal.dao.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.AbstractDalParser;

public class NormalParserPerformance {
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

	public NormalParserPerformance() {
		try {
			parser = new NormalParser();
			client = DalClientFactory.getClient(parser.getDatabaseName());
			dao = new DalTableDao<ClientTestModel>(parser);
		} catch (Exception e) {
			log.error("fail", e);
			e.printStackTrace();
		}
	}

	/**
	 * If the table exists, drop it, then create a new table
	 * 
	 * @throws SQLException
	 */
	public void dropAndCreateTable() throws SQLException {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { 
				String.format(DROP_TABLE_SQL, this.parser.getTableName()),
				String.format(CREATE_TABLE_SQL, this.parser.getTableName())};
		client.batchUpdate(sqls, hints);
	}

	/**
	 * Insert the specified count jpa_performance_test records
	 * 
	 * @param count
	 *            the specified count
	 * @return The actual insert success count
	 * @throws SQLException
	 */
	public int[] randomInsert(int count) throws SQLException {
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
	 * Update the lastChange column multiple entities to now according to the
	 * specified where Clause
	 * 
	 * @param whereClause
	 *            Example: "type==3", or "true" for all
	 * @return The actual update success count
	 * @throws SQLException
	 */
	public int[] updateLastChangedTime(String whereClause) throws SQLException {
		List<ClientTestModel> entities = this.query(whereClause);
		for (ClientTestModel model : entities) {
			model.setLastChanged(new Timestamp(System.currentTimeMillis()));
		}
		return this.update(entities);
	}

	/**
	 * Update the multiple entities according to their ID and the current fields
	 * 
	 * @param entities
	 *            The entities need to be updated
	 * @return The actual update success count
	 * @throws SQLException
	 */
	public int[] update(List<ClientTestModel> entities) throws SQLException {
		DalHints hints = new DalHints();
		return dao.batchUpdate(hints, entities);
	}

	/**
	 * Query the entities according to the specified where Clause
	 * 
	 * @param whereClause
	 *            Example: "type==3", or "true" for all
	 * @return The list of result set
	 * @throws SQLException
	 */
	public List<ClientTestModel> query(String whereClause) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		return dao.query(whereClause, parameters, hints);
	}

	private static class NormalParser extends
			AbstractDalParser<ClientTestModel> {
		public static final String DATABASE_NAME = "dao_test";
		public static final String TABLE_NAME = "jpa_performance_test";
		private static final String[] COLUMNS = new String[] { 
				"id", "quantity", "type", "score", "orderid",
				"address1","address2","address3","address4","address5",
				"address6","address7","address8","address9","address10", 
				"last_changed" };

		private static final String[] PRIMARY_KEYS = new String[] { "id", };

		private static final int[] COLUMN_TYPES = new int[] {
				Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.FLOAT, Types.BIGINT,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, 
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, 
				Types.TIMESTAMP };

		public NormalParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
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
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			primaryKeys.put("id", pojo.getId());
			return primaryKeys;
		}

		@Override
		public Map<String, ?> getFields(ClientTestModel pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("id", pojo.getId());
			map.put("quantity", pojo.getQuantity());
			map.put("type", pojo.getType());
			map.put("score", pojo.getScore());
			map.put("orderid", pojo.getOrderid());
			map.put("address1", pojo.getAddress1());
			map.put("address2", pojo.getAddress2());
			map.put("address3", pojo.getAddress3());
			map.put("address4", pojo.getAddress4());
			map.put("address5", pojo.getAddress5());
			map.put("address6", pojo.getAddress6());
			map.put("address7", pojo.getAddress7());
			map.put("address8", pojo.getAddress8());
			map.put("address9", pojo.getAddress9());
			map.put("address10", pojo.getAddress10());
			map.put("last_changed", pojo.getLastChanged());
			return map;
		}

		@Override
		public ClientTestModel map(ResultSet rs, int rowNum)
				throws SQLException {
			ClientTestModel model = new ClientTestModel();
			model.setId(((Number) rs.getObject("id")).intValue());
			model.setQuantity(((Number) rs.getObject("quantity")).intValue());
			model.setType(((Number) rs.getObject("type")).shortValue());
			model.setScore(((Number) rs.getObject("score")).floatValue());
			model.setOrderid(((Number) rs.getObject("orderid")).longValue());
			model.setAddress1(rs.getString("address1"));
			model.setAddress2(rs.getString("address2"));
			model.setAddress3(rs.getString("address3"));
			model.setAddress4(rs.getString("address4"));
			model.setAddress5(rs.getString("address5"));
			model.setAddress6(rs.getString("address6"));
			model.setAddress7(rs.getString("address7"));
			model.setAddress8(rs.getString("address8"));
			model.setAddress9(rs.getString("address9"));
			model.setAddress10(rs.getString("address10"));
			model.setLastChanged((Timestamp) rs.getObject("last_changed"));
			return model;
		}

	}

	public static class ClientTestModel {
		private Integer id;
		private Integer quantity;
		private Short type;
		private float score;
		private long orderid;
		private String address1;
		private String address2;
		private String address3;
		private String address4;
		private String address5;
		private String address6;
		private String address7;
		private String address8;
		private String address9;
		private String address10;
		private Timestamp last_changed;

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

		public void setAddress1(String address1) {
			this.address1 = address1;
		}

		public long getOrderid() {
			return orderid;
		}

		public void setOrderid(long orderid) {
			this.orderid = orderid;
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

		public float getScore() {
			return score;
		}

		public void setScore(float score) {
			this.score = score;
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

		public Timestamp getLast_changed() {
			return last_changed;
		}

		public void setLast_changed(Timestamp last_changed) {
			this.last_changed = last_changed;
		}

		public Timestamp getLastChanged() {
			return last_changed;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.last_changed = lastChanged;
		}
	}
}

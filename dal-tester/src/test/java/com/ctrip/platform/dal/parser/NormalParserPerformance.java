package com.ctrip.platform.dal.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.AbstractDalParser;

public class NormalParserPerformance {
	private static Logger log = Logger.getLogger("performance");
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s";
	private final static String CREATE_TABLE_SQL = "CREATE TABLE %s("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int," + "type smallint, "
			+ "address VARCHAR(64) not null, "
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
			log.error(e);
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
			model.setAddress("CTRIP");
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
		private static final String[] COLUMNS = new String[] { "id",
				"quantity", "type", "address", "last_changed" };

		private static final String[] PRIMARY_KEYS = new String[] { "id", };

		private static final int[] COLUMN_TYPES = new int[] { Types.INTEGER,
				Types.INTEGER, Types.SMALLINT, Types.VARCHAR, Types.TIMESTAMP };

		public NormalParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS,
					COLUMN_TYPES);
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
			map.put("address", pojo.getAddress());
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
			model.setAddress(rs.getString("address"));
			model.setLastChanged((Timestamp) rs.getObject("last_changed"));
			return model;
		}

	}

	public static class ClientTestModel {
		private Integer id;
		private Integer quantity;
		private Short type;
		private String address;
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

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Timestamp getLastChanged() {
			return last_changed;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.last_changed = lastChanged;
		}
	}
}

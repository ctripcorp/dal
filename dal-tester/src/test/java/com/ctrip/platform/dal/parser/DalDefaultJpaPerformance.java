package com.ctrip.platform.dal.parser;

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

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.ext.parser.DalDefaultJpaParser;
import com.ctrip.platform.dal.ext.persistence.Type;

/**
 * Used for performance test
 * @author wcyuan
 */
public class DalDefaultJpaPerformance {
	private static Logger log = Logger.getLogger("performance");
	
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s";
	private final static String CREATE_TABLE_SQL = "CREATE TABLE %s("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	private DalParser<ClientTestModel> parser = null;
	private DalTableDao<ClientTestModel> dao = null;
	private static DalClient client = null;
	
	public DalDefaultJpaPerformance(DatabaseCategory dbCategory,String dbName){
		try {
			parser = DalDefaultJpaParser.create(ClientTestModel.class, dbName);
			client = DalClientFactory.getClient(parser.getDatabaseName());
			dao = new DalTableDao<ClientTestModel>(parser);
		} catch (Exception e) {
			log.error(e);
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
			model.setAddress("CTRIP");
			model.setLastChanged(new Timestamp(System.currentTimeMillis()));
			entities[i] = model;
		}
		return dao.insert(new DalHints(), Arrays.asList(entities));
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
		return dao.update(hints, entities);
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
		@Type(value = Types.VARCHAR)
		private String address;
		
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

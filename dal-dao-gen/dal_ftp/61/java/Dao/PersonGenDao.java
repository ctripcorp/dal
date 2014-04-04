package com.ctrip.dal.test.test2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.AbstractDalParser;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class PersonGenDao {
	private static final String DATA_BASE = "dao_test";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from Person";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM Person";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM Person WHERE LIMIT %s, %s";
	
	private DalParser<PersonGen> parser = new PersonGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<PersonGen> rowextractor = null;
	private DalTableDao<PersonGen> client;
	private DalClient baseClient;

	public PersonGenDao() {
		this.client = new DalTableDao<PersonGen>(parser);
		this.rowextractor = new DalRowMapperExtractor<PersonGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public PersonGen queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}

	public PersonGen queryByPk(PersonGen pk)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(pk, hints);
	}
	
	public long count()  throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		long result = (Long)this.baseClient.query(COUNT_SQL_PATTERN, parameters, hints, extractor);
		return result;
	}
	
	public List<PersonGen> queryByPage(PersonGen pk, int pageSize, int pageNo)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");
		
        StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String sql = "";
		sql = String.format(PAGE_MYSQL_PATTERN, (pageNo - 1) * pageSize, pageSize);
		return this.baseClient.query(sql, parameters, hints, rowextractor);
	}
	
	public List<PersonGen> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<PersonGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	public void insert(PersonGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, null, daoPojos);
	}

	public void insert(KeyHolder keyHolder, PersonGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, keyHolder, daoPojos);
	}

	public void delete(PersonGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.delete(hints, daoPojos);
	}

	public void update(PersonGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.update(hints, daoPojos);
	}

    public int updatePerson(Integer ID, String Telephone, String Name) throws SQLException {
		String sql = "UPDATE Person SET  Name = ? , Telephone = ?  WHERE  ID != ? ";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
		parameters.set(i++, Types.INTEGER, ID);
		parameters.set(i++, Types.VARCHAR, Telephone);
		parameters.set(i++, Types.VARCHAR, Name);
		return baseClient.update(sql, parameters, hint);
	}
    public int deletePersonById(Integer ID) throws SQLException {
		String sql = "Delete FROM Person WHERE  ID = @ID ";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
		parameters.set(i++, Types.INTEGER, ID);
		return baseClient.update(sql, parameters, hint);
	}

	private static class PersonGenParser extends AbstractDalParser<PersonGen> {
		public static final String DATABASE_NAME = "dao_test";
		public static final String TABLE_NAME = "Person";
		private static final String[] COLUMNS = new String[]{
			"ID",
			"Address",
			"Telephone",
			"Name",
			"Age",
			"Gender",
			"Birth",
			"PartmentID",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"ID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.TIMESTAMP,
			Types.INTEGER,
		};
		
		public PersonGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public PersonGen map(ResultSet rs, int rowNum) throws SQLException {
			PersonGen pojo = new PersonGen();
			
			pojo.setID((Integer)rs.getObject("ID"));
			pojo.setAddress((String)rs.getObject("Address"));
			pojo.setTelephone((String)rs.getObject("Telephone"));
			pojo.setName((String)rs.getObject("Name"));
			pojo.setAge((Integer)rs.getObject("Age"));
			pojo.setGender((Integer)rs.getObject("Gender"));
			pojo.setBirth((Timestamp)rs.getObject("Birth"));
			pojo.setPartmentID((Integer)rs.getObject("PartmentID"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(PersonGen pojo) {
			return pojo.getID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(PersonGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ID", pojo.getID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(PersonGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("ID", pojo.getID());
			map.put("Address", pojo.getAddress());
			map.put("Telephone", pojo.getTelephone());
			map.put("Name", pojo.getName());
			map.put("Age", pojo.getAge());
			map.put("Gender", pojo.getGender());
			map.put("Birth", pojo.getBirth());
			map.put("PartmentID", pojo.getPartmentID());
	
			return map;
		}
	}
}
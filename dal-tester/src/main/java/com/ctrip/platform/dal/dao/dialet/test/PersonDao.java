package com.ctrip.platform.dal.dao.dialet.test;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersonDao {
	private static final String DATA_BASE = "dao_test";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from Person";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM Person";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM Person LIMIT %s, %s";
	
	private DalParser<Person> parser = new PersonParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<Person> rowextractor = null;
	private DalTableDao<Person> client;
	private DalClient baseClient;

	public PersonDao() {
		this.client = new DalTableDao<Person>(parser);
		this.rowextractor = new DalRowMapperExtractor<Person>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public DalParser<Person> getParser(){
		return this.parser;
	}
	/**
	 * Query Person by the specified ID
	 * The ID must be a number
	**/
	public Person queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}
    /**
	 * Query Person by Person instance which the primary key is set
	**/
	public Person queryByPk(Person pk)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(pk, hints);
	}
	
	/**
	 * Get the records count
	**/
	public int count() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();	
		Number result = (Number)this.baseClient.query(COUNT_SQL_PATTERN, parameters, hints, extractor);
		return result.intValue();
	}
	
	/**
	 * Query Person with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<Person> queryByPage(int pageSize, int pageNo)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
        StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		String sql = "";
		sql = String.format(PAGE_MYSQL_PATTERN, (pageNo - 1) * pageSize, pageSize);
		return this.baseClient.query(sql, parameters, hints, rowextractor);
	}
	
	/**
	 * Get all records in the whole table
	**/
	public List<Person> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<Person> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	/**
	 * SQL insert
	 * Note: there must be one non-null field in daoPojo
	**/
	public void insert(Person...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		DalHints hints = new DalHints();
		client.insert(hints, null, daoPojos);
	}
	
	/**
	 * SQL insert with batch mode
	**/
	public int[] batchInsert(Person...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		DalHints hints = new DalHints();
		return client.batchInsert(hints, daoPojos);
	}

	/**
	 * SQL insert with keyHolder
	 * Note: there must be one non-null field in daoPojo
	**/
	public void insert(KeyHolder keyHolder, Person...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		DalHints hints = new DalHints();
		client.insert(hints, keyHolder, daoPojos);
	}

	/**
	 * SQL delete
	 * Note: there must be one non-null field in daoPojo
	**/
	public void delete(Person...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		DalHints hints = new DalHints();
		client.delete(hints, daoPojos);
	}
	
	/**
	 * SQL delete with batch mode
	**/
	public int[] batchDelete(Person...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return new int[0];
		DalHints hints = new DalHints();
		return client.batchDelete(hints, daoPojos);
	}

	/**
	 * SQL update
	 * Note: there must be one non-null field in daoPojo
	**/
	public void update(DalHints hints, Person...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		client.update(hints, daoPojos);
	}


	public static class PersonParser extends AbstractDalParser<Person> {
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
		
		public PersonParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public Person map(ResultSet rs, int rowNum) throws SQLException {
			Person pojo = new Person();
			
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
		public Number getIdentityValue(Person pojo) {
			return pojo.getID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(Person pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ID", pojo.getID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(Person pojo) {
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


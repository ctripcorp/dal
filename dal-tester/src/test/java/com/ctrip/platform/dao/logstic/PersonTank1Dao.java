package com.ctrip.platform.dao.logstic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.AbstractDalParser;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class PersonTank1Dao {
	private static final String DATA_BASE = "dao_test";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from Person";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM Person";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM Person LIMIT %s, %s";
	
	private DalParser<PersonTank1> parser = new PersonTank1Parser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<PersonTank1> rowextractor = null;
	private DalTableDao<PersonTank1> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;

	public PersonTank1Dao() {
		this.client = new DalTableDao<PersonTank1>(parser);
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.rowextractor = new DalRowMapperExtractor<PersonTank1>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query PersonTank1 by the specified ID
	 * The ID must be a number
	**/
	public PersonTank1 queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
    /**
	 * Query PersonTank1 by PersonTank1 instance which the primary key is set
	**/
	public PersonTank1 queryByPk(PersonTank1 pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}
	/**
	 * Get the records count
	**/
	public int count(DalHints hints) throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		Number result = (Number)this.baseClient.query(COUNT_SQL_PATTERN, parameters, hints, extractor);
		return result.intValue();
	}
	
	/**
	 * Query PersonTank1 with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<PersonTank1> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
        StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String sql = "";
		sql = String.format(PAGE_MYSQL_PATTERN, (pageNo - 1) * pageSize, pageSize);
		return this.baseClient.query(sql, parameters, hints, rowextractor);
	}
	
	/**
	 * Get all records in the whole table
	**/
	public List<PersonTank1> getAll(DalHints hints) throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<PersonTank1> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	/**
	 * SQL insert
	 * Note: there must be one non-null field in daoPojo
	**/
	public void insert(DalHints hints, PersonTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.insert(hints, null, Arrays.asList(daoPojos));
	}
	
	/**
	 * SQL insert with batch mode
	**/
	public int[] batchInsert(DalHints hints, PersonTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, Arrays.asList(daoPojos));
	}

	/**
	 * SQL insert with keyHolder
	 * Note: there must be one non-null field in daoPojo
	**/
	public void insert(DalHints hints, KeyHolder keyHolder, PersonTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.insert(hints, keyHolder, Arrays.asList(daoPojos));
	}

	/**
	 * SQL delete
	 * Note: there must be one non-null field in daoPojo
	**/
	public void delete(DalHints hints, PersonTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.delete(hints, Arrays.asList(daoPojos));
	}
	
	/**
	 * SQL delete with batch mode
	**/
	public int[] batchDelete(DalHints hints, PersonTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchDelete(hints, Arrays.asList(daoPojos));
	}

	/**
	 * SQL update
	 * Note: there must be one non-null field in daoPojo
	**/
	public void update(DalHints hints, PersonTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.update(hints, Arrays.asList(daoPojos));
	}

		/**
		 * MySql构建SQL常规测试: Single + Simple type+paging
		**/
	public String query1(Integer age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "SELECT `Name` FROM Person WHERE  `Age` > ?  ORDER BY Address asc";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		return queryDao.queryForObjectNullable(sql, parameters, hints, String.class);
	}
		/**
		 * MySql构建SQL常规测试: Single + Simple type+no paging
		**/
	public String query2(Integer age, DalHints hints) throws SQLException {
		String sql = "SELECT `Name` FROM Person WHERE  `Age` > ?  ORDER BY Birth desc";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		return queryDao.queryForObjectNullable(sql, parameters, hints, String.class);
	}
		/**
		 * MySql构建SQL常规测试: List+ Simple type+paging
		**/
	public List<String> query3(Integer age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sqlPattern = "SELECT `Name` FROM Person WHERE `Age` > ? ORDER BY Birth DESC limit %s, %s";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		return queryDao.query(sql, parameters, hints, String.class);
	}
		/**
		 * MySql构建SQL常规测试: List+ Simple type+ no paging
		**/
	public List<String> query4(Integer age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sqlPattern = "SELECT `Telephone` FROM Person WHERE `Age` > ? ORDER BY Name DESC limit %s, %s";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		return queryDao.query(sql, parameters, hints, String.class);
	}
		/**
		 * MySql构建SQL常规测试:First + Simple type + paging
		**/
	public String query5(Integer age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "SELECT `Telephone` FROM Person WHERE  `Age` > ?  ORDER BY Birth desc";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		return queryDao.queryFirstNullable(sql, parameters, hints, String.class);
	}
		/**
		 * MySql构建SQL常规测试: First+ Simple type+ no paging
		**/
	public String query6(Integer age, DalHints hints) throws SQLException {
		String sql = "SELECT `Telephone` FROM Person WHERE  `Gender` = ?  ORDER BY Birth desc";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		return queryDao.queryFirstNullable(sql, parameters, hints, String.class);
	}
		/**
		 * MySql构建SQL常规测试: Single+ Entity+ paging
		**/
	public List<PersonTank1> query7(Integer age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sqlPattern = "SELECT `Birth`, `Name`, `Age`, `Telephone`, `PartmentID`, `Gender`, `Address`, `ID`, `space` FROM Person WHERE `Age` >= ? ORDER BY Name DESC limit %s, %s";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		return queryDao.query(sql, parameters, hints, parser);
	}
		/**
		 * MySql构建SQL常规测试: Single+ Entity+ no paging
		**/
	public List<PersonTank1> query8(Integer minAge, Integer maxAge, DalHints hints) throws SQLException {
		String sql = "SELECT `Birth`,`Name`,`Age`,`Telephone`,`PartmentID`,`Gender`,`Address`,`ID`,`space` FROM Person WHERE  `Age` BETWEEN ? AND ?  ORDER BY Name desc";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, minAge);
		parameters.set(i++, Types.INTEGER, maxAge);
		return queryDao.query(sql, parameters, hints, parser);
	}
		/**
		 * MySql构建SQL常规测试: List+ Entity+ paging
		**/
	public List<PersonTank1> query9(String namelike, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sqlPattern = "SELECT `Birth`, `Name`, `Age`, `Telephone`, `PartmentID`, `Gender`, `Address`, `ID`, `space` FROM Person WHERE `Name` LIKE ? ORDER BY Age DESC limit %s, %s";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.VARCHAR, namelike);
		return queryDao.query(sql, parameters, hints, parser);
	}
		/**
		 * MySql构建SQL常规测试: List+ Entity+ no paging
		**/
	public List<PersonTank1> query10(Timestamp minBirth, Timestamp maxBirth, DalHints hints) throws SQLException {
		String sql = "SELECT `Birth`,`Name`,`Age`,`Telephone`,`PartmentID`,`Gender`,`Address`,`ID`,`space` FROM Person WHERE  `Birth` BETWEEN ? AND ?  ORDER BY Name desc";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.TIMESTAMP, minBirth);
		parameters.set(i++, Types.TIMESTAMP, maxBirth);
		return queryDao.query(sql, parameters, hints, parser);
	}
		/**
		 * MySql构建SQL常规测试: First+ Entity+ paging
		**/
	public PersonTank1 query11(Integer age, String address, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "SELECT `Birth`,`Name`,`Age`,`Telephone`,`PartmentID`,`Gender`,`Address`,`ID`,`space` FROM Person WHERE  `Age` > ?  AND  `Address` Like ?  ORDER BY Name desc";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		parameters.set(i++, Types.VARCHAR, address);
		return queryDao.queryFirstNullable(sql, parameters, hints, parser);
	}
		/**
		 * MySql构建SQL常规测试: First+ Entity+ no paging
		**/
	public PersonTank1 query12(Integer partment, DalHints hints) throws SQLException {
		String sql = "SELECT `Birth`,`Name`,`Age`,`Telephone`,`PartmentID`,`Gender`,`Address`,`ID`,`space` FROM Person WHERE  `PartmentID` = ? ";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, partment);
		return queryDao.queryFirstNullable(sql, parameters, hints, parser);
	}


	public static class PersonTank1Parser extends AbstractDalParser<PersonTank1> {
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
			"space",
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
			Types.VARCHAR,
		};
		
		public PersonTank1Parser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public PersonTank1 map(ResultSet rs, int rowNum) throws SQLException {
			PersonTank1 pojo = new PersonTank1();
			
			pojo.setID((Integer)rs.getObject("ID"));
			pojo.setAddress((String)rs.getObject("Address"));
			pojo.setTelephone((String)rs.getObject("Telephone"));
			pojo.setName((String)rs.getObject("Name"));
			pojo.setAge((Integer)rs.getObject("Age"));
			pojo.setGender((Integer)rs.getObject("Gender"));
			pojo.setBirth((Timestamp)rs.getObject("Birth"));
			pojo.setPartmentID((Integer)rs.getObject("PartmentID"));
			pojo.setSpace((String)rs.getObject("space"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(PersonTank1 pojo) {
			return pojo.getID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(PersonTank1 pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ID", pojo.getID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(PersonTank1 pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("ID", pojo.getID());
			map.put("Address", pojo.getAddress());
			map.put("Telephone", pojo.getTelephone());
			map.put("Name", pojo.getName());
			map.put("Age", pojo.getAge());
			map.put("Gender", pojo.getGender());
			map.put("Birth", pojo.getBirth());
			map.put("PartmentID", pojo.getPartmentID());
			map.put("space", pojo.getSpace());
	
			return map;
		}
	}
}
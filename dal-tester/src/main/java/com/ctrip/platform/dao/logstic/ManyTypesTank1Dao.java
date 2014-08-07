package com.ctrip.platform.dao.logstic;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ManyTypesTank1Dao {
	private static final String DATA_BASE = "dao_test";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from ManyTypes";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM ManyTypes";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM ManyTypes LIMIT %s, %s";
	
	private DalParser<ManyTypesTank1> parser = new ManyTypesTank1Parser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<ManyTypesTank1> rowextractor = null;
	private DalTableDao<ManyTypesTank1> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;

	public ManyTypesTank1Dao() {
		this.client = new DalTableDao<ManyTypesTank1>(parser);
		this.client.setDelimiter('`','`');
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.rowextractor = new DalRowMapperExtractor<ManyTypesTank1>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query ManyTypesTank1 by the specified ID
	 * The ID must be a number
	**/
	public ManyTypesTank1 queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
    /**
	 * Query ManyTypesTank1 by ManyTypesTank1 instance which the primary key is set
	**/
	public ManyTypesTank1 queryByPk(ManyTypesTank1 pk, DalHints hints)
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
	 * Query ManyTypesTank1 with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<ManyTypesTank1> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
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
	public List<ManyTypesTank1> getAll(DalHints hints) throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<ManyTypesTank1> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	/**
	 * SQL insert
	 * Note: there must be one non-null field in daoPojo
	**/
	public void insert(DalHints hints, ManyTypesTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.insert(hints, null, daoPojos);
	}
	
	/**
	 * SQL insert with batch mode
	**/
	public int[] batchInsert(DalHints hints, ManyTypesTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}

	/**
	 * SQL insert with keyHolder
	 * Note: there must be one non-null field in daoPojo
	**/
	public void insert(DalHints hints, KeyHolder keyHolder, ManyTypesTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.insert(hints, keyHolder, daoPojos);
	}

	/**
	 * SQL delete
	 * Note: there must be one non-null field in daoPojo
	**/
	public void delete(DalHints hints, ManyTypesTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.delete(hints, daoPojos);
	}
	
	/**
	 * SQL delete with batch mode
	**/
	public int[] batchDelete(DalHints hints, ManyTypesTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchDelete(hints, daoPojos);
	}

	/**
	 * SQL update
	 * Note: there must be one non-null field in daoPojo
	**/
	public void update(DalHints hints, ManyTypesTank1...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.update(hints, daoPojos);
	}



	public static class ManyTypesTank1Parser extends AbstractDalParser<ManyTypesTank1> {
		public static final String DATABASE_NAME = "dao_test";
		public static final String TABLE_NAME = "ManyTypes";
		private static final String[] COLUMNS = new String[]{
			"Id",
			"TinyIntCol",
			"SmallIntCol",
			"IntCol",
			"BigIntCol",
			"DecimalCol",
			"DoubleCol",
			"FloatCol",
			"BitCol",
			"CharCol",
			"VarCharCol",
			"DateCol",
			"DateTimeCol",
			"TimeCol",
			"TimestampCol",
			"YearCol",
			"BinaryCol",
			"BlobCol",
			"LongBlobCol",
			"MediumBlobCol",
			"TinyBlobCol",
			"VarBinaryCol",
			"LongTextCol",
			"MediumTextCol",
			"TextCol",
			"TinyTextCol",
			"TinyIntOne",
			"CharTow",
			"Year",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"Id",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.TINYINT,
			Types.SMALLINT,
			Types.INTEGER,
			Types.BIGINT,
			Types.DECIMAL,
			Types.DOUBLE,
			Types.REAL,
			Types.BIT,
			Types.CHAR,
			Types.VARCHAR,
			Types.DATE,
			Types.TIMESTAMP,
			Types.TIME,
			Types.TIMESTAMP,
			Types.DATE,
			Types.BINARY,
			Types.LONGVARBINARY,
			Types.LONGVARBINARY,
			Types.LONGVARBINARY,
			Types.BINARY,
			Types.VARBINARY,
			Types.LONGVARCHAR,
			Types.LONGVARCHAR,
			Types.LONGVARCHAR,
			Types.VARCHAR,
			Types.BIT,
			Types.CHAR,
			Types.DATE,
		};
		
		public ManyTypesTank1Parser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public ManyTypesTank1 map(ResultSet rs, int rowNum) throws SQLException {
			ManyTypesTank1 pojo = new ManyTypesTank1();
			
			pojo.setId((Integer)rs.getObject("Id"));
			pojo.setTinyIntCol((Integer)rs.getObject("TinyIntCol"));
			pojo.setSmallIntCol((Integer)rs.getObject("SmallIntCol"));
			pojo.setIntCol((Integer)rs.getObject("IntCol"));
			pojo.setBigIntCol((Long)rs.getObject("BigIntCol"));
			pojo.setDecimalCol((BigDecimal)rs.getObject("DecimalCol"));
			pojo.setDoubleCol((Double)rs.getObject("DoubleCol"));
			pojo.setFloatCol((Float)rs.getObject("FloatCol"));
			pojo.setBitCol((Boolean)rs.getObject("BitCol"));
			pojo.setCharCol((String)rs.getObject("CharCol"));
			pojo.setVarCharCol((String)rs.getObject("VarCharCol"));
			pojo.setDateCol((Date)rs.getObject("DateCol"));
			pojo.setDateTimeCol((Timestamp)rs.getObject("DateTimeCol"));
			pojo.setTimeCol((Time)rs.getObject("TimeCol"));
			pojo.setTimestampCol((Timestamp)rs.getObject("TimestampCol"));
			pojo.setYearCol((Date)rs.getObject("YearCol"));
			pojo.setBinaryCol((byte[])rs.getObject("BinaryCol"));
			pojo.setBlobCol((byte[])rs.getObject("BlobCol"));
			pojo.setLongBlobCol((byte[])rs.getObject("LongBlobCol"));
			pojo.setMediumBlobCol((byte[])rs.getObject("MediumBlobCol"));
			pojo.setTinyBlobCol((byte[])rs.getObject("TinyBlobCol"));
			pojo.setVarBinaryCol((byte[])rs.getObject("VarBinaryCol"));
			pojo.setLongTextCol((String)rs.getObject("LongTextCol"));
			pojo.setMediumTextCol((String)rs.getObject("MediumTextCol"));
			pojo.setTextCol((String)rs.getObject("TextCol"));
			pojo.setTinyTextCol((String)rs.getObject("TinyTextCol"));
			pojo.setTinyIntOne((Boolean)rs.getObject("TinyIntOne"));
			pojo.setCharTow((String)rs.getObject("CharTow"));
			pojo.setYear((Date)rs.getObject("Year"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(ManyTypesTank1 pojo) {
			return pojo.getId();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(ManyTypesTank1 pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("Id", pojo.getId());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(ManyTypesTank1 pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("Id", pojo.getId());
			map.put("TinyIntCol", pojo.getTinyIntCol());
			map.put("SmallIntCol", pojo.getSmallIntCol());
			map.put("IntCol", pojo.getIntCol());
			map.put("BigIntCol", pojo.getBigIntCol());
			map.put("DecimalCol", pojo.getDecimalCol());
			map.put("DoubleCol", pojo.getDoubleCol());
			map.put("FloatCol", pojo.getFloatCol());
			map.put("BitCol", pojo.getBitCol());
			map.put("CharCol", pojo.getCharCol());
			map.put("VarCharCol", pojo.getVarCharCol());
			map.put("DateCol", pojo.getDateCol());
			map.put("DateTimeCol", pojo.getDateTimeCol());
			map.put("TimeCol", pojo.getTimeCol());
			map.put("TimestampCol", pojo.getTimestampCol());
			map.put("YearCol", pojo.getYearCol());
			map.put("BinaryCol", pojo.getBinaryCol());
			map.put("BlobCol", pojo.getBlobCol());
			map.put("LongBlobCol", pojo.getLongBlobCol());
			map.put("MediumBlobCol", pojo.getMediumBlobCol());
			map.put("TinyBlobCol", pojo.getTinyBlobCol());
			map.put("VarBinaryCol", pojo.getVarBinaryCol());
			map.put("LongTextCol", pojo.getLongTextCol());
			map.put("MediumTextCol", pojo.getMediumTextCol());
			map.put("TextCol", pojo.getTextCol());
			map.put("TinyTextCol", pojo.getTinyTextCol());
			map.put("TinyIntOne", pojo.getTinyIntOne());
			map.put("CharTow", pojo.getCharTow());
			map.put("Year", pojo.getYear());
	
			return map;
		}
	}
}
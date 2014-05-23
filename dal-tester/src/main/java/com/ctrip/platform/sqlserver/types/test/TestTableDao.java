package com.ctrip.platform.sqlserver.types.test;

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

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.AbstractDalParser;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class TestTableDao {
	private static final String DATA_BASE = "HotelPubDB_Test";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from TestTable";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select %s, row_number() over(order by ID desc ) as rownum" 
			+" from TestTable (nolock)) select %s from CTE where rownum between %s and %s";
	
	private DalParser<TestTable> parser = new TestTableParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<TestTable> rowextractor = null;
	private DalTableDao<TestTable> client;
	private DalClient baseClient;

	public TestTableDao() {
		this.client = new DalTableDao<TestTable>(parser);
		this.rowextractor = new DalRowMapperExtractor<TestTable>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	/**
	 * Query TestTable by the specified ID
	 * The ID must be a number
	**/
	public TestTable queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
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
	 * Query TestTable with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<TestTable> queryByPage(int pageSize, int pageNo)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
        StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		String sql = "";
		int fromRownum = (pageNo - 1) * pageSize + 1;
		String columns = StringUtils.join(this.parser.getColumnNames(), ",");
        int endRownum = pageSize * pageNo;
		sql = String.format(PAGE_SQL_PATTERN, columns, columns, fromRownum, endRownum);
		return this.baseClient.query(sql, parameters, hints, rowextractor);
	}
	
	public List<TestTable> queryWhereClause(String whereClause) throws SQLException{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<TestTable> result = null;
		String columns = StringUtils.join(this.parser.getColumnNames(), ",");
		String sql = "SELECT " + columns + " FROM " + this.parser.getTableName() + " WHERE " + whereClause;
		result = this.baseClient.query(sql, parameters, hints, rowextractor);
		return result;
	}

	/**
	 * SQL insert
	 * Note: there must be one non-null field in daoPojo
	**/
	public void insert(TestTable...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		DalHints hints = new DalHints();
		client.insert(hints, null, daoPojos);
	}
	
	public static class TestTableParser extends AbstractDalParser<TestTable> {
		public static final String DATABASE_NAME = "HotelPubDB_Test";
		public static final String TABLE_NAME = "TestTable";
		private static final String[] COLUMNS = new String[]{
			"ID",
			"datetime",
			"datetime2",
			"smalldatetime",
			"date",
			"datetimeoffset",
			"time",
			"smallint",
			"tinyint",
			"bigint",
			"money",
			"smallmoney",
			"float",
			"text",
			"ntext",
			"xml",
			"char",
			"varchar",
			"nchar",
			"nvarchar",
			"real",
			"decimal",
			"bit",
			"numeric",
			"binary",
			"guid",
			"image",
			"timestamp",
			"charone",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"ID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
			Types.DATE,
			Types.TIMESTAMP,
			Types.TIME,
			Types.SMALLINT,
			Types.TINYINT,
			Types.BIGINT,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DOUBLE,
			Types.LONGVARCHAR,
			Types.LONGNVARCHAR,
			Types.LONGNVARCHAR,
			Types.CHAR,
			Types.VARCHAR,
			Types.NCHAR,
			Types.NVARCHAR,
			Types.REAL,
			Types.DECIMAL,
			Types.BIT,
			Types.NUMERIC,
			Types.BINARY,
			Types.CHAR,
			Types.LONGVARBINARY,
			Types.BINARY,
			Types.CHAR,
		};
		
		public TestTableParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public TestTable map(ResultSet rs, int rowNum) throws SQLException {
			TestTable pojo = new TestTable();
			
			pojo.setID((Integer)rs.getObject("ID"));
			pojo.setDatetime((Timestamp)rs.getObject("datetime"));
			pojo.setDatetime2((Timestamp)rs.getObject("datetime2"));
			pojo.setSmalldatetime((Timestamp)rs.getObject("smalldatetime"));
			pojo.setDate((Date)rs.getObject("date"));
			pojo.setDatetimeoffset(rs.getTimestamp("datetimeoffset"));
			pojo.setTime((Time)rs.getObject("time"));
			pojo.setSmallint((Short)rs.getObject("smallint"));
			pojo.setTinyint((Short)rs.getObject("tinyint"));
			pojo.setBigint((Long)rs.getObject("bigint"));
			pojo.setMoney((BigDecimal)rs.getObject("money"));
			pojo.setSmallmoney((BigDecimal)rs.getObject("smallmoney"));
			pojo.setFloat((Double)rs.getObject("float"));
			pojo.setText((String)rs.getObject("text"));
			pojo.setNtext((String)rs.getObject("ntext"));
			pojo.setXml((String)rs.getObject("xml"));
			pojo.setChar((String)rs.getObject("char"));
			pojo.setVarchar((String)rs.getObject("varchar"));
			pojo.setNchar((String)rs.getObject("nchar"));
			pojo.setNvarchar((String)rs.getObject("nvarchar"));
			pojo.setReal((Float)rs.getObject("real"));
			pojo.setDecimal((BigDecimal)rs.getObject("decimal"));
			pojo.setBit((Boolean)rs.getObject("bit"));
			pojo.setNumeric((BigDecimal)rs.getObject("numeric"));
			pojo.setBinary((byte[])rs.getObject("binary"));
			pojo.setGuid((String)rs.getObject("guid"));
			pojo.setImage((byte[])rs.getObject("image"));
			pojo.setTimestamp((byte[])rs.getObject("timestamp"));
			pojo.setCharone((String)rs.getObject("charone"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(TestTable pojo) {
			return pojo.getID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(TestTable pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ID", pojo.getID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(TestTable pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("ID", pojo.getID());
			map.put("datetime", pojo.getDatetime());
			map.put("datetime2", pojo.getDatetime2());
			map.put("smalldatetime", pojo.getSmalldatetime());
			map.put("date", pojo.getDate());
			map.put("datetimeoffset", pojo.getDatetimeoffset());
			map.put("time", pojo.getTime());
			map.put("smallint", pojo.getSmallint());
			map.put("tinyint", pojo.getTinyint());
			map.put("bigint", pojo.getBigint());
			map.put("money", pojo.getMoney());
			map.put("smallmoney", pojo.getSmallmoney());
			map.put("float", pojo.getFloat());
			map.put("text", pojo.getText());
			map.put("ntext", pojo.getNtext());
			map.put("xml", pojo.getXml());
			map.put("char", pojo.getChar());
			map.put("varchar", pojo.getVarchar());
			map.put("nchar", pojo.getNchar());
			map.put("nvarchar", pojo.getNvarchar());
			map.put("real", pojo.getReal());
			map.put("decimal", pojo.getDecimal());
			map.put("bit", pojo.getBit());
			map.put("numeric", pojo.getNumeric());
			map.put("binary", pojo.getBinary());
			map.put("guid", pojo.getGuid());
			map.put("image", pojo.getImage());
			map.put("timestamp", pojo.getTimestamp());
			map.put("charone", pojo.getCharone());
	
			return map;
		}
	}
}

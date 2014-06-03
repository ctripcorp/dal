package com.ctrip.platform.dal.tester;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SqlServerTypesTest {
	private final static String DATABASE_NAME = "HotelPubDB";
	private final static String TABLE_NAME = "dal_client_test";
	private final static String DROP_TABLE_SQL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME +"("
			+ "[_ID] [int] IDENTITY(1,1) NOT NULL,"
			+ "[_datetime] [datetime] NULL,"
			+ "[_datetime2] [datetime2](7) NULL,"
			+ "[_smalldatetime] [smalldatetime] NULL,"
			+ "[_date] [date] NULL,"
			+ "[_datetimeoffset] [datetimeoffset](7) NULL,"
			+ "[_time] [time](7) NULL,"
			+ "[_smallint] [smallint] NULL,"
			+ "[_tinyint] [tinyint] NULL,"
			+ "[_bigint] [bigint] NULL,"
			+ "[_money] [money] NULL,"
			+ "[_smallmoney] [smallmoney] NULL,"
			+ "[_float] [float] NULL,"
			+ "[_text] [text] NULL,"
			+ "[_ntext] [ntext] NULL,"
			+ "[_xml] [xml] NULL,"
			+ "[_char] [char](10) NULL,"
			+ "[_varchar] [varchar](50) NULL,"
			+ "[_nchar] [nchar](10) NULL,"
			+ "[_nvarchar] [nvarchar](50) NULL,"
			+ "[_real] [real] NULL,"
			+ "[_decimal] [decimal](18, 0) NULL,"
			+ "[_bit] [bit] NULL,"
			+ "[_numeric] [numeric](18, 0) NULL,"
			+ "[_binary] [binary](50) NULL,"
			+ "[_varbinary] [varbinary](50) NULL, "
			+ "[_guid] [uniqueidentifier] NULL,"
			+ "[_image] [image] NULL,"
			+ "[_sql_variant] [sql_variant] NULL,"
			+ "[_timestamp] [timestamp] NULL,"
			+ "[_charone] [char](1) NULL)";
	
	private static DalClient client = null;
	static {
		try {
			DalClientFactory.initPrivateFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL};
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL };
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}
	
	public void sqlVariantNotSupportedTest(){
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String sql = "SELECT _sql_variant FROM " + TABLE_NAME;
		try{
			client.query(sql, parameters, hints, new DalResultSetExtractor<Boolean>(){
				@Override
				public Boolean extract(ResultSet rs) throws SQLException {
					Assert.fail();
					return true;
				}});
		}catch(SQLException e){}
	}
	
	@Test
	public void datetimeoffsetTest(){
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String sql = "SELECT _datetimeoffset FROM " + TABLE_NAME;
		try{
			client.query(sql, parameters, hints, new DalResultSetExtractor<Boolean>(){
				@Override
				public Boolean extract(ResultSet rs) throws SQLException {
					Object obj = rs.getObject("_datetimeoffset");
					return true;
				}});
		}catch(SQLException e){}
	}
	
	public static class ClientTestModel
	{
		private Integer _ID;
		private Timestamp _datetime;
		private Timestamp _datetime2;
		private Timestamp _smalldatetime;
		private Date _date;
		private Date _datetimeoffset;
		private Time _time;
		private Short _smallint;
		private Short _tinyint;
		private Long _bigint;
		private BigDecimal _money;
		private BigDecimal _smallmoney;
		private Double _float;
		private String _text;
		private String _ntext;
		private String _xml;
		private Character _char;
		private String _varchar;
		private String _nchar;
		private String _nvarchar;
		private Float _real;
		private BigDecimal _decimal;
		private Boolean _bit;
		private BigDecimal _numeric;
		private byte[] _binary;
		private String _guid;
		private byte[] _image;
		private byte[] _timestamp;
		private String _charone;
		private byte[] _varbinary;
		
		public Integer get_ID() {
			return _ID;
		}
		public void set_ID(Integer _ID) {
			this._ID = _ID;
		}
		public Timestamp get_datetime() {
			return _datetime;
		}
		public void set_datetime(Timestamp _datetime) {
			this._datetime = _datetime;
		}
		public Timestamp get_datetime2() {
			return _datetime2;
		}
		public void set_datetime2(Timestamp _datetime2) {
			this._datetime2 = _datetime2;
		}
		public Timestamp get_smalldatetime() {
			return _smalldatetime;
		}
		public void set_smalldatetime(Timestamp _smalldatetime) {
			this._smalldatetime = _smalldatetime;
		}
		public Date get_date() {
			return _date;
		}
		public void set_date(Date _date) {
			this._date = _date;
		}
		public Date get_datetimeoffset() {
			return _datetimeoffset;
		}
		public void set_datetimeoffset(Date _datetimeoffset) {
			this._datetimeoffset = _datetimeoffset;
		}
		public Time get_time() {
			return _time;
		}
		public void set_time(Time _time) {
			this._time = _time;
		}
		public Short get_smallint() {
			return _smallint;
		}
		public void set_smallint(Short _smallint) {
			this._smallint = _smallint;
		}
		public Short get_tinyint() {
			return _tinyint;
		}
		public void set_tinyint(Short _tinyint) {
			this._tinyint = _tinyint;
		}
		public Long get_bigint() {
			return _bigint;
		}
		public void set_bigint(Long _bigint) {
			this._bigint = _bigint;
		}
		public BigDecimal get_money() {
			return _money;
		}
		public void set_money(BigDecimal _money) {
			this._money = _money;
		}
		public BigDecimal get_smallmoney() {
			return _smallmoney;
		}
		public void set_smallmoney(BigDecimal _smallmoney) {
			this._smallmoney = _smallmoney;
		}
		public Double get_float() {
			return _float;
		}
		public void set_float(Double _float) {
			this._float = _float;
		}
		public String get_text() {
			return _text;
		}
		public void set_text(String _text) {
			this._text = _text;
		}
		public String get_ntext() {
			return _ntext;
		}
		public void set_ntext(String _ntext) {
			this._ntext = _ntext;
		}
		public String get_xml() {
			return _xml;
		}
		public void set_xml(String _xml) {
			this._xml = _xml;
		}
		public Character get_char() {
			return _char;
		}
		public void set_char(Character _char) {
			this._char = _char;
		}
		public String get_varchar() {
			return _varchar;
		}
		public void set_varchar(String _varchar) {
			this._varchar = _varchar;
		}
		public String get_nchar() {
			return _nchar;
		}
		public void set_nchar(String _nchar) {
			this._nchar = _nchar;
		}
		public String get_nvarchar() {
			return _nvarchar;
		}
		public void set_nvarchar(String _nvarchar) {
			this._nvarchar = _nvarchar;
		}
		public Float get_real() {
			return _real;
		}
		public void set_real(Float _real) {
			this._real = _real;
		}
		public BigDecimal get_decimal() {
			return _decimal;
		}
		public void set_decimal(BigDecimal _decimal) {
			this._decimal = _decimal;
		}
		public Boolean get_bit() {
			return _bit;
		}
		public void set_bit(Boolean _bit) {
			this._bit = _bit;
		}
		public BigDecimal get_numeric() {
			return _numeric;
		}
		public void set_numeric(BigDecimal _numeric) {
			this._numeric = _numeric;
		}
		public byte[] get_binary() {
			return _binary;
		}
		public void set_binary(byte[] _binary) {
			this._binary = _binary;
		}
		public String get_guid() {
			return _guid;
		}
		public void set_guid(String _guid) {
			this._guid = _guid;
		}
		public byte[] get_image() {
			return _image;
		}
		public void set_image(byte[] _image) {
			this._image = _image;
		}
		public byte[] get_timestamp() {
			return _timestamp;
		}
		public void set_timestamp(byte[] _timestamp) {
			this._timestamp = _timestamp;
		}
		public String get_charone() {
			return _charone;
		}
		public void set_charone(String _charone) {
			this._charone = _charone;
		}
		public byte[] get_varbinary() {
			return _varbinary;
		}
		public void set_varbinary(byte[] _varbinary) {
			this._varbinary = _varbinary;
		}
		
	}
	
	private static class ClientTestDalRowMapper implements DalRowMapper<ClientTestModel> {

		@Override
		public ClientTestModel map(ResultSet rs, int rowNum)
				throws SQLException {
			ClientTestModel model = new ClientTestModel();
			model.set_ID(rs.getInt("_ID"));
			model.set_bigint(rs.getLong("_bigint"));
			model.set_binary(rs.getBytes("_binary"));
			model.set_bit(rs.getBoolean("_bit"));
			model.set_char((Character)rs.getObject("_char"));
			model.set_charone(rs.getString("_charone"));
			model.set_date(rs.getDate("_date"));
			model.set_datetime(rs.getTimestamp("_datetime"));
			model.set_datetime2(rs.getTimestamp("_datetime2"));
			model.set_datetimeoffset(rs.getDate("_datetimeoffset"));
			model.set_decimal(rs.getBigDecimal("_decimal"));
			model.set_float(rs.getDouble("_float"));
			model.set_guid(rs.getString("_guid"));
			model.set_image(rs.getBytes("_image"));
			model.set_money(rs.getBigDecimal("_money"));
			model.set_nchar(rs.getString("_nchar"));
			model.set_ntext(rs.getString("_ntext"));
			model.set_numeric(rs.getBigDecimal("_numeric"));
			model.set_nvarchar(rs.getString("_nvarchar"));
			model.set_real(rs.getFloat("_real"));
			model.set_smalldatetime(rs.getTimestamp("_smalldatetime"));
			model.set_smallint(rs.getShort("_smallint"));
			model.set_smallmoney(rs.getBigDecimal("_smallmoney"));
			model.set_text(rs.getString("_text"));
			model.set_time(rs.getTime("_time"));
			model.set_timestamp(rs.getBytes("_timestamp"));
			model.set_tinyint(rs.getShort("_tinyint"));
			model.set_varbinary(rs.getBytes("_varbinary"));
			model.set_varchar(rs.getString("_varchar"));
			model.set_xml(rs.getString("_xml"));
			return null;
		}
		
	}
}


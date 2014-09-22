package com.ctrip.platform.dal.tester;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import microsoft.sql.DateTimeOffset;

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
			DalClientFactory.initClientFactory();
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
	
	@Test
	public void insertTest() throws SQLException{
		String sql = "INSERT INTO " + TABLE_NAME + "("
				//+ "_ID, "
				+ "_datetime, "
				+ "_datetime2, "
				+ "_smalldatetime,"
				+ "_date,"
				+ "_datetimeoffset, "
				+ "_time,"
				+ "_smallint,"
				+ "_tinyint,"
				+ "_bigint,"
				+ "_money,"
				+ "_smallmoney,"
				+ "_float,"
				+ "_text,"
				+ "_ntext,"
				+ "_xml,"
				+ "_char,"
				+ "_varchar,"
				+ "_nchar,"
				+ "_nvarchar,"
				+ "_real,"
				+ "_decimal,"
				+ "_bit, "
				+ "_numeric,"
				+ "_binary,"
				+ "_guid,"
				+ "_image,"
				//+ "_timestamp,"
				+ "_charone,"
				+ "_varbinary) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		ClientTestModel model = new ClientTestModel();
		
		model.set_datetime(new Timestamp(System.currentTimeMillis()));
		model.set_datetime2(new Timestamp(System.currentTimeMillis()));
		model.set_smalldatetime(new Timestamp(System.currentTimeMillis()));
		model.set_date(new Date(System.currentTimeMillis()));
		model.set_datetimeoffset(DateTimeOffset.valueOf(new Timestamp(System.currentTimeMillis()), 1));
		model.set_time(new Time(System.currentTimeMillis()));
		model.set_smallint(Short.MAX_VALUE);
		model.set_tinyint(Short.valueOf("0"));
		model.set_bigint(Long.MAX_VALUE);
		model.set_money(BigDecimal.ONE);
		model.set_smallmoney(BigDecimal.TEN);
		model.set_float(Double.MIN_VALUE);
		model.set_text("This is a text");
		model.set_ntext("This is a ntext");
		model.set_xml("<xml>hello</xml>");
		model.set_char("C");
		model.set_varchar("This is a varchar");
		model.set_nchar("T");
		model.set_nvarchar("TT");
		model.set_real(Float.MAX_VALUE);
		model.set_decimal(BigDecimal.ZERO);
		model.set_bit(true);
		model.set_numeric(BigDecimal.TEN);
		model.set_binary("This is binary".getBytes());
		model.set_guid("2A66057D-F4E5-4E2B-B2F1-38C51A96D385");
		model.set_image("This is image".getBytes());
		model.set_timestamp("This is timestamp".getBytes());
		model.set_charone("C");
		model.set_varbinary("This is varbinary".getBytes());
		
		StatementParameters parameters = new StatementParameters();
		//parameters.set(1, Types.INTEGER, model.get_ID());
		parameters.set(1, Types.TIMESTAMP, model.get_datetime());
		parameters.set(2, Types.TIMESTAMP, model.get_datetime2());
		parameters.set(3, Types.TIMESTAMP, model.get_smalldatetime());
		parameters.set(4, Types.DATE, model.get_date());
		parameters.set(5, microsoft.sql.Types.DATETIMEOFFSET, model.get_datetimeoffset());
		parameters.set(6, Types.TIME, model.get_time());
		parameters.set(7, Types.SMALLINT, model.get_smallint());
		parameters.set(8, Types.SMALLINT, model.get_tinyint());
		parameters.set(9, Types.BIGINT, model.get_bigint());
		parameters.set(10, Types.DECIMAL, model.get_money());
		parameters.set(11, Types.DECIMAL, model.get_smallmoney());
		parameters.set(12, Types.DOUBLE, model.get_float());
		parameters.set(13, Types.LONGVARCHAR, model.get_text());
		parameters.set(14, Types.LONGNVARCHAR, model.get_ntext());
		parameters.set(15, Types.LONGVARCHAR, model.get_xml());
		parameters.set(16, Types.CHAR, model.get_char());
		parameters.set(17, Types.VARCHAR, model.get_varchar());
		parameters.set(18, Types.CHAR, model.get_nchar());
		parameters.set(19, Types.VARCHAR, model.get_nvarchar());
		parameters.set(20, Types.REAL, model.get_real());
		parameters.set(21, Types.DECIMAL, model.get_decimal());
		parameters.set(22, Types.BIT, model.get_bit());
		parameters.set(23, Types.NUMERIC, model.get_numeric());
		parameters.set(24, Types.BINARY, model.get_binary());
		parameters.set(25, Types.NVARCHAR, model.get_guid());
		parameters.set(26, Types.LONGVARBINARY, model.get_image());
		//parameters.set(28, Types.BINARY,model.get_timestamp());
		parameters.set(27, Types.CHAR,model.get_charone());
		parameters.set(28, Types.VARBINARY, model.get_varbinary());
		
		int count = client.update(sql, parameters, new DalHints());
		
		System.out.println(count);
	}
	
	public static class ClientTestModel
	{
		private Integer _ID;
		private Timestamp _datetime;
		private Timestamp _datetime2;
		private Timestamp _smalldatetime;
		private Date _date;
		private DateTimeOffset _datetimeoffset;
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
		private String _char;
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
		public DateTimeOffset get_datetimeoffset() {
			return _datetimeoffset;
		}
		public void set_datetimeoffset(DateTimeOffset _datetimeoffset) {
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
		public String get_char() {
			return _char;
		}
		public void set_char(String _char) {
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
			model.set_char(rs.getString("_char"));
			model.set_charone(rs.getString("_charone"));
			model.set_date(rs.getDate("_date"));
			model.set_datetime(rs.getTimestamp("_datetime"));
			model.set_datetime2(rs.getTimestamp("_datetime2"));
			model.set_datetimeoffset((DateTimeOffset)rs.getObject("_datetimeoffset"));
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


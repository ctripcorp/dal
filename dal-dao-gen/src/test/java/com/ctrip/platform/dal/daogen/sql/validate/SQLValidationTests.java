package com.ctrip.platform.dal.daogen.sql.validate;

import static org.junit.Assert.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class SQLValidationTests {
	
	private static final String MYSQLDB = "dao_test";
	private static final String MSDB = "HotelPubDB_test1";

	@Test
	public void testMySQLQueryValidate() {
		Items item = Items.create()
				.add("ID", "=", Types.INTEGER)
				.add("NAME", "like", Types.VARCHAR);
		String sql = "SELECT * FROM Person WHERE " + item.toWhere();
		ValidateResult vret = SQLValidation.validate(MYSQLDB, sql, item.toTypes());
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMSQueryValidate() {
		String sql = "SELECT * FROM TestTable WHERE ID=?";
		int[] sqlTypes = new int[]{Types.INTEGER};
		ValidateResult vret = SQLValidation.validate(MSDB, sql, sqlTypes);
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMySQLQueryTypesValidate() {
		
		String sql = "SELECT * FROM ManyTypes WHERE Id = ? AND TinyIntCol = ? AND SmallIntCol = ? AND "
				+ "IntCol = ? AND BigIntCol = ? AND DecimalCol = ? AND DoubleCol = ? AND FloatCol = ? AND "
				+ "BitCol = ? AND CharCol = ? AND VarCharCol = ? AND DateCol = ? AND DateTimeCol = ? AND "
				+ "TimeCol = ? AND TimestampCol = ? AND YearCol = ? AND BinaryCol = ? AND BlobCol = ? AND "
				+ "LongBlobCol = ? AND MediumBlobCol = ? AND TinyBlobCol = ? AND VarBinaryCol = ? AND "
				+ "LongTextCol = ? AND MediumTextCol = ? AND TextCol = ? AND TinyTextCol = ? AND TinyIntOne = ? AND "
				+ "CharTow = ? AND Year = ?";
		int[] sqlTypes = new int[]{Types.INTEGER,Types.TINYINT,Types.SMALLINT,Types.INTEGER,Types.BIGINT,Types.DECIMAL,
				Types.DOUBLE,Types.REAL,Types.BIT,Types.CHAR,Types.VARCHAR,Types.DATE,Types.TIMESTAMP,Types.TIME,Types.TIMESTAMP,
				Types.DATE,Types.BINARY,Types.LONGVARBINARY,Types.LONGVARBINARY,Types.LONGVARBINARY,Types.BINARY,Types.VARBINARY,
				Types.LONGVARCHAR,Types.LONGVARCHAR,Types.LONGVARCHAR,Types.VARCHAR,Types.BIT,Types.CHAR,Types.DATE};
		ValidateResult vret = SQLValidation.validate(MYSQLDB, sql, sqlTypes);
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMSQueryTypesValidate() {
		String sql = "SELECT * FROM TestTable WHERE [ID]=? AND [datetime] =? AND datetime2] =? AND [smalldatetime] =? AND [date] =? AND [datetimeoffset] =? AND [time] =? AND [smallint] =? AND [tinyint] =? AND [bigint] =? AND [money] =? AND [smallmoney] =? AND [float] =? AND [text] =? AND [ntext] =? AND [xml] =? AND [char] =? AND [varchar] =? AND [nchar] =? AND [nvarchar] =? AND [real] =? AND ,[decimal] =? AND [bit] =? AND [numeric] =? AND [binary] =? AND [guid] =? AND [image] = ? AND[timestamp] = ? AND[charone] = ?";
		int[] sqlTypes = new int[]{Types.INTEGER,Types.TIMESTAMP,Types.TIMESTAMP,Types.TIMESTAMP,Types.DATE,microsoft.sql.Types.DATETIMEOFFSET,Types.TIME,Types.SMALLINT,Types.TINYINT,Types.BIGINT,Types.DECIMAL,Types.DECIMAL,Types.DOUBLE,Types.LONGVARCHAR,Types.LONGNVARCHAR,Types.CHAR,Types.VARCHAR,Types.NCHAR,Types.NVARCHAR,Types.REAL,Types.DECIMAL,Types.BIT,Types.NUMERIC,Types.BINARY,Types.CHAR,Types.BINARY,Types.CHAR};
		ValidateResult vret = SQLValidation.validate(MSDB, sql, sqlTypes);
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMySQLUpdateValidate(){
		String sql = "INSERT INTO ManyTypes(TinyIntCol,SmallIntCol,"
				+ "IntCol,BigIntCol,DecimalCol,DoubleCol,FloatCol,"
				+ "BitCol,CharCol,VarCharCol,DateCol,DateTimeCol,"
				+ "TimeCol,TimestampCol,BinaryCol,BlobCol,"
				+ "LongBlobCol,MediumBlobCol,TinyBlobCol,VarBinaryCol,"
				+ "LongTextCol,MediumTextCol,TextCol,TinyTextCol,TinyIntOne,"
				+ "CharTow) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] sqlTypes = new int[]{Types.TINYINT,Types.SMALLINT,Types.INTEGER,Types.BIGINT,Types.DECIMAL,
				Types.DOUBLE,Types.REAL,Types.BIT,Types.CHAR,Types.VARCHAR,Types.DATE,Types.TIMESTAMP,Types.TIME,Types.TIMESTAMP,
				Types.BINARY,Types.LONGVARBINARY,Types.LONGVARBINARY,Types.LONGVARBINARY,Types.BINARY,Types.VARBINARY,
				Types.LONGVARCHAR,Types.LONGVARCHAR,Types.LONGVARCHAR,Types.VARCHAR,Types.TINYINT,Types.CHAR};
		ValidateResult vret = SQLValidation.validate(MYSQLDB, sql, sqlTypes);
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMSUpdateValidate(){
		fail("Not yet implemented");
	}
	
	private static class Items{
		List<String> operations;
		List<Integer> types;
		private Items(){
			operations = new ArrayList<String>();
			types = new ArrayList<Integer>();
		}
		public static Items create(){
			return new Items();
		}
		
		public Items add(String name, String opt, int val){
			if(opt.equals("=")){
				this.operations.add(name + " = ?");
				this.types.add(val);
				return this;
			}
			if(opt.equalsIgnoreCase("like")){
				this.operations.add(name + " like ?");
				this.types.add(val);
				return this;
			}
			if(opt.equalsIgnoreCase("between")){
				this.operations.add(name + " between ? and ?");
				this.types.add(val);
				this.types.add(val);
				return this;
			}
			
			if(opt.equalsIgnoreCase("in")){
				this.operations.add(name + " int (?, ?)");
				this.types.add(val);
				this.types.add(val);
				return this;
			}
			
			return this;
		}
		
		public String toWhere(){
			return StringUtils.join(this.operations, " AND ");
		}
		
		public String toInsert(){
			return null;
		}
		
		public int[] toTypes(){
			return this.toTypes().clone();
		}
	}
}

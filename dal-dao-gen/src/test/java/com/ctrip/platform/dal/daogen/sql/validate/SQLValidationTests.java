package com.ctrip.platform.dal.daogen.sql.validate;

import static org.junit.Assert.assertTrue;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class SQLValidationTests {
	
	private static final String MYSQLDB = "dao_test";
	private static final String MSDB = "HotelPubDB_test1";

	@Test
	public void testMySQLQueryAffectRows(){
		String sql = "SELECT * FROM Person";
		ValidateResult vret = SQLValidation.validate(MYSQLDB, sql, new int[]{});
		assertTrue(vret.isPassed());
		assertTrue(vret.getAffectRows() > 0);
	}
	
	@Test
	public void testSqlServerQueryAffectRows(){
		String sql = "SELECT * FROM TestTable";
		ValidateResult vret = SQLValidation.validate(MSDB, sql, new int[]{});
		assertTrue(vret.isPassed());
		assertTrue(vret.getAffectRows() > 0);
	}
	
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
		Items item = Items.create()
				.add("ID", "=", Types.INTEGER);
		String sql = "SELECT * FROM TestTable WHERE " + item.toWhere();
		int[] sqlTypes = new int[]{Types.INTEGER};
		ValidateResult vret = SQLValidation.validate(MSDB, sql, sqlTypes);
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMySQLQueryTypesValidate() {
		Items item = Items.create()
				.add("Id", "=", Types.INTEGER)
				.add("TinyIntCol", "=", Types.TINYINT)
				.add("SmallIntCol", "=", Types.SMALLINT)
				.add("IntCol", "=", Types.INTEGER)
				.add("BigIntCol", "=", Types.BIGINT)
				.add("DecimalCol", "=", Types.DECIMAL)
				.add("DoubleCol", "=", Types.DOUBLE)
				.add("FloatCol", "=", Types.REAL)
				.add("BitCol", "=", Types.BIT)
				.add("CharCol", "=", Types.CHAR)
				.add("VarCharCol", "=", Types.VARCHAR)
				.add("DateCol", "=", Types.DATE)
				.add("DateTimeCol", "=", Types.TIMESTAMP)
				.add("TimeCol", "=", Types.TIME)
				.add("TimestampCol", "=", Types.TIMESTAMP)
				.add("YearCol", "=", Types.DATE)
				.add("BinaryCol", "=", Types.BINARY)
				.add("BlobCol", "=", Types.BLOB)
				.add("LongBlobCol", "=", Types.LONGVARBINARY)
				.add("MediumBlobCol", "=", Types.LONGVARBINARY)
				.add("TinyBlobCol", "=", Types.LONGVARBINARY)
				.add("VarBinaryCol", "=", Types.VARBINARY)
				.add("LongTextCol", "=", Types.LONGVARCHAR)
				.add("MediumTextCol", "=", Types.LONGVARCHAR)
				.add("TextCol", "=", Types.LONGVARCHAR)
				.add("TinyTextCol", "=", Types.VARCHAR)
				.add("TinyIntOne", "=", Types.BIT)
				.add("CharTow", "=", Types.CHAR)
				.add("Year", "=", Types.DATE);
		
		String sql = "SELECT * FROM ManyTypes WHERE " + item.toWhere();
		ValidateResult vret = SQLValidation.validate(MYSQLDB, sql, item.toTypes());
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMSQueryTypesValidate() {
		Items item = Items.create()
				.add("[ID]", "=", Types.INTEGER)
				.add("[datetime]", "=", Types.TIMESTAMP)
				.add("[datetime2]", "=", Types.TIMESTAMP)
				.add("[smalldatetime]", "=", Types.TIMESTAMP)
				.add("[date]", "=", Types.DATE)
				.add("[datetimeoffset]", "=", microsoft.sql.Types.DATETIMEOFFSET)
				.add("[time]","=", Types.TIME)
				.add("[tinyint]", "=", Types.TINYINT)
				.add("[bigint]", "=", Types.BIGINT)
				.add("[money]", "=", Types.DECIMAL)
				.add("[smallmoney]", "=", Types.DECIMAL)
				.add("[float]", "=", Types.DOUBLE)
				.add("[text]", "like", Types.LONGVARCHAR)
				.add("[ntext]", "like", Types.LONGVARCHAR)
				.add("[char]", "=", Types.CHAR)
				.add("[varchar]", "like", Types.VARCHAR)
				.add("[nchar]", "=", Types.NCHAR)
				.add("[nvarchar]", "like", Types.NVARCHAR)
				.add("[real]", "=", Types.REAL)
				.add("[decimal]", "=", Types.DECIMAL)		
				.add("[bit]", "=", Types.BIT)
				.add("[numeric]", "=", Types.NUMERIC)
				.add("[binary]", "=", Types.BINARY)
				.add("[guid]", "=", Types.NVARCHAR)
				//.add("[timestamp]","=", Types.TIMESTAMP)
				.add("[charone]", "=", Types.CHAR);
		String sql = "SELECT * FROM TestTable WHERE " + item.toWhere();
		ValidateResult vret = SQLValidation.validate(MSDB, sql, item.toTypes());
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMySQLUpdateValidate(){
		Items item = Items.create()
				.add("TinyIntCol", Types.TINYINT)
				.add("SmallIntCol", Types.SMALLINT)
				.add("IntCol", Types.INTEGER)
				.add("BigIntCol", Types.BIGINT)
				.add("DecimalCol", Types.DECIMAL)
				.add("DoubleCol", Types.DOUBLE)
				.add("FloatCol", Types.REAL)
				.add("BitCol", Types.BIT)
				.add("CharCol", Types.CHAR)
				.add("VarCharCol", Types.VARCHAR)
				.add("DateCol", Types.DATE)
				.add("DateTimeCol", Types.TIMESTAMP)
				.add("TimeCol", Types.TIME)
				.add("TimestampCol", Types.TIMESTAMP)
				.add("BinaryCol", Types.BINARY)
				.add("BlobCol", Types.BLOB)
				.add("LongBlobCol", Types.LONGVARBINARY)
				.add("MediumBlobCol", Types.LONGVARBINARY)
				.add("TinyBlobCol", Types.BINARY)
				.add("VarBinaryCol", Types.VARBINARY)
				.add("LongTextCol", Types.LONGVARCHAR)
				.add("MediumTextCol", Types.LONGVARCHAR)
				.add("TextCol", Types.LONGVARCHAR)
				.add("TinyTextCol", Types.VARCHAR)
				.add("TinyIntOne", Types.TINYINT)
				.add("CharTow", Types.CHAR);
				
		String sql = "INSERT INTO ManyTypes(" + item.toInsert() + ") VALUES(" + item.toValues() + ")";
		ValidateResult vret = SQLValidation.validate(MYSQLDB, sql, item.toTypes());
		System.out.println(vret.getAffectRows());
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMSUpdateValidate(){
		Items item = Items.create()
				.add("[datetime]", Types.TIMESTAMP)
				.add("[datetime2]", Types.TIMESTAMP)
				.add("[smalldatetime]", Types.TIMESTAMP)
				.add("[date]", Types.DATE)
				.add("[datetimeoffset]", microsoft.sql.Types.DATETIMEOFFSET)
				.add("[time]", Types.TIME)
				.add("[tinyint]",Types.TINYINT)
				.add("[bigint]",  Types.BIGINT)
				.add("[money]",  Types.DECIMAL)
				.add("[smallmoney]", Types.DECIMAL)
				.add("[float]", Types.DOUBLE)
				.add("[text]", Types.LONGVARCHAR)
				.add("[ntext]", Types.LONGVARCHAR)
				.add("[char]",  Types.CHAR)
				.add("[varchar]", Types.VARCHAR)
				.add("[nchar]",  Types.NCHAR)
				.add("[nvarchar]",  Types.NVARCHAR)
				.add("[real]",  Types.REAL)
				.add("[decimal]",  Types.DECIMAL)		
				.add("[bit]",  Types.BIT)
				.add("[numeric]", Types.NUMERIC)
				.add("[binary]", Types.BINARY)
				//.add("[guid]", Types.NVARCHAR)
				//.add("[timestamp]", Types.TIMESTAMP)
				.add("[charone]", Types.CHAR);
		String sql = "INSERT INTO TestTable(" + item.toInsert() + ")" + "VALUES(" + item.toValues() + ")";
		ValidateResult vret = SQLValidation.validate(MSDB, sql, item.toTypes());
		assertTrue(vret.isPassed());
	}
	
	private static class Items{
		List<String> fields;
		List<String> operations;
		List<Integer> types;
		private Items(){
			operations = new ArrayList<String>();
			fields = new ArrayList<String>();
			types = new ArrayList<Integer>();
		}
		public static Items create(){
			return new Items();
		}
		
		public Items add(String name, String opt, int val){
			this.fields.add(name);
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
		
		public Items add(String name, int val){
			this.fields.add(name);
			this.types.add(val);
			return this;
		}
		
		public String toWhere(){
			return StringUtils.join(this.operations, " AND ");
		}
		
		public String toInsert(){
			return StringUtils.join(this.fields, ",");
		}
		
		public String toValues(){
			List<String> qs = new ArrayList<String>();
			for (int i = 0; i < this.fields.size(); i++) {
				qs.add("?");
			}
			return StringUtils.join(qs, ",");
		}
		
		public int[] toTypes(){
			int[] ts = new int[this.types.size()];
			for (int i = 0; i < ts.length; i++) {
				ts[i] = this.types.get(i);
			}
			return ts;
		}
	}
}

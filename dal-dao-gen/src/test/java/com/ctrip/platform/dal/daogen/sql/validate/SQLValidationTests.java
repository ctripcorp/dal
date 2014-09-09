package com.ctrip.platform.dal.daogen.sql.validate;

import static org.junit.Assert.*;

import java.sql.Types;

import org.junit.Test;

public class SQLValidationTests {
	
	private static final String MYSQLDB = "dao_test";
	private static final String MSDB = "HotelPubDB_test1";

	@Test
	public void testMySQLQueryValidate() {
		String sql = "SELECT * FROM Person WHERE ID = ? AND NAME LIKE ?";
		int[] sqlTypes = new int[]{Types.INTEGER, Types.VARCHAR};
		ValidateResult vret = SQLValidation.validate(MYSQLDB, sql, sqlTypes);
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
		fail("Not yet implemented");
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
}

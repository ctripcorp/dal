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
		String sql = "SELECT * FROM ManyTypes WHERE Id = ?, TinyIntCol = ?, SmallIntCol = ?, IntCol = ?, "
				+ "BigIntCol = ?, DecimalCol = ?, DoubleCol = ?, FloatCol = ?, BitCol = ?, CharCol =?, "
				+ "VarCharCol = ?, DateCol = ?, DateTimeCol =?, TimeCol =?, TimestampCol = ?, YearCol =?, "
				+ "BinaryCol = ?, BlobCol = ?, LongBlobCol =?, MediumBlobCol =?, TinyBlobCol = ?, "
				+ "VarBinaryCol = ?, LongTextCol = ?, MediumTextCol =?, TextCol =?, TinyTextCol = ?, "
				+ "TinyIntOne = ?, CharTow =?, Year =?";
		int[] sqlTypes = new int[]{Types.INTEGER, Types.TINYINT, Types.SMALLINT, Types.INTEGER, 
				Types.BIGINT, Types.DECIMAL, Types.DOUBLE, Types.FLOAT, Types.BIT, Types.CHAR, 
				Types.VARCHAR, Types.DATE, Types.DATE, Types.TIME, Types.TIMESTAMP, Types.SMALLINT, 
				Types.BINARY, Types.BLOB, Types.LONGVARBINARY, Types.BINARY, Types.BINARY, 
				Types.VARBINARY, Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.LONGVARCHAR, Types.NVARCHAR, 
				Types.TINYINT, Types.CHAR, Types.SMALLINT};
		ValidateResult vret = SQLValidation.validate(MYSQLDB, sql, sqlTypes);
		assertTrue(vret.isPassed());
	}
	
	@Test
	public void testMSQueryTypesValidate() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testMySQLUpdateValidate(){
		fail("Not yet implemented");
	}
	
	@Test
	public void testMSUpdateValidate(){
		fail("Not yet implemented");
	}
}

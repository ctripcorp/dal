package com.ctrip.platform.dal.daogen.enums;

import java.util.HashMap;
import java.util.Map;

public enum DbType {

	AnsiString(0),

	Binary(1),

	Byte(2),

	Boolean(3),

	Currency(4),

	Date(5),

	DateTime(6),

	Decimal(7),

	Double(8),

	Guid(9),

	Int16(10),

	Int32(11),

	Int64(12),

	Object(13),

	SByte(14),

	Single(15),

	String(16),

	Time(17),

	UInt16(18),

	UInt32(19),

	UInt64(20),

	VarNumeric(21),

	AnsiStringFixedLength(22),

	StringFixedLength(23),

	Xml(25),

	DateTime2(26),

	DateTimeOffset(27);

	private int intVal;

	private static Map<Integer, DbType> jdbcTypeToNetDbType = new HashMap<Integer, DbType>();
	// DbType转c#语言的数据类型
	private static Map<DbType, String> netDbTypeToNetType = new HashMap<DbType, String>();
	private static final Map<Integer, DbType> intToEnum = new HashMap<Integer, DbType>();

	static {
		for (DbType blah : values()) {
			intToEnum.put(blah.getIntVal(), blah);
		}

		jdbcTypeToNetDbType.put(java.sql.Types.BIT, Boolean);
		jdbcTypeToNetDbType.put(java.sql.Types.TINYINT, Byte);
		jdbcTypeToNetDbType.put(java.sql.Types.SMALLINT, Int16);
		jdbcTypeToNetDbType.put(java.sql.Types.INTEGER, Int32);
		jdbcTypeToNetDbType.put(java.sql.Types.BIGINT, Int64);
		jdbcTypeToNetDbType.put(java.sql.Types.FLOAT, Double);
		jdbcTypeToNetDbType.put(java.sql.Types.REAL, Single);
		jdbcTypeToNetDbType.put(java.sql.Types.DOUBLE, Double);
		jdbcTypeToNetDbType.put(java.sql.Types.NUMERIC, Decimal);
		jdbcTypeToNetDbType.put(java.sql.Types.DECIMAL, Decimal);
		jdbcTypeToNetDbType.put(java.sql.Types.CHAR, AnsiStringFixedLength);
		jdbcTypeToNetDbType.put(java.sql.Types.VARCHAR, AnsiString);
		jdbcTypeToNetDbType.put(java.sql.Types.LONGVARCHAR, String);
		jdbcTypeToNetDbType.put(java.sql.Types.DATE, Date);
		jdbcTypeToNetDbType.put(java.sql.Types.TIME, Time);
		jdbcTypeToNetDbType.put(java.sql.Types.TIMESTAMP, Binary);
		jdbcTypeToNetDbType.put(java.sql.Types.BINARY, Binary);
		jdbcTypeToNetDbType.put(java.sql.Types.VARBINARY, Binary);
		jdbcTypeToNetDbType.put(java.sql.Types.LONGVARBINARY, Binary);
		jdbcTypeToNetDbType.put(java.sql.Types.NCHAR, StringFixedLength);
		jdbcTypeToNetDbType.put(java.sql.Types.NVARCHAR, String);
		jdbcTypeToNetDbType.put(java.sql.Types.LONGNVARCHAR, String);
		jdbcTypeToNetDbType.put(java.sql.Types.SQLXML, Xml);
		// 10001 ---> uniqueidentifier
		jdbcTypeToNetDbType.put(10001, Guid);
		// jdbcTypeToNetDbType.put(-150, Object);

		netDbTypeToNetType.put(AnsiString, "string");
		netDbTypeToNetType.put(Binary, "byte[]");
		netDbTypeToNetType.put(Byte, "byte");
		netDbTypeToNetType.put(Boolean, "bool");
		// netDbTypeToNetType.put(Currency, "decimal");
		netDbTypeToNetType.put(Date, "DateTime");
		netDbTypeToNetType.put(DateTime, "DateTime");
		netDbTypeToNetType.put(Decimal, "decimal");
		netDbTypeToNetType.put(Double, "double");
		netDbTypeToNetType.put(Guid, "Guid");
		netDbTypeToNetType.put(Int16, "short");
		netDbTypeToNetType.put(Int32, "int");
		netDbTypeToNetType.put(Int64, "long");
		netDbTypeToNetType.put(Object, "object");
		netDbTypeToNetType.put(SByte, "sbyte");
		netDbTypeToNetType.put(Single, "float");
		netDbTypeToNetType.put(String, "string");
		netDbTypeToNetType.put(Time, "TimeSpan");
		netDbTypeToNetType.put(UInt16, "ushort");
		netDbTypeToNetType.put(UInt32, "uint");
		netDbTypeToNetType.put(UInt64, "ulong");
		netDbTypeToNetType.put(VarNumeric, "decimal");
		netDbTypeToNetType.put(AnsiStringFixedLength, "string");
		netDbTypeToNetType.put(StringFixedLength, "string");
		netDbTypeToNetType.put(Xml, "Xml");
		netDbTypeToNetType.put(DateTime2, "DateTime");
		netDbTypeToNetType.put(DateTimeOffset, "DateTimeOffset");
	}

	DbType(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}

	public static DbType fromInt(int symbol) {
		return intToEnum.get(symbol);
	}

	public static DbType getDbTypeFromJdbcType(Integer i) {
		return jdbcTypeToNetDbType.get(i);
	}

	public static String getCSharpType(DbType t) {
		return netDbTypeToNetType.get(t);
	}

}
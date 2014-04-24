package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class DefaultLoader extends Loader{

	private static Map<Class<?>, Integer> ms_sql_server = null;
	private static Map<Class<?>, Integer> my_sql = null;
	
	private DBType dbtype = DBType.mysql;
	
	//ref:http://docs.oracle.com/cd/E19159-01/819-3672/gbxjk/index.html
	static{
		ms_sql_server = new HashMap<Class<?>, Integer>();
		ms_sql_server.put(boolean.class, Types.BIT);
		ms_sql_server.put(java.lang.Boolean.class, Types.BIT);
		ms_sql_server.put(int.class, Types.INTEGER);
		ms_sql_server.put(java.lang.Integer.class, Types.INTEGER);
		ms_sql_server.put(long.class, Types.NUMERIC);
		ms_sql_server.put(java.lang.Long.class, Types.NUMERIC);
		ms_sql_server.put(float.class, Types.FLOAT);
		ms_sql_server.put(java.lang.Float.class, Types.FLOAT);
		ms_sql_server.put(double.class, Types.FLOAT);
		ms_sql_server.put(java.lang.Double.class, Types.FLOAT);
		ms_sql_server.put(short.class, Types.SMALLINT);
		ms_sql_server.put(java.lang.Short.class, Types.SMALLINT);
		ms_sql_server.put(byte.class, Types.SMALLINT);
		ms_sql_server.put(java.lang.Byte.class, Types.SMALLINT);
		ms_sql_server.put(java.lang.Number.class, Types.NUMERIC);
		ms_sql_server.put(java.math.BigInteger.class, Types.NUMERIC);
		ms_sql_server.put(java.math.BigDecimal.class, Types.NUMERIC);
		ms_sql_server.put(java.lang.String.class, Types.VARCHAR);
		ms_sql_server.put(char.class, Types.CHAR);
		ms_sql_server.put(java.lang.Character.class, Types.CHAR);
		ms_sql_server.put(byte[].class, Types.BINARY);
		ms_sql_server.put(java.lang.Byte[].class, Types.BINARY);
		ms_sql_server.put(java.sql.Blob.class, Types.BINARY);
		ms_sql_server.put(char[].class, Types.VARCHAR);
		ms_sql_server.put(java.lang.Character[].class, Types.VARCHAR);
		ms_sql_server.put(java.sql.Clob.class, Types.VARCHAR);
		ms_sql_server.put(java.sql.Date.class, Types.TIMESTAMP);
		ms_sql_server.put(java.sql.Time.class, Types.TIMESTAMP);
		ms_sql_server.put(java.sql.Timestamp.class, Types.TIMESTAMP);
		
		
		my_sql = new HashMap<Class<?>, Integer>();
		my_sql.put(boolean.class, Types.TINYINT);
		my_sql.put(java.lang.Boolean.class, Types.TINYINT);
		my_sql.put(int.class, Types.INTEGER);
		my_sql.put(java.lang.Integer.class, Types.INTEGER);
		my_sql.put(long.class, Types.BIGINT);
		my_sql.put(java.lang.Long.class, Types.BIGINT);
		my_sql.put(float.class, Types.FLOAT);
		my_sql.put(java.lang.Float.class, Types.FLOAT);
		my_sql.put(double.class, Types.DOUBLE);
		my_sql.put(java.lang.Double.class, Types.DOUBLE);
		my_sql.put(short.class, Types.SMALLINT);
		my_sql.put(java.lang.Short.class, Types.SMALLINT);
		my_sql.put(byte.class, Types.SMALLINT);
		my_sql.put(java.lang.Byte.class, Types.SMALLINT);
		my_sql.put(java.lang.Number.class, Types.DECIMAL);
		my_sql.put(java.math.BigInteger.class, Types.BIGINT);
		my_sql.put(java.math.BigDecimal.class, Types.DECIMAL);
		my_sql.put(java.lang.String.class, Types.VARCHAR);
		my_sql.put(char.class, Types.CHAR);
		my_sql.put(java.lang.Character.class, Types.CHAR);
		my_sql.put(byte[].class, Types.BLOB);
		my_sql.put(java.lang.Byte[].class, Types.BLOB);
		my_sql.put(java.sql.Blob.class, Types.BLOB);
		my_sql.put(char[].class, Types.VARCHAR);
		my_sql.put(java.lang.Character[].class, Types.VARCHAR);
		my_sql.put(java.sql.Clob.class, Types.VARCHAR);
		my_sql.put(java.sql.Date.class, Types.DATE);
		my_sql.put(java.sql.Time.class, Types.TIME);
		my_sql.put(java.sql.Timestamp.class, Types.TIMESTAMP);
	}
	
	public DefaultLoader(){ }
	
	public DefaultLoader(DBType type){ 
		this.dbtype = type;
	}
	
	@Override
	public Object load(Field field, Object value)
			throws ReflectiveOperationException {
		return field.get(value);
	}

	@Override
	public Object save(Field field, Object entity)
			throws ReflectiveOperationException {
		
		return this.load(field, entity);
	}

	@Override
	public int getSqlType(Class<?> javaType) {
		if(this.dbtype == DBType.sqlserver){
			return ms_sql_server.get(javaType);
		}
		return my_sql.get(javaType);
	}

}

package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.DalRowMapper;

/**
 * In case you need get Short cloumn value from Db, use ShortRowMapper 
 * instead of this, because the default getObject will return Interger instead of Short for such column.
 * 
 * @author jhhe
 *
 * @param <T>
 */
public class DalObjectRowMapper<T> implements DalRowMapper<T> {
	private static final Map<Class<?>, TypeConverter<?>> typeConverterCache = new ConcurrentHashMap<>();
	
	private static TypeConverter<?> sameTypeConverter = new TypeConverter<Object>() {
		public Object convert(Object value) {
			return value;
		}
	};

	private Class<T> type;
	private TypeConverter<T> converter;
	
	public DalObjectRowMapper() {
	}
	
	public DalObjectRowMapper(Class<T> type) {
		this.type = type;
		converter = (TypeConverter<T>)typeConverterCache.get(type);
	}

	@SuppressWarnings("unchecked")
	public T map(ResultSet rs, int rowNum) throws SQLException {
		Object value = rs.getObject(1);
		
		if(value == null || converter == null || type.isInstance(value))
			return (T)value;

		return converter.convert(value);
	}
	
	
	private static interface TypeConverter<T> {
		T convert(Object value);
	}
	
	static {
		typeConverterCache.put(Short.class, new TypeConverter<Short>(){ public Short convert(Object value) {
			return ((Number)value).shortValue();}});

		typeConverterCache.put(Byte.class, new TypeConverter<Byte>(){ public Byte convert(Object value) {
			return ((Number)value).byteValue();}});

		typeConverterCache.put(Integer.class, new TypeConverter<Integer>(){ public Integer convert(Object value) {
			return ((Number)value).intValue();}});

		typeConverterCache.put(Long.class, new TypeConverter<Long>(){ public Long convert(Object value) {
			return ((Number)value).longValue();}});

		typeConverterCache.put(Float.class, new TypeConverter<Float>(){ public Float convert(Object value) {
			return ((Number)value).floatValue();}});

		typeConverterCache.put(Double.class, new TypeConverter<Double>(){ public Double convert(Object value) {
			return ((Number)value).doubleValue();}});
	}
}
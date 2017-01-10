package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalDefaultJpaMapper<T> implements DalRowMapper<T>, SupportPartialResultMapping<T> {
	
	private Class<T> clazz = null;
	private String[] columnNames = null;
	private Map<String, Field> fieldsMap = null;
	private boolean ignorMissingFields = false;
	
	public DalDefaultJpaMapper(Class<T> clazz) throws SQLException {
		this.clazz = clazz;
		EntityManager manager = EntityManager.getEntityManager(clazz);
		this.columnNames = manager.getColumnNames();
		this.fieldsMap = manager.getFieldMap();
	}
	
	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		try {
			T instance = this.clazz.newInstance();
			for (int i = 0; i < columnNames.length; i++) {
				Field field = fieldsMap.get(columnNames[i]);
				if(field == null)
					if(ignorMissingFields)
						continue;
					else
						throw new DalException(ErrorCode.FieldNotExists, clazz.getName(), columnNames[i]);
				setValue(field, instance, rs.getObject(columnNames[i]));
			}
			return instance;
		} catch (Throwable e) {
			throw DalException.wrap(e);
		}
	}

	private void setValue(Field field, Object entity, Object val)
			throws ReflectiveOperationException {
		if (val == null) {
			field.set(entity, val);
			return;
		}
		// The following order is optimized for most cases 
		if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
			field.set(entity, ((Number) val).longValue());
			return;
		}
		if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
			field.set(entity, ((Number) val).intValue());
			return;
		}
		if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
			field.set(entity, ((Number) val).doubleValue());
			return;
		}
		if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
			field.set(entity, ((Number) val).floatValue());
			return;
		}
		if (field.getType().equals(Byte.class) || field.getType().equals(byte.class)) {
			field.set(entity, ((Number) val).byteValue());
			return;
		}
		if (field.getType().equals(Short.class) || field.getType().equals(short.class)) {
			field.set(entity, ((Number) val).shortValue());
			return;
		}
		field.set(entity, val);
	}
	
	@Override
	public DalRowMapper<T> mapWith(String[] selectedColumns, boolean ignorMissingFields)
			throws SQLException {
		return new DalDefaultJpaMapper<T>(this, selectedColumns, ignorMissingFields);
	}
	
	/**
	 * For map partial result set with given column names.
	 * Copy fields from rawMapper
	 * 
	 * @param rawMapper
	 * @param clazz
	 * @throws SQLException
	 */
	private DalDefaultJpaMapper(DalDefaultJpaMapper<T> rawMapper, String[] columnNames, boolean ignorMissingFields) throws SQLException {
		this.clazz = rawMapper.clazz;
		this.columnNames = columnNames;
		this.fieldsMap = rawMapper.fieldsMap;
		this.ignorMissingFields = ignorMissingFields;
	}	
}

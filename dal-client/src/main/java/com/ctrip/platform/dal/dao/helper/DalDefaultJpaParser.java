package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;

/**
 * 
 * If Use this parser, the JPA Entity should flow the rules
 * 	1.The entity must contain non-parameters constructor.
 *  2.Each field of the entity must declare the SqlType annotation.
 */
public class DalDefaultJpaParser<T> extends AbstractDalParser<T> implements CustomizableMapper<T> {
	
	private Map<String, Field> fieldsMap;
	public Class<T> clazz;
	private Field identity;
	private boolean autoIncrement;
	private DalDefaultJpaMapper<T> rowMapper;
	
	public DalDefaultJpaParser(Class<T> clazz) throws SQLException {
		EntityManager manager = EntityManager.getEntityManager(clazz);
		this.dataBaseName = manager.getDatabaseName();
		this.tableName = manager.getTableName();
		this.columns = manager.getColumnNames();
		this.primaryKeyColumns = manager.getPrimaryKeyNames();
		this.columnTypes = manager.getColumnTypes();
		this.clazz = clazz;
		this.autoIncrement = manager.isAutoIncrement();
		this.fieldsMap = manager.getFieldMap();
		Field[] identities = manager.getIdentity();
		this.identity = identities != null && identities.length == 1 ? identities[0] : null;
		this.rowMapper = new DalDefaultJpaMapper<T>(clazz);
		this.sensitiveColumnNames = manager.getSensitiveColumnNames();
		this.versionColumn = manager.getVersionColumn();
		this.updatableColumnNames = manager.getUpdatableColumnNames();
		this.insertableColumnNames = manager.getInsertableColumnNames();
	}
	
	/**
	 * To allow config DB name
	 * @param clazz
	 * @param dataBaseName
	 * @throws SQLException
	 */
	public DalDefaultJpaParser(Class<T> clazz, String dataBaseName) throws SQLException {
		this(clazz);
		this.dataBaseName = dataBaseName;
	}
	
	/**
	 * To allow config DB and table name
	 * @param clazz
	 * @param dataBaseName
	 * @param tableName
	 * @throws SQLException
	 */
	public DalDefaultJpaParser(Class<T> clazz, String dataBaseName, String tableName) throws SQLException {
		this(clazz);
		this.dataBaseName = dataBaseName;
		this.tableName = tableName;
	}
	
	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		return rowMapper.map(rs, rowNum);
	}

	@Override
	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	@Override
	public Number getIdentityValue(T pojo) {
		if (pojo.getClass().equals(this.clazz) && identity != null) {
			try {
				Object val = identity.get(pojo);
				if (val instanceof Number)
					return (Number) val;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public Map<String, ?> getPrimaryKeys(T pojo) {
		return getFields(getPrimaryKeyNames(), pojo);
	}

	@Override
	public Map<String, ?> getFields(T pojo) {
		return getFields(getColumnNames(), pojo);
	}
	
	private Map<String, ?> getFields(String[] columnNames, T pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (String columnName: columnNames) {
			try {
				map.put(columnName, fieldsMap.get(columnName).get(pojo));
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return map;
	}

	@Override
	public DalRowMapper<T> mapWith(ResultSet rs, DalHints hints)
			throws SQLException {
		return rowMapper.mapWith(rs, hints);
	}

    @Override
    public DalRowMapper<T> mapWith(String[] columns) throws SQLException {
        return rowMapper.mapWith(columns);
    }
}

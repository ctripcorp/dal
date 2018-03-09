package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * 
 * @author gzxia
 * 
 */
public class EntityManager {
	private static ConcurrentHashMap<Class<?>, EntityManager> registeredManager = new ConcurrentHashMap<>();

	private Class<?> clazz; 
	private Map<String, Field> fieldMap = new HashMap<>();
	private List<Integer> types = new ArrayList<>();
	private boolean autoIncremental = false;
	private List<String> columnNameList = new ArrayList<>();
	private List<String> sensitiveColumnNameList = new ArrayList<>();
	private List<String> insertableColumnList = new ArrayList<>();
	private List<String> updatableColumnList = new ArrayList<>();
	private List<String> primaryKeyNameList = new ArrayList<String>();
	private List<Field> identityList = new ArrayList<>();
	private String versionColumn = null;
	
	public static <T> EntityManager getEntityManager(Class<T> clazz) throws SQLException {
		if(registeredManager.containsKey(clazz))
			return registeredManager.get(clazz);
		
		EntityManager manager = new EntityManager(clazz);
		EntityManager value = registeredManager.putIfAbsent(clazz, manager);
		return value == null ? manager : value;
	}
	
	public static <T> DalRowMapper<T> getMapper(Class<T> clazz) throws SQLException {
		return clazz.getAnnotation(Entity.class) == null ? new DalObjectRowMapper<>(clazz) : new DalDefaultJpaMapper<T>(clazz);
	}	
	
	private <T> EntityManager(Class<T> clazz) throws SQLException {
		this.clazz = clazz;
		Field[] allFields = clazz.getDeclaredFields();
		if (null == allFields || allFields.length == 0)
			throw new SQLException("The entity[" + clazz.getName() +"] has no fields.");

		
		for (Field f: allFields) {
			Column column = f.getAnnotation(Column.class);
			Id id =  f.getAnnotation(Id.class);
			if (column == null && id == null)
				continue;

			String columnName = (column == null || column.name().trim().length() == 0)? f.getName() : column.name();
			
			if(f.getAnnotation(Type.class) == null)
				throw new DalException(ErrorCode.TypeNotDefined);
			
			if(fieldMap.containsKey(columnName))
				throw new DalException(ErrorCode.DuplicateColumnName);
				
			f.setAccessible(true);
			fieldMap.put(columnName, f);
			
			columnNameList.add(columnName);
			types.add(f.getAnnotation(Type.class).value());
			
			if(column == null || column.updatable())
				updatableColumnList.add(columnName);
			
			if(column == null || column.insertable())
				insertableColumnList.add(columnName);
			
			if(id != null)
				primaryKeyNameList.add(columnName);
			
			GeneratedValue generatedValue = f.getAnnotation(GeneratedValue.class);
			if (!autoIncremental && null != generatedValue && (generatedValue.strategy() == GenerationType.AUTO 
					|| generatedValue.strategy() == GenerationType.IDENTITY))
				autoIncremental = true;

			if (f.getAnnotation(Id.class) != null && generatedValue != null && generatedValue.strategy() == GenerationType.AUTO) {
				identityList.add(f);
			}
			
			if (f.getAnnotation(Sensitive.class) != null)
				sensitiveColumnNameList.add(columnName);
			
			if (f.getAnnotation(Version.class) != null) {
				if(versionColumn != null)
					throw new DalException(ErrorCode.MoreThanOneVersionColumn);
				versionColumn = columnName;
			}
		}
	}
	
	public String getDatabaseName() throws DalException {
		Database db = clazz.getAnnotation(Database.class);
		if (db != null && db.name() != null)
			return db.name();
		throw new DalException(ErrorCode.NoDatabaseDefined);
	}
	
	public <T> String getTableName() {
		Table table = clazz.getAnnotation(Table.class);
		if (table != null && table.name() != null)
			return table.name();
		Entity entity = clazz.getAnnotation(Entity.class);
		if ( entity != null && (!entity.name().isEmpty()) )
			return entity.name();
		return clazz.getSimpleName();
	}
	
	public boolean isAutoIncrement() throws SQLException {
		return autoIncremental;
	}
	
	public String[] getSensitiveColumnNames() {
		return sensitiveColumnNameList.toArray(new String[sensitiveColumnNameList.size()]);
	}
	
	public String getVersionColumn() throws DalException {
		return versionColumn;
	}
	
	public String[] getUpdatableColumnNames() {
		return updatableColumnList.toArray(new String[updatableColumnList.size()]);
	}
	
	public String[] getInsertableColumnNames() {
		return insertableColumnList.toArray(new String[insertableColumnList.size()]);
	}
	
	public String[] getPrimaryKeyNames() throws SQLException {
		return primaryKeyNameList.toArray(new String[primaryKeyNameList.size()]);
	}
	
	public Field[] getIdentity() {
		return identityList.toArray(new Field[identityList.size()]);
	}
	
	public Map<String, Field> getFieldMap() throws SQLException {
		return fieldMap;
	}
	
	public String[] getColumnNames() {
		return columnNameList.toArray(new String[columnNameList.size()]);
	}
	
	public int[] getColumnTypes() throws SQLException {
		int[] columnTypes = new int[types.size()];
		
		for(int i = 0; i < types.size(); i++)
			columnTypes[i] = types.get(i);

		return columnTypes;
	}	
}

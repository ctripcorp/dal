package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

/**
 * 
 * @author gzxia
 * 
 */
public class EntityManager<T> {

	private Class<T> clazz = null;
	private List<Field> fields = null;
	
	public EntityManager(Class<T> clazz) throws SQLException {
		this.clazz = clazz;
		this.fields = getFields(clazz);
	}
	
	private List<Field> getFields(Class<T> clazz) throws SQLException {
		Field[] allFields = clazz.getDeclaredFields();
		if (null == allFields || allFields.length == 0)
			throw new SQLException("The entity[" + clazz.getName() +"] has no fields.");
				
		List<Field> fields = new ArrayList<>();
		for (Field f: allFields) {
			if (f.getAnnotation(Type.class) == null) {
				continue;
//				throw new SQLException("Each field of entity[" + clazz.getName() +"] must declare it's Type annotation.");
			}
			fields.add(f);
			f.setAccessible(true);
		}

		return fields;
	}
	
	public String getDatabaseName() {
		Database db = clazz.getAnnotation(Database.class);
		if (db != null && db.name() != null)
			return db.name();
		throw new RuntimeException("The entity must configure Database annotation.");
	}
	
	public String getTableName() {
		Table table = clazz.getAnnotation(Table.class);
		if (table != null && table.name() != null)
			return table.name();
		Entity entity = clazz.getAnnotation(Entity.class);
		if ( entity != null && (!entity.name().isEmpty()) )
			return entity.name();
		return clazz.getSimpleName();
	}
	
	public boolean isAutoIncrement() throws SQLException {
		for (Field field : fields) {
			GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
			if (null != generatedValue && (generatedValue.strategy() == GenerationType.AUTO 
					|| generatedValue.strategy() == GenerationType.IDENTITY)) {
				return true;
			}
		}
		return false;
	}
	
	public String[] getSensitiveColumnNames() {
		List<String> sensitiveColumnNames = new ArrayList<String>();
		for (Field field: fields) {
			Sensitive sensitive = field.getAnnotation(Sensitive.class);
			if (sensitive != null)
				sensitiveColumnNames.add(getColumnName(field));
		}
		String[] scns = new String[sensitiveColumnNames.size()];
		sensitiveColumnNames.toArray(scns);
		return scns;
	}
	
	public String[] getPrimaryKeyNames() throws SQLException {
		List<String> primaryKeyNames = new ArrayList<String>();
		for (Field field: fields) {
			Id id = field.getAnnotation(Id.class);
			if(id != null)
				primaryKeyNames.add(getColumnName(field));
		}
		String[] primaryKeys = new String[primaryKeyNames.size()];
		primaryKeyNames.toArray(primaryKeys);
		return primaryKeys;
	}
	
	public Field[] getIdentity() {
		List<Field> identities = new ArrayList<Field>();
		for (Field field: fields) {
			Id id = field.getAnnotation(Id.class);
			GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
			if (id != null && generatedValue != null && generatedValue.strategy() == GenerationType.AUTO) {
				identities.add(field);
			}
		}
		Field[] result = new Field[identities.size()];
		identities.toArray(result);
		return result;
	}
	
	public Map<String, Field> getFieldMap() throws SQLException {
		Map<String, Field> fieldsMap = new HashMap<String, Field>();
		for (Field field: fields) {
			String columnName = getColumnName(field);
			if(!fieldsMap.containsKey(columnName))
				fieldsMap.put(columnName, field);
		}
		return fieldsMap;
	}
	
	public String[] getColumnNames() {
		String[] columnNames = new String[fields.size()];
		int i = 0;
		for (Field field: fields) {
			columnNames[i++] = getColumnName(field);
		}
		return columnNames;
	}
	
	public int[] getColumnTypes() throws SQLException {
		int[] columnTypes = new int[fields.size()];
		int i = 0;
		for (Field field: fields) {
			Type sqlType = field.getAnnotation(Type.class);
			columnTypes[i++] = sqlType.value();
		}
		return columnTypes;
	}
	
	private String getColumnName(Field field) {
		Column column = field.getAnnotation(Column.class);
		if (null != column && column.name() != null)
			return column.name().isEmpty() ? field.getName() : column.name();
		return field.getName();
	}
}

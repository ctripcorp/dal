package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * @modified yn.wang
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
		if (registeredManager.containsKey(clazz))
			return registeredManager.get(clazz);

		EntityManager manager = new EntityManager(clazz);
		EntityManager value = registeredManager.putIfAbsent(clazz, manager);
		return value == null ? manager : value;
	}

	public static <T> DalRowMapper<T> getMapper(Class<T> clazz) {
		try {
			return clazz.getAnnotation(Entity.class) == null ? new DalObjectRowMapper<>(clazz)
					: new DalDefaultJpaMapper<T>(clazz);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private <T> EntityManager(Class<T> clazz) throws SQLException {
		this.clazz = clazz;
		Class<?> currentClass = clazz;

		Map<String, String> columnNamesMap = new HashMap<>(); // Key:column name;Value:class name
		Set<String> classNameSet = new HashSet<>(); // Used for autoIncrement
		boolean containsFields = false;

		while (currentClass != null) {
			String currentClassName = currentClass.getName();
			Field[] allFields = currentClass.getDeclaredFields();

			// If current class doesn't contain any field,then we continue to try its parent class.
			if (null == allFields || allFields.length == 0) {
				currentClass = currentClass.getSuperclass();
				continue;
			}

			containsFields = true;
			processAllFields(currentClassName, allFields, columnNamesMap, classNameSet);

			currentClass = currentClass.getSuperclass();
		}

		// If child class and its all parent classes(if exist) don't contain any field,then we throw an exception.
		if (!containsFields)
			throw new SQLException("The entity[" + clazz.getName() + "] has no fields.");
	}

	private void processAllFields(String currentClassName, Field[] allFields, Map<String, String> columnNamesMap,
								  Set<String> classNameSet) throws SQLException {

		for (Field field : allFields) {
			Column column = field.getAnnotation(Column.class);
			Id id = field.getAnnotation(Id.class);
			if (column == null && id == null)
				continue;

			if (field.getAnnotation(Type.class) == null)
				throw new DalException(ErrorCode.TypeNotDefined);

			String columnName =
					(column == null || column.name().trim().length() == 0) ? field.getName() : column.name();

			String tempClassName = columnNamesMap.get(columnName);
			if (tempClassName != null) {
				if (tempClassName.equals(currentClassName)) {
					throw new DalException(ErrorCode.DuplicateColumnName); // If two fields with same column name are in
					// same class,then we throw an exception.
				} else {
					continue; // If two fields with same column name are distributed in different class,then we abandom
					// current field.
				}
			}

			columnNamesMap.put(columnName, currentClassName);

			processField(columnName, field);

			processUpdatableColumn(columnName, column);
			processInsertableColumn(columnName, column);

			processPrimaryKeyColumn(id, columnName);
			processAutoIncrementColumn(field, currentClassName, classNameSet);

			processSensitiveColumn(columnName, field);
			processVersionColumn(columnName, field, currentClassName, columnNamesMap);
		}
	}

	private void processField(String columnName, Field field) {
		field.setAccessible(true);
		fieldMap.put(columnName, field);

		columnNameList.add(columnName);
		types.add(field.getAnnotation(Type.class).value());
	}

	private void processUpdatableColumn(String columnName, Column column) {
		if (column == null || column.updatable())
			updatableColumnList.add(columnName);
	}

	private void processInsertableColumn(String columnName, Column column) {
		if (column == null || column.insertable())
			insertableColumnList.add(columnName);
	}

	private void processPrimaryKeyColumn(Id id, String columnName) {
		if (id != null)
			primaryKeyNameList.add(columnName);
	}

	private void processAutoIncrementColumn(Field field, String currentClassName, Set<String> classNameSet) {
		GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
		if (!autoIncremental && null != generatedValue && (generatedValue.strategy() == GenerationType.AUTO
				|| generatedValue.strategy() == GenerationType.IDENTITY))
			autoIncremental = true;

		if (field.getAnnotation(Id.class) != null && generatedValue != null
				&& generatedValue.strategy() == GenerationType.AUTO) {
			// first add identity(auto increment)
			if (classNameSet.isEmpty()) {
				classNameSet.add(currentClassName);
				identityList.add(field);
				return;
			}

			if (classNameSet.contains(currentClassName)) {
				identityList.add(field);
			}
		}
	}

	private void processSensitiveColumn(String columnName, Field field) {
		if (isSensitiveField(field))
			sensitiveColumnNameList.add(columnName);
	}

	private boolean isSensitiveField(Field field) {
		return field.getAnnotation(Sensitive.class) != null;
	}

	private void processVersionColumn(String columnName, Field field, String currentClassName,
									  Map<String, String> columnNamesMap) throws SQLException {
		if (isVersionField(field)) {
			if (versionColumn == null) {
				versionColumn = columnName;
				return;
			}

			String versionClassName = columnNamesMap.get(versionColumn);
			if (versionClassName.equals(currentClassName)) {
				throw new DalException(ErrorCode.MoreThanOneVersionColumn); // If one class has more than
				// one Version column,then we throw an
				// exception.
			}
		}
	}

	private boolean isVersionField(Field field) {
		return field.getAnnotation(Version.class) != null;
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
		if (entity != null && (!entity.name().isEmpty()))
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

		for (int i = 0; i < types.size(); i++)
			columnTypes[i] = types.get(i);

		return columnTypes;
	}

}

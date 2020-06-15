package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.ShardingCategory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.UpdatableEntity;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.*;

public class TaskAdapter<T> extends BaseTaskAdapter implements DaoTask<T> {
	public static final String GENERATED_KEY = "GENERATED_KEY";

	//public static final String TMPL_SQL_FIND_BY = "SELECT * FROM %s WHERE %s";

	protected static final String COLUMN_SEPARATOR = ", ";
	protected static final String PLACE_HOLDER = "?";
	protected static final String TMPL_SET_VALUE = "%s=?";
	protected static final String AND = " AND ";
	protected static final String OR = " OR ";
	protected static final String TMPL_CALL = "{call %s(%s)}";
	protected static final String SQLSERVER_TMPL_CALL = "exec %s %s";
	protected static final String SQLSERVER_TMPL_SET_VALUE = "@%s=?";

	public static String findtmp = "SELECT * FROM %s WHERE %s";

	protected DalParser<T> parser;
	protected DatabaseCategory dbCategory;
	protected String pkSql;
	protected Set<String> pkColumns;
	protected Set<String> sensitiveColumns;
	protected Map<String, Integer> columnTypes = new HashMap<String, Integer>();
	
	protected String updateCriteriaTmpl;
	protected String setValueTmpl;
	protected String setVersionValueTmpl;
	protected boolean hasVersion;
	protected boolean isVersionUpdatable;
	protected Set<String> defaultUpdateColumnNames;
	public boolean tableShardingEnabled;
	protected String rawTableName;
	private Map<String, String> taskSettings;

	public void initialize(DalParser<T> parser) {
		super.initialize(parser.getDatabaseName());
		this.parser = parser;
		rawTableName = parser.getTableName();
		tableShardingEnabled = isTableShardingEnabled(logicDbName, rawTableName);
		initShardingCategory();
		initColumnTypes();
		dbCategory = getDatabaseSet(logicDbName).getDatabaseCategory();
		initDbSpecific();
		initSensitiveColumns();
	}

	public void initTaskSettings(Map<String, String> settings){
		taskSettings = settings;
	}

	public String getTaskSetting(String key){
		return taskSettings.get(key);
	}

	public void initDbSpecific() {
		pkSql = initPkSql();
		initUpdateColumns();
		setValueTmpl = dbCategory.getNullableUpdateTpl();
		initVersionColumnUpdateTemplate();
	}
	
	private void initUpdateColumns() {
		defaultUpdateColumnNames = new LinkedHashSet<>(Arrays.asList(parser.getUpdatableColumnNames()));
		
		for (String column : parser.getPrimaryKeyNames()) {
			defaultUpdateColumnNames.remove(column);
		}
		
		hasVersion = parser.getVersionColumn() != null;
		isVersionUpdatable = hasVersion ? defaultUpdateColumnNames.contains(parser.getVersionColumn()) : false;

		// Remove Version from updatable columns
		if(hasVersion)
			defaultUpdateColumnNames.remove(parser.getVersionColumn());
	}
	
	/**
	 * If there is version column and it is updatable, the column can not be null and it will always use the update version template.
	 */
	private void initVersionColumnUpdateTemplate() {
		String versionColumn = parser.getVersionColumn();
		updateCriteriaTmpl = pkSql;

		if(versionColumn == null)
			return;
		
		String quotedVersionColumn = quote(parser.getVersionColumn());
		updateCriteriaTmpl += AND + String.format(TMPL_SET_VALUE, quotedVersionColumn);
		
		if(!isVersionUpdatable)
			return;

		int versionType = getColumnType(versionColumn);

		String valueTmpl = null;
		if(versionType == Types.TIMESTAMP){
			valueTmpl = dbCategory.getTimestampExp();
		}else{
			valueTmpl = quote(parser.getVersionColumn()) + "+1";
		}
		
		setVersionValueTmpl = quotedVersionColumn + "=" + valueTmpl;
	}
	
	public String getTableName(DalHints hints) throws SQLException {
		return getTableName(hints, null, null);
	}
	
	public String getTableName(DalHints hints, StatementParameters parameters) throws SQLException {
		return getTableName(hints, parameters, null);
	}
	
	public String getTableName(DalHints hints, Map<String, ?> fields) throws SQLException {
		return getTableName(hints, null, fields);
	}
	
	public String getTableName(DalHints hints, StatementParameters parameters, Map<String, ?> fields) throws SQLException {
		return quote(getRawTableName(hints, parameters, fields));
	}
	
	public String getRawTableName(DalHints hints) throws SQLException {
		return getRawTableName(hints, null, null);
	}
	
	public String getRawTableName(DalHints hints, StatementParameters parameters) throws SQLException {
		return getRawTableName(hints, parameters, null);
	}
	
	public String getRawTableName(DalHints hints, Map<String, ?> fields) throws SQLException {
		return getRawTableName(hints, null, fields);
	}
	
	public String getRawTableName(DalHints hints, StatementParameters parameters, Map<String, ?> fields) throws SQLException {
		if(tableShardingEnabled == false)
			return rawTableName;
		
		hints.cleanUp();
		return rawTableName + buildShardStr(logicDbName, rawTableName, locateTableShardId(logicDbName, rawTableName, hints, parameters, fields));
	}
	
	/**
	 * Add all the entries into the parameters by index. The parameter index
	 * will depends on the index of the entry in the entry set, value will be
	 * entry value. The value can be null.
	 * 
	 * @param parameters A container that holds all the necessary parameters
	 * @param entries Key value pairs to be added into parameters
	 */
	public void addParameters(StatementParameters parameters,
			Map<String, ?> entries) {
		int index = parameters.size() + 1;
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			addParameter(parameters, index++, entry.getKey(), entry.getValue());
		}
	}

	public void addParameters(StatementParameters parameters,
			Map<String, ?> entries, String[] validColumns) {
		int index = parameters.size() + 1;
		for(String column : validColumns){
			addParameter(parameters, index++, column, entries.get(column));
		}
	}
	
	public int addParameters(int start, StatementParameters parameters,
			Map<String, ?> entries, List<String> validColumns) {
		int count = 0;
		for(String column : validColumns){
			addParameter(parameters, count + start, column, entries.get(column));
			count++;
		}
		return count;
	}
	
	/**
	 * Add all the entries into the parameters by name. The parameter name will
	 * be the entry key, value will be entry value. The value can be null. This
	 * method will be used to set input parameters for stored procedure.
	 * 
	 * @param parameters A container that holds all the necessary parameters
	 * @param entries Key value pairs to be added into parameters
	 */
	public void addParametersByName(StatementParameters parameters,
			Map<String, ?> entries) {
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			addParameter(parameters, entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Add all the entries into the parameters by name. The parameter name will
	 * be the entry key, value will be entry value. The value can be null. This
	 * method will be used to set input parameters for stored procedure.
	 * 
	 * @param parameters A container that holds all the necessary parameters
	 * @param entries Key value pairs to be added into parameters
	 */
	public void addParametersByName(StatementParameters parameters,
			Map<String, ?> entries, String[] validColumns) {
		for(String column : validColumns){
			addParameter(parameters, column, entries.get(column));
		}
	}

	/**
	 * According to DBA, call SP by parameter name will invoke sp_sproc_columns to get SP metadata,  this is a 
	 * very costly operation. To avoid the cost, it is required to call sp by parameter index instead of name
	 */
	public void addParametersByIndex(StatementParameters parameters,
			Map<String, ?> entries) {
		int index = parameters.size() + 1;
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			addParameterByIndex(parameters, index++, entry.getKey(), entry.getValue());
		}
	}

	/**
	 * According to DBA, call SP by parameter name will invoke sp_sproc_columns to get SP metadata,  this is a 
	 * very costly operation. To avoid the cost, it is required to call sp by parameter index instead of name
	 */
	public void addParametersByIndex(StatementParameters parameters,
			Map<String, ?> entries, String[] validColumns) {
		int index = parameters.size() + 1;
		for(String column : validColumns){
			addParameterByIndex(parameters, index++, column, entries.get(column));
		}
	}

	public void addParameterByIndex(StatementParameters parameters, int index, String columnName, Object value) {
		if(isSensitive(columnName))
			parameters.setSensitive(index, getColumnType(columnName), value);
		else
			parameters.set(index, getColumnType(columnName), value);
	}

	public void addParameter(StatementParameters parameters, int index, String columnName, Object value) {
		if(isSensitive(columnName))
			parameters.setSensitive(index, columnName, getColumnType(columnName), value);
		else
			parameters.set(index, columnName, getColumnType(columnName), value);
	}

	private void addParameter(StatementParameters parameters, String columnName, Object value) {
		if(isSensitive(columnName))
			parameters.setSensitive(columnName, getColumnType(columnName), value);
		else
			parameters.set(columnName, getColumnType(columnName), value);
	}

	/**
	 * Get the column type defined in java.sql.Types.
	 * 
	 * @param columnName The column name of the table
	 * @return value defined in java.sql.Types
	 */
	public int getColumnType(String columnName) {
		return columnTypes.get(columnName);
	}

	/**
	 * Remove all the null value in the given map.
	 * 
	 * @param fields
	 * @return the original map reference
	 */
	public Map<String, ?> filterNullFileds(Map<String, ?> fields) {
		for (String columnName : parser.getColumnNames()) {
			if (fields.get(columnName) == null)
				fields.remove(columnName);
		}
		return fields;
	}
	
	public Set<String> getUpdatedColumns(T rawPojo) {
		return rawPojo instanceof UpdatableEntity ?
				((UpdatableEntity)rawPojo).getUpdatedColumns() :
					(Set<String>)Collections.EMPTY_SET;
	}

	public Set<String> filterColumns(DalHints hints) {
		Set<String> qulifiedColumns = new HashSet<>(defaultUpdateColumnNames);
		if(hints.is(DalHintEnum.includedColumns))
			qulifiedColumns.retainAll(hints.getIncluded());
			
		if(hints.is(DalHintEnum.excludedColumns))
			qulifiedColumns.removeAll(hints.getExcluded());
			
		return qulifiedColumns;
	}

	public Map<String, ?> removeAutoIncrementPrimaryFields(Map<String, ?> fields){
		// This is bug here, for My Sql, auto incremental id can be part of the joint primary key.
		// But for Ctrip, a table must have a pk defined by single column as mandatory, so we don't have problem here
		if(parser.isAutoIncrement())
			fields.remove(parser.getPrimaryKeyNames()[0]);
		return fields;
	}
	
	public String buildCallSql(String spName, int paramCount) {
		return String.format(TMPL_CALL, spName,
				combine(PLACE_HOLDER, paramCount, COLUMN_SEPARATOR));
	}
	
	public boolean isEmpty(List<?> daoPojos) {
		return null == daoPojos || daoPojos.size() == 0;
	}
	
	public List<Map<String, ?>> getPojosFields(List<T> daoPojos) {
		List<Map<String, ?>> pojoFields = new LinkedList<Map<String, ?>>();
		if (null == daoPojos || daoPojos.size() < 1)
			return pojoFields;
		
		for (T pojo: daoPojos){
			pojoFields.add(parser.getFields(pojo));
		}
		
		return pojoFields;
	}
	
	public Map<Integer, Map<String, ?>> getPojosFieldsMap(List<T> daoPojos) {
		Map<Integer, Map<String, ?>> daoPojosMaps = new LinkedHashMap<>();
		for(int i = 0; i < daoPojos.size(); i ++) 
			daoPojosMaps.put(i, parser.getFields(daoPojos.get(i)));
		return daoPojosMaps;
	}


	public boolean isPrimaryKey(String fieldName){
		return pkColumns.contains(fieldName);
	}
	
	public boolean isSensitive(String fieldName){
		if(sensitiveColumns.isEmpty())
			return false;
		
		return sensitiveColumns.contains(fieldName);
	}
	
	public String initPkSql() {
		pkColumns = new HashSet<String>();
		Collections.addAll(pkColumns, parser.getPrimaryKeyNames());

		// Build primary key template
		String template = combine(TMPL_SET_VALUE, parser.getPrimaryKeyNames().length, AND);

		return String.format(template, (Object[]) quote(parser.getPrimaryKeyNames()));
	}

	public void initSensitiveColumns() {
		sensitiveColumns = new HashSet<String>();
		if(parser.getSensitiveColumnNames() != null)
			Collections.addAll(sensitiveColumns, parser.getSensitiveColumnNames());
	}

	// Build a lookup table
	public void initColumnTypes() {
		String[] cloumnNames = parser.getColumnNames();
		int[] columnsTypes = parser.getColumnTypes();
		for (int i = 0; i < cloumnNames.length; i++) {
			columnTypes.put(cloumnNames[i], columnsTypes[i]);
		}
	}
	
	public Map<String, ?> getPrimaryKeys(Map<String, ?> fields) {
		Map<String, Object> pks = new LinkedHashMap<>();
		for(String pkName: parser.getPrimaryKeyNames())
			pks.put(pkName, fields.get(pkName));
		return pks;
	}


	public String buildWhereClause(Map<String, ?> fields) {
		return String.format(combine(TMPL_SET_VALUE, fields.size(), AND),
				quote(fields.keySet()));
	}

	public String combineColumns(Collection<String> values, String separator) {
		StringBuilder valuesSb = new StringBuilder();
		int i = 0;
		for (String value : values) {
			quote(valuesSb, value);
			if (++i < values.size())
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}

	public String combine(String value, int count, String separator) {
		StringBuilder valuesSb = new StringBuilder();

		for (int i = 1; i <= count; i++) {
			valuesSb.append(value);
			if (i < count)
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}
	
	public String quote(String column) {
		return dbCategory.quote(column);
	}

	public StringBuilder quote(StringBuilder sb, String column) {
		return sb.append(dbCategory.quote(column));
	}
	
	public Object[] quote(Set<String> columns) {
		Object[] quatedColumns = columns.toArray();
		for(int i = 0; i < quatedColumns.length; i++)
			quatedColumns[i] = quote((String)quatedColumns[i]);
		return quatedColumns;
	}
	
	public String[] quote(String[] columns) {
		String[] quatedColumns = new String[columns.length];
		for(int i = 0; i < columns.length; i++)
			quatedColumns[i] = quote(columns[i]);
		return quatedColumns;
	}

	@Override
	public void initShardingCategory(){
		if (shardingEnabled && tableShardingEnabled)
			this.shardingCategory = ShardingCategory.DBTableShard;
		else if (shardingEnabled)
			this.shardingCategory = ShardingCategory.DBShard;
		else if (tableShardingEnabled)
			this.shardingCategory = ShardingCategory.TableShard;
		else
			this.shardingCategory = ShardingCategory.NoShard;
	}

	protected String buildSqlServerCallSql(String spName, String[] columns) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String value : columns) {
			sb.append(String.format(SQLSERVER_TMPL_SET_VALUE, value));
			if (++i < columns.length)
				sb.append(COLUMN_SEPARATOR);
		}

		return String.format(SQLSERVER_TMPL_CALL, spName, sb.toString());
	}

}

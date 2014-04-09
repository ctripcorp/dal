package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Base table DAO wraps common CRUD for particular table.
 * The generated table DAO should use this DAO to perform CRUD.
 * @author jhhe
 */
public final class DalTableDao<T> {
	public static final String GENERATED_KEY = "GENERATED_KEY";

	private static final String TMPL_SQL_FIND_BY = "SELECT * FROM %s WHERE %s";
	private static final String TMPL_SQL_INSERT = "INSERT INTO %s(%s) VALUES(%s)";
	private static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";
	private static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";
	
	private static final String COLUMN_SEPARATOR = ", ";
	private static final String PLACE_HOLDER = "?";
	private static final String TMPL_SET_VALUE = "%s=?";
	private static final String AND = " AND ";
	private static final String OR = " OR ";
	private static final String TMPL_CALL = "{call %s(%s)}";
	
	private DalClient client;
	private DalQueryDao queryDao;
	private DalParser<T> parser;

	private final String pkSql;
	private Set<String> pkColumns;
	private Map<String, Integer> columnTypes = new HashMap<String, Integer>();

	public DalTableDao(DalParser<T> parser) {
		this.client = DalClientFactory.getClient(parser.getDatabaseName());
		this.parser = parser;
		queryDao = new DalQueryDao(parser.getDatabaseName());
		initColumnTypes();
		pkSql = initSql();
	}
	
	public T queryByPk(Number id, DalHints hints)
			throws SQLException {
		if(parser.getPrimaryKeyNames().length != 1)
			throw new SQLException("The primary key of this table is consists of more than one column");

		StatementParameters parameters = new StatementParameters();
		parameters.set(1, getColumnType(parser.getPrimaryKeyNames()[0]), id);
		
		String selectSql = String.format(TMPL_SQL_FIND_BY, parser.getTableName(), pkSql);

		return queryDao.queryForObject(selectSql, parameters, hints, parser);
	}

	public T queryByPk(T pk, DalHints hints)
			throws SQLException {
		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, parser.getPrimaryKeys(pk));
		
		String selectSql = String.format(TMPL_SQL_FIND_BY, parser.getTableName(), pkSql);

		return queryDao.queryForObject(selectSql, parameters, hints, parser);
	}
	
	public List<T> queryLike(T sample, DalHints hints)
			throws SQLException {
		StatementParameters parameters = new StatementParameters();
		Map<String, ?> queryCriteria = filterNullFileds(parser.getFields(sample));
		addParameters(parameters, queryCriteria);
		String whereClause = buildWhereClause(queryCriteria);

		return query(whereClause, parameters, hints);
	}
	
	public List<T> query(String whereClause, StatementParameters parameters, DalHints hints)
			throws SQLException {
		String selectSql = String.format(TMPL_SQL_FIND_BY, parser.getTableName(), whereClause);
		return queryDao.query(selectSql, parameters, hints, parser);
	}
	
	public T queryFirst(String whereClause, StatementParameters parameters, DalHints hints)
			throws SQLException {
		String selectSql = String.format(TMPL_SQL_FIND_BY, parser.getTableName(), whereClause);
		return queryDao.queryFirst(selectSql, parameters, hints, parser);
	}

	public List<T> queryTop(String whereClause, StatementParameters parameters, DalHints hints, int count)
			throws SQLException {
		String selectSql = String.format(TMPL_SQL_FIND_BY, parser.getTableName(), whereClause);
		return queryDao.queryTop(selectSql, parameters, hints, parser, count);
	}
	
	public List<T> queryFrom(String whereClause, StatementParameters parameters, DalHints hints, int start, int count)
			throws SQLException {
		String selectSql = String.format(TMPL_SQL_FIND_BY, parser.getTableName(), whereClause);
		return queryDao.queryFrom(selectSql, parameters, hints, parser, start, count);
	}
	
	/**
	 * Insert pojos one by one.
	 * If you want to inert them in the batch mode, user batchInsert instead.
	 * @param hints additional parameters. DalHintEnum.continueOnError can be used to 
	 * indicate that the inserting can be go on if there is any failure.
	 * @param daoPojos list of pojos to be inserted
	 * @return
	 */
	public int insert(DalHints hints, T...daoPojos) throws SQLException {
		return insert(hints, null, daoPojos);
	}

	/**
	 * Insert pojos and get the generated PK back in keyHolder.
	 * Because certain JDBC driver may not support such feature, like MS JDBC driver, 
	 * make sure the local test is performed before use this API.
	 * @param hints additional parameters. DalHintEnum.continueOnError can be used to 
	 * indicate that the inserting can be go on if there is any failure.
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return
	 * @throws SQLException
	 */
	public int insert(DalHints hints, KeyHolder keyHolder, T...daoPojos) throws SQLException {
		int count = 0;
		// Try to insert one by one
		for(T pojo: daoPojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			// TODO revise and improve performance
			String insertSql = buildInsertSql(fields);

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			
			try {
				if(keyHolder == null)
					count += client.update(insertSql, parameters, hints);
				else
					count += client.update(insertSql, parameters, hints, keyHolder);
			} catch (SQLException e) {
				if(hints.isStopOnError())
					throw e;
			}
		}
		return count;
	}
	
	/**
	 * Insert pojos in batch mode.
	 * @param hints
	 * @param daoPojos
	 * @return
	 * @throws SQLException
	 */
	public int[] batchInsert(DalHints hints, T...daoPojos) throws SQLException {
		String insertSql = buildBatchInsertSql();
		
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		int i = 0;
		for(T pojo: daoPojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			parametersList[i++] = parameters;
		}

		return client.batchUpdate(insertSql, parametersList, hints);
	}
	
	public int delete(DalHints hints, T...daoPojos) throws SQLException {
		int count = 0;
		for(T pojo: daoPojos) {
			String deleteSql = buildDeleteSql(pojo);

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, parser.getPrimaryKeys(pojo));
			
			try {
				count += client.update(deleteSql, parameters, hints);
			} catch (SQLException e) {
				if(hints.isStopOnError())
					throw e;
			}
		}
		return count;
	}
	
	public int update(DalHints hints, T...daoPojos) throws SQLException {
		int count = 0;
		for(T pojo: daoPojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			Map<String, ?> pk = parser.getPrimaryKeys(pojo);
			
			String updateSql = buildUpdateSql(fields);
			
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			addParameters(parameters, pk);
			
			try {
				count += client.update(updateSql, parameters, hints);
			} catch (SQLException e) {
				if(hints.isStopOnError())
					throw e;
			}
		}
		return count;
	}
	
	public int delete(String whereClause, StatementParameters parameters, DalHints hints) throws SQLException {
		return client.update(String.format(TMPL_SQL_DELETE, parser.getTableName(), whereClause), parameters, hints);
	}
	
	public int update(String sql, StatementParameters parameters, DalHints hints) throws SQLException {
		return client.update(sql, parameters, hints);
	}
	
	public void addParameters(StatementParameters parameters, Map<String, ?> entries) {
		int index = parameters.size() + 1;
		for(Map.Entry<String, ?> entry: entries.entrySet()) {
			parameters.set(index++, getColumnType(entry.getKey()), entry.getValue());
		}
	}
	
	public void addParametersByName(StatementParameters parameters, Map<String, ?> entries) {
		for(Map.Entry<String, ?> entry: entries.entrySet()) {
			parameters.set(entry.getKey(), getColumnType(entry.getKey()), entry.getValue());
		}
	}
	
	public int getColumnType(String columnName) {
		return columnTypes.get(columnName);
	}
	
	public Map<String, ?> filterNullFileds(Map<String, ?> fields) {
		for(String columnName: parser.getColumnNames()) {
			if(fields.get(columnName) == null)
				fields.remove(columnName);
		}
		return fields;
	}
	
	public String buildCallSql(String spName, int paramCount) {
		return String.format(TMPL_CALL, spName, combine(PLACE_HOLDER, paramCount, COLUMN_SEPARATOR));
	}
	
	private String initSql() {
		pkColumns = new HashSet<String>();
		Collections.addAll(pkColumns, parser.getPrimaryKeyNames());
		
		// Build primary key template
 		String template = parser.isAutoIncrement() ? 
				TMPL_SET_VALUE :
				combine(TMPL_SET_VALUE, parser.getPrimaryKeyNames().length, AND);
		
		return String.format(template, (Object[])parser.getPrimaryKeyNames());
	}

	// Build a lookup table
	private void initColumnTypes() {
		String[] cloumnNames = parser.getColumnNames();
		int[] columnsTypes = parser.getColumnTypes();
		for (int i = 0; i < cloumnNames.length; i++) {
			columnTypes.put(cloumnNames[i], columnsTypes[i]);
		}
	}
	
	private String buildInsertSql(Map<String, ?> fields) {
		filterNullFileds(fields);
		Set<String> remainedColumns = fields.keySet();
		String cloumns = combine(remainedColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, remainedColumns.size(), COLUMN_SEPARATOR);
		
		return String.format(TMPL_SQL_INSERT, parser.getTableName(), cloumns, values);
	}
	
	private String buildBatchInsertSql() {
		String[] allColumns = parser.getColumnNames();
		String cloumns = combine(allColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, allColumns.length, COLUMN_SEPARATOR);
		
		return String.format(TMPL_SQL_INSERT, parser.getTableName(), cloumns, values);
	}
	
	private String buildDeleteSql(T pojo) {
		return String.format(TMPL_SQL_DELETE, parser.getTableName(), pkSql);
	}

	private String buildUpdateSql(Map<String, ?> fields) {
		// Remove null value or primary key
		for(String column: parser.getColumnNames()) {
			if(fields.get(column) == null || pkColumns.contains(column))
				fields.remove(column);
		}
		
		String columns = String.format(combine(TMPL_SET_VALUE, fields.size(), COLUMN_SEPARATOR), fields.keySet().toArray());
		
		return String.format(TMPL_SQL_UPDATE, parser.getTableName(), columns, pkSql);
	}
	
	private String buildWhereClause(Map<String, ?> fields) {
		return String.format(combine(TMPL_SET_VALUE, fields.size(), AND), fields.keySet().toArray());
	}

	private String combine(String[] values, String separator) {
		StringBuilder valuesSb = new StringBuilder();
		int i = 0;
		for(String value: values) {
			valuesSb.append(value);
			if(++i < values.length)
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}
	
	private String combine(Collection<String> values, String separator) {
		return combine(values.toArray(new String[values.size()]), separator);
	}

	private String combine(String value, int count, String separator) {
		StringBuilder valuesSb = new StringBuilder();

		for(int i = 1; i <= count; i++) {
			valuesSb.append(value);
			if(i < count)
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}
}

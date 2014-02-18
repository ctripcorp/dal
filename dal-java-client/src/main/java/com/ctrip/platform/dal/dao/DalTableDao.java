package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @author jhhe
 */
public final class DalTableDao<T> {
	public static final String TMPL_SQL_FIND_BY_PK = "SELECT %s FROM %s WHERE %s";
	public static final String TMPL_SQL_INSERT = "INSERT INTO %s(%s) VALUES(%s)";
	public static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";
	public static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";
	
	private static final String COLUMN_SEPARATOR = ", ";
	private static final String PLACE_HOLDER = "?";
	private static final String TMPL_SET_VALUE = "%s=?";
	private static final String AND = "AND";
	private static final String OR = "OR";
	
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
		pkSql = initSql();
	}
	
	public T queryByPk(Number id, DalHints hints)
			throws SQLException {
//		queryDao.q
		return null;
	}

	public T queryByPk(T pk, DalHints hints)
			throws SQLException {
//		queryDao.q
		return null;
	}
	
	public List<T> queryLike(T pk, DalHints hints)
			throws SQLException {
		return null;
	}
	
	public List<T> query(String whereClause, StatementParameters parameters, DalHints hints)
			throws SQLException {
		return null;
	}
	
	public T queryFirst(String whereClause, StatementParameters parameters, DalHints hints)
			throws SQLException {
		return null;
	}

	public T queryTop(String whereClause, StatementParameters parameters, DalHints hints, int count)
			throws SQLException {
		return null;
	}
	
	public T queryFrom(String whereClause, StatementParameters parameters, DalHints hints, int start, int count)
			throws SQLException {
		return null;
	}
	
	/**
	 * TODO Support auto incremental id. 
	 * TODO add hints to set batch or non batch
	 * The generated id if any will be set into the pojos
	 * @param pojo
	 */
	public void insert(DalHints hints, T...daoPojos) throws SQLException {
		// Try to insert one by one
		insert(hints, null, daoPojos);
	}

	public void insert(DalHints hints, KeyHolder keyHolder, T...daoPojos) throws SQLException {
		if(hints.is(DalHintEnum.usingBatch)){
			// TODO should revise for batch
		}
		
		// Try to insert one by one
		for(T pojo: daoPojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			String insertSql = buildInsertSql(fields.keySet());

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			
			if(keyHolder == null)
				client.update(insertSql, parameters, hints);
			else
				client.update(insertSql, parameters, hints, keyHolder);
		}
	}
	
	public void delete(DalHints hints, T...daoPojos) throws SQLException {
		for(T pojo: daoPojos) {
			String deleteSql = buildDeleteSql(pojo);

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, parser.getPrimaryKeys(pojo));
			
			client.update(deleteSql, parameters, hints);
		}
	}
	
	public void update(DalHints hints, T...daoPojos) throws SQLException {
		for(T pojo: daoPojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			Map<String, ?> pk = parser.getPrimaryKeys(pojo);
			
			String updateSql = buildUpdateSql(fields);
			
			// Remove primary keys from set values
			for(String key: pk.keySet())
				fields.remove(key);

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			addParameters(parameters, pk);
			
			client.update(updateSql, parameters, hints);
		}
	}
	
	public void delete(String whereClause, StatementParameters parameters, DalHints hints) throws SQLException {
		client.update(String.format(TMPL_SQL_DELETE, parser.getTableName(), whereClause), parameters, hints);
	}
	
	public void update(String sql, StatementParameters parameters, DalHints hints) throws SQLException {
		client.update(sql, parameters, hints);
	}
	
	private void addParameters(StatementParameters parameters, Map<String, ?> entries) {
		int index = parameters.size() + 1;
		for(Map.Entry<String, ?> entry: entries.entrySet()) {
			parameters.set(index, getColumnType(entry.getKey()), entry.getValue());
		}
	}
	
	private int getColumnType(String columnName) {
		return columnTypes.get(columnName);
	}
	
	private String initSql() {
		pkColumns = new HashSet<String>();
		Collections.addAll(pkColumns, parser.getPrimaryKeyNames());
		
		// Build a lookup table
		String[] cloumnNames = parser.getColumnNames();
		int[] columnsTypes = parser.getColumnTypes();
		for (int i = 0; i < cloumnNames.length; i++) {
			columnTypes.put(cloumnNames[i], columnsTypes[i]);
		}
		
		// Build primary key template
 		String template = parser.isAutoIncrement() ? 
				TMPL_SET_VALUE :
				combine(TMPL_SET_VALUE, parser.getPrimaryKeyNames().length, AND);
		
		return String.format(template, (Object[])parser.getPrimaryKeyNames());
	}
	
	private String buildInsertSql(Collection<String> columns) {
		String cloumns = combine(columns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, columns.size(), COLUMN_SEPARATOR);
		
		return String.format(TMPL_SQL_INSERT, parser.getTableName(), cloumns, values);
	}
	
	private String buildDeleteSql(T pojo) {
		return String.format(TMPL_SQL_DELETE, parser.getTableName(), pkSql);
	}

	private String buildUpdateSql(Map<String, ?> fields) {
		// Build SET
		List<String> nonNullColumns = new LinkedList<String>();
		
		for(String column: parser.getColumnNames()) {
			// For null value, we just keep it the same
			if(fields.get(column) == null || pkColumns.contains(column))
				continue;
			nonNullColumns.add(column);
		}
		
		String columns = String.format(combine(TMPL_SET_VALUE, nonNullColumns.size(), COLUMN_SEPARATOR), nonNullColumns);
		
		return String.format(TMPL_SQL_UPDATE, parser.getTableName(), columns, pkSql);
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
		;
		for(int i = 1; i <= count; i++) {
			valuesSb.append(value);
			if(i < count)
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}
}

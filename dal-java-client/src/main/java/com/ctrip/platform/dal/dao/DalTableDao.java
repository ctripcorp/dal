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
 * Base table DAO wraps common CRUD for particular table. The generated table
 * DAO should use this DAO to perform CRUD.
 * 
 * @author jhhe
 */
public final class DalTableDao<T> {
	public static final String GENERATED_KEY = "GENERATED_KEY";

	private static final String TMPL_SQL_FIND_BY = "SELECT * FROM %s WHERE %s";
	private static final String TMPL_SQL_INSERT = "INSERT INTO %s(%s) VALUES(%s)";
	private static final String TMPL_SQL_MULTIPLE_INSERT = "INSERT INTO %s(%s) VALUES %s";
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

	/**
	 * Query by Primary key. The key column type should be Integer, Long, etc.
	 * For table that the primary key is not of Integer type, this method will
	 * fail.
	 * 
	 * @param id
	 * @param hints
	 * @return
	 * @throws SQLException
	 */
	public T queryByPk(Number id, DalHints hints) throws SQLException {
		if (parser.getPrimaryKeyNames().length != 1)
			throw new SQLException(
					"The primary key of this table is consists of more than one column");

		StatementParameters parameters = new StatementParameters();
		parameters.set(1, getColumnType(parser.getPrimaryKeyNames()[0]), id);

		String selectSql = String.format(TMPL_SQL_FIND_BY,
				parser.getTableName(), pkSql);

		return queryDao.queryForObject(selectSql, parameters, hints, parser);
	}

	/**
	 * Query by Primary key, the key columns are pass in the pojo.
	 * 
	 * @param pk
	 * @param hints
	 * @return
	 * @throws SQLException
	 */
	public T queryByPk(T pk, DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, parser.getPrimaryKeys(pk));

		String selectSql = String.format(TMPL_SQL_FIND_BY,
				parser.getTableName(), pkSql);

		return queryDao.queryForObject(selectSql, parameters, hints, parser);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used to build the where clause.
	 * 
	 * @param sample
	 * @param hints
	 * @return
	 * @throws SQLException
	 */
	public List<T> queryLike(T sample, DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		Map<String, ?> queryCriteria = filterNullFileds(parser
				.getFields(sample));
		addParameters(parameters, queryCriteria);
		String whereClause = buildWhereClause(queryCriteria);

		return query(whereClause, parameters, hints);
	}

	/**
	 * Query by the given where clause and parameters. The where clause can
	 * contain value placeholder "?". The parameter should match the index of
	 * the placeholder.
	 * 
	 * @param whereClause
	 * @param parameters
	 * @param hints
	 * @return
	 * @throws SQLException
	 */
	public List<T> query(String whereClause, StatementParameters parameters,
			DalHints hints) throws SQLException {
		String selectSql = String.format(TMPL_SQL_FIND_BY,
				parser.getTableName(), whereClause);
		return queryDao.query(selectSql, parameters, hints, parser);
	}

	/**
	 * Query the first row of the given where clause and parameters. The where
	 * clause can contain value placeholder "?". The parameter should match the
	 * index of the placeholder.
	 * 
	 * @param whereClause
	 * @param parameters
	 * @param hints
	 * @return
	 * @throws SQLException
	 */
	public T queryFirst(String whereClause, StatementParameters parameters,
			DalHints hints) throws SQLException {
		String selectSql = String.format(TMPL_SQL_FIND_BY,
				parser.getTableName(), whereClause);
		return queryDao.queryFirst(selectSql, parameters, hints, parser);
	}

	/**
	 * Query the top rows of the given where clause and parameters. The where
	 * clause can contain value placeholder "?". The parameter should match the
	 * index of the placeholder.
	 * 
	 * @param whereClause
	 * @param parameters
	 * @param hints
	 * @param count
	 *            how may rows to return
	 * @return The qualified list of pojo
	 * @throws SQLException
	 */
	public List<T> queryTop(String whereClause, StatementParameters parameters,
			DalHints hints, int count) throws SQLException {
		String selectSql = String.format(TMPL_SQL_FIND_BY,
				parser.getTableName(), whereClause);
		return queryDao.queryTop(selectSql, parameters, hints, parser, count);
	}

	/**
	 * Query range of result for the given where clause and parameters. The
	 * where clause can contain value placeholder "?". The parameter should
	 * match the index of the placeholder.
	 * 
	 * @param whereClause
	 * @param parameters
	 * @param hints
	 * @param start
	 *            the start number
	 * @param count
	 *            how may rows to return
	 * @return The qualified list of pojo
	 * @throws SQLException
	 */
	public List<T> queryFrom(String whereClause,
			StatementParameters parameters, DalHints hints, int start, int count)
			throws SQLException {
		String selectSql = String.format(TMPL_SQL_FIND_BY,
				parser.getTableName(), whereClause);
		return queryDao.queryFrom(selectSql, parameters, hints, parser, start,
				count);
	}

	/**
	 * Insert pojos one by one. If you want to inert them in the batch mode,
	 * user batchInsert instead.
	 * 
	 * @param hints
	 *            additional parameters. DalHintEnum.continueOnError can be used
	 *            to indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 */
	public int insert(DalHints hints, T... daoPojos) throws SQLException {
		return insert(hints, null, daoPojos);
	}

	/**
	 * Insert pojos and get the generated PK back in keyHolder. Because certain
	 * JDBC driver may not support such feature, like MS JDBC driver, make sure
	 * the local test is performed before use this API.
	 * 
	 * @param hints
	 *            additional parameters. DalHintEnum.continueOnError can be used
	 *            to indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, KeyHolder keyHolder, T... daoPojos)
			throws SQLException {
		int count = 0;
		// Try to insert one by one
		for (T pojo : daoPojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			// TODO revise and improve performance
			String insertSql = buildInsertSql(fields);

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);

			try {
				if (keyHolder == null)
					count += client.update(insertSql, parameters, hints);
				else
					count += client.update(insertSql, parameters, hints,
							keyHolder);
			} catch (SQLException e) {
				if (hints.isStopOnError())
					throw e;
			}
		}
		return count;
	}

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in
	 * keyHolder If the nocount is on, the keyholder is not available
	 * 
	 * @param hints
	 * @param keyHolder
	 * @param daoPojos
	 * @return
	 * @throws SQLException
	 */
	public int insertCombined(DalHints hints, KeyHolder keyHolder,
			T... daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.length < 1)
			return 0;
		Map<String, ?> fields = this.parser.getFields(daoPojos[1]);
		Set<String> remainedColumns = fields.keySet();
		String cloumns = combine(remainedColumns, COLUMN_SEPARATOR);
		int count = daoPojos.length;
		StatementParameters parameters = new StatementParameters();
		StringBuilder values = new StringBuilder();

		int startIndex = 1;
		for (int i = 0; i < count; i++) {
			Map<String, ?> vfields = parser.getFields(daoPojos[i]);
			int paramCount = addParameters(startIndex, parameters, vfields);
			startIndex += paramCount;
			values.append(String.format("(%s),",
					this.combine("?", paramCount, ",")));
		}

		String sql = String.format(TMPL_SQL_MULTIPLE_INSERT,
				this.parser.getTableName(), cloumns,
				values.substring(0, values.length() - 2) + ")");

		return null == keyHolder ? this.client.update(sql, parameters, hints)
				: this.client.update(sql, parameters, hints, keyHolder);
	}

	/**
	 * Insert pojos in batch mode.
	 * 
	 * @param hints
	 * @param daoPojos
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchInsert(DalHints hints, T... daoPojos) throws SQLException {
		String insertSql = buildBatchInsertSql();
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		int i = 0;
		for (T pojo : daoPojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			parametersList[i++] = parameters;
		}

		return client.batchUpdate(insertSql, parametersList, hints);
	}

	/**
	 * Delete the given pojos list in batch mode
	 * 
	 * @param hints
	 * @param daoPojos
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DalHints hints, T... daoPojos) throws SQLException {
		int count = 0;
		for (T pojo : daoPojos) {
			String deleteSql = buildDeleteSql();

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, parser.getPrimaryKeys(pojo));

			try {
				count += client.update(deleteSql, parameters, hints);
			} catch (SQLException e) {
				if (hints.isStopOnError())
					throw e;
			}
		}
		return count;
	}

	/**
	 * Delete the given pojo list.
	 * 
	 * @param hints
	 * @param daoPojos
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchDelete(DalHints hints, T... daoPojos) throws SQLException {
		String deleteSql = buildDeleteSql();
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		int i = 0;
		for (T pojo : daoPojos) {
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, parser.getPrimaryKeys(pojo));
			parametersList[i++] = parameters;
		}

		return client.batchUpdate(deleteSql, parametersList, hints);
	}

	/**
	 * Update the given pojo list.Default,if the filed of pojo is null value,
	 * the field will be ignored,means,the filed will not be update. You can
	 * overwrite this by set updateNullField in hints.
	 * 
	 * @param hints
	 * 			additional parameters. DalHintEnum.updateNullField can be used
	 *          to indicate that the field of pojo is null value will be update.
	 * @param daoPojos
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(DalHints hints, T... daoPojos) throws SQLException {
		int count = 0;
		for (T pojo : daoPojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			Map<String, ?> pk = parser.getPrimaryKeys(pojo);

			String updateSql = buildUpdateSql(fields, hints);

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			addParameters(parameters, pk);

			try {
				if (fields.size() == 0)
					throw new SQLException(
							"There is no column to be updated. Please check if needed fields have been set in pojo.");

				count += client.update(updateSql, parameters, hints);
			} catch (SQLException e) {
				if (hints.isStopOnError())
					throw e;
			}
		}
		return count;
	}

	/**
	 * Delete for the given where clause and parameters.
	 * 
	 * @param whereClause
	 * @param parameters
	 * @param hints
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(String whereClause, StatementParameters parameters,
			DalHints hints) throws SQLException {
		return client.update(String.format(TMPL_SQL_DELETE,
				parser.getTableName(), whereClause), parameters, hints);
	}

	/**
	 * Update for the given where clause and parameters.
	 * 
	 * @param sql
	 * @param parameters
	 * @param hints
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(String sql, StatementParameters parameters, DalHints hints)
			throws SQLException {
		return client.update(sql, parameters, hints);
	}

	/**
	 * Add all the entries into the parameters by index. The parameter index
	 * will depends on the index of the entry in the entry set, value will be
	 * entry value. The value can be null.
	 * 
	 * @param parameters
	 * @param entries
	 */
	public void addParameters(StatementParameters parameters,
			Map<String, ?> entries) {
		int index = parameters.size() + 1;
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			parameters.set(index++, getColumnType(entry.getKey()),
					entry.getValue());
		}
	}

	private int addParameters(int start, StatementParameters parameters,
			Map<String, ?> entries) {
		int count = 0;
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			boolean isKey = false;
			if (this.parser.isAutoIncrement())
				for (String key : this.parser.getPrimaryKeyNames()) {
					if (entry.getKey().equals(key)) {
						isKey = true;
						break;
					}
				}
			Object value = isKey ? null : entry.getValue();
			parameters.set(count + start, this.parser.getColumnTypes()[count],
					value);
			count++;
		}
		return count;
	}

	/**
	 * Add all the entries into the parameters by name. The parameter name will
	 * be the entry key, value will be entry value. The value can be null. This
	 * method will be used to set input parameters for stored procedure.
	 * 
	 * @param parameters
	 * @param entries
	 */
	public void addParametersByName(StatementParameters parameters,
			Map<String, ?> entries) {
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			parameters.set(entry.getKey(), getColumnType(entry.getKey()),
					entry.getValue());
		}
	}

	/**
	 * Get the column type defined in java.sql.Types.
	 * 
	 * @param columnName
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

	public String buildCallSql(String spName, int paramCount) {
		return String.format(TMPL_CALL, spName,
				combine(PLACE_HOLDER, paramCount, COLUMN_SEPARATOR));
	}

	private String initSql() {
		pkColumns = new HashSet<String>();
		Collections.addAll(pkColumns, parser.getPrimaryKeyNames());

		// Build primary key template
		String template = parser.isAutoIncrement() ? TMPL_SET_VALUE : combine(
				TMPL_SET_VALUE, parser.getPrimaryKeyNames().length, AND);

		return String.format(template, (Object[]) parser.getPrimaryKeyNames());
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
		String values = combine(PLACE_HOLDER, remainedColumns.size(),
				COLUMN_SEPARATOR);

		return String.format(TMPL_SQL_INSERT, parser.getTableName(), cloumns,
				values);
	}

	private String buildBatchInsertSql() {
		String[] allColumns = parser.getColumnNames();
		String cloumns = combine(allColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, allColumns.length,
				COLUMN_SEPARATOR);

		return String.format(TMPL_SQL_INSERT, parser.getTableName(), cloumns,
				values);
	}

	private String buildDeleteSql() {
		return String.format(TMPL_SQL_DELETE, parser.getTableName(), pkSql);
	}

	private String buildUpdateSql(Map<String, ?> fields, DalHints hints) {
		// Remove null value when hints is not DalHintEnum.updateNullField or
		// primary key
		for (String column : parser.getColumnNames()) {
			if ((fields.get(column) == null && !hints
					.is(DalHintEnum.updateNullField))
					|| pkColumns.contains(column))
				fields.remove(column);
		}

		String columns = String.format(
				combine(TMPL_SET_VALUE, fields.size(), COLUMN_SEPARATOR),
				fields.keySet().toArray());

		return String.format(TMPL_SQL_UPDATE, parser.getTableName(), columns,
				pkSql);
	}

	private String buildWhereClause(Map<String, ?> fields) {
		return String.format(combine(TMPL_SET_VALUE, fields.size(), AND),
				fields.keySet().toArray());
	}

	private String combine(String[] values, String separator) {
		StringBuilder valuesSb = new StringBuilder();
		int i = 0;
		for (String value : values) {
			valuesSb.append(value);
			if (++i < values.length)
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}

	private String combine(Collection<String> values, String separator) {
		return combine(values.toArray(new String[values.size()]), separator);
	}

	private String combine(String value, int count, String separator) {
		StringBuilder valuesSb = new StringBuilder();

		for (int i = 1; i <= count; i++) {
			valuesSb.append(value);
			if (i < count)
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}
}

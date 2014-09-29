package com.ctrip.platform.dal.dao;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.buildShardStr;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.executeByDbShard;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isAlreadySharded;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isTableShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.locateTableShardId;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.helper.DalShardingHelper;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper.BulkTask;
import com.ctrip.platform.dal.sql.exceptions.DalException;
import com.ctrip.platform.dal.sql.exceptions.ErrorCode;
import com.ctrip.platform.dal.sql.logging.DalWatcher;

/**
 * Base table DAO wraps common CRUD for particular table. The generated table
 * DAO should use this DAO to perform CRUD.
 * All operations support corss-shard case. Including DB, table or DB + table sharding combination.
 * 
 * @author jhhe
 */
public final class DalTableDao<T> {
	public static final String GENERATED_KEY = "GENERATED_KEY";

	//private static final String TMPL_SQL_FIND_BY = "SELECT * FROM %s WHERE %s";
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

	private String findtmp = "SELECT * FROM %s WHERE %s";
	
	private DalClient client;
	private DalQueryDao queryDao;
	private DalParser<T> parser;

	private final String logicDbName;
	private final String pkSql;
	private Set<String> pkColumns;
	private String columnsForInsert;
	private List<String> validColumnsForInsert;
	private Map<String, Integer> columnTypes = new HashMap<String, Integer>();
	private Character startDelimiter;
	private Character endDelimiter;
	
	private boolean tableShardingEnabled;
	private String rawTableName;

	public DalTableDao(DalParser<T> parser) {
		this.client = DalClientFactory.getClient(parser.getDatabaseName());
		this.parser = parser;
		this.logicDbName = parser.getDatabaseName();
		queryDao = new DalQueryDao(parser.getDatabaseName());
		initColumnTypes();
		pkSql = initPkSql();
		validColumnsForInsert = buildValidColumnsForInsert();
		columnsForInsert = combineColumns(validColumnsForInsert, COLUMN_SEPARATOR);
		
		rawTableName = parser.getTableName();
		tableShardingEnabled = isTableShardingEnabled(logicDbName, rawTableName);
	}
	
	/**
	 * Specify the delimiter used to quote column name. The delimiter will be used as
	 * both start and end delimiter. This is useful when column name happens 
	 * to be keyword of target database and the start and end delimiter are the same.
	 * @param delimiter the char used to quote column name.
	 */
	public void setDelimiter(Character delimiter) {
		startDelimiter = delimiter;
		endDelimiter = delimiter;
	}

	/**
	 * Specify the sql template for find by primary key
	 * @param tmp the sql template for find by primary key
	 */
	public void setFindTemplate(String tmp){
		this.findtmp = tmp;
	}
	
	/**
	 * Specify the start and end delimiter used to quote column name.  
	 * This is useful when column name happens  to be keyword of target database.
	 * @param startDelimiter the start char used quote column name on .
	 * @param endDelimiter the end char used to quote column name.
	 */
	public void setDelimiter(Character startDelimiter, Character endDelimiter) {
		this.startDelimiter = startDelimiter;
		this.endDelimiter = endDelimiter;
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
		if(tableShardingEnabled == false)
			return parser.getTableName();
		
		hints.cleanUp();
		return rawTableName + buildShardStr(logicDbName, locateTableShardId(logicDbName, hints, parameters, fields));
	}

	/**
	 * Query by Primary key. The key column type should be Integer, Long, etc.
	 * For table that the primary key is not of Integer type, this method will
	 * fail.
	 * 
	 * @param id The primary key in number format
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return entity of this table. Null if no result found.
	 * @throws SQLException
	 */
	public T queryByPk(Number id, DalHints hints) throws SQLException {
		if (parser.getPrimaryKeyNames().length != 1)
			throw new DalException(ErrorCode.ValidatePrimaryKeyCount);

		DalWatcher.begin();
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, getColumnType(parser.getPrimaryKeyNames()[0]), id);

		String selectSql = String.format(findtmp,
				getTableName(hints, parameters), pkSql);

		return queryDao.queryForObjectNullable(selectSql, parameters, hints, parser);
	}
	
	/**
	 * Query by Primary key, the key columns are pass in the pojo.
	 * 
	 * @param pk The pojo used to represent primary key(s)
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return entity of this table. Null if no result found.
	 * @throws SQLException
	 */
	public T queryByPk(T pk, DalHints hints) throws SQLException {
		DalWatcher.begin();
		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, parser.getPrimaryKeys(pk));

		Map<String, ?> fields = parser.getFields(pk);
		String selectSql = String.format(findtmp,
				getTableName(hints, parameters, fields), pkSql);

		return queryDao.queryForObjectNullable(selectSql, parameters, hints.clone().setFields(fields), parser);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	 * 
	 * @param sample The pojo used for sampling
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return List of pojos that have the same attributes like in the sample
	 * @throws SQLException
	 */
	public List<T> queryLike(T sample, DalHints hints) throws SQLException {
		DalWatcher.begin();
		StatementParameters parameters = new StatementParameters();
		Map<String, ?> fields = parser.getFields(sample);
		Map<String, ?> queryCriteria = filterNullFileds(fields);
		addParameters(parameters, queryCriteria);
		String whereClause = buildWhereClause(queryCriteria);

		return query(whereClause, parameters, hints.clone().setFields(fields));
	}

	/**
	 * Query by the given where clause and parameters. The where clause can
	 * contain value placeholder "?". The parameter should match the index of
	 * the placeholder.
	 * 
	 * @param whereClause the where section for the search statement.
	 * @param parameters A container that holds all the necessary parameters 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return List of pojos that meet the search criteria
	 * @throws SQLException
	 */
	public List<T> query(String whereClause, StatementParameters parameters,
			DalHints hints) throws SQLException {
		DalWatcher.begin();
		String selectSql = String.format(findtmp,
				getTableName(hints, parameters), whereClause);
		return queryDao.query(selectSql, parameters, hints, parser);
	}

	/**
	 * Query the first row of the given where clause and parameters. The where
	 * clause can contain value placeholder "?". The parameter should match the
	 * index of the placeholder.
	 * 
	 * @param whereClause the where section for the search statement.
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return Null if no result found.
	 * @throws SQLException
	 */
	public T queryFirst(String whereClause, StatementParameters parameters,
			DalHints hints) throws SQLException {
		DalWatcher.begin();
		String selectSql = String.format(findtmp,
				getTableName(hints, parameters), whereClause);
		return queryDao.queryFirstNullable(selectSql, parameters, hints, parser);
	}

	/**
	 * Query the top rows of the given where clause and parameters. The where
	 * clause can contain value placeholder "?". The parameter should match the
	 * index of the placeholder.
	 * 
	 * @param whereClause the where section for the search statement.
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param count
	 *            how may rows to return
	 * @return The qualified list of pojo
	 * @throws SQLException
	 */
	public List<T> queryTop(String whereClause, StatementParameters parameters,
			DalHints hints, int count) throws SQLException {
		DalWatcher.begin();
		String selectSql = String.format(findtmp,
				getTableName(hints, parameters), whereClause);
		return queryDao.queryTop(selectSql, parameters, hints, parser, count);
	}

	/**
	 * Query range of result for the given where clause and parameters. The
	 * where clause can contain value placeholder "?". The parameter should
	 * match the index of the placeholder.
	 * 
	 * @param whereClause the where section for the search statement.
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param start
	 *            the start number. It is zero(0) based, means the index is from 0. 1 will be the 2nd row.
	 * @param count
	 *            how may rows to return
	 * @return The qualified list of pojo
	 * @throws SQLException
	 */
	public List<T> queryFrom(String whereClause,
			StatementParameters parameters, DalHints hints, int start, int count)
			throws SQLException {
		DalWatcher.begin();
		String selectSql = String.format(findtmp,
				getTableName(hints, parameters), whereClause);
		return queryDao.queryFrom(selectSql, parameters, hints, parser, start,
				count);
	}

	/**
	 * Insert pojos one by one. If you want to inert them in the batch mode,
	 * user batchInsert instead. You can also use the combinedInsert.
	 * 
	 * @param hints 
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 *            DalHintEnum.continueOnError can be used
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
	 * Insert pojos one by one. If you want to inert them in the batch mode,
	 * user batchInsert instead. You can also use the combinedInsert.
	 * 
	 * @param hints 
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 *            DalHintEnum.continueOnError can be used
	 *            to indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 */
	public int insert(DalHints hints, List<T> daoPojos) throws SQLException {
		return insert(hints, null, daoPojos);
	}

	/**
	 * Insert pojos and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 *            DalHintEnum.continueOnError can be used
	 *            to indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            Array of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, KeyHolder keyHolder, T... daoPojos)
			throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		return insert(hints, keyHolder, Arrays.asList(daoPojos));
	}
	
	/**
	 * Insert pojos and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 *            DalHintEnum.continueOnError can be used
	 *            to indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, KeyHolder keyHolder, List<T> daoPojos)
			throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		int count = 0;
		hints = hints.clone();
		for (T pojo : daoPojos) {
			DalWatcher.begin();
			Map<String, ?> fields = parser.getFields(pojo);
			filterAutoIncrementPrimaryFields(fields);
			
			String insertSql = buildInsertSql(hints, fields);

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
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder holder for generated primary keys. Don not use it(set to null) for MS SqlServer
	 * @param daoPojos array of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, KeyHolder keyHolder,
			T... daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		return combinedInsert(hints, keyHolder, Arrays.asList(daoPojos));
	}
	
	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, KeyHolder keyHolder,
			List<T> daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		hints.setDetailResults(new DalDetailResults<KeyHolder>());
		
		if(isAlreadySharded(logicDbName, rawTableName, hints))
			return combinedInsertByTable(hints, keyHolder, getPojosFields(daoPojos));
		else
			return executeByDbShard(logicDbName, rawTableName, hints, getPojosFields(daoPojos), new CombinedInsertTask(keyHolder));
	}
	
	private class CombinedInsertTask implements BulkTask<Integer> {
		private KeyHolder keyHolder;
		
		public CombinedInsertTask(KeyHolder keyHolder) {
			this.keyHolder = keyHolder;
		}
		
		@Override
		public Integer execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException {
			return combinedInsertByTable(hints, keyHolder, shaffled);
		}

		@Override
		public Integer merge(List<Integer> results) {
			int value = 0;
			for(Integer i: results) value += i;
			return value;
		}
	}
	
	private int combinedInsertByTable(DalHints hints, KeyHolder keyHolder, List<Map<String, ?>> daoPojos) throws SQLException {
		DalWatcher.begin();
		
		StatementParameters parameters = new StatementParameters();
		StringBuilder values = new StringBuilder();

		int startIndex = 1;
		for (Map<String, ?> vfields: daoPojos) {
			filterAutoIncrementPrimaryFields(vfields);
			int paramCount = addParameters(startIndex, parameters, vfields, validColumnsForInsert);
			startIndex += paramCount;
			values.append(String.format("(%s),",
					this.combine("?", paramCount, ",")));
		}

		String sql = String.format(TMPL_SQL_MULTIPLE_INSERT,
				getTableName(hints), columnsForInsert,
				values.substring(0, values.length() - 2) + ")");

		if(keyHolder == null) {
			return client.update(sql, parameters, hints);
		} else{
			KeyHolder tmpHolder = new KeyHolder();
			int count = client.update(sql, parameters, hints, tmpHolder);
			keyHolder.merge(tmpHolder);
			hints.addDetailResults(tmpHolder);
			return count;
		}
	}
	
	
	/**
	 * Insert pojos in batch mode. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchInsert(DalHints hints, List<T> daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return new int[0];
		
		hints.setDetailResults(new DalDetailResults<int[]>());
		
		if(isAlreadySharded(getLogicDbName(), rawTableName, hints))
			return batchInsertByTable(hints, getPojosFields(daoPojos));
		else
			return executeByDbShard(logicDbName, rawTableName, hints, getPojosFields(daoPojos), new BatchInsertTask());
	}
	

	/**
	 * Insert pojos in batch mode. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos array of pojos to be inserted
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchInsert(DalHints hints, T... daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return new int[0];
		
		return batchInsert(hints, Arrays.asList(daoPojos));
	}

	private class BatchInsertTask implements BulkTask<int[]> {
		@Override
		public int[] execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException {
			return batchInsertByTable(hints, shaffled);
		}

		@Override
		public int[] merge(List<int[]> results) {
			return DalShardingHelper.combine(results.toArray(new int[results.size()][]));
		}
	}
	
	private int[] batchInsertByTable(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		DalWatcher.begin();
		
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;
		for (Map<String, ?> fields : daoPojos) {
			filterAutoIncrementPrimaryFields(fields);
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			parametersList[i++] = parameters;
		}

		String batchInsertSql = buildBatchInsertSql(getTableName(hints));
		int[] result = client.batchUpdate(batchInsertSql, parametersList, hints);
		hints.addDetailResults(result);
		return result;
	}
	
	/**
	 * Delete the given pojos list one by one.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DalHints hints, List<T> daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		int count = 0;
		hints = hints.clone();
		for (T pojo : daoPojos) {
			DalWatcher.begin();
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, parser.getPrimaryKeys(pojo));
			try {
				Map<String, ?> fields = parser.getFields(pojo);
				
				String deleteSql = buildDeleteSql(getTableName(hints, parameters, fields));

				count += client.update(deleteSql, parameters, hints.setFields(fields));
			} catch (SQLException e) {
				if (hints.isStopOnError())
					throw e;
			}
		}
		return count;
	}
	
	/**
	 * Delete the given pojos list one by one.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos array of pojos to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DalHints hints, T... daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		return delete(hints, Arrays.asList(daoPojos));
	}
	

	/**
	 * Delete the given pojo list in batch. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos array of pojos to be deleted
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchDelete(DalHints hints, T... daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return new int[0];
		
		return batchDelete(hints, Arrays.asList(daoPojos));
	}
	
	/**
	 * Delete the given pojo list in batch. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchDelete(DalHints hints, List<T> daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return new int[0];

		hints.setDetailResults(new DalDetailResults<int[]>());
		
		if(isAlreadySharded(getLogicDbName(), rawTableName, hints))
			return batchDeleteByTable(hints, getPojosFields(daoPojos));
		else
			return executeByDbShard(logicDbName, rawTableName, hints, getPojosFields(daoPojos), new BatchDeleteTask());
	}
	
	private class BatchDeleteTask implements BulkTask<int[]> {
		@Override
		public int[] execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException {
			return batchDeleteByTable(hints, shaffled);
		}

		@Override
		public int[] merge(List<int[]> results) {
			return DalShardingHelper.combine(results.toArray(new int[results.size()][]));
		}
	}
	
	private int[] batchDeleteByTable(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		DalWatcher.begin();
		
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;
		List<String> pkNames = Arrays.asList(parser.getPrimaryKeyNames());
		for (Map<String, ?> pojo : daoPojos) {
			StatementParameters parameters = new StatementParameters();
			addParameters(1, parameters, pojo, pkNames);
			parametersList[i++] = parameters;
		}
		
		String deleteSql = buildDeleteSql(getTableName(hints));
		int[] result = client.batchUpdate(deleteSql, parametersList, hints);
		hints.addDetailResults(result);
		return result;
	}
	
	/**
	 * Update the given pojo list one by one. By default, if a field of pojo is null value,
	 * that field will be ignored, so that it will not be updated. You can
	 * overwrite this by set updateNullField in hints.
	 * 
	 * @param hints
	 * 			Additional parameters that instruct how DAL Client perform database operation.
	 *          DalHintEnum.updateNullField can be used
	 *          to indicate that the field of pojo is null value will be update.
	 * @param daoPojos array of pojos to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(DalHints hints, T... daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		return this.update(hints, Arrays.asList(daoPojos));
	}
	
	/**
	 * Update the given pojo list one by one. By default, if a field of pojo is null value,
	 * that field will be ignored, so that it will not be updated. You can
	 * overwrite this by set updateNullField in hints.
	 * 
	 * @param hints
	 * 			Additional parameters that instruct how DAL Client perform database operation.
	 *          DalHintEnum.updateNullField can be used
	 *          to indicate that the field of pojo is null value will be update.
	 * @param daoPojos list of pojos to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(DalHints hints, List<T> daoPojos) throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		int count = 0;
		for (T pojo : daoPojos) {
			DalWatcher.begin();
			Map<String, ?> fields = parser.getFields(pojo);
			Map<String, ?> pk = parser.getPrimaryKeys(pojo);

			String updateSql = buildUpdateSql(getTableName(hints, fields), fields, hints);

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			addParameters(parameters, pk);

			try {
				if (fields.size() == 0)
					throw new DalException(ErrorCode.ValidateFieldCount);

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
	 * @param whereClause the condition specified for delete operation
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(String whereClause, StatementParameters parameters,
			DalHints hints) throws SQLException {
		DalWatcher.begin();
		return client.update(String.format(TMPL_SQL_DELETE,
				getTableName(hints, parameters), whereClause), parameters, hints);
	}

	/**
	 * Update for the given where clause and parameters.
	 * 
	 * @param sql the statement that used to update the db.
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(String sql, StatementParameters parameters, DalHints hints)
			throws SQLException {
		DalWatcher.begin();
		return client.update(sql, parameters, hints);
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
			parameters.set(index++, entry.getKey(), getColumnType(entry.getKey()),
					entry.getValue());
		}
	}

	private int addParameters(int start, StatementParameters parameters,
			Map<String, ?> entries, List<String> validColumns) {	
		int count = 0;
		for(String column : validColumns){
			if(entries.containsKey(column))
				parameters.set(count + start, column, this.getColumnType(column), entries.get(column));
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
			parameters.set(entry.getKey(), getColumnType(entry.getKey()),
					entry.getValue());
		}
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

	public Map<String, ?> filterAutoIncrementPrimaryFields(Map<String, ?> fields){
		// This is bug here, for My Sql, auto incremental id and be part of the joint primary key.
		// But for Ctrip, a table must have a pk defined by sigle column as mandatory, so we don't have problem here
		if(parser.isAutoIncrement())
			fields.remove(parser.getPrimaryKeyNames()[0]);
		return fields;
	}
	
	public String buildCallSql(String spName, int paramCount) {
		return String.format(TMPL_CALL, spName,
				combine(PLACE_HOLDER, paramCount, COLUMN_SEPARATOR));
	}
	
	private boolean isEmpty(List<T> daoPojos) {
		return null == daoPojos || daoPojos.size() == 0;
	}
	
	private boolean isEmpty(T... daoPojos) {
		return null == daoPojos || daoPojos.length == 0;
	}
	
	private List<Map<String, ?>> getPojosFields(List<T> daoPojos) {
		List<Map<String, ?>> pojoFields = new LinkedList<Map<String, ?>>();
		if (null == daoPojos || daoPojos.size() < 1)
			return pojoFields;
		
		for (T pojo: daoPojos){
			pojoFields.add(parser.getFields(pojo));
		}
		
		return pojoFields;
	}

	private String getLogicDbName() {
		return parser.getDatabaseName();
	}

	private boolean isPrimaryKey(String fieldName){
		return pkColumns.contains(fieldName);
	}
	
	private String initPkSql() {
		pkColumns = new HashSet<String>();
		Collections.addAll(pkColumns, parser.getPrimaryKeyNames());

		// Build primary key template
		String template = combine(TMPL_SET_VALUE, parser.getPrimaryKeyNames().length, AND);

		return String.format(template, (Object[]) quote(parser.getPrimaryKeyNames()));
	}

	// Build a lookup table
	private void initColumnTypes() {
		String[] cloumnNames = parser.getColumnNames();
		int[] columnsTypes = parser.getColumnTypes();
		for (int i = 0; i < cloumnNames.length; i++) {
			columnTypes.put(cloumnNames[i], columnsTypes[i]);
		}
	}

	private String buildInsertSql(DalHints hints, Map<String, ?> fields) throws SQLException {
		filterNullFileds(fields);
		Set<String> remainedColumns = fields.keySet();
		String cloumns = combineColumns(remainedColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, remainedColumns.size(),
				COLUMN_SEPARATOR);

		return String.format(TMPL_SQL_INSERT, getTableName(hints, fields), cloumns,
				values);
	}
	
	private List<String> buildValidColumnsForInsert() {
		List<String> validColumns = new ArrayList<String>();
		for(String s : parser.getColumnNames()){
			if(!(parser.isAutoIncrement() && isPrimaryKey(s)))
				validColumns.add(s);
		}
		
		return validColumns;

	}
	
	private String buildBatchInsertSql(String tableName) {
		int validColumnsSize = parser.getColumnNames().length;
		if(parser.isAutoIncrement())
			validColumnsSize--;
		
		String values = combine(PLACE_HOLDER, validColumnsSize,
				COLUMN_SEPARATOR);

		return String.format(TMPL_SQL_INSERT, tableName, columnsForInsert,
				values);
	}


	private String buildDeleteSql(String tableName) {
		return String.format(TMPL_SQL_DELETE, tableName, pkSql);
	}

	private String buildUpdateSql(String tableName, Map<String, ?> fields, DalHints hints) {
		// Remove null value when hints is not DalHintEnum.updateNullField or
		// primary key
		for (String column : parser.getColumnNames()) {
			if ((fields.get(column) == null && !hints
					.is(DalHintEnum.updateNullField))
					|| isPrimaryKey(column))
				fields.remove(column);
		}

		String columns = String.format(
				combine(TMPL_SET_VALUE, fields.size(), COLUMN_SEPARATOR),
				quote(fields.keySet()));

		return String.format(TMPL_SQL_UPDATE, tableName, columns,
				pkSql);
	}

	private String buildWhereClause(Map<String, ?> fields) {
		return String.format(combine(TMPL_SET_VALUE, fields.size(), AND),
				quote(fields.keySet()));
	}

	private String combineColumns(Collection<String> values, String separator) {
		StringBuilder valuesSb = new StringBuilder();
		int i = 0;
		for (String value : values) {
			quote(valuesSb, value);
			if (++i < values.size())
				valuesSb.append(separator);
		}
		return valuesSb.toString();
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
	
	private String quote(String column) {
		if(startDelimiter == null)
			return column;
		return new StringBuilder().append(startDelimiter).append(column).append(endDelimiter).toString();
	}

	private StringBuilder quote(StringBuilder sb, String column) {
		if(startDelimiter == null)
			return sb.append(column);
		return sb.append(startDelimiter).append(column).append(endDelimiter);
	}
	
	private Object[] quote(Set<String> columns) {
		if(startDelimiter == null)
			return columns.toArray();
		
		Object[] rawColumns = columns.toArray();
		for(int i = 0; i < rawColumns.length; i++)
			rawColumns[i] = quote((String)rawColumns[i]);
		return rawColumns;
	}
	
	private String[] quote(String[] columns) {
		if(startDelimiter == null)
			return columns;
		for(int i = 0; i < columns.length; i++)
			columns[i] = quote(columns[i]);
		return columns;
	}
}

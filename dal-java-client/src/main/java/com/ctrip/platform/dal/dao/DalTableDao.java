package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.client.DalWatcher;
import com.ctrip.platform.dal.dao.task.BulkTask;
import com.ctrip.platform.dal.dao.task.DefaultTaskExecutor;
import com.ctrip.platform.dal.dao.task.DefaultTaskFactory;
import com.ctrip.platform.dal.dao.task.SingleTask;
import com.ctrip.platform.dal.dao.task.TaskAdapter;
import com.ctrip.platform.dal.dao.task.TaskExecutor;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * Base table DAO wraps common CRUD for particular table. The generated table
 * DAO should use this DAO to perform CRUD.
 * All operations support corss-shard case. Including DB, table or DB + table sharding combination.
 * 
 * @author jhhe
 */
public final class DalTableDao<T> extends TaskAdapter<T> {
	public static final String GENERATED_KEY = "GENERATED_KEY";

	private static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";

	private SingleTask<T> singleInsertTask;
	private SingleTask<T> singleDeleteTask;
	private SingleTask<T> singleUpdateTask;

	private BulkTask<Integer, T> combinedInsertTask;

	private BulkTask<int[], T> batchInsertTask;
	private BulkTask<int[], T> batchDeleteTask;
	private BulkTask<int[], T> batchUpdateTask;

	private TaskExecutor<T> executor; 
			
	public DalTableDao(DalParser<T> parser) {
		this(parser, DalClientFactory.getTaskFactory());
	}
	
	public DalTableDao(DalParser<T> parser, DalTaskFactory factory) {
		this(parser, factory, new DefaultTaskExecutor<T>(parser));
	}
	
	public DalTableDao(DalParser<T> parser, TaskExecutor<T> executor) {
		this(parser, DalClientFactory.getTaskFactory(), executor);
	}
	
	public DalTableDao(DalParser<T> parser, DalTaskFactory factory, TaskExecutor<T> executor) {
		initialize(parser);
		initTasks(factory);
		this.executor = executor;
	}
	
	private void initTasks(DalTaskFactory factory){
		singleInsertTask = factory.createSingleInsertTask(parser);
		singleDeleteTask = factory.createSingleDeleteTask(parser);
		singleUpdateTask = factory.createSingleUpdateTask(parser);
		
		combinedInsertTask = factory.createCombinedInsertTask(parser);
		
		batchInsertTask = factory.createBatchInsertTask(parser);
		batchDeleteTask = factory.createBatchDeleteTask(parser);
		batchUpdateTask = factory.createBatchUpdateTask(parser);
	}
	
	public DalClient getClient() {
		return client;
	}
	
	public DatabaseCategory getDatabaseCategory() {
		return dbCategory;
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
	 * Insert pojo and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, T daoPojo)
			throws SQLException {
		return executor.execute(hints, daoPojo, singleInsertTask);
	}
	
	/**
	 * Insert pojo and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, KeyHolder keyHolder, T daoPojo)
			throws SQLException {
		return executor.execute(hints.setKeyHolder(keyHolder), daoPojo, singleInsertTask);
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
	public int[] insert(DalHints hints, List<T> daoPojos) throws SQLException {
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
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<T> daoPojos)
			throws SQLException {
		return executor.execute(hints.setKeyHolder(keyHolder), daoPojos, singleInsertTask);
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
	public int combinedInsert(DalHints hints, List<T> daoPojos) 
			throws SQLException {
		return executor.execute(hints, daoPojos, combinedInsertTask, 0);
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
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<T> daoPojos) 
			throws SQLException {
		return executor.execute(hints.setKeyHolder(keyHolder), daoPojos, combinedInsertTask, 0);
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
		return executor.execute(hints, daoPojos, batchInsertTask, new int[0]);
	}
	
	/**
	 * Delete the given pojo.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojo pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DalHints hints, T daoPojo) throws SQLException {
		return executor.execute(hints, daoPojo, singleDeleteTask);
	}
	
	/**
	 * Delete the given pojos list one by one.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] delete(DalHints hints, List<T> daoPojos) throws SQLException {
		return executor.execute(hints, daoPojos, singleDeleteTask);
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
		return executor.execute(hints, daoPojos, batchDeleteTask, new int[0]);
	}
	
	/**
	 * Update the given pojo . By default, if a field of pojo is null value,
	 * that field will be ignored, so that it will not be updated. You can
	 * overwrite this by set updateNullField in hints.
	 * 
	 * @param hints
	 * 			Additional parameters that instruct how DAL Client perform database operation.
	 *          DalHintEnum.updateNullField can be used
	 *          to indicate that the field of pojo is null value will be update.
	 * @param daoPojo pojo to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(DalHints hints, T daoPojo) throws SQLException {
		return executor.execute(hints, daoPojo, singleUpdateTask);
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
	public int[] update(DalHints hints, List<T> daoPojos) throws SQLException {
		return executor.execute(hints, daoPojos, singleUpdateTask);
	}
	
	public int[] batchUpdate(DalHints hints, List<T> daoPojos) throws SQLException {
		return executor.execute(hints, daoPojos, batchUpdateTask, new int[0]);
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
//		return new ResultMerger.IntSummary();
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
//		return new ResultMerger.IntSummary();
		return client.update(sql, parameters, hints);
	}
}
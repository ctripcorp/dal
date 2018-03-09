package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.DeleteSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.InsertSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.TableSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.UpdateSqlBuilder;
import com.ctrip.platform.dal.dao.task.BulkTask;
import com.ctrip.platform.dal.dao.task.DalBulkTaskRequest;
import com.ctrip.platform.dal.dao.task.DalRequestExecutor;
import com.ctrip.platform.dal.dao.task.DalSingleTaskRequest;
import com.ctrip.platform.dal.dao.task.DalSqlTaskRequest;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.dao.task.DeleteSqlTask;
import com.ctrip.platform.dal.dao.task.QuerySqlTask;
import com.ctrip.platform.dal.dao.task.SingleTask;
import com.ctrip.platform.dal.dao.task.TaskAdapter;
import com.ctrip.platform.dal.dao.task.UpdateSqlTask;
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

	private SingleTask<T> singleInsertTask;
	private SingleTask<T> singleDeleteTask;
	private SingleTask<T> singleUpdateTask;

	private BulkTask<Integer, T> combinedInsertTask;

	private BulkTask<int[], T> batchInsertTask;
	private BulkTask<int[], T> batchDeleteTask;
	private BulkTask<int[], T> batchUpdateTask;
	
	private DeleteSqlTask<T> deleteSqlTask;
	private UpdateSqlTask<T> updateSqlTask;

	private DalRequestExecutor executor; 
			
	public DalTableDao(DalParser<T> parser) {
		this(parser, DalClientFactory.getTaskFactory());
	}
	
	public DalTableDao(Class<T> entityType) throws SQLException {
		this(new DalDefaultJpaParser<>(entityType));
	}
	
	public DalTableDao(Class<T> entityType, String dataBaseName) throws SQLException {
		this(new DalDefaultJpaParser<>(entityType, dataBaseName));
	}
	
	public DalTableDao(Class<T> entityType, String dataBaseName, String tableName) throws SQLException {
		this(new DalDefaultJpaParser<>(entityType, dataBaseName, tableName));
	}
	
	public DalTableDao(DalParser<T> parser, DalTaskFactory factory) {
		this(parser, factory, new DalRequestExecutor());
	}
	
	public DalTableDao(DalParser<T> parser, DalRequestExecutor executor) {
		this(parser, DalClientFactory.getTaskFactory(), executor);
	}
	
	public DalTableDao(DalParser<T> parser, DalTaskFactory factory, DalRequestExecutor executor) {
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
		
		deleteSqlTask = factory.createDeleteSqlTask(parser);
		updateSqlTask = factory.createUpdateSqlTask(parser);
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

		StatementParameters parameters = new StatementParameters();
		parameters.set(1, parser.getPrimaryKeyNames()[0], getColumnType(parser.getPrimaryKeyNames()[0]), id);

		return queryObject(new SelectSqlBuilder().where(pkSql).with(parameters).requireSingle().nullable(), hints);
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
		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, parser.getPrimaryKeys(pk));

		return queryObject(new SelectSqlBuilder().where(pkSql).with(parameters).requireSingle().nullable(), hints.setFields(parser.getFields(pk)));
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
		StatementParameters parameters = new StatementParameters();
		Map<String, ?> fields = parser.getFields(sample);
		Map<String, ?> queryCriteria = filterNullFileds(fields);
		addParameters(parameters, queryCriteria);
		String whereClause = buildWhereClause(queryCriteria);

		return query(whereClause, parameters, hints.setFields(fields));
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
		return query(new SelectSqlBuilder().where(whereClause).with(parameters), hints);
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
	public List<T> query(SelectSqlBuilder selectBuilder, DalHints hints) throws SQLException {
		return commonQuery(selectBuilder.mapWith(parser).nullable(), hints);
	}

	/**
	 * Query by the given where clause and parameters. The where clause can
	 * contain value placeholder "?". The parameter should match the index of
	 * the placeholder.
	 * 
	 * @param whereClause the where section for the search statement.
	 * @param parameters A container that holds all the necessary parameters 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz the return type, not the pojo, but simple type
	 * @return List of pojos that meet the search criteria
	 * @throws SQLException
	 */
	public <K> List<K> query(SelectSqlBuilder selectBuilder, DalHints hints, Class<K> clazz) throws SQLException {
		return commonQuery((SelectSqlBuilder)selectBuilder.mapWith(clazz).nullable(), hints);
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
		return queryObject(new SelectSqlBuilder().where(whereClause).with(parameters).requireFirst().nullable(), hints);
	}

	/**
	 * Query pojo for the given query builder. The requireSingle or requireFirst MUST be set on builder.
	 * @param selectBuilder select builder which represents the query criteria
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return
	 * @throws SQLException
	 */
	public T queryObject(SelectSqlBuilder selectBuilder, DalHints hints) throws SQLException {
		return commonQuery(selectBuilder.mapWith(parser), hints);
	}
	
	/**
	 * Query object for the given type for the given query builder. The requireSingle or requireFirst MUST be set on builder.
	 * @param selectBuilder select builder which represents the query criteria
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz the class which the returned result belongs to.
	 * @return
	 * @throws SQLException
	 */
	public <K> K queryObject(SelectSqlBuilder selectBuilder, DalHints hints, Class<K> clazz) throws SQLException {
		return commonQuery((SelectSqlBuilder)selectBuilder.mapWith(clazz), hints);
	}

	public Number count(String whereClause, StatementParameters parameters, DalHints hints) throws SQLException {
		return count(new SelectSqlBuilder().where(whereClause).with(parameters).selectCount(), hints);
	}
	
	//Assume selectCount() is already invoked
	public Number count(SelectSqlBuilder selectBuilder, DalHints hints) throws SQLException {
		return commonQuery(selectBuilder, hints);
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
		return query(new SelectSqlBuilder().where(whereClause).with(parameters).top(count), hints);
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
		return query(new SelectSqlBuilder().where(whereClause).with(parameters).range(start, count), hints);
	}

	private <K> K commonQuery(SelectSqlBuilder builder, DalHints hints) throws SQLException {
		DalSqlTaskRequest<K> request = new DalSqlTaskRequest<K>(
				logicDbName, populate(builder), hints, 
				new QuerySqlTask<>((DalResultSetExtractor<K>)builder.getResultExtractor(hints)), (ResultMerger<K>)builder.getResultMerger(hints));
		
		return executor.execute(hints, request, builder.isNullable());
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
		return insert(hints, hints.getKeyHolder(), daoPojo);
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
		return getSafeResult(executor.execute(setSize(hints, keyHolder, daoPojo), new DalSingleTaskRequest<>(logicDbName, hints, daoPojo, singleInsertTask)));
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
		return insert(hints, hints.getKeyHolder(), daoPojos);
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
		return executor.execute(setSize(hints, keyHolder, daoPojos), new DalSingleTaskRequest<>(logicDbName, hints, daoPojos, singleInsertTask));
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
		return combinedInsert(hints, hints.getKeyHolder(), daoPojos);
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
		return getSafeResult(executor.execute(setSize(hints, keyHolder, daoPojos), new DalBulkTaskRequest<>(logicDbName, rawTableName, hints, daoPojos, combinedInsertTask)));
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
		return executor.execute(hints, new DalBulkTaskRequest<>(logicDbName, rawTableName, hints, daoPojos, batchInsertTask));
	}
	
	/**
	 * Insert with InsertSqlBuilder.
	 * @param insertBuilder sql builder that represents the insert operation
	 * @param hints
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public int insert(InsertSqlBuilder insertBuilder, DalHints hints) throws SQLException {
		return getSafeResult(executor.execute(hints, new DalSqlTaskRequest<>(logicDbName, populate(insertBuilder), hints, updateSqlTask, new ResultMerger.IntSummary())));
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
		return getSafeResult(getSafeResult(executor.execute(hints, new DalSingleTaskRequest<>(logicDbName, hints, daoPojo, singleDeleteTask))));
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
		return executor.execute(hints, new DalSingleTaskRequest<>(logicDbName, hints, daoPojos, singleDeleteTask));
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
		return executor.execute(hints, new DalBulkTaskRequest<>(logicDbName, rawTableName, hints, daoPojos, batchDeleteTask));
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
		return getSafeResult(executor.execute(hints, new DalSingleTaskRequest<>(logicDbName, hints, daoPojo, singleUpdateTask)));
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
		return executor.execute(hints, new DalSingleTaskRequest<>(logicDbName, hints, daoPojos, singleUpdateTask));
	}
	
	public int[] batchUpdate(DalHints hints, List<T> daoPojos) throws SQLException {
		return executor.execute(hints, new DalBulkTaskRequest<>(logicDbName, rawTableName, hints, daoPojos, batchUpdateTask));
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
		return delete(new DeleteSqlBuilder().where(whereClause).with(parameters), hints);
	}

	/**
	 * Delete for the given delete sql builder.
	 * 
	 * @param deleteBuilder the builder represents delete sql
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DeleteSqlBuilder deleteBuilder, DalHints hints) throws SQLException {
		return getSafeResult(executor.execute(hints, new DalSqlTaskRequest<>(logicDbName, populate(deleteBuilder), hints, deleteSqlTask, new ResultMerger.IntSummary())));
	}

	/**
	 * Update for the given sql and parameters. The sql must be the standard update statement.
	 * E.g. "UPDATE ABC SET ....". Because it is the raw sql, table shard will not be supported
	 * if the table name is logic one, you can provide real table name in sql if you want to update
	 * certain phisical table.
	 * 
	 * @param sql the statement that used to update the db.
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(String sql, StatementParameters parameters, DalHints hints)
			throws SQLException {
		return getSafeResult(executor.execute(hints, new DalSqlTaskRequest<>(logicDbName, new FreeUpdateSqlBuilder(dbCategory).setTemplate(sql).with(parameters), hints, updateSqlTask, new ResultMerger.IntSummary())));
	}
	
	/**
	 * Update for the given UpdateSqlBuilder and parameters.
	 * 
	 * @param updateBuilder the builder that used to update the db.
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(UpdateSqlBuilder updateBuilder, DalHints hints)
			throws SQLException {
		return getSafeResult(executor.execute(hints, new DalSqlTaskRequest<>(logicDbName, populate(updateBuilder), hints, updateSqlTask, new ResultMerger.IntSummary())));
	}
	
	private SqlBuilder populate(TableSqlBuilder builder) throws SQLException {
		builder.from(rawTableName).setDatabaseCategory(dbCategory);
		return builder;
	}
	
	private int getSafeResult(Integer value) {
		if(value == null)
			return 0;
		return value;
	}
	
	private int getSafeResult(int[] counts) {
		if(counts == null)
			return 0;
		return counts[0];
	}
	
	private DalHints setSize(DalHints hints, KeyHolder keyHolder, List<T> pojos) {
		if(keyHolder != null && pojos != null)
			keyHolder.initialize(pojos.size());
		
		return hints.setKeyHolder(keyHolder);
	}

	private DalHints setSize(DalHints hints, KeyHolder keyHolder, T pojo) {
		if(keyHolder != null && pojo != null)
			keyHolder.initialize(1);
		
		return hints.setKeyHolder(keyHolder);
	}
}

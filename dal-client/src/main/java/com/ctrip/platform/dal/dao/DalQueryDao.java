package com.ctrip.platform.dal.dao;

import static com.ctrip.platform.dal.dao.helper.EntityManager.getMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.DalRangedResultMerger;
import com.ctrip.platform.dal.dao.helper.DalRowCallbackExtractor;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.MultipleSqlBuilder;
import com.ctrip.platform.dal.dao.task.*;

/**
 * DAO class that provides multiple common query functions and simple update function.
 * It supports DB shard. It is usually used for free style dao.
 *  
 * @author jhhe
 *
 */
public final class DalQueryDao extends BaseTaskAdapter {

	private static final boolean NULLABLE = true;
	private DalRequestExecutor executor;

	public DalQueryDao(String logicDbName) {
		this(logicDbName, new DalRequestExecutor());
	}
	
	public DalQueryDao(String logicDbName, DalRequestExecutor executor) {
		initialize(logicDbName);
		this.executor = executor;
	}

	public DalClient getClient() {
		return client;
	}

	/**
	 * Execute query by the given sql with parameters. The result will be wrapped into type defined by the given mapper.
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return List of entities that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		return query(new FreeSelectSqlBuilder<List<T>>().setTemplate(sql).mapWith(mapper), parameters, hints);
	}

	public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper,
							 ShardExecutionCallback<List<T>> callback) throws SQLException {
		return query(new FreeSelectSqlBuilder<List<T>>().setTemplate(sql).mapWith(mapper), parameters, hints, callback);
	}

	/**
	 * Execute query by the given sql with parameters. The result will be the list of instance of the given clazz.
	 * Please don't use this when clazz is Short because ResultSet will return Integer instead of Short.
	 * In such case, please use ShortRowMapper. 
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type 
	 * @return List of instance of clazz that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		return query(new FreeSelectSqlBuilder<List<T>>().setTemplate(sql).mapWith(getMapper(clazz)), parameters, hints);
	}

	public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz,
							 ShardExecutionCallback<List<T>> callback) throws SQLException {
		return query(new FreeSelectSqlBuilder<List<T>>().setTemplate(sql).mapWith(getMapper(clazz)), parameters, hints, callback);
	}

	/**
	 * Execute query by the given sql with parameters. The result will be processed by the given callback.
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param callback Helper that process each row.
	 * @throws SQLException when things going wrong during the execution
	 */
	public void query(String sql, StatementParameters parameters, DalHints hints, DalRowCallback callback) 
			throws SQLException {
		query(new FreeSelectSqlBuilder<>().setTemplate(sql).extractorWith(new DalRowCallbackExtractor(callback)).nullable(), parameters, hints);
	}
	
	/**
	 * Execute query by the given sqls with parameters. The result will be wrapped into type defined by the given extractors.
	 * 
	 * @param mqr The multiple query request value object
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return List of entities that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public List<?> query(MultipleSqlBuilder mqr, DalHints hints) throws SQLException {
		return query(mqr, hints, null);
	}

	public List<?> query(MultipleSqlBuilder mqr, DalHints hints, ShardExecutionCallback<List<?>> callback) throws SQLException {
		DalSqlTaskRequest<List<?>> request = new DalSqlTaskRequest<>(logicDbName, mqr, hints,
				DalClientFactory.getTaskFactory().createMultipleQueryTask(logicDbName, mqr.getExtractors()),
				mqr.getMergers(), callback);
		return executor.execute(hints, request, NULLABLE, callback);
	}
	
	/**
	 * Select with FreeSelectSqlBuilder. The builder contains sql template. If there is IN (?) clause, the number of "?" should be 1.
	 * The system will check how many values for the in parameter and compile correct ? for the final sql.
	 * This method is mainly used with Code Generator which builder and paramteres are provided separately.
	 * 
	 * @param builder
	 * @param parameters
	 * @param hints
	 * @return result defined by the type specified when constructing builder
	 * @throws SQLException
	 */
	public <T> T query(FreeSelectSqlBuilder<T> builder, StatementParameters parameters, DalHints hints) throws SQLException {
		return query(builder, parameters, hints, null);
	}

	public <T> T query(FreeSelectSqlBuilder<T> builder, StatementParameters parameters, DalHints hints,
					   ShardExecutionCallback<T> callback) throws SQLException {
		ResultMerger<T> merger = builder.getResultMerger(hints);
		DalResultSetExtractor<T> extractor = builder.getResultExtractor(hints);
		DalSqlTaskRequest<T> request = new DalSqlTaskRequest<>(logicDbName,
				builder.setLogicDbName(logicDbName).with(parameters), hints,
				DalClientFactory.getTaskFactory().createFreeSqlQueryTask(logicDbName, extractor), merger, callback);
		return executor.execute(hints, request, builder.isNullable(), callback);
	}
	
    /**
     * Select with FreeSelectSqlBuilder. The builder contains sql template. If there is IN (?) clause, the number of "?" should be 1.
     * The system will check how many values for the in parameter and compile correct ? for the final sql.
     * 
     * This method is recommended if you use builder build the parameters with SQL
     * 
     * @param builder
     * @param hints
     * @return result defined by the type specified when constructing builder
     * @throws SQLException
     */
	public <T> T query(FreeSelectSqlBuilder<T> builder, DalHints hints) throws SQLException {
		return query(builder, hints, null);
	}

	public <T> T query(FreeSelectSqlBuilder<T> builder, DalHints hints, ShardExecutionCallback<T> callback)
			throws SQLException {
		ResultMerger<T> merger = builder.getResultMerger(hints);
		DalResultSetExtractor<T> extractor = builder.getResultExtractor(hints);
		DalSqlTaskRequest<T> request = new DalSqlTaskRequest<>(logicDbName,
				builder.setLogicDbName(logicDbName).setHints(hints), hints,
				DalClientFactory.getTaskFactory().createFreeSqlQueryTask(logicDbName, extractor), merger, callback);
		return executor.execute(hints, request, builder.isNullable(), callback);
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result or more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.  
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return entity that represent the query result.
	 * @throws SQLException If there is no result or more than 1 result found.
	 */
	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		return queryForObject(sql, parameters, hints, mapper, !NULLABLE);
	}

	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper,
								ShardExecutionCallback<T> callback) throws SQLException {
		return queryForObject(sql, parameters, hints, mapper, !NULLABLE, callback);
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result, it will return null. If there is more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.  
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return entity that represent the query result. Or null if no result found.
	 * @throws SQLException If there is than 1 result found.
	 */
	public <T> T queryForObjectNullable(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		return queryForObject(sql, parameters, hints, mapper, NULLABLE);
	}

	public <T> T queryForObjectNullable(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper,
										ShardExecutionCallback<T> callback) throws SQLException {
		return queryForObject(sql, parameters, hints, mapper, NULLABLE, callback);
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result or more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type  
	 * @return instance of clazz that represent the query result.
	 * @throws SQLException If there is no result or more than 1 result found.
	 */
	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		return queryForObject(sql, parameters, hints, getMapper(clazz), !NULLABLE);
	}

	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz,
								ShardExecutionCallback<T> callback) throws SQLException {
		return queryForObject(sql, parameters, hints, getMapper(clazz), !NULLABLE, callback);
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result, it will return null. If there is more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type  
	 * @return instance of clazz that represent the query result. Or null if no result found.
	 * @throws SQLException If there is more than 1 result found.
	 */
	public <T> T queryForObjectNullable(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		return queryForObject(sql, parameters, hints, getMapper(clazz), NULLABLE);
	}

	public <T> T queryForObjectNullable(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz,
										ShardExecutionCallback<T> callback) throws SQLException {
		return queryForObject(sql, parameters, hints, getMapper(clazz), NULLABLE, callback);
	}
	
	/**
	 * Query for the first object in the result. It is expected that there is at least one result should be found.
	 * If there is no result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the only one result, please use queryObject instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return entity that represent the query result.
	 * @throws SQLException If there is no result found.
	 */
	public <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		return queryFirst(sql, parameters, hints, mapper, !NULLABLE);
	}

	public <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper,
							ShardExecutionCallback<T> callback) throws SQLException {
		return queryFirst(sql, parameters, hints, mapper, !NULLABLE, callback);
	}

	/**
	 * Query for the first object in the result. It is expected that there is at least one result should be found.
	 * If there is no result found, it will return null.
	 * If you want to get the only one result, please use queryObject instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return entity that represent the query result. Null if no result found.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> T queryFirstNullable(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		return queryFirst(sql, parameters, hints, mapper, NULLABLE);
	}

	public <T> T queryFirstNullable(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper,
									ShardExecutionCallback<T> callback) throws SQLException {
		return queryFirst(sql, parameters, hints, mapper, NULLABLE, callback);
	}
	
	/**
	 * Query for the first object in the result. It is expected that there is at least one result should be found.
	 * If there is no result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the only one result, please use queryObject instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type 
	 * @return instance of clazz that represent the query result.
	 * @throws SQLException If there is no result found.
	 */
	public <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		return queryFirst(sql, parameters, hints, getMapper(clazz), !NULLABLE);
	}

	public <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz,
							ShardExecutionCallback<T> callback) throws SQLException {
		return queryFirst(sql, parameters, hints, getMapper(clazz), !NULLABLE, callback);
	}

	/**
	 * Query for the first object in the result. It is expected that there is at least one result should be found.
	 * If there is no result found, it will return null.
	 * If you want to get the only one result, please use queryObject instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type 
	 * @return instance of clazz that represent the query result. Null if no result found.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> T queryFirstNullable(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		return queryFirst(sql, parameters, hints, getMapper(clazz), NULLABLE);
	}

	public <T> T queryFirstNullable(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz,
									ShardExecutionCallback<T> callback) throws SQLException {
		return queryFirst(sql, parameters, hints, getMapper(clazz), NULLABLE, callback);
	}

	/**
	 * Query the first count of object in the result. If the query return more result than 
	 * count. It will return top count of result. If there is not enough result, it will 
	 * return all the results.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity.
	 * @param count number of result 
	 * @return list of entity that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> queryTop(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int count) 
			throws SQLException {
		return queryRange(sql, parameters, hints, mapper, 0, count);
	}

	public <T> List<T> queryTop(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int count,
								ShardExecutionCallback<List<T>> callback) throws SQLException {
		return queryRange(sql, parameters, hints, mapper, 0, count, callback);
	}

	/**
	 * Query the first count of object in the result. If the query return more result than 
	 * count. It will return top count of result. If there is not enough result, it will 
	 * return all the results.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type .
	 * @param count number of result 
	 * @return list of instance of clazz that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> queryTop(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz, int count) 
			throws SQLException {
		return queryRange(sql, parameters, hints,  getMapper(clazz), 0, count);
	}

	public <T> List<T> queryTop(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz, int count,
								ShardExecutionCallback<List<T>> callback) throws SQLException {
		return queryRange(sql, parameters, hints,  getMapper(clazz), 0, count, callback);
	}

	/**
	 * Execute query and return partial result against the given start and count.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity.
	 * @param start the row number to be started counting
	 * @param count number of result 
	 * @return list of entity that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper,
								 int start, int count) throws SQLException {
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return queryRange(sql, parameters, hints, mapper, start, count);
	}

	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper,
								 int start, int count, ShardExecutionCallback<List<T>> callback) throws SQLException {
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return queryRange(sql, parameters, hints, mapper, start, count, callback);
	}

	/**
	 * Execute query and return partial result against the given start and count.
	 * If the query is executed under cross shard mode(all shards, or in some shards), 
	 * the result will be ranged after result from all shard is collected and sorted.
	 * For non-corss shard case, just do the range when walk through result set.
	 *  
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type .
	 * @param start the row number to be started counting
	 * @param count number of result 
	 * @return list of instance of clazz that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz,
								 int start, int count) throws SQLException {
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return queryRange(sql, parameters, hints, getMapper(clazz), start, count);
	}

	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz,
								 int start, int count, ShardExecutionCallback<List<T>> callback) throws SQLException {
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return queryRange(sql, parameters, hints, getMapper(clazz), start, count, callback);
	}
	
	/**
	 * Update with FreeUpdateSqlBuilder. The builder contains sql template. If there is IN (?) clause, the number of "?" should be 1.
	 * The system will check how many values for the in parameter and compile correct ? for the final sql.
	 * This method is mainly used with Code Generator which builder and paramteres are provided separately.
	 * 
	 * @param builder
	 * @param parameters
	 * @param hints
	 * @return affected rows
	 * @throws SQLException
	 */
	public int update(FreeUpdateSqlBuilder builder, StatementParameters parameters, DalHints hints) throws SQLException {
		return update((FreeUpdateSqlBuilder)builder.with(parameters), hints);
	}

	public int update(FreeUpdateSqlBuilder builder, StatementParameters parameters, DalHints hints,
					  ShardExecutionCallback<Integer> callback) throws SQLException {
		return update((FreeUpdateSqlBuilder)builder.with(parameters), hints, callback);
	}
	
    /**
     * Update with FreeUpdateSqlBuilder. The builder contains sql template. If there is IN (?) clause, the number of "?" should be 1.
     * The system will check how many values for the in parameter and compile correct ? for the final sql.
     * 
     * This method is recommended if you use builder build the parameters with SQL
     * 
     * @param builder
     * @return affected rows
     * @throws SQLException
     */
    public int update(FreeUpdateSqlBuilder builder, DalHints hints) throws SQLException {
        return update(builder, hints, null);
    }

	public int update(FreeUpdateSqlBuilder builder, DalHints hints,
					  ShardExecutionCallback<Integer> callback) throws SQLException {
		return getSafeResult((Integer)executor.execute(hints, new DalSqlTaskRequest<>(logicDbName,
				builder.setLogicDbName(logicDbName).setHints(hints), hints,
				DalClientFactory.getTaskFactory().createFreeUpdateTask(logicDbName),
				new ResultMerger.IntSummary(), callback), callback));
	}
    
	private int getSafeResult(Integer value) {
		if(value == null)
			return 0;
		return value;
	}

	private <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, boolean nullable)
			throws SQLException {
		return query(new FreeSelectSqlBuilder<T>().setTemplate(sql).mapWith(mapper).requireSingle().setNullable(nullable), parameters, hints);
	}

	private <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, boolean nullable,
								 ShardExecutionCallback<T> callback) throws SQLException {
		return query(new FreeSelectSqlBuilder<T>().setTemplate(sql).mapWith(mapper).requireSingle().setNullable(nullable), parameters, hints, callback);
	}

	private <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, boolean nullable) 
			throws SQLException {
		return query(new FreeSelectSqlBuilder<T>().setTemplate(sql).mapWith(mapper).requireFirst().setNullable(nullable), parameters, hints);
	}

	private <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, boolean nullable,
							 ShardExecutionCallback<T> callback) throws SQLException {
		return query(new FreeSelectSqlBuilder<T>().setTemplate(sql).mapWith(mapper).requireFirst().setNullable(nullable), parameters, hints, callback);
	}

	private <T> List<T> queryRange(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int start, int count)
			throws SQLException {
		return queryRange(sql, parameters, hints, mapper, start, count, null);
	}

	private <T> List<T> queryRange(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int start, int count,
								   ShardExecutionCallback<List<T>> callback) throws SQLException {
		FreeSelectSqlBuilder<List<T>> builder = new FreeSelectSqlBuilder<List<T>>().setTemplate(sql).mapWith(mapper);
		if (hints.isAllShards() || hints.isInShards()) {
			builder.range(0, (count + start));
			builder.mergerWith(new DalRangedResultMerger<>((Comparator<T>) hints.getSorter(), start, count));
			builder.extractorWith(new DalRowMapperExtractor<T>(mapper));
		} else {
			builder.range(start, count);
			builder.extractorWith(new DalRowMapperExtractor<T>(mapper));
		}
		return query(builder, parameters, hints, callback);
	}

}

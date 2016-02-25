package com.ctrip.platform.dal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.DalFirstResultMerger;
import com.ctrip.platform.dal.dao.helper.DalListMerger;
import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRangedResultMerger;
import com.ctrip.platform.dal.dao.helper.DalRowCallbackExtractor;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultMerger;
import com.ctrip.platform.dal.dao.helper.MultipleQueryRequest;
import com.ctrip.platform.dal.dao.task.DalRequestExecutor;
import com.ctrip.platform.dal.dao.task.DalSqlTaskRequest;
import com.ctrip.platform.dal.dao.task.MultipleQueryTask;
import com.ctrip.platform.dal.dao.task.QuerySqlTask;

/**
 * DAO class that provides common query based functions.
 *  
 * @author jhhe
 *
 */
public final class DalQueryDao {
	private String logicDbName;
	private DalClient client;
	private static final boolean NULLABLE = true;
	private DalRequestExecutor executor;

	public DalQueryDao(String logicDbName) {
		this(logicDbName, new DalRequestExecutor());
	}
	
	public DalQueryDao(String logicDbName, DalRequestExecutor executor) {
		this.logicDbName = logicDbName;
		this.client = DalClientFactory.getClient(logicDbName);
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
		return queryList(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper));
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
		return queryList(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()));
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
		queryList(sql, parameters, hints, new DalRowCallbackExtractor(callback));
	}
	
	/**
	 * Execute query by the given sqls with parameters. The result will be wrapped into type defined by the given extractors.
	 * 
	 * @param mqr The multiple query request value object
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @return List of entities that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public List<?> query(MultipleQueryRequest mqr, StatementParameters parameters, DalHints hints) 
			throws SQLException {
		DalSqlTaskRequest<List<?>> request = new DalSqlTaskRequest<>(
				logicDbName, mqr.getSqls(), parameters, hints, 
				new MultipleQueryTask(mqr.getExtractors()), mqr.getMergers());
		
		return executor.execute(hints, request, NULLABLE);
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
		return queryForObject(sql, parameters, hints, new DalObjectRowMapper<T>(), !NULLABLE);
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
		return queryForObject(sql, parameters, hints, new DalObjectRowMapper<T>(), NULLABLE);
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
		return queryFirst(sql, parameters, hints, new DalObjectRowMapper<T>(), !NULLABLE);
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
		return queryFirst(sql, parameters, hints, new DalObjectRowMapper<T>(), NULLABLE);
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
		return queryRange(sql, parameters, hints,  new DalObjectRowMapper<T>(), 0, count);
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
	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int start, int count) throws SQLException {
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return queryRange(sql, parameters, hints, mapper, start, count);
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
	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz, int start, int count) throws SQLException {
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return queryRange(sql, parameters, hints, new DalObjectRowMapper<T>(), start, count);
	}
	
	private <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, boolean nullable) 
			throws SQLException {
		ResultMerger<T> defaultMerger = new DalSingleResultMerger<>();
		return commonQuery(sql, parameters, hints, new DalSingleResultExtractor<T>(mapper, true), defaultMerger, nullable);
	}

	private <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, boolean nullable) 
			throws SQLException {
		ResultMerger<T> defaultMerger = new DalFirstResultMerger<>((Comparator<T>)hints.getSorter());
		return commonQuery(sql, parameters, hints, new DalSingleResultExtractor<T>(mapper, false), defaultMerger, nullable);
	}

	private <T> List<T> queryList(String sql, StatementParameters parameters, DalHints hints, DalResultSetExtractor<List<T>> extractor) 
			throws SQLException {
		ResultMerger<List<T>> defaultMerger = new DalListMerger<>((Comparator<T>)hints.getSorter());
		return commonQuery(sql, parameters, hints, extractor, defaultMerger, NULLABLE);
	}
	
	private <T> List<T> queryRange(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int start, int count) 
			throws SQLException {
		ResultMerger<List<T>> defaultMerger;
		DalRowMapperExtractor<T> extractor;
		
		if(hints.isAllShards() || hints.isInShards()) {
			defaultMerger = new DalRangedResultMerger<>((Comparator<T>)hints.getSorter(), start, count);
			extractor = new DalRowMapperExtractor<T>(mapper);
		} else {
			defaultMerger = null;// No need for non cross shard operation
			extractor = new DalRowMapperExtractor<T>(mapper, start, count);
		}
		
		return commonQuery(sql, parameters, hints, extractor, defaultMerger, NULLABLE);
	}

	private <T> T commonQuery(
			String sql, StatementParameters parameters, 
			DalHints hints, DalResultSetExtractor<T> extractor,
			ResultMerger<T> defaultMerger,
			boolean nullable) throws SQLException {
		ResultMerger<T> merger = hints.is(DalHintEnum.resultMerger) ?
					(ResultMerger<T>)hints.get(DalHintEnum.resultMerger) : 
					defaultMerger;
		
		DalSqlTaskRequest<T> request = new DalSqlTaskRequest<>(
				logicDbName, sql, parameters, hints, 
				new QuerySqlTask<>(extractor), merger);
		
		return executor.execute(hints, request, nullable);
	}
}

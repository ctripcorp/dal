package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

/**
 * Only for Sql Server SPA, SP3 case.
 * @author jhhe
 *
 * @param <T>
 */
public class CtripTableSpDao<T> {

	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from Person WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM Person WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by ID desc ) as rownum"
			+ " from Person (nolock)) select * from CTE where rownum between %s and %s";

	private static final String BASIC_INSERT_SP_NAME = "spA_Person_i";
	private static final String BATCH_INSERT_SP_NAME = "sp3_Person_i";
	private static final String BASIC_DELETE_SP_NAME = "spA_Person_d";
	private static final String BATCH_DELETE_SP_NAME = "sp3_Person_d";
	private static final String BASIC_UPDATE_SP_NAME = "spA_Person_u";
	private static final String BATCH_UPDATE_SP_NAME = "sp3_Person_u";
	private static final String RET_CODE = "retcode";

	private DalParser<T> parser;
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<T> rowextractor = null;
	private DalTableDao<T> client;
	private DalClient baseClient;
	private String idName;
	

	public CtripTableSpDao(DalParser<T> parser) {
		this.baseClient = DalClientFactory.getClient(parser.getDatabaseName());
		this.client = new DalTableDao<T>(parser);
		this.rowextractor = new DalRowMapperExtractor<T>(parser);
		// Check with YKN about the naming convention
		this.idName = parser.getPrimaryKeyNames()[0];
	}

	/**
	 * Query T by the specified ID The ID must be a number
	 **/
	public T queryByPk(Number id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query T by T instance which the primary key is set
	 **/
	public T queryByPk(T pk, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}

	/**
	 * Get the records count
	 **/
	public int count(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		Number result = (Number) this.baseClient.query(COUNT_SQL_PATTERN,
				parameters, hints, extractor);
		return result.intValue();
	}

	/**
	 * Query T with paging function The pageSize and pageNo must be greater than
	 * zero.
	 **/
	public List<T> queryByPage(int pageSize, int pageNo, DalHints hints)
			throws SQLException {
		if (pageNo < 1 || pageSize < 1)
			throw new SQLException("Illigal pagesize or pageNo, pls check");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String sql = "";
		int fromRownum = (pageNo - 1) * pageSize + 1;
		int endRownum = pageSize * pageNo;
		sql = String.format(PAGE_SQL_PATTERN, fromRownum, endRownum);
		return this.baseClient.query(sql, parameters, hints, rowextractor);
	}

	/**
	 * Get all records in the whole table
	 **/
	public List<T> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<T> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints,
				rowextractor);
		return result;
	}

	/**
	 * SP Insert
	 **/
	public int insert(DalHints hints, T daoPojo) throws SQLException {
		if (null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(BASIC_INSERT_SP_NAME, parameters,
				parser.getFields(daoPojo));
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		return (Integer) results.get(RET_CODE);
	}

	/**
	 * SP Insert
	 **/
	public int insert(DalHints hints, KeyHolder holder, T daoPojo)
			throws SQLException {
		if (null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(BASIC_INSERT_SP_NAME, parameters,
				parser.getFields(daoPojo));
		parameters.registerInOut(idName, Types.INTEGER, parser.getIdentityValue(daoPojo));
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

		if (holder != null) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("ID", parameters.get("ID", ParameterDirection.InputOutput)
					.getValue());
			holder.getKeyList().add(map);
		}
		return (Integer) results.get(RET_CODE);
	}

	/**
	 * Batch insert without out parameters Return how many rows been affected
	 * for each of parameters
	 **/
	public int[] insert(DalHints hints, T... daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_INSERT_SP_NAME, parser
				.getFields(daoPojos[0]).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for (int i = 0; i < daoPojos.length; i++) {
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters,
					parser.getFields(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}

	/**
	 * Batch insert without out parameters Return how many rows been affected
	 * for each of parameters
	 **/
	public int[] insert(DalHints hints, List<T> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_INSERT_SP_NAME, parser
				.getFields(daoPojos.get(0)).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos
				.size()];
		for (int i = 0; i < daoPojos.size(); i++) {
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters,
					parser.getFields(daoPojos.get(i)));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}

	/**
	 * SP delete
	 **/
	public int delete(DalHints hints, T daoPojo) throws SQLException {
		if (null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(BASIC_DELETE_SP_NAME, parameters,
				parser.getPrimaryKeys(daoPojo));
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		return (Integer) results.get(RET_CODE);
	}

	/**
	 * Batch SP delete without out parameters Return how many rows been affected
	 * for each of parameters
	 */
	public int[] delete(DalHints hints, T... daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_DELETE_SP_NAME, 1);
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for (int i = 0; i < daoPojos.length; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.registerInOut(idName, Types.INTEGER, parser.getIdentityValue(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}

	/**
	 * Batch SP delete without out parameters Return how many rows been affected
	 * for each of parameters
	 */
	public int[] delete(DalHints hints, List<T> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_DELETE_SP_NAME, 1);
		StatementParameters[] parametersList = new StatementParameters[daoPojos
				.size()];
		for (int i = 0; i < daoPojos.size(); i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.registerInOut(idName, Types.INTEGER, parser.getIdentityValue(daoPojos.get(i)));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}

	/**
	 * SP update
	 **/
	public int update(DalHints hints, T daoPojo) throws SQLException {
		if (null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(BASIC_UPDATE_SP_NAME, parameters,
				parser.getFields(daoPojo));
		parameters.registerInOut(idName, Types.INTEGER, parser.getIdentityValue(daoPojo));
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		Integer iD = (Integer) parameters.get("ID",
				ParameterDirection.InputOutput).getValue();
		return (Integer) results.get(RET_CODE);
	}

	/**
	 * Batch SP update without out parameters Return how many rows been affected
	 * for each of parameters
	 */
	public int[] update(DalHints hints, T... daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_UPDATE_SP_NAME, parser
				.getFields(daoPojos[0]).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for (int i = 0; i < daoPojos.length; i++) {
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters,
					parser.getFields(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}

	/**
	 * Batch SP update without out parameters Return how many rows been affected
	 * for each of parameters
	 */
	public int[] update(DalHints hints, List<T> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_UPDATE_SP_NAME, parser
				.getFields(daoPojos.get(0)).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos
				.size()];
		for (int i = 0; i < daoPojos.size(); i++) {
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters,
					parser.getFields(daoPojos.get(i)));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}

	private String prepareSpCall(String SpName, StatementParameters parameters,
			Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}
}

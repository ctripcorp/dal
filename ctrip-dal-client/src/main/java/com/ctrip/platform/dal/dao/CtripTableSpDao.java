package com.ctrip.platform.dal.dao;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.detectDistributedTransaction;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.executeByDbShard;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isAlreadySharded;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.client.DalWatcher;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper.AbstractIntArrayBulkTask;

/**
 * This DAO is to simplify Ctrip special MS Sql Server CUD case. 
 * Ctrip use SP3 or SPA to perform CUD on MS Sql Server.
 * The rules:
 * 1. If there are both SP3 and SPA for the table, the batch CUD will use SP3, the non-batch will use SPA.
 *    The reason is because a special setting in Ctrip Sql Server that prevent batch SPA CUD
 * 2. If there is only SP3 for the table, both batch and non-batch will using SP3
 * 3. If there is only SPA for the table, only non-batch CUD supported
 * 4. If there is no SP3 or SPA, the original DalTableDao should be used.
 * 
 * For sharding support: it is confirmed from DBA that Ctrip has shard by DB case, but no shard by table case.
 * For inout, out parameter: only insert SP3/SPA has inout/out parameter
 * 
 * @author jhhe
 */
public class CtripTableSpDao<T> {
	private static final String INSERT_SPA_TPL = "spA_%s_i";
	private static final String INSERT_SP3_TPL = "sp3_%s_i";
	private static final String DELETE_SPA_TPL = "spA_%s_d";
	private static final String DELETE_SP3_TPL = "sp3_%s_d";
	private static final String UPDATE_SPA_TPL = "spA_%s_u";
	private static final String UPDATE_SP3_TPL = "sp3_%s_u";

	private final String insertSPA;
	private final String insertSP3;
	private final String deleteSPA;
	private final String deleteSP3;
	private final String updateSPA;
	private final String updateSP3;
	
	private static final String RET_CODE = "retcode";

	private DalParser<T> parser;
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalTableDao<T> client;
	private DalClient baseClient;
	private String idName;
	private String[] inOutPramNames;
	private String[] outputPramNames;
	
	private String logicDbName;
	private String rawTableName;
	

	public CtripTableSpDao(DalParser<T> parser, String[] inOutPramNames, String[] outputPramNames) {
		this.baseClient = DalClientFactory.getClient(parser.getDatabaseName());
		this.client = new DalTableDao<T>(parser);
		// Check with YKN about the naming convention
		this.idName = parser.getPrimaryKeyNames()[0];
		
		String tableName = parser.getTableName();
		insertSP3 = String.format(INSERT_SP3_TPL, tableName);
		insertSPA = String.format(INSERT_SPA_TPL, tableName);
		
		deleteSP3 = String.format(DELETE_SP3_TPL, tableName);
		deleteSPA = String.format(DELETE_SPA_TPL, tableName);

		updateSP3 = String.format(UPDATE_SP3_TPL, tableName);
		updateSPA = String.format(UPDATE_SPA_TPL, tableName);		
		
		this.inOutPramNames = inOutPramNames;
		this.outputPramNames = outputPramNames;
		this.logicDbName = parser.getDatabaseName();
		this.rawTableName = parser.getTableName();
	}

	/**
	 * Query pojo by the specified ID. The ID must be a number
	 **/
	public T queryByPk(Number id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query pojo by pojo instance which the primary key is set
	 **/
	public T queryByPk(T pk, DalHints hints) throws SQLException {
		return client.queryByPk(pk, hints);
	}

	/**
	 * SPA Insert
	 **/
	public int insert(DalHints hints, T daoPojo) throws SQLException {
		return insert(hints, null, daoPojo);		
	}

	/**
	 * SPA Insert with primary key holder
	 * TODO make it like DalTableDao
	 **/
	public int insert(DalHints hints, KeyHolder keyHolder, T daoPojo) throws SQLException {
		return insert(hints, keyHolder, parser.getFields(daoPojo));
	}
	
	/**
	 * Insert multiple pojos one by one using SPA, you can use hints to specify if continue insert on error.  
	 * @param hints
	 * @param daoPojos
	 * @return the return code for each SPA call
	 * @throws SQLException
	 */
	public int[] insert(DalHints hints, List<T> daoPojos)
			throws SQLException {
		return insert(hints, null, daoPojos);
	}
	
	/**
	 * Insert multiple pojos one by one using SPA, you can use hints to specify if continue insert on error.
	 * @param hints
	 * @param keyHolder
	 * @param daoPojos
	 * @return the return code for each SPA call
	 * @throws SQLException
	 */
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<T> daoPojos)
			throws SQLException {
		if(client.isEmpty(daoPojos)) return new int[0];

		List<Map<String, ?>> pojos = client.getPojosFields(daoPojos);
		detectDistributedTransaction(logicDbName, hints, pojos);
		
		hints = hints.clone();// To avoid shard id being polluted by each pojos
		int[] retCodes = new int[daoPojos.size()];
		for (int i = 0; i < pojos.size(); i++) {
			DalWatcher.begin();
			try {
				retCodes[i] = insert(hints, keyHolder, pojos.get(i));
			} catch (SQLException e) {
				if (hints.isStopOnError())
					throw e;
			}
		}
		return retCodes;
	}
	
	private int insert(DalHints hints, KeyHolder keyHolder, Map<String, ?> daoPojo) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		String callSql = prepareSpCall(insertSPA, parameters, daoPojo);
		
		register(parameters, daoPojo);
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		extract(parameters, keyHolder);

		return (Integer)results.get(RET_CODE);
	}

	/**
	 * Batch insert using SP3 without out parameters
	 * @param hints
	 * @param daoPojos
	 * @return Return how many rows been affected. for each of parameters
	 * @throws SQLException
	 */
	public int[] batchInsert(DalHints hints, T... daoPojos) throws SQLException {
		if(client.isEmpty(daoPojos)) return new int[0];
		
		return batchInsert(hints, Arrays.asList(daoPojos));
	}

	/**
	 * Batch insert without out parameters Return how many rows been affected
	 * for each of parameters
	 **/
	public int[] batchInsert(DalHints hints, List<T> daoPojos) throws SQLException {
		if(client.isEmpty(daoPojos)) return new int[0];

		hints.setDetailResults(new DalDetailResults<int[]>());
		
		if(isAlreadySharded(client.getLogicDbName(), rawTableName, hints))
			return batchInsertSp3ByTable(hints, client.getPojosFields(daoPojos));
		else
			return executeByDbShard(logicDbName, rawTableName, hints, client.getPojosFields(daoPojos), new BatchInsertSp3Task());
	}
	
	private class BatchInsertSp3Task extends AbstractIntArrayBulkTask {
		@Override
		public int[] execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException {
			return batchInsertSp3ByTable(hints, shaffled);
		}
	}
	
	// Ctrip does not have shard by table case.
	private int[] batchInsertSp3ByTable(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		DalWatcher.begin();

		String callSql = client.buildCallSql(insertSP3, parser.getColumnNames().length);
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		
		for (int i = 0; i < daoPojos.size(); i++) {
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, daoPojos.get(i));
			parametersList[i] = parameters;
		}
		
		int[] result = baseClient.batchCall(callSql, parametersList, hints);
		hints.addDetailResults(result);
		return result; 
	}

	/**
	 * SP delete
	 **/
	public int delete(DalHints hints, T daoPojo) throws SQLException {
		if (null == daoPojo) return 0;
		
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		String callSql = prepareSpCall(deleteSPA, parameters, parser.getPrimaryKeys(daoPojo));

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer) results.get(RET_CODE);
	}

	/**
	 * Batch SP delete without out parameters Return how many rows been affected
	 * for each of parameters
	 */
	public int[] batchDelete(DalHints hints, T... daoPojos) throws SQLException {
		if (client.isEmpty(daoPojos)) return new int[0];
		
		return batchDelete(hints, Arrays.asList(daoPojos));
	}

	/**
	 * Batch SP delete without out parameters Return how many rows been affected
	 * for each of parameters
	 */
	public int[] batchDelete(DalHints hints, List<T> daoPojos) throws SQLException {
		if(client.isEmpty(daoPojos)) return new int[0];
		
		hints.setDetailResults(new DalDetailResults<int[]>());
		
		if(isAlreadySharded(client.getLogicDbName(), rawTableName, hints))
			return batchDeleteSp3ByTable(hints, client.getPojosFields(daoPojos));
		else
			return executeByDbShard(logicDbName, rawTableName, hints, client.getPojosFields(daoPojos), new BatchDeleteSp3Task());
	}

	private class BatchDeleteSp3Task  extends AbstractIntArrayBulkTask {
		@Override
		public int[] execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException {
			return batchDeleteSp3ByTable(hints, shaffled);
		}
	}
	
	private int[] batchDeleteSp3ByTable(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		DalWatcher.begin();
		
		String callSql = client.buildCallSql(deleteSP3, parser.getPrimaryKeyNames().length);
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		for (int i = 0; i < daoPojos.size(); i++) {
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, daoPojos.get(i), parser.getPrimaryKeyNames());
			parametersList[i] = parameters;
		}
		
		int[] result = baseClient.batchCall(callSql, parametersList, hints);
		hints.addDetailResults(result);
		return result;
	}

	/**
	 * SP update
	 **/
	public int update(DalHints hints, T daoPojo) throws SQLException {
		if (null == daoPojo) return 0;
		
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		String callSql = prepareSpCall(updateSPA, parameters, parser.getFields(daoPojo));

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

		return (Integer) results.get(RET_CODE);
	}

	/**
	 * Batch SP update without out parameters Return how many rows been affected
	 * for each of parameters
	 */
	public int[] batchUpdate(DalHints hints, T... daoPojos) throws SQLException {
		if (client.isEmpty(daoPojos)) return new int[0];
		
		return batchUpdate(hints, Arrays.asList(daoPojos));
	}

	/**
	 * Batch SP update without out parameters Return how many rows been affected
	 * for each of parameters
	 */
	public int[] batchUpdate(DalHints hints, List<T> daoPojos) throws SQLException {
		if (client.isEmpty(daoPojos)) return new int[0];

		hints.setDetailResults(new DalDetailResults<int[]>());
		
		if(isAlreadySharded(client.getLogicDbName(), rawTableName, hints))
			return batchUpdateSp3ByTable(hints, client.getPojosFields(daoPojos));
		else
			return executeByDbShard(logicDbName, rawTableName, hints, client.getPojosFields(daoPojos), new BatchUpdateSp3Task());
	}
	
	private class BatchUpdateSp3Task extends AbstractIntArrayBulkTask {
		@Override
		public int[] execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException {
			return batchUpdateSp3ByTable(hints, shaffled);
		}
	}

	private int[] batchUpdateSp3ByTable(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		DalWatcher.begin();

		hints = DalHints.createIfAbsent(hints);

		String callSql = client.buildCallSql(updateSP3, parser.getColumnNames().length);
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		
		for (int i = 0; i < daoPojos.size(); i++) {
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, daoPojos.get(i));
			parametersList[i] = parameters;
		}
		
		int[] result = baseClient.batchCall(callSql, parametersList, hints);
		hints.addDetailResults(result);
		return result;

	}

	private String prepareSpCall(String SpName, StatementParameters parameters,
			Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}
	
	private void register(StatementParameters parameters, Map<String, ?> fields) {
		if(inOutPramNames != null) {
			for(String name: inOutPramNames)
				parameters.registerInOut(name, client.getColumnType(name), fields.get(name));
		}
		
		if(outputPramNames != null){
			for(String name: outputPramNames)
				parameters.registerOut(name, client.getColumnType(name));
		}
	}
	
	private void extract(StatementParameters parameters, KeyHolder holder) {
		if(holder == null) return;
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if(inOutPramNames != null) {
			for(String name: inOutPramNames)
				map.put(name, parameters.get(name, ParameterDirection.InputOutput).getValue());
		}
		
		if(outputPramNames != null){
			for(String name: outputPramNames)
				map.put(name, parameters.get(name, ParameterDirection.Output).getValue());
		}
		
		holder.getKeyList().add(map);
	}
}
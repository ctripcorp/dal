package com.ctrip.platform.dal.dao;

/**
 * All valid entries for DalHints. Include parameters, config, and user defined entry.
 * userDefined0 to userDefined10 can be used to pass customer parameters.
 * @author jhhe
 */
public enum DalHintEnum {
	/* All the parameters for each Dal client call. Used for locating shard, real database */
	operation, //DalClient.operation
	
	/*sql,
	
	sqls,
	
	callString,
	
	parameters,
	
	parametersList,
	
	commands,*/

	shardColValues,// Map<String, Integer> of column name value pair
	
	shard, // String
	
	shards, // Set<String>
	/* End of parameters for each Dal client call */
	
	/*
	 * used in batch sp, when set the connection auto commit will be true.
	 */
	forceAutoCommit,
	
	/* Settings for initialize statement */
	timeout,
	
	/* 
	 * resultSetType a result set type; one of
     *         <code>ResultSet.TYPE_FORWARD_ONLY</code>,
     *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
     *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code> 
	 */
	resultSetType,
	
	/* 
	 * resultSetConcurrency a concurrency type; one of
     *         <code>ResultSet.CONCUR_READ_ONLY</code> or
     *         <code>ResultSet.CONCUR_UPDATABLE</code>
	 */
    resultSetConcurrency,
    
	/*  */
	fetchSize,
	
	skipResultsProcessing,
	
//	skipUndeclaredResults,
	
	/*  */
	maxRows,
	
	maxFieldSize,
	/* End of settings for initialize connection and statement*/
	
	/* Is the SQL sensitive */
	sensitive,

	/*  */
	masterOnly, 

	/*  */
	startRow,
	
	/*  */
	rowCount,
	
	/* SQL Server flag, for batch CUD operation. Using table as parameter */
	// SPT,
	
	/* for logging */
	callingUrl,
	
	/* For insert, delete, update multiple pojos */ 
	continueOnError,
	
	/*  Connection.TRANSACTION_READ_UNCOMMITTED, Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ, Connection.TRANSACTION_SERIALIZABLE, or Connection.TRANSACTION_NONE.*/
	isolationLevel,
	
	/* cache for store old isolationLevel of connection before we apply isolationLevel. It is for internal use. */
	oldIsolationLevel,
	
	/*
	 * used in DalTableDao, when set the update field can be null value.
	 */
	updateNullField,
	
	/* Allow customization */
	userDefined0,
	
	userDefined1,
	
	userDefined2,
	
	userDefined3,
	
	userDefined4,
	
	userDefined5,
	
	userDefined6,
	
	userDefined7,
	
	userDefined8,
	
	userDefined9,
	
	userDefined10,
}

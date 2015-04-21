package com.ctrip.platform.dal.dao;

/**
 * All valid entries for DalHints. Include parameters, config, and user defined entry.
 * userDefined0 to userDefined10 can be used to pass customer parameters.
 * @author jhhe
 */
public enum DalHintEnum {
	operation, //DalEventEnum

	/*
	 * Value used to help sharding strategy locate DB shard. Can be any type
	 */
	shardValue,
	
	/*
	 * Value used to help sharding strategy locate table shard. Can be any type
	 */
	tableShardValue,
	
	/*
	 * Map<String, Object> of column name value pair. To help sharding strategy locate
	 * shard
	 */
	shardColValues,
	
	/*
	 * Entity columns to help sharding strategy locate shard
	 */
	fields,
	
	/*
	 * StatementParameters to help sharding strategy locate shard
	 */
	parameters,
	
	/*
	 * Explicitly indicate which shard the operation will be performed.
	 * Value should be String
	 */
	shard,
	
	/*
	 * Explicitly indicate which table shard the operation will be performed.
	 * Value should be String
	 */
	tableShard,
	
	/*
	 * used in batch sp, when set the connection auto commit will be true.
	 */
	forceAutoCommit,
	
	/* 
	 * Settings for initialize statement 
	 */
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
    
	/*
	 *  Parameter for statement.setFetchSize(fetchSize); 
	 */
	fetchSize,
	
	/*
	 * Indicate that processing of result set and update count can be skipped for 
	 * stored procedure.
	 */
	skipResultsProcessing,
	
//	skipUndeclaredResults,
	
	/* 
	 * Parameter for statement.setMaxRows(maxRows); 
	 */
	maxRows,
	
	/* End of settings for initialize connection and statement*/
	
	/* 
	 * Is the SQL sensitive, if set, the sql will be replaced by * in the log. 
	 */
	sensitive,

	/* 
	 * Indicate using master database even the operation can be routed to slave database 
	 */
	masterOnly, 
	
	heighAvaliable,
	
	/* 
	 * For insert, delete, update multiple pojos 
	 */ 
	continueOnError,
	
	/*  
	 * Indicate which isolation level should be used to set on conection
	 * Connection.TRANSACTION_READ_UNCOMMITTED, 
	 * Connection.TRANSACTION_READ_COMMITTED, 
	 * Connection.TRANSACTION_REPEATABLE_READ, 
	 * Connection.TRANSACTION_SERIALIZABLE,
	 * Connection.TRANSACTION_NONE.
	 */
	isolationLevel,
	
	/*
	 * used in DalTableDao, when set the update field can be null value.
	 */
	updateNullField,
	
	/**
	 * The detail affected rows by DB and Table shard Id
	 */
	detailResults,
	
	/**
	 * Indicate the cud operation will async execute
	 */
	asyncExecuteCUD
	
}

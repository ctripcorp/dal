package com.ctrip.platform.dal.dao;

public enum DalHintEnum {
	/* All the parameters for each Dal client call. Used for locating real database */
	operation, //DalClient.operation
	
	sql,
	
	sqls,
	
	callString,
	
	parameters,
	
	parametersList,
	
	commands,
	/* End of parameters for each Dal client call */
	
	/* Settings for initialize connection and statement*/
	timeout,
	
	/*  */
	fetchSize,
	
	skipResultsProcessing,
	
//	skipUndeclaredResults,
	
	/*  */
	maxRows,
	
	maxFieldSize,
	/* End of settings for initialize connection and statement*/
	
	/*  */
	shardCol, 
	
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
	usingBatch,
	
	/* For insert, delete, update multiple pojos */ 
	stopOnError,
	
	/*  Connection.TRANSACTION_READ_UNCOMMITTED, Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ, Connection.TRANSACTION_SERIALIZABLE, or Connection.TRANSACTION_NONE.*/
	isolationLevel,
	
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

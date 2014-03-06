package com.ctrip.platform.dal.dao;

public enum DalHintEnum {
	/*  */
	timeout,
	
	/*  */
	fetchSize,
	
	skipResultsProcessing,
	
//	skipUndeclaredResults,
	
	/*  */
	maxRows,
	
	maxFieldSize,

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
	
	/* SQL Server flag. For CUD operation. Using string as parameter */
	SPA,
	
	/* SQL Server flag, for batch CUD operation. Using table as parameter */
	// SPT,
	
	/*  */
	columns,
	// TODO do we need separate operation type like Dal Fx?
	
	/* for logging */
	callingUrl,
	
	/* For insert, delete, update multiple pojos */ 
	usingBatch,
	
	/* For insert, delete, update multiple pojos */ 
	stopOnError,
	
	/*  Connection.TRANSACTION_READ_UNCOMMITTED, Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ, Connection.TRANSACTION_SERIALIZABLE, or Connection.TRANSACTION_NONE.*/
	isolationLevel,
	
	/* Allow customization */
	userDefined,
}

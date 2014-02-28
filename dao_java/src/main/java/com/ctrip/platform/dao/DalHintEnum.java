package com.ctrip.platform.dao;

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

	/*  */
	masterOnly, 

	/*  */
	startRow,
	
	/*  */
	rowCount,
	
	/*  */
	columns,
	// TODO do we need separate operation type like Dal Fx?
	
	/* Allow customization */
	userDefined,
}

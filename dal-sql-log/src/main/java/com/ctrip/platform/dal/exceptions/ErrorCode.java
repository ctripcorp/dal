package com.ctrip.platform.dal.exceptions;

public enum ErrorCode {
	/**
	 * It is expected to return only %s result. But the actually count is %s
	 */
	AssertEqual(ErrorClassify.Assert, 5000, "It is expected to return only %s result. But the actually count is %s"),
	
	/**
	 * There is no result found!
	 */
	AssertGreatThan(ErrorClassify.Assert, 5001, "There is no result found!"),
	
	/**
	 * The primary key of this table is consists of more than one column
	 */
	ValidatePrimaryKeyCount(ErrorClassify.Validate, 5100, "The primary key of this table is consists of more than one column"),
	
	/**
	 * There is no column to be updated. Please check if needed fields have been set in pojo
	 */
	ValidateFieldCount(ErrorClassify.Validate, 5101, "There is no column to be updated. Please check if needed fields have been set in pojo"),
	
	/**
	 * Non or More than one generated keys are returned: %s
	 */
	ValidateKeyHolderSize(ErrorClassify.Validate, 5102, "Non or More than one generated keys are returned: %s"),
	
	/**
	 * Non or More than one entries found for the generated key: %s
	 */
	ValidateKeyHolderFetchSize(ErrorClassify.Validate, 5103, "Non or More than one entries found for the generated key: %s"),
	
	/**
	 * Can not convert generated key to number
	 */
	ValidateKeyHolderConvert(ErrorClassify.Validate, 5104, "Can not convert generated key to number"),
	
	/**
	 * Can not locate shard for %s
	 */
	ShardLocated(ErrorClassify.Shard, 5900, "Can not locate shard for %s"),
	
	/**
	 * No shard defined for id: 
	 */
	NoShardId(ErrorClassify.Shard, 5901,"No shard defined for id: %s"),
	
	/**
	 * No sharding stradegy defined
	 */
	NoShardStradegy(ErrorClassify.Shard, 5902, "No sharding stradegy defined"),
	
	/**
	 * The current transaction is already rolled back or completed
	 */
	TransactionState(ErrorClassify.Transaction, 5600, "The current transaction is already rolled back or completed"),
	
	/**
	 * Transaction level mismatch. Expected: %d Actual: %d
	 */
	TransactionLevelMatch(ErrorClassify.Transaction, 5601, "Transaction level mismatch. Expected: %d Actual: %d"),
	
	/**
	 * DAL do not support distributed transaction. Current DB: %s, DB requested: %s
	 */
	TransactionDistributed(ErrorClassify.Transaction, 5602, "DAL do not support distributed transaction. Current DB: %s, DB requested: %s"),
	
	/**
	 * Calling endTransaction with empty ConnectionCache
	 */
	TransactionEnd(ErrorClassify.Transaction, 5603, "Calling endTransaction with empty ConnectionCache"),
	
	/**
	 * Can not get connection from DB %s
	 */
	CantGetConnection(ErrorClassify.Connection, 5300, "Can not get connection from DB %s"),
	
	MarkdownConnection(ErrorClassify.Connection, 5301, "The DB or allinonekey %s has bean marked down"),
	
	NoMoreConnectionToFailOver(ErrorClassify.Connection, 5300, "There is no more fail over connections to try"),
	
	/**
	 * Logic Db Name is empty!
	 */
	LogicDbEmpty(ErrorClassify.Connection, 5301, "Logic Db Name is empty!"),
	
	Unknown(ErrorClassify.Unknown, 9999 , "Unknown Exception");
	
	private final ErrorClassify classify;
	private final int code;
	private final String msg;
	ErrorCode(ErrorClassify classify, int code, String msg){
		this.classify = classify;
		this.code = code;
		this.msg = msg;
	}

	public int getCode(){
		return this.code;
	}
	
	public String getMessage(){
		return this.msg;
	}
	
	public ErrorClassify getErrorClassify(){
		return this.classify;
	}
}
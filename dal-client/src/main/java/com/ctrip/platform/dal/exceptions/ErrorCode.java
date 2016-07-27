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
	 * It is expected to return only 1 or no result. But the actually count is more than 1.
	 */
	AssertSingle(ErrorClassify.Assert, 5002, "It is expected to return only 1 result. But the actually count is more than 1"),
	
	/**
	 * It is expected to return only 1 or no result. But the actually count is more than 1.
	 */
	AssertNull(ErrorClassify.Assert, 5003, "It is expected to return 1 result. But found none"),
	
	/**
	 * It is expected to return only 1 or no result. But the actually count is more than 1.
	 */
	NotSupported(ErrorClassify.Assert, 5004, "The requested operation is not supported"),

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
	 * The insertion is fail or not completed yet.
	 */
	KeyGenerationFailOrNotCompleted(ErrorClassify.Validate, 5105, "The insertion is fail or not completed yet."),
	
	/**
	 * Sql cannot be null
	 */
	ValidateSql(ErrorClassify.Validate, 5200, "The given sql is null"),
	
	/**
	 * Pojos cannot be null
	 */
	ValidatePojoList(ErrorClassify.Validate, 5201, "The given pojo list is null"),
	
	/**
	 * Pojos cannot be null
	 */
	ValidatePojo(ErrorClassify.Validate, 5202, "The given pojo is null"),
	
	/**
	 * Task cannot be null
	 */
	ValidateTask(ErrorClassify.Validate, 5203, "The given dao task is null. Means the calling DAO method is not supported. Please contact your DAL team."),
	
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
	
	MarkdownConnection(ErrorClassify.Connection, 5301, "The DB or allinonekey [%s] has bean marked down"),
	
	NullLogicDbName(ErrorClassify.Connection, 5302, "The master/slave database set is empty"),
	
	NoMoreConnectionToFailOver(ErrorClassify.Connection, 5303, "There is no more fail over connections to try"),
	
	MarkdownLogicDb(ErrorClassify.Connection, 5304, "Database Set %s has been marked down"),
	
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
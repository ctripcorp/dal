package com.ctrip.platform.dal.exceptions;

import com.ctrip.platform.dal.dao.DalHintEnum;

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
	 * It is expected to return 1 result. But found none
	 */
	AssertNull(ErrorClassify.Assert, 5003, "It is expected to return 1 result. But found none"),
	
	/**
	 * The requested operation is not supported.
	 */
	NotSupported(ErrorClassify.Assert, 5004, "The requested operation is not supported"),

	/**
	 * The requested operation is not supported.
	 */
	MoreThanOneVersionColumn(ErrorClassify.Assert, 5005, "The entity contains more than one version annotation"),

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
	 * There is no field defined in pojo
	 */
	FieldNotExists(ErrorClassify.Validate, 5106, "There is no field defined in pojo %s for column %s. Please check with DalHintEnum.ignoreMissingFields"),
	
    /**
     * Can not put generated primary key back to pojo
     */
    SetPrimaryKeyFailed(ErrorClassify.Validate, 5107, "Can not put generated primary key back to pojo %s for column %s"),
    
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
	 * Version column is null
	 */
	ValidateVersion(ErrorClassify.Validate, 5204, "Version column can not be null"),
	
	/**
	 * Column type is not defined
	 */
	TypeNotDefined(ErrorClassify.Validate, 5206, "Column type is not defined"),
	
	/**
	 * Duplicated column name is found
	 */
	DuplicateColumnName(ErrorClassify.Validate, 5207, "Column name is already used by other field"),
	
	/**
	 * No Database annotation found.
	 */
	NoDatabaseDefined(ErrorClassify.Validate, 5208, "The entity must configure Database annotation."),
	
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
	 * Calling endTransaction with empty ConnectionCache
	 */
	TransactionNoFound(ErrorClassify.Transaction, 5604, "There is no transaction found"),
	
    /**
     * DAL do not support distributed transaction in same db but different shard
     */
    TransactionDistributedShard(ErrorClassify.Transaction, 5605, "DAL do not support distributed transaction in same DB but different shard. Current shard: %s, requested in hints: %s"),
    
    /**
     * The result mapping is faild.
     */
    ResultMappingError(ErrorClassify.Extract, 5700, "Can not extract from result set. If the columns in result set does not match with columns in pojo, please check with DalHintEnum.allowPartial or partialQuery. "
            + "For more info please refer to https://github.com/ctripcorp/dal/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF-Hints%E8%AF%B4%E6%98%8E#allowpartial"),
    
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
	LogicDbEmpty(ErrorClassify.Connection, 5305, "Logic Db Name is empty!"),
	
	InvalidDatabaseKeyName(ErrorClassify.Connection, 5306, "The given database key name is not qualified: %s"),
	
	Unknown(ErrorClassify.Unknown, 9999 , "Unknown Exception, caused by: %s");
	
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
package com.ctrip.platform.dal.dao;

/**
 * All valid entries for DalHints. Include parameters, config, and user defined entry. userDefined0 to userDefined10 can
 * be used to pass customer parameters.
 * 
 * @author jhhe
 */
public enum DalHintEnum {
    /**
     * For user to set what ever he want
     */
    userDefined1,

    /**
     * For user to set what ever he want
     */
    userDefined2,

    /**
     * For user to set what ever he want
     */
    userDefined3,

    operation, // DalEventEnum

    /*
     * Value used to help sharding strategy locate DB shard. Can be any type
     */
    shardValue,

    /*
     * Value used to help sharding strategy locate table shard. Can be any type
     */
    tableShardValue,

    /*
     * Map<String, Object> of column name value pair. To help sharding strategy locate shard
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

    /**
     * Explicitly indicate in which database the operation will be performed. This is because a logic Db can include
     * multiple physical Db. So sometimes we need to select the certain Db Value should be String
     */
    designatedDatabase,

    /*
     * Explicitly indicate in which shard the operation will be performed. Value should be String
     */
    shard,

    /*
     * Explicitly indicate which table shard the operation will be performed. Value should be String
     */
    tableShard,

    /*
     * Indicate that the query will be executed in all shards
     */
    allShards,

    /*
     * Indicate that the query will be executed in the given shards
     */
    shards,

    /*
     * Indicate name of the parameter that will partition shards for the request.
     */
    shardBy,

    /*
     * The merger that is used to merge query result
     */
    resultMerger,

    /*
     * The comparator that is used to sort query result with default merger
     */
    resultSorter,

    /*
     * used in batch sp, when set the connection auto commit will be true.
     */
    forceAutoCommit,

    /*
     * Settings for initialize statement. Sets the number of seconds the driver will wait for a Statement object to
     * execute to the given number of seconds. zero means there is no limit.
     */
    timeout,

    /**
     * Specify how many seconds the slave is behind master. Dal framework does not use it directly. User can customize
     * DatabaseSelector in order to use it
     * 
     */
    freshness,

    /*
     * resultSetType a result set type; one of <code>ResultSet.TYPE_FORWARD_ONLY</code>,
     * <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
     */
    resultSetType,

    /*
     * resultSetConcurrency a concurrency type; one of <code>ResultSet.CONCUR_READ_ONLY</code> or
     * <code>ResultSet.CONCUR_UPDATABLE</code>
     */
    resultSetConcurrency,

    /*
     * Parameter for statement.setFetchSize(fetchSize);
     */
    fetchSize,

    /*
     * Indicate that processing of result set and update count can be skipped for stored procedure.
     */
    skipResultsProcessing,

    /*
     * Auto processing all result set and update count
     */
    retrieveAllSpResults,

    /*
     * Parameter for statement.setMaxRows(maxRows);
     */
    maxRows,

    /* End of settings for initialize connection and statement */

    /*
     * Is the SQL sensitive, if set, the sql will be replaced by * in the log.
     */
    sensitive,

    /*
     * Indicate using master database even the operation can be routed to slave database
     */
    masterOnly,

    /*
     * Indicate using slave database even the operation is not a query
     */
    slaveOnly,

    heighAvaliable,

    /*
     * For insert, delete, update multiple pojos
     */
    continueOnError,

    /*
     * Indicate which isolation level should be used to set on conection Connection.TRANSACTION_READ_UNCOMMITTED,
     * Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ,
     * Connection.TRANSACTION_SERIALIZABLE, Connection.TRANSACTION_NONE.
     */
    isolationLevel,

    /*
     * used in DalTableDao, when set the insert field can be null value.
     */
    insertNullField,

    /*
     * used in DalTableDao, when set the update field can be null value.
     */
    updateNullField,

    /*
     * used in DalTableDao, when set the update field can be unchanged value after select from DB.
     */
    updateUnchangedField,

    /**
     * Indicate the cud operation will async execute
     */
    asyncExecution,

    /*
     * To execute CURD in sequential way.
     */
    sequentialExecution,

    /**
     * Indicate the queryCallback for async execution
     */
    resultCallback,

    /**
     * Indicate the futureResult for async execution when queryCallback is not specified
     */
    futureResult,

    /**
     * allow insert incremental id. So DAL will not remove id from pojo before any of the inser operation
     */
    enableIdentityInsert,

    /**
     * Set generated incremental id back to the original pojo
     */
    setIdentityBack,

    /**
     * Columns that will be excluded for update
     */
    excludedColumns,

    /**
     * Columns that will be included for update
     */
    includedColumns,

    /**
     * If it is OK to allow some column not defined in pojo
     */
    ignoreMissingFields,

    /**
     * Columns that will be included for query
     */
    partialQuery,

    /**
     * Allow columns in result set do not match columns declared in entity. It will populate the common set of columns
     * from result set and entity columns. It request extractor or mapper to be HintsAwareExtractor or HintsAareMapper
     * to do the required work
     */
    allowPartial,

    /**
     * when select all columns, use column names instead of *
     */
    selectByNames,

    // added hints of table shards related
    /*
     * Indicate that the operation will be executed in all table shards
     */
    allTableShards,

    /*
     * Indicate that the operation perhaps will be executed in all table shards(in lowest priority)
     */
    implicitAllTableShards,

    /*
     * Indicate that the operation will be executed in the given table shards
     */
    tableShards,

    /*
     * Indicate name of the parameter that will partition table shards for the request.
     */
    tableShardBy,

    /**
     * @deprecated will be removed in the future
     * Table for the sql.
     */
    specifiedTableName,

    /**
     * The entity class of the table.
     * For customer DalClient to know the type class of result
     */
    resultClass

}

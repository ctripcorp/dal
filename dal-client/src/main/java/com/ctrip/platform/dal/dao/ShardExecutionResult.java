package com.ctrip.platform.dal.dao;

/**
 * @author c7ch23en
 */
public interface ShardExecutionResult<V> extends ExecutionResult<V> {

    String getDbShard();

    String getTableShard();

}

package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.common.enums.ShardingCategory;
import com.ctrip.platform.dal.dao.client.LogEntry;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lilj on 2018/7/27.
 */
public interface DalTaskContext {
    Set<String> getTables();
    ShardingCategory getShardingCategory();
    List<Map<String, Object>> getIdentityFields();
    int getPojosCount();
    DalTaskContext fork();
    long getStatementExecuteTime();
    LogEntry getLogEntry();
}
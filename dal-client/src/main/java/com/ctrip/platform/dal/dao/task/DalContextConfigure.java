package com.ctrip.platform.dal.dao.task;


import com.ctrip.platform.dal.common.enums.ShardingCategory;
import com.ctrip.platform.dal.dao.client.LogEntry;

/**
 * Created by lilj on 2018/8/7.
 */
public interface DalContextConfigure {
   void addTables(String...tables);

   void setShardingCategory(ShardingCategory category);

   void sumExecuteStatementTime(long executeStatementTime);

   void setLogEntry(LogEntry entry);
}

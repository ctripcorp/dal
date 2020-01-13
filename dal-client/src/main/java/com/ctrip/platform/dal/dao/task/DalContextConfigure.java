package com.ctrip.platform.dal.dao.task;


import com.ctrip.platform.dal.common.enums.ShardingCategory;

/**
 * Created by lilj on 2018/8/7.
 */
public interface DalContextConfigure {
   void addTables(String...tables);

   void setShardingCategory(ShardingCategory category);
}

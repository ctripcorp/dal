package com.ctrip.platform.dal.dao.task;


import com.ctrip.platform.dal.common.enums.ShardingCategory;

import java.util.Set;

/**
 * Created by lilj on 2018/7/27.
 */
public interface DalTaskContext {
    Set<String> getTables();
    ShardingCategory getShardingCategory();
    DalTaskContext fork();
}
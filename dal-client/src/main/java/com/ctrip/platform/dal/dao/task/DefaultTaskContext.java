package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.common.enums.ShardingCategory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lilj on 2018/7/27.
 */
public class DefaultTaskContext implements DalTaskContext, DalContextConfigure {
    protected Set<String> tables = new HashSet<>();
    protected ShardingCategory category;

    @Override
    public Set<String> getTables() {
        Set<String> copyTables = new HashSet<>();
        copyTables.addAll(this.tables);
        return copyTables;
    }

    @Override
    public void addTables(String... tables) {
        for (String table : tables)
            this.tables.add(table.toLowerCase());
    }

    @Override
    public ShardingCategory getShardingCategory() {
        return this.category;
    }

    @Override
    public void setShardingCategory(ShardingCategory category) {
        this.category = category;
    }

    @Override
    public DefaultTaskContext fork() {
        DefaultTaskContext taskContext = new DefaultTaskContext();
        taskContext.tables.addAll(this.tables);
        taskContext.category = this.category;
        return taskContext;
    }
}

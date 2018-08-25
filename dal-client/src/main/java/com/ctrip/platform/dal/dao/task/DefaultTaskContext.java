package com.ctrip.platform.dal.dao.task;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lilj on 2018/7/27.
 */
public class DefaultTaskContext implements DalTaskContext, DalTableNameConfigure {
    protected Set<String> tables = new HashSet<>();

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
    public DefaultTaskContext fork() {
        DefaultTaskContext taskContext = new DefaultTaskContext();
        taskContext.tables.addAll(this.tables);
        return taskContext;
    }
}

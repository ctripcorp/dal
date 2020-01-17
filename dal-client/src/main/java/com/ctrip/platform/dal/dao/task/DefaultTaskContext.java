package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.common.enums.ShardingCategory;
import com.ctrip.platform.dal.dao.client.LogEntry;

import java.util.HashSet;
import java.util.Set;
import java.util.*;

/**
 * Created by lilj on 2018/7/27.
 */
public class DefaultTaskContext implements DalTaskContext, DalContextConfigure {
    protected Set<String> tables = new HashSet<>();
    protected ShardingCategory category;

    protected long statementExecuteTime = 0;
    protected LogEntry logEntry;

    protected int pojosCount = 0;
    // cc 20181010
    protected List<Map<String, Object>> identityFields;

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
    public void sumExecuteStatementTime(long executeStatementTime) {
        this.statementExecuteTime += executeStatementTime;
    }

    @Override
    public void setLogEntry(LogEntry entry) {
        this.logEntry = entry;
    }

    public List<Map<String, Object>> getIdentityFields() {
        if (null == identityFields) {
            return null;
        }
        List<Map<String, Object>> copyList = new ArrayList<>(identityFields.size());
        for (Map<String, Object> field : identityFields) {
            Map<String, Object> copyField = new HashMap<>();
            for (String key : field.keySet()) {
                copyField.put(key, field.get(key));
            }
            copyList.add(copyField);
        }
        return copyList;
    }

    public void setIdentityFields(List<Map<String, Object>> identityFields) {
        this.identityFields = identityFields;
    }

    public int getPojosCount() {
        return pojosCount;
    }

    public void setPojosCount(int pojosCount) {
        this.pojosCount = pojosCount;
    }

    @Override
    public DefaultTaskContext fork() {
        DefaultTaskContext taskContext = new DefaultTaskContext();
        taskContext.tables.addAll(this.tables);
        taskContext.category = this.category;
        taskContext.identityFields = getIdentityFields();
        taskContext.pojosCount = this.pojosCount;
        return taskContext;
    }

    @Override
    public long getStatementExecuteTime() {
        return this.statementExecuteTime;
    }

    @Override
    public LogEntry getLogEntry() {
        return this.logEntry;
    }
}

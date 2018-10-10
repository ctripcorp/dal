package com.ctrip.platform.dal.dao.task;

import java.util.*;

/**
 * Created by lilj on 2018/7/27.
 */
public class DefaultTaskContext implements DalTaskContext, DalTableNameConfigure {
    protected Set<String> tables = new HashSet<>();

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
    public List<Map<String, Object>> getIdentityFields() {
        if (null == identityFields || identityFields.size() == 0) {
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

    @Override
    public DefaultTaskContext fork() {
        DefaultTaskContext taskContext = new DefaultTaskContext();
        taskContext.tables.addAll(this.tables);
        return taskContext;
    }
}

package com.ctrip.platform.dal.dao.task;

public class CombinedReplaceTask<T> extends AbstractCombinedInsertTask<T> {
    public static final String TPL_SQL_COMBINED_REPLACE = "REPLACE INTO %s(%s) VALUES %s";

    @Override
    protected String getSqlTpl() {
        return TPL_SQL_COMBINED_REPLACE;
    }
}

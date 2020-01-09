package com.ctrip.platform.dal.dao.task;


public class BatchReplaceTask<T> extends AbstractBatchInsertTask<T> {
    private static final String TPL_SQL_BATCH_REPLACE = "REPLACE INTO %s (%s) VALUES (%s)";

    @Override
    protected String getSqlTpl() {
        return TPL_SQL_BATCH_REPLACE;
    }
}

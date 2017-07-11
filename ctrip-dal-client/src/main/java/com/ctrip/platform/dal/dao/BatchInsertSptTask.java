package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.task.BulkTaskContext;

import java.sql.SQLException;
import java.util.Map;

public class BatchInsertSptTask<T> extends CtripSptTask<T> {
    private static final String INSERT_SPT_TPL = "spT_%s_i";

    @Override
    public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, BulkTaskContext<T> taskContext)
            throws SQLException {
        return execute(hints, daoPojos, taskContext, INSERT_SPT_TPL);
    }

}
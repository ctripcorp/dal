package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.task.BulkTaskContext;

import java.sql.SQLException;
import java.util.Map;

public class BatchDeleteSptTask<T> extends CtripSptTask<T> {
    private static final String DELETE_SPT_TPL = "spT_%s_d";

    @Override
    public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, BulkTaskContext<T> taskContext)
            throws SQLException {
        return execute(hints, daoPojos, taskContext, DELETE_SPT_TPL);
    }
}

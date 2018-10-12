package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.task.AbstractIntArrayBulkTask;

public abstract class CtripSp3Task<T> extends AbstractIntArrayBulkTask<T> {
    protected static final String CALL_SP_BY_NAME = "callSpbyName";
    protected static final String CALL_SP_BY_SQLSEVER = "callSpbySqlServerSyntax";
    protected static final String CALL_SPT = "callSpt";

    public void initialize(DalParser<T> parser) {
        super.initialize(parser);
        CallSpByIndexValidator.validate(parser, Boolean.parseBoolean(getTaskSetting(CALL_SP_BY_NAME)));
    }
}

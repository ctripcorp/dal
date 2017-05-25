package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.task.AbstractIntArrayBulkTask;

public abstract class CtripSp3Task<T> extends AbstractIntArrayBulkTask<T> {
    public void initialize(DalParser<T> parser) {
        super.initialize(parser);
        CallSpByIndexValidator.validate(parser, CtripTaskFactory.callSpbyName);        
    }
}

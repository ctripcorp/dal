package com.ctrip.platform.dal.dao.callBySpt;

import com.ctrip.platform.dal.dao.BaseSingleInsertTest;
import com.ctrip.platform.dal.dao.CtripTaskFactory;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.task.SingleTask;

public class SingleInsertTest extends BaseSingleInsertTest {
    @Override
    public <T> SingleTask<T> getTest(DalParser<T> parser) {
        return new CtripTaskFactory().createSingleInsertTask(parser);
    }
}

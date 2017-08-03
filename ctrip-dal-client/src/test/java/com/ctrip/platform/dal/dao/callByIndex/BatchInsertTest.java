package com.ctrip.platform.dal.dao.callByIndex;

import com.ctrip.platform.dal.dao.BaseBatchInsertTest;
import com.ctrip.platform.dal.dao.CtripTaskFactory;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.task.BulkTask;

public class BatchInsertTest extends BaseBatchInsertTest {
    @Override
    public <T> BulkTask<int[], T> getTest(DalParser<T> parser) {
        return (BulkTask<int[], T>)new CtripTaskFactory().createBatchInsertTask(parser);
    }
}

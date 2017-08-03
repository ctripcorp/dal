package com.ctrip.platform.dal.dao.callBySpt;

import com.ctrip.platform.dal.dao.BaseBatchDeleteTest;
import com.ctrip.platform.dal.dao.CtripTaskFactory;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.task.BulkTask;

public class BatchDeleteTest extends BaseBatchDeleteTest {
    public <T> BulkTask<int[], T> getTest(DalParser<T> parser) {
        return (BulkTask<int[], T>)new CtripTaskFactory().createBatchDeleteTask(parser);
    }
}

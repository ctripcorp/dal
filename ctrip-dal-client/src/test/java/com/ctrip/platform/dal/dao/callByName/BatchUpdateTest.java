package com.ctrip.platform.dal.dao.callByName;

import com.ctrip.platform.dal.dao.BaseBatchUpdateTest;
import com.ctrip.platform.dal.dao.CtripTaskFactory;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.task.BulkTask;

public class BatchUpdateTest extends BaseBatchUpdateTest {
    @Override
    public <T> BulkTask<int[], T> getTest(DalParser<T> parser) {
        return (BulkTask<int[], T>)new CtripTaskFactory().createBatchUpdateTask(parser);
    }
}

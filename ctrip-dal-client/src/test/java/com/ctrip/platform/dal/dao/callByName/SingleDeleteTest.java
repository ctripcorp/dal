package com.ctrip.platform.dal.dao.callByName;

import com.ctrip.platform.dal.dao.BaseSingleDeleteTest;
import com.ctrip.platform.dal.dao.CtripTaskFactory;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.task.SingleTask;

public class SingleDeleteTest extends BaseSingleDeleteTest {

    @Override
    public <T> SingleTask<T> getTest(DalParser<T> parser) {
        return new CtripTaskFactory().createSingleDeleteTask(parser);
    }
}

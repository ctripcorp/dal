package com.ctrip.platform.dal.dao.callByNativeSyntax;

import com.ctrip.platform.dal.dao.BaseBatchUpdateTest;
import com.ctrip.platform.dal.dao.CtripTaskFactory;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.task.BulkTask;

public class BatchUpdateTest extends BaseBatchUpdateTest {
    @Override
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpByNativeSyntax();
    }
}

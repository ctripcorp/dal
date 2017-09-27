package com.ctrip.platform.dal.dao.callBySpt;

import com.ctrip.platform.dal.dao.BaseBatchDeleteTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class BatchDeleteTest extends BaseBatchDeleteTest {
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpBySpt();
    }
}

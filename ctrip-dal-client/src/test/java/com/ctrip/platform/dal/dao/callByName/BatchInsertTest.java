package com.ctrip.platform.dal.dao.callByName;

import com.ctrip.platform.dal.dao.BaseBatchInsertTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class BatchInsertTest extends BaseBatchInsertTest {
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpByName();
    }
}

package com.ctrip.platform.dal.dao.callByIndex;

import com.ctrip.platform.dal.dao.BaseSingleInsertTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class SingleInsertTest extends BaseSingleInsertTest {
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpByIndex();
    }
}

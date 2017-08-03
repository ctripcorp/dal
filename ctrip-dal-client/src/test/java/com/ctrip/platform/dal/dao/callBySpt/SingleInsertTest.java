package com.ctrip.platform.dal.dao.callBySpt;

import com.ctrip.platform.dal.dao.BaseSingleInsertTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class SingleInsertTest extends BaseSingleInsertTest {
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpBySpt();
    }
}

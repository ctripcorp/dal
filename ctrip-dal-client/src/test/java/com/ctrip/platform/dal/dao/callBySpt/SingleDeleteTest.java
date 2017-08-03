package com.ctrip.platform.dal.dao.callBySpt;

import com.ctrip.platform.dal.dao.BaseSingleDeleteTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class SingleDeleteTest extends BaseSingleDeleteTest {
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpBySpt();
    }
}

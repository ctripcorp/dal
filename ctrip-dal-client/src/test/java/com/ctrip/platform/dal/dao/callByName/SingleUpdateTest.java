package com.ctrip.platform.dal.dao.callByName;

import com.ctrip.platform.dal.dao.BaseSingleUpdateTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class SingleUpdateTest extends BaseSingleUpdateTest {
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpByName();
    }
}

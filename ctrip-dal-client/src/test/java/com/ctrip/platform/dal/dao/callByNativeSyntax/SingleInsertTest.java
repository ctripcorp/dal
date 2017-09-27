package com.ctrip.platform.dal.dao.callByNativeSyntax;

import com.ctrip.platform.dal.dao.BaseSingleInsertTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class SingleInsertTest extends BaseSingleInsertTest {
    @Override
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpByNativeSyntax();
    }
}

package com.ctrip.platform.dal.dao.callByNativeSyntax;

import com.ctrip.platform.dal.dao.BaseSingleDeleteTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class SingleDeleteTest extends BaseSingleDeleteTest {
    @Override
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpByNativeSyntax();
    }
}

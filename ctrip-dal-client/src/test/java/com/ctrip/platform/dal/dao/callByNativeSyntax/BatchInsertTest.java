package com.ctrip.platform.dal.dao.callByNativeSyntax;

import com.ctrip.platform.dal.dao.BaseBatchInsertTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class BatchInsertTest extends BaseBatchInsertTest {
    @Override
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpByNativeSyntax();
    }
}

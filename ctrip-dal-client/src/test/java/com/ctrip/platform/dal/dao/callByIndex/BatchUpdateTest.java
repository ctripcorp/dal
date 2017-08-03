package com.ctrip.platform.dal.dao.callByIndex;

import com.ctrip.platform.dal.dao.BaseBatchUpdateTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class BatchUpdateTest extends BaseBatchUpdateTest {
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpByIndex();
    }
}

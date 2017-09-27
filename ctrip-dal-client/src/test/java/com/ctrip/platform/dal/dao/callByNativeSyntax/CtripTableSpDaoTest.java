package com.ctrip.platform.dal.dao.callByNativeSyntax;

import com.ctrip.platform.dal.dao.BaseCtripTableSpDaoTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class CtripTableSpDaoTest extends BaseCtripTableSpDaoTest {
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpByNativeSyntax();
    }
}

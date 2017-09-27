package com.ctrip.platform.dal.dao.callBySpt;

import com.ctrip.platform.dal.dao.BaseCtripTableSpDaoTest;
import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

public class CtripTableSpDaoTest extends BaseCtripTableSpDaoTest {
    public void setOptionTest() {
        CtripTaskFactoryOptionSetter.callSpBySpt();
    }
}

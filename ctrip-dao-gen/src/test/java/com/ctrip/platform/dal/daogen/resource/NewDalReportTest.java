package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.DalReportDao;
import org.junit.Test;

public class NewDalReportTest {
    @Test
    public void test() throws Exception {
        DalReportDao dao = new DalReportDao();
        dao.runTask();
    }

}

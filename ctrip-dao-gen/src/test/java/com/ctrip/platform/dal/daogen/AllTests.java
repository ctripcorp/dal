package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.resource.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AlldbTest.class,
        DalDynamicDSTest.class,
        NewDalReportTest.class,
        TitanKeyInfoReportTest.class,
        DecryptResourceTest.class
})
public class AllTests {
}

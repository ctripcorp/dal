package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.resource.*;
import com.ctrip.platform.dal.daogen.util.DBLevelInfoApiTest;
import com.ctrip.platform.dal.daogen.util.DalClusterInfoApiTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AlldbTest.class,
        DalDynamicDSTest.class,
        NewDalReportTest.class,
        TitanKeyInfoReportTest.class,
        DecryptResourceTest.class,
        DalClusterInfoApiTest.class,
        DBLevelInfoApiTest.class
})
public class AllTests {
}

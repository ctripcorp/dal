package com.ctrip.framework.db.cluster;

import com.ctrip.framework.db.cluster.service.repository.ClusterServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by @author zhuYongMing on 2019/10/26.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    ClusterServiceTest.class,
})
public class AllTests {

}

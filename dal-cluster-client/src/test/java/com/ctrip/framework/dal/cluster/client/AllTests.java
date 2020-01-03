package com.ctrip.framework.dal.cluster.client;

import com.ctrip.framework.dal.cluster.client.cluster.DefaultClusterTest;
import com.ctrip.framework.dal.cluster.client.cluster.DefaultLocalConfigProviderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by @author zhuYongMing on 2019/11/29.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        DefaultClusterTest.class,
        DefaultLocalConfigProviderTest.class
})
public class AllTests {
}

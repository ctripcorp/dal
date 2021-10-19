package com.ctrip.framework.dal.cluster.client;

import com.ctrip.framework.dal.cluster.client.cluster.DefaultClusterTest;
import com.ctrip.framework.dal.cluster.client.cluster.DefaultLocalConfigProviderTest;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLParserTest;
import com.ctrip.framework.dal.cluster.client.database.DummyDatabaseTest;
import com.ctrip.framework.dal.cluster.client.extended.CustomDataSourceFactoryTest;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitivePropertiesTest;
import com.ctrip.framework.dal.cluster.client.util.ObjectHolderTest;
import com.ctrip.framework.dal.cluster.client.util.PropertiesUtilsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by @author zhuYongMing on 2019/11/29.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ClusterConfigXMLParserTest.class,
        PropertiesUtilsTest.class,
        DummyDatabaseTest.class,
        CustomDataSourceFactoryTest.class,
        DefaultClusterTest.class,
        DefaultLocalConfigProviderTest.class,
        ObjectHolderTest.class,
        CaseInsensitivePropertiesTest.class
})
// test: 30/30 passed/all env:fat
public class AllTests {
}

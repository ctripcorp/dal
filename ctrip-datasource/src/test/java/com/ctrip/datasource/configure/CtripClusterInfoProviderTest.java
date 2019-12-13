package com.ctrip.datasource.configure;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import org.junit.Assert;
import org.junit.Test;

public class CtripClusterInfoProviderTest {

    private CtripClusterInfoProvider provider;

    public CtripClusterInfoProviderTest() {
        provider = new CtripClusterInfoProvider(DalPropertiesManager.getInstance().getDalPropertiesLocator(), HttpExecutor.getInstance());
    }

    @Test
    public void doTest() {
        ClusterInfo clusterInfo = provider.getClusterInfo("fltorderprocessviewshard01db_w");
        Assert.assertNotNull(clusterInfo);
    }

}

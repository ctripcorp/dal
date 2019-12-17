package com.ctrip.datasource.configure;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;

public class CtripClusterInfoProviderTest {

    private CtripClusterInfoProvider provider;

    public CtripClusterInfoProviderTest() {
        provider = new CtripClusterInfoProvider(DalPropertiesManager.getInstance().getDalPropertiesLocator(), HttpExecutor.getInstance());
    }

    @Test
    public void doTest() {
        ClusterInfo clusterInfo = provider.getClusterInfo("DalService2DB_W");
        Assert.assertNotNull(clusterInfo);
    }

    @Test
    public void testGetClusterDataSource() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        DataSource dataSource = factory.createDataSource("DalService2DB_W");
        Assert.assertNotNull(dataSource);
    }

}

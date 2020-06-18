package com.ctrip.datasource.configure;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.datasource.util.CtripEnvUtils;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;

public class CtripClusterInfoProviderTest {

    private static final EnvUtils envUtils = DalElementFactory.DEFAULT.getEnvUtils();

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

    @Test
    public void testCheckEnv() {
        CtripEnvUtils ctripEnvUtils = (CtripEnvUtils) envUtils;

        ctripEnvUtils.clear();
        ctripEnvUtils.setEnv("fat");
        Assert.assertTrue(provider.checkEnv());

        ctripEnvUtils.clear();
        ctripEnvUtils.setEnv("fat");
        ctripEnvUtils.setSubEnv("sub");
        Assert.assertFalse(provider.checkEnv());

        ctripEnvUtils.clear();
        ctripEnvUtils.setEnv("pro");
        Assert.assertTrue(provider.checkEnv());

        ctripEnvUtils.clear();
        ctripEnvUtils.setEnv("pro");
        ctripEnvUtils.setSubEnv("sub");
        Assert.assertTrue(provider.checkEnv());

        ctripEnvUtils.clear();
        ctripEnvUtils.setEnv("pro");
        ctripEnvUtils.setIdc("shaoy");
        Assert.assertTrue(provider.checkEnv());

        ctripEnvUtils.clear();
        ctripEnvUtils.setEnv("pro");
        ctripEnvUtils.setIdc("fra-aws");
        Assert.assertFalse(provider.checkEnv());

        ctripEnvUtils.clear();
    }

}

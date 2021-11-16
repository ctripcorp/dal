package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.platform.dal.dao.configure.DefaultDalConfigCustomizedOption;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static com.ctrip.framework.dal.cluster.client.extended.CustomDataSourceConfigureConstants.DATASOURCE_FACTORY;
import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.DRIVER_CLASS_NAME;

/**
 * @Author limingdong
 * @create 2021/10/18
 */
public class ClusterConfigXMLParserTest {

    public static final String PATH = "qconfig_cluster_ch_config.xml";

    private ClusterConfigXMLParser clusterConfigXMLParser;


    @Before
    public void setUp() throws Exception {
        clusterConfigXMLParser = new ClusterConfigXMLParser();
    }

    @Test
    public void parse() {
        DalConfigCustomizedOption customizedOption = new DefaultDalConfigCustomizedOption();
        ClusterConfigImpl clusterConfig = (ClusterConfigImpl) clusterConfigXMLParser.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH), customizedOption);
        Properties properties = clusterConfig.getCustomProperties();
        Assert.assertNotNull(properties.getProperty(DATASOURCE_FACTORY));
        Assert.assertNotNull(properties.getProperty(DRIVER_CLASS_NAME));
    }
}
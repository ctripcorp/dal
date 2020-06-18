package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

public class DalDataSourceFactoryTest {
    private static final EnvUtils envUtils = DalElementFactory.DEFAULT.getEnvUtils();

    private static final String name = "mysqldaltest01db_W";

    private static final String NULL_CONFIGURE_NAME = "nullConfigure";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // DalClientFactory.initClientFactory();
    }

    @Test
    public void testDalDataSourceFactoryCreateDataSource() throws Exception {
        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(name);
            DataSourceLocator locator = new DataSourceLocator();
            DataSource dataSource = locator.getDataSource(name);
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            String url = metaData.getURL();
            Assert.assertTrue(url != null);
        } catch (Throwable e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testSpringCreateDataSource() throws Exception {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
            DataSource dataSource = (DataSource) context.getBean("dataSource");
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            String url = metaData.getURL();
            Assert.assertTrue(url != null);
        } catch (Throwable e) {
            Assert.fail();
        }

        // String resource = "mybatis-config.xml";
        // Reader reader = Resources.getResourceAsReader(resource);
        // SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        // SqlSession session = sqlSessionFactory.openSession();
    }

    @Test
    public void testDalDataSourceFactoryCreateForceSwitchDataSource() {
        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(NULL_CONFIGURE_NAME, true);
            Assert.assertTrue(true);
        } catch (Throwable e) {
            Assert.assertTrue(false);
        }
    }

//    @Test
    public void testGetOrCreateNonShardingDataSource() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        try {
            factory.getOrCreateNonShardingDataSource("non-exist-cluster");
            Assert.fail("non-exist-cluster");
        } catch (Exception e) {
            // ok
        }
        DataSource ds = factory.getOrCreateNonShardingDataSource("cluster_config_1");
        Assert.assertNotNull(ds);
        try {
            factory.getOrCreateNonShardingDataSource("cluster_config_sharding");
            Assert.fail("cluster_config_sharding");
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

    @Test
    public void testGetOrCreateNonShardingDataSourceFat() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        try {
            factory.getOrCreateNonShardingDataSource("non-exist-cluster");
            Assert.fail("non-exist-cluster");
        } catch (Exception e) {
            // ok
        }
        DataSource ds = factory.getOrCreateNonShardingDataSource("dalservice2db_dalcluster");
        Assert.assertNotNull(ds);
        try {
            factory.getOrCreateNonShardingDataSource("dal_sharding_cluster");
            Assert.fail("dal_sharding_cluster");
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

//    @Test
    public void testGetOrCreateAllMasterDataSources() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        List<DataSource> dss = factory.getOrCreateAllMasterDataSources("cluster_config_sharding_with_slaves");
        Assert.assertEquals(2, dss.size());
    }

    @Test
    public void testGetOrCreateAllMasterDataSourcesFat() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        List<DataSource> dss = factory.getOrCreateAllMasterDataSources("dal_sharding_cluster");
        Assert.assertEquals(4, dss.size());
    }

}

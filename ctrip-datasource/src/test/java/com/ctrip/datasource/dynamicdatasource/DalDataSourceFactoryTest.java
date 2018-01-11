package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class DalDataSourceFactoryTest {
    private static final String name = "mysqldaltest01db_W";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // DalClientFactory.initClientFactory();
    }

    @Test
    public void testDalDataSourceFactoryCreateDataSource() throws Exception {
        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(name);
        } catch (Throwable e) {
            Assert.assertTrue(false);
        }

        DataSourceLocator locator = new DataSourceLocator();
        DataSource dataSource = locator.getDataSource(name);
        Assert.assertTrue(dataSource != null);
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
            Assert.assertTrue(false);
        }

        // String resource = "mybatis-config.xml";
        // Reader reader = Resources.getResourceAsReader(resource);
        // SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        // SqlSession session = sqlSessionFactory.openSession();
    }

}

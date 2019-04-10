package com.ctrip.datasource.spring.integration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.InterceptorChain;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.datasource.spring.annotation.EnableDalMybatis;
import org.springframework.test.util.ReflectionTestUtils;

public class MybatisIntegrationTest {

    @Test
    public void testIntegration() throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        SqlSessionFactory sqlSessionFactory = context.getBean(SqlSessionFactory.class);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            sqlSession.getMapper(AppMapper.class).queryAll(0, 10);
        } finally {
            sqlSession.close();
        }

        // It takes 30 seconds for Cat to send out SQL transactions
        TimeUnit.SECONDS.sleep(35);
    }

    @Test
    public void testInitializingBean() {
        try {
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            SqlSessionFactory factory = context.getBean(SqlSessionFactory.class);
            org.apache.ibatis.session.Configuration configuration = factory.getConfiguration();
            InterceptorChain interceptorChain =
                    (InterceptorChain) ReflectionTestUtils.getField(configuration, "interceptorChain");
            if (interceptorChain == null)
                Assert.fail();

            List<Interceptor> interceptors =
                    (List<Interceptor>) ReflectionTestUtils.getField(interceptorChain, "interceptors");
            if (interceptors == null)
                Assert.fail();

            Assert.assertTrue(interceptors.size() == 3);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @EnableDalMybatis
    @Configuration
    static class AppConfig {
        private DalDataSourceFactory factory = new DalDataSourceFactory();

        @Bean
        public DataSource dataSource() throws Exception {
            return factory.createDataSource("mysqldaltest01db_R");
        }

        @Bean
        public SqlSessionFactory sessionFactory(ResourceLoader resourceLoader) throws Exception {
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("test", transactionFactory, dataSource());
            org.apache.ibatis.session.Configuration configuration =
                    new org.apache.ibatis.session.Configuration(environment);
            configuration.addMapper(AppMapper.class);
            return new SqlSessionFactoryBuilder().build(configuration);
        }

    }
}

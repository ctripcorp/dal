package com.ctrip.datasource.spring.integration;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.datasource.spring.annotation.EnableDalMybatis;

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

    //It takes 30 seconds for Cat to send out SQL transactions
    TimeUnit.SECONDS.sleep(35);
  }

  @EnableDalMybatis
  @Configuration
  static class AppConfig {
    private DalDataSourceFactory factory = new DalDataSourceFactory();

    @Bean
    public DataSource dataSource() throws Exception {
      return factory.createDataSource("mysqltitantest04db_W");
    }

    @Bean
    public SqlSessionFactory sessionFactory(ResourceLoader resourceLoader) throws Exception {
      TransactionFactory transactionFactory = new JdbcTransactionFactory();
      Environment environment = new Environment("test", transactionFactory, dataSource());
      org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(environment);
      configuration.addMapper(AppMapper.class);
      return new SqlSessionFactoryBuilder().build(configuration);
    }

  }
}

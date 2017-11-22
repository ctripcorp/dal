package com.ctrip.framework.dal.demo;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.datasource.spring.annotation.EnableDalMybatis;
import com.ctrip.framework.dal.demo.mapper.AppMapper;
import com.ctrip.framework.dal.demo.mapper.SqlServerAppMapper;

@EnableDalMybatis(encryptParameters = false)
@Configuration
public class DemoConfig {

  private DalDataSourceFactory factory = new DalDataSourceFactory();

  @Bean
  public DataSource mysqlDataSource() throws Exception {
    return factory.createDataSource("mysqltitantest04db_W");
  }

  @Bean
  public PlatformTransactionManager mysqlTransactionManager() throws Exception {
    return new DataSourceTransactionManager(mysqlDataSource());
  }

  @Bean
  public SqlSessionFactoryBean mysqlSessionFactory(ResourceLoader resourceLoader) throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(mysqlDataSource());
    sqlSessionFactoryBean.setConfigLocation(resourceLoader.getResource("classpath:mybatis-config.xml"));

    return sqlSessionFactoryBean;
  }

  @Bean
  public SqlSessionTemplate mysqlSqlSessionTemplate(@Autowired @Qualifier("mysqlSessionFactory")
                                                    SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  @Bean
  public AppMapper appMapper(@Autowired @Qualifier("mysqlSqlSessionTemplate")
      SqlSessionTemplate sqlSessionTemplate) {
    return sqlSessionTemplate.getMapper(AppMapper.class);
  }

  @Bean
  public DataSource sqlServerDataSource() throws Exception {
    return factory.createDataSource("DalServiceDB");
  }

  @Bean
  public SqlSessionFactory sqlServerSessionFactory(ResourceLoader resourceLoader) throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(sqlServerDataSource());
    sqlSessionFactoryBean.setConfigLocation(resourceLoader.getResource("classpath:mybatis-config.xml"));

    return sqlSessionFactoryBean.getObject();
  }

  @Bean
  public SqlSessionTemplate sqlServerSqlSessionTemplate(@Autowired @Qualifier("sqlServerSessionFactory")
      SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  @Bean
  public SqlServerAppMapper sqlServerAppMapper(@Autowired @Qualifier("sqlServerSqlSessionTemplate")
      SqlSessionTemplate sqlSessionTemplate) {
    return sqlSessionTemplate.getMapper(SqlServerAppMapper.class);
  }

}

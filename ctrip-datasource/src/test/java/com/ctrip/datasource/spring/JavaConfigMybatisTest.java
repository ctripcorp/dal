package com.ctrip.datasource.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ctrip.datasource.mybatis.interceptor.AbstractExecutorInterceptor;
import com.ctrip.datasource.mybatis.interceptor.AbstractStatementPrepareHandlerInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ctrip.datasource.mybatis.interceptor.ExecutorInterceptor;
import com.ctrip.datasource.mybatis.interceptor.StatementHandlerInterceptor;
import com.ctrip.datasource.spring.annotation.EnableDalMybatis;

public class JavaConfigMybatisTest extends AbstractMybatisTest {
  @Test
  public void testEnableDalMybatis() throws Exception {
    new AnnotationConfigApplicationContext(AppConfig1.class, CommonConfig.class);

    final ArgumentCaptor<Interceptor> captor = ArgumentCaptor.forClass(Interceptor.class);
    final ArgumentCaptor<Interceptor> anotherCaptor = ArgumentCaptor.forClass(Interceptor.class);

    verify(someConfiguration, times(3)).addInterceptor(captor.capture());
    verify(anotherConfiguration, times(3)).addInterceptor(anotherCaptor.capture());

    verify(someConfiguration, times(1)).addInterceptor(any(AbstractExecutorInterceptor.class));
    verify(anotherConfiguration, times(1)).addInterceptor(any(AbstractExecutorInterceptor.class));
    verify(someConfiguration, times(1)).addInterceptor(any(AbstractStatementPrepareHandlerInterceptor.class));
    verify(anotherConfiguration, times(1)).addInterceptor(any(AbstractStatementPrepareHandlerInterceptor.class));

    StatementHandlerInterceptor someStatementHandlerInterceptor = getFirstMatchingTypeFromCaptor(captor,
        StatementHandlerInterceptor.class);
    StatementHandlerInterceptor anotherStatementHandlerInterceptor = getFirstMatchingTypeFromCaptor(captor,
        StatementHandlerInterceptor.class);

    assertNotNull(someStatementHandlerInterceptor);
    assertEquals(someStatementHandlerInterceptor, anotherStatementHandlerInterceptor);
    assertTrue(someStatementHandlerInterceptor.isNeedEncryptParameters());
  }

  @Test
  public void testEnableDalMybatisMultipleTimes() throws Exception {
    new AnnotationConfigApplicationContext(AppConfig1.class, AppConfig4.class, CommonConfig.class);

    final ArgumentCaptor<Interceptor> captor = ArgumentCaptor.forClass(Interceptor.class);
    final ArgumentCaptor<Interceptor> anotherCaptor = ArgumentCaptor.forClass(Interceptor.class);

    verify(someConfiguration, times(3)).addInterceptor(captor.capture());
    verify(anotherConfiguration, times(3)).addInterceptor(anotherCaptor.capture());

    verify(someConfiguration, times(1)).addInterceptor(any(AbstractExecutorInterceptor.class));
    verify(anotherConfiguration, times(1)).addInterceptor(any(AbstractExecutorInterceptor.class));
    verify(someConfiguration, times(1)).addInterceptor(any(AbstractStatementPrepareHandlerInterceptor.class));
    verify(anotherConfiguration, times(1)).addInterceptor(any(AbstractStatementPrepareHandlerInterceptor.class));

    StatementHandlerInterceptor someStatementHandlerInterceptor = getFirstMatchingTypeFromCaptor(captor,
        StatementHandlerInterceptor.class);
    StatementHandlerInterceptor anotherStatementHandlerInterceptor = getFirstMatchingTypeFromCaptor(captor,
        StatementHandlerInterceptor.class);

    assertNotNull(someStatementHandlerInterceptor);
    assertEquals(someStatementHandlerInterceptor, anotherStatementHandlerInterceptor);
    assertTrue(someStatementHandlerInterceptor.isNeedEncryptParameters());
  }

  @Test
  public void testEnableDalMybatisWithEncryptionDisabled() throws Exception {
    new AnnotationConfigApplicationContext(AppConfig3.class, CommonConfig.class);

    final ArgumentCaptor<Interceptor> captor = ArgumentCaptor.forClass(Interceptor.class);
    final ArgumentCaptor<Interceptor> anotherCaptor = ArgumentCaptor.forClass(Interceptor.class);

    verify(someConfiguration, times(3)).addInterceptor(captor.capture());
    verify(anotherConfiguration, times(3)).addInterceptor(anotherCaptor.capture());

    verify(someConfiguration, times(1)).addInterceptor(any(AbstractExecutorInterceptor.class));
    verify(anotherConfiguration, times(1)).addInterceptor(any(AbstractExecutorInterceptor.class));
    verify(someConfiguration, times(1)).addInterceptor(any(AbstractStatementPrepareHandlerInterceptor.class));
    verify(anotherConfiguration, times(1)).addInterceptor(any(AbstractStatementPrepareHandlerInterceptor.class));

    StatementHandlerInterceptor someStatementHandlerInterceptor = getFirstMatchingTypeFromCaptor(captor,
        StatementHandlerInterceptor.class);
    StatementHandlerInterceptor anotherStatementHandlerInterceptor = getFirstMatchingTypeFromCaptor(captor,
        StatementHandlerInterceptor.class);

    assertNotNull(someStatementHandlerInterceptor);
    assertEquals(someStatementHandlerInterceptor, anotherStatementHandlerInterceptor);
    assertFalse(someStatementHandlerInterceptor.isNeedEncryptParameters());
  }

  @Test
  public void testDisableDalMybatis() throws Exception {
    new AnnotationConfigApplicationContext(AppConfig2.class, CommonConfig.class);

    verify(someConfiguration, never()).addInterceptor(any(Interceptor.class));
    verify(anotherConfiguration, never()).addInterceptor(any(Interceptor.class));
  }

  @Configuration
  static class CommonConfig {

    @Bean
    public SqlSessionFactory someSqlSessionFactory() {
      return someSqlSessionFactory;
    }

    @Bean
    public SqlSessionFactory anotherSqlSessionFactory() {
      return anotherSqlSessionFactory;
    }
  }

  @EnableDalMybatis
  @Configuration
  static class AppConfig1{
  }

  @EnableDalMybatis(tracing = false)
  @Configuration
  static class AppConfig2{
  }

  @EnableDalMybatis(encryptParameters = false)
  @Configuration
  static class AppConfig3{
  }

  @EnableDalMybatis
  @Configuration
  static class AppConfig4{
  }
}

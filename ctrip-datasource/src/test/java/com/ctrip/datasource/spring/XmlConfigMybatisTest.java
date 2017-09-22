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
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ctrip.datasource.mybatis.interceptor.ExecutorInterceptor;
import com.ctrip.datasource.mybatis.interceptor.StatementHandlerInterceptor;

public class XmlConfigMybatisTest extends AbstractMybatisTest {

  @Test
  public void testEnableDalMybatis() throws Exception {
    new ClassPathXmlApplicationContext("spring/XmlConfigMybatisTest1.xml", "spring/CommonXmlConfig.xml");

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
    new ClassPathXmlApplicationContext("spring/XmlConfigMybatisTest4.xml", "spring/CommonXmlConfig.xml");

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
    new ClassPathXmlApplicationContext("spring/XmlConfigMybatisTest3.xml", "spring/CommonXmlConfig.xml");

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
    new ClassPathXmlApplicationContext("spring/XmlConfigMybatisTest2.xml", "spring/CommonXmlConfig.xml");

    verify(someConfiguration, never()).addInterceptor(any(Interceptor.class));
    verify(anotherConfiguration, never()).addInterceptor(any(Interceptor.class));
  }

  static class MockSqlSessionFactoryBean implements FactoryBean<SqlSessionFactory> {

    private int id;

    @Override
    public SqlSessionFactory getObject() throws Exception {
      if (id == 1) {
        return someSqlSessionFactory;
      } else if (id == 2) {
        return anotherSqlSessionFactory;
      }
      return null;
    }

    @Override
    public Class<?> getObjectType() {
      return SqlSessionFactory.class;
    }

    @Override
    public boolean isSingleton() {
      return true;
    }

    public void setId(int id) {
      this.id = id;
    }
  }
}

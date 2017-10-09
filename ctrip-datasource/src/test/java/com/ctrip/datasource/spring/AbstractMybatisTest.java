package com.ctrip.datasource.spring;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ctrip.datasource.mybatis.interceptor.DalMybatisInterceptorFactory;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.ctrip.datasource.spring.config.DalMybatisInterceptorRegistrar;
import org.springframework.util.ReflectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class AbstractMybatisTest {
  protected static org.apache.ibatis.session.Configuration someConfiguration;
  protected static org.apache.ibatis.session.Configuration anotherConfiguration;
  protected static SqlSessionFactory someSqlSessionFactory;
  protected static SqlSessionFactory anotherSqlSessionFactory;

  @BeforeClass
  public static void beforeClass() throws Exception {
    System.setProperty("cat.client.enabled", "false");
  }

  @Before
  public void setUp() throws Exception {
    someConfiguration = mock(Configuration.class);
    anotherConfiguration = mock(Configuration.class);
    someSqlSessionFactory = mock(SqlSessionFactory.class);
    anotherSqlSessionFactory = mock(SqlSessionFactory.class);

    when(someSqlSessionFactory.getConfiguration()).thenReturn(someConfiguration);
    when(anotherSqlSessionFactory.getConfiguration()).thenReturn(anotherConfiguration);

    ReflectionTestUtils.setField(DalMybatisInterceptorRegistrar.class, "INTERCEPTOR_ADDED", new AtomicBoolean(false));
    Method clearInterceptors = ReflectionUtils.findMethod(DalMybatisInterceptorFactory.class, "clearInterceptors");
    ReflectionUtils.makeAccessible(clearInterceptors);
    ReflectionUtils.invokeMethod(clearInterceptors, null);
  }

  protected  <T> T getFirstMatchingTypeFromCaptor(ArgumentCaptor<? super T> captor, Class<T> clazz) {
    for (Object value : captor.getAllValues()) {
      if (clazz.isInstance(value)) {
        return (T) value;
      }
    }
    return null;
  }
}

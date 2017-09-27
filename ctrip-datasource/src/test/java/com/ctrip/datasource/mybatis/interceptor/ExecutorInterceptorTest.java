package com.ctrip.datasource.mybatis.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.ctrip.datasource.mybatis.DalMybatisContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorInterceptorTest {

  private ExecutorInterceptor executorInterceptor;

  @Mock
  private Invocation invocation;

  @Before
  public void setUp() throws Exception {
    executorInterceptor = new ExecutorInterceptor();

  }

  @Test
  public void testIntercept() throws Throwable {
    MappedStatement someMappedStatement = mock(MappedStatement.class);
    Object[] args = new Object[]{someMappedStatement};
    Object someResult = mock(Object.class);

    when(invocation.getArgs()).thenReturn(args);
    when(invocation.proceed()).thenReturn(someResult);

    Object result = executorInterceptor.intercept(invocation);

    verify(invocation, times(1)).getArgs();
    verify(invocation, times(1)).proceed();

    assertEquals(someResult, result);
    assertNull(DalMybatisContextHolder.getContext().getMappedStatement());
  }

  @Test
  public void testInterceptWithException() throws Throwable {
    MappedStatement someMappedStatement = mock(MappedStatement.class);
    Object[] args = new Object[]{someMappedStatement};

    when(invocation.getArgs()).thenReturn(args);
    when(invocation.proceed()).thenReturn(new RuntimeException("some exception"));

    try {
      executorInterceptor.intercept(invocation);
    } catch (Throwable ex) {

    }

    verify(invocation, times(1)).getArgs();
    verify(invocation, times(1)).proceed();

    assertNull(DalMybatisContextHolder.getContext().getMappedStatement());
  }
}

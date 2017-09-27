package com.ctrip.datasource.mybatis.interceptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ctrip.datasource.mybatis.DalMybatisContextHolder;
import java.lang.reflect.Method;
import java.sql.Connection;
import org.apache.ibatis.plugin.Invocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StatementPrepareHandlerInterceptorTest {

  private StatementPrepareHandlerInterceptor statementPrepareHandlerInterceptor;

  @Mock
  private Invocation invocation;

  @Before
  public void setUp() throws Exception {
    //just in case
    DalMybatisContextHolder.getContext().clearContext();

    statementPrepareHandlerInterceptor = new StatementPrepareHandlerInterceptor();
  }

  @After
  public void tearDown() throws Exception {
    DalMybatisContextHolder.getContext().clearContext();
  }

  @Test
  public void testPrepare() throws Throwable {
    Connection someConnection = mock(Connection.class);
    Object[] args = new Object[]{someConnection};
    Object someResult = mock(Object.class);

    when(invocation.getArgs()).thenReturn(args);
    when(invocation.proceed()).thenReturn(someResult);

    Object result = statementPrepareHandlerInterceptor.intercept(invocation);

    verify(invocation, times(1)).getArgs();
    verify(invocation, times(1)).proceed();

    assertEquals(someResult, result);
    assertEquals(someConnection, DalMybatisContextHolder.getContext().getConnection());
  }
}

package com.ctrip.datasource.mybatis.interceptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ctrip.datasource.mybatis.DalMybatisContextHolder;
import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Invocation;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class StatementHandlerInterceptorTest {

  private StatementHandlerInterceptor statementHandlerInterceptor;

  @Mock
  private Invocation invocation;

  private boolean needEncryptParam;
  private String encryptKey;

  @Before
  public void setUp() throws Exception {
    needEncryptParam = false;
    encryptKey = LoggerAdapter.DEFAULT_SECRET_KEY;

    //just in case
    DalMybatisContextHolder.getContext().clearContext();

    statementHandlerInterceptor = spy(new StatementHandlerInterceptor(needEncryptParam, encryptKey));
  }

  @After
  public void tearDown() throws Exception {
    DalMybatisContextHolder.getContext().clearContext();
  }



  @Test
  public void testExecuteStatement() throws Throwable {
    StatementHandler someStatementHandler = mock(StatementHandler.class);
    BoundSql someBoundSql = mock(BoundSql.class);
    Connection someConnection = mock(Connection.class);
    final Transaction someTransaction = mock(Transaction.class);
    Object someResult = mock(Object.class);

    DalMybatisContextHolder.getContext().setConnection(someConnection);

    when(invocation.getTarget()).thenReturn(someStatementHandler);
    when(someStatementHandler.getBoundSql()).thenReturn(someBoundSql);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        return someTransaction;
      }
    }).when(statementHandlerInterceptor).startTransaction(someBoundSql, someConnection);
    when(invocation.proceed()).thenReturn(someResult);

    Object result = statementHandlerInterceptor.intercept(invocation);

    verify(invocation, times(1)).getTarget();
    verify(invocation, times(1)).proceed();
    verify(someTransaction, times(1)).complete();

    assertEquals(someResult, result);
  }

  @Test
  public void testExecuteStatementWithConnectionFailure() throws Throwable {
    StatementHandler someStatementHandler = mock(StatementHandler.class);
    BoundSql someBoundSql = mock(BoundSql.class);
    Connection someConnection = mock(Connection.class);
    PooledConnection somePooledConnection = mock(PooledConnection.class);
    final Transaction someTransaction = mock(Transaction.class);
    SQLException someSqlException = new SQLException("some exception", "08S01");
    InvocationTargetException someInvocationException = new InvocationTargetException(someSqlException);

    DalMybatisContextHolder.getContext().setConnection(someConnection);

    when(invocation.getTarget()).thenReturn(someStatementHandler);
    when(someStatementHandler.getBoundSql()).thenReturn(someBoundSql);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        return someTransaction;
      }
    }).when(statementHandlerInterceptor).startTransaction(someBoundSql, someConnection);
    when(someConnection.unwrap(PooledConnection.class)).thenReturn(somePooledConnection);

    when(invocation.proceed()).thenThrow(someInvocationException);

    Throwable actualThrowable = null;
    try {
      statementHandlerInterceptor.intercept(invocation);
    } catch (Throwable ex) {
      actualThrowable = ex;
    }

    assertEquals(someInvocationException, actualThrowable);

    verify(somePooledConnection, times(1)).setDiscarded(true);
    verify(someTransaction, times(1)).setStatus(someSqlException);
    verify(someTransaction, times(1)).complete();
  }


  @Test
  public void testExecuteStatementWithNonConnectionFailure() throws Throwable {
    StatementHandler someStatementHandler = mock(StatementHandler.class);
    BoundSql someBoundSql = mock(BoundSql.class);
    Connection someConnection = mock(Connection.class);
    final Transaction someTransaction = mock(Transaction.class);
    SQLException someSqlException = new SQLException("some exception", "42000");
    InvocationTargetException someInvocationException = new InvocationTargetException(someSqlException);

    DalMybatisContextHolder.getContext().setConnection(someConnection);

    when(invocation.getTarget()).thenReturn(someStatementHandler);
    when(someStatementHandler.getBoundSql()).thenReturn(someBoundSql);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        return someTransaction;
      }
    }).when(statementHandlerInterceptor).startTransaction(someBoundSql, someConnection);

    when(invocation.proceed()).thenThrow(someInvocationException);

    Throwable actualThrowable = null;
    try {
      statementHandlerInterceptor.intercept(invocation);
    } catch (Throwable ex) {
      actualThrowable = ex;
    }

    assertEquals(someInvocationException, actualThrowable);

    verify(someConnection, never()).unwrap(PooledConnection.class);
    verify(someTransaction, times(1)).setStatus(someSqlException);
    verify(someTransaction, times(1)).complete();
  }

  @Test
  public void testFetchQueryRows() throws Exception {
    assertEquals(0, statementHandlerInterceptor.fetchQueryRows(null));

    assertEquals(1, statementHandlerInterceptor.fetchQueryRows(new Object()));

    assertEquals(0, statementHandlerInterceptor.fetchQueryRows(Collections.emptyList()));
    assertEquals(1, statementHandlerInterceptor.fetchQueryRows(Lists.newArrayList(new Object())));
    assertEquals(2, statementHandlerInterceptor.fetchQueryRows(Lists.newArrayList(new Object(), new Object())));
  }

  @Test
  public void testBuildSqlType() throws Exception {
    assertEquals("SELECT", statementHandlerInterceptor.buildSqlType(" select * from abc"));
    assertEquals("SELECT", statementHandlerInterceptor.buildSqlType(" SELECT * from abc"));

    assertEquals("UPDATE", statementHandlerInterceptor.buildSqlType(" update abc set a = 1"));
    assertEquals("UPDATE", statementHandlerInterceptor.buildSqlType(" UPDATE abc set a = 1"));

    assertEquals("INSERT", statementHandlerInterceptor.buildSqlType(" insert into abc(a) values (1)"));
    assertEquals("INSERT", statementHandlerInterceptor.buildSqlType(" INSERT into abc(a) values (1)"));

    assertEquals("DELETE", statementHandlerInterceptor.buildSqlType(" delete from abc where a = 1"));
    assertEquals("DELETE", statementHandlerInterceptor.buildSqlType(" DELETE from abc where a = 1"));

    assertEquals("SP", statementHandlerInterceptor.buildSqlType(" call someSp(1)"));
    assertEquals("SP", statementHandlerInterceptor.buildSqlType(" CALL someSp(1)"));
    assertEquals("SP", statementHandlerInterceptor.buildSqlType(" { call someSp(1) }"));
    assertEquals("SP", statementHandlerInterceptor.buildSqlType(" { CALL someSp(1) }"));
    assertEquals("SP", statementHandlerInterceptor.buildSqlType(" exec someSp @a=1"));
    assertEquals("SP", statementHandlerInterceptor.buildSqlType(" EXEC someSp @a=1"));

    assertEquals("UNKNOWN", statementHandlerInterceptor.buildSqlType("abc"));
    assertEquals("UNKNOWN", statementHandlerInterceptor.buildSqlType(""));
    assertEquals("UNKNOWN", statementHandlerInterceptor.buildSqlType(null));
  }
}

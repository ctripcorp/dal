package com.ctrip.datasource.mybatis.interceptor;

import com.ctrip.datasource.mybatis.DalMybatisConfig;
import com.google.common.collect.Sets;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.crypto.NoSuchPaddingException;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public class DalMybatisInterceptorFactory {
  private static final Object LOCK = new Object();
  private static final AtomicReference<Set<Interceptor>> INTERCEPTORS = new AtomicReference<>(null);

  public static Set<Interceptor> dalMybatisInterceptors(DalMybatisConfig config)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
    if (INTERCEPTORS.get() == null) {
      synchronized (LOCK) {
        if (INTERCEPTORS.get() == null) {
          Set<Interceptor> interceptors = Sets.newHashSet();
          if (isNewMybatisExecutor()) {
            interceptors.add(new NewExecutorInterceptor());
          } else {
            interceptors.add(new ExecutorInterceptor());
          }
          if (isOldMybatisStatementHandler()) {
            interceptors.add(new StatementPrepareHandlerInterceptor());
          } else {
            interceptors.add(new NewStatementPrepareHandlerInterceptor());
          }
          interceptors.add(new StatementHandlerInterceptor(config.isNeedEncryptParam(), config.getEncryptKey()));
          INTERCEPTORS.set(interceptors);
        }
      }
    }

    return INTERCEPTORS.get();
  }

  private static boolean isOldMybatisStatementHandler() {
    try {
      StatementHandler.class.getMethod("prepare", Connection.class);

      return true;
    } catch (NoSuchMethodException ex) {
    }

    return false;
  }

  private static boolean isNewMybatisExecutor() {
    try {
      Executor.class.getMethod("query", MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class,
          CacheKey.class, BoundSql.class);

      return true;
    } catch (NoSuchMethodException executor) {
    }

    return false;
  }

  //only for test
  private static void clearInterceptors() {
    INTERCEPTORS.set(null);
  }
}

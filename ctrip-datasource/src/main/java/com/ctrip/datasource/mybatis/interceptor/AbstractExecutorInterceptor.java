package com.ctrip.datasource.mybatis.interceptor;

import com.ctrip.datasource.mybatis.DalMybatisContextHolder;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

public abstract class AbstractExecutorInterceptor implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    try {
      Object[] args = invocation.getArgs();
      MappedStatement ms = (MappedStatement) args[0];

      DalMybatisContextHolder.getContext().setMappedStatement(ms);

      return invocation.proceed();
    } finally {
      DalMybatisContextHolder.getContext().clearContext();
    }
  }

  @Override
  public Object plugin(Object target) {
    if (target instanceof Executor) {
      return Plugin.wrap(target, this);
    }
    return target;
  }

  @Override
  public void setProperties(Properties properties) {

  }

}

package com.ctrip.datasource.mybatis.interceptor;

import com.ctrip.datasource.mybatis.DalMybatisContextHolder;
import java.sql.Connection;
import java.util.Properties;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;

public abstract class AbstractStatementPrepareHandlerInterceptor implements Interceptor {
  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] args = invocation.getArgs();

    Connection connection = (Connection) args[0];

    DalMybatisContextHolder.getContext().setConnection(connection);

    return invocation.proceed();
  }

  @Override
  public Object plugin(Object target) {
    if (target instanceof StatementHandler) {
      return Plugin.wrap(target, this);
    }
    return target;
  }

  @Override
  public void setProperties(Properties properties) {

  }

}

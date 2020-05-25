package com.ctrip.datasource.mybatis.interceptor;

import com.ctrip.platform.dal.dao.datasource.jdbc.ClusterDatabaseMetaData;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalDatabaseMetaData;
import com.dianping.cat.message.Event;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import com.ctrip.datasource.mybatis.DalMybatisContextHolder;
import com.ctrip.datasource.util.DalEncrypter;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.NoSuchPaddingException;

@Intercepts({
    @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
    @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
    @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class StatementHandlerInterceptor implements Interceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(StatementHandlerInterceptor.class);
  private static final Joiner.MapJoiner PARAMETER_JOINER = Joiner.on(",").withKeyValueSeparator("=");
  private static final String DAL_CLUSTER = "DAL.cluster";
  private static final String RECORD_COUNT = "DAL.recordCount";
  private static final String UNKNOWN = "UNKNOWN";
  private boolean needEncryptParamemters;
  private DalEncrypter encrypter;

  public StatementHandlerInterceptor(boolean needEncryptParam, String encryptKey)
      throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
    needEncryptParamemters = needEncryptParam;
    if (needEncryptParamemters) {
      encrypter = new DalEncrypter(encryptKey);
    }
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Transaction transaction = null;
    Connection connection = null;

    try {
      StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
      BoundSql boundSql = statementHandler.getBoundSql();
      connection = DalMybatisContextHolder.getContext().getConnection();

      transaction = startTransaction(boundSql, connection);

      Object result = invocation.proceed();

      Cat.logSizeEvent(RECORD_COUNT, fetchQueryRows(result));

      return result;
    } catch (Throwable ex) {
      Throwable realThrowable = ExceptionUtil.unwrapThrowable(ex);
      if (connection != null && realThrowable instanceof SQLException) {
        checkException(connection, (SQLException) realThrowable);
      }
      if (transaction != null) {
        transaction.setStatus(realThrowable);
      }
      Cat.logError(realThrowable);

      String daoName = transaction == null ? UNKNOWN : transaction.getName();
      LOGGER.error("Error occurred when executing {}", daoName, realThrowable);
      throw ex;
    } finally {
      if (transaction != null) {
        transaction.complete();
      }
    }
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

  Transaction startTransaction(BoundSql boundSql, Connection connection) {
    Transaction transaction = null;
    try {
      MappedStatement mappedStatement = DalMybatisContextHolder.getContext().getMappedStatement();

      String sql = boundSql.getSql();
      String parameters = "";

      if (mappedStatement != null) {
        transaction = Cat.newTransaction(CatConstants.TYPE_SQL, mappedStatement.getId());
        transaction.addData(sql);
        transaction.setStatus(Transaction.SUCCESS);
        parameters = parseParameters(mappedStatement.getConfiguration(), boundSql);
        if (needEncryptParamemters) {
          parameters = encrypter.desEncrypt(parameters);
        }
      }

      Cat.logEvent(CatConstants.TYPE_SQL_METHOD, buildSqlType(sql), Message.SUCCESS, parameters);

      if (connection != null) {
        DatabaseMetaData metaData = connection.getMetaData();
        String logName;
        if (metaData instanceof DalDatabaseMetaData)
          logName = ((DalDatabaseMetaData) metaData).getExtendedURL();
        else
          logName = metaData.getURL();
        Cat.logEvent(CatConstants.TYPE_SQL_DATABASE, logName);

        try {
          if (metaData.isWrapperFor(ClusterDatabaseMetaData.class)) {
            ClusterDatabaseMetaData clusterDatabaseMetaData = metaData.unwrap(ClusterDatabaseMetaData.class);
            Cat.logEvent(DAL_CLUSTER, clusterDatabaseMetaData.getClusterName(),
                    Event.SUCCESS, "shard=" + clusterDatabaseMetaData.getShardIndex());
          }
        } catch (Throwable t) {
          LOGGER.warn("log cluster error", t);
        }
      }

    } catch (Throwable ex) {
      // ignore
    }
    return transaction;
  }

  String buildSqlType(String sql) {
    try {
      char c = sql.trim().charAt(0);
      if (c == 's' || c == 'S') {
        return "SELECT";
      } else if (c == 'u' || c == 'U') {
        return "UPDATE";
      } else if (c == 'i' || c == 'I') {
        return "INSERT";
      } else if (c == 'd' || c == 'D') {
        return "DELETE";
      } else if (c == 'c' || c == 'C' || c == 'e' || c == 'E' || c == '{') {
        //call, exec, {call()}
        return "SP";
      }
    } catch (Throwable e) {
    }
    return UNKNOWN;
  }

  /**
   * Check whether exception is connection failure, discard the connection if so
   *
   * Migrated from HikariCP's ProxyConnection
   */
  void checkException(Connection connection, SQLException sqle) {
    try {
      if (connection.isClosed()) {
        return;
      }

      SQLException nse = sqle;
      for (int depth = 0; nse != null && depth < 10; depth++) {
        final String sqlState = nse.getSQLState();
        if (sqlState != null && sqlState.startsWith("08")) {
          // broken connection
          LOGGER.warn("Connection {} marked as discarded because of SQLSTATE({}), ErrorCode({})", connection, sqlState,
              nse.getErrorCode(), nse);
          PooledConnection pooledConnection = connection.unwrap(PooledConnection.class);
          pooledConnection.setDiscarded(true);
          break;
        } else {
          nse = nse.getNextException();
        }
      }
    } catch (Throwable ex) {
      //ignore
    }
  }

  int fetchQueryRows(Object result) {
    if (result == null) {
      return 0;
    }

    if (result instanceof Collection<?>) {
      return ((Collection<?>) result).size();
    }

    return 1;
  }

  /**
   * parse the parameters to string
   * <br />
   * Migrated from DefaultParameterHandler#setParameters(java.sql.PreparedStatement) in Mybatis 3.4.5
   */
  String parseParameters(Configuration configuration, BoundSql boundSql) {
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

    if (parameterMappings == null || parameterMappings.isEmpty()) {
      return "";
    }

    TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    Object parameterObject = boundSql.getParameterObject();

    Map<String, String> parameters = Maps.newHashMapWithExpectedSize(parameterMappings.size());

    for (int i = 0; i < parameterMappings.size(); i++) {
      ParameterMapping parameterMapping = parameterMappings.get(i);
      if (parameterMapping.getMode() != ParameterMode.OUT) {
        Object value;
        String propertyName = parameterMapping.getProperty();
        if (boundSql.hasAdditionalParameter(propertyName)) {
          value = boundSql.getAdditionalParameter(propertyName);
        } else if (parameterObject == null) {
          value = null;
        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
          value = parameterObject;
        } else {
          MetaObject metaObject = configuration.newMetaObject(parameterObject);
          value = metaObject.getValue(propertyName);
        }
        parameters.put(propertyName, String.valueOf(value));
      }
    }

    return PARAMETER_JOINER.join(parameters);
  }

  public boolean isNeedEncryptParameters() {
    return needEncryptParamemters;
  }
}

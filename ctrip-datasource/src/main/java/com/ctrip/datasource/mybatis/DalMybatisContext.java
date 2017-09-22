package com.ctrip.datasource.mybatis;

import java.sql.Connection;

import org.apache.ibatis.mapping.MappedStatement;

public class DalMybatisContext {

  private MappedStatement mappedStatement;
  private Connection connection;

  public MappedStatement getMappedStatement() {
    return mappedStatement;
  }

  public void setMappedStatement(MappedStatement mappedStatement) {
    this.mappedStatement = mappedStatement;
  }

  public Connection getConnection() {
    return connection;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public void clearContext() {
    this.mappedStatement = null;
    this.connection = null;
  }


}

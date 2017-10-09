package com.ctrip.datasource.mybatis;

public class DalMybatisContextHolder {

  private static final ThreadLocal<DalMybatisContext> CONTEXT = new ThreadLocal<DalMybatisContext>() {
    @Override
    protected DalMybatisContext initialValue() {
      return new DalMybatisContext();
    }
  };

  public static DalMybatisContext getContext() {
    return CONTEXT.get();
  }
}

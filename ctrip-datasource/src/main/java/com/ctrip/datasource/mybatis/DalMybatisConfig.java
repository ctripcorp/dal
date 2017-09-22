package com.ctrip.datasource.mybatis;

public class DalMybatisConfig {
  private boolean needEncryptParam;
  private String encryptKey;

  public boolean isNeedEncryptParam() {
    return needEncryptParam;
  }

  public void setNeedEncryptParam(boolean needEncryptParam) {
    this.needEncryptParam = needEncryptParam;
  }

  public String getEncryptKey() {
    return encryptKey;
  }

  public void setEncryptKey(String encryptKey) {
    this.encryptKey = encryptKey;
  }
}

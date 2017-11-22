package com.ctrip.framework.dal.demo.entity;

import java.sql.Timestamp;
import microsoft.sql.DateTimeOffset;

public class App {

  // 主键
  private Long id;

  // AppID
  private String appId;

  // 应用名
  private String name;

  // 创建人邮箱前缀
  private String datachangeCreatedby;

  // 创建时间
  private Timestamp datachangeCreatedtime;

  // 最后修改人邮箱前缀
  private String datachangeLastmodifiedby;

  // 最后修改时间
  private Timestamp datachangeLasttime;

  private DateTimeOffset datachangeCreatedtimewithoffset;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDatachangeCreatedby() {
    return datachangeCreatedby;
  }

  public void setDatachangeCreatedby(String datachangeCreatedby) {
    this.datachangeCreatedby = datachangeCreatedby;
  }

  public Timestamp getDatachangeCreatedtime() {
    return datachangeCreatedtime;
  }

  public void setDatachangeCreatedtime(Timestamp datachangeCreatedtime) {
    this.datachangeCreatedtime = datachangeCreatedtime;
  }

  public String getDatachangeLastmodifiedby() {
    return datachangeLastmodifiedby;
  }

  public void setDatachangeLastmodifiedby(String datachangeLastmodifiedby) {
    this.datachangeLastmodifiedby = datachangeLastmodifiedby;
  }

  public Timestamp getDatachangeLasttime() {
    return datachangeLasttime;
  }

  public void setDatachangeLasttime(Timestamp datachangeLasttime) {
    this.datachangeLasttime = datachangeLasttime;
  }

  public DateTimeOffset getDatachangeCreatedtimewithoffset() {
    return datachangeCreatedtimewithoffset;
  }

  public void setDatachangeCreatedtimewithoffset(DateTimeOffset datachangeCreatedtimewithoffset) {
    this.datachangeCreatedtimewithoffset = datachangeCreatedtimewithoffset;
  }

  @Override
  public String toString() {
    return String.format("{appId: %s, name: %s}", appId, name);
  }
}

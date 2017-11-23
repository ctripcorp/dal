package com.ctrip.framework.dal.mysql.test.entity;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.sql.Types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

@Entity
@Database(name="DalServiceDB")
@Table(name="integrationuser")
public class IntegrationUser implements DalPojo {

  @Id
  @Column(name="Id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Type(value=Types.BIGINT)
  private BigInteger id;

  @Column(name="UserId")
  @Type(value=Types.BIGINT)
  private Long userId;

  @Column(name="UserName")
  @Type(value=Types.VARCHAR)
  private String userName;

  @Column(name="DataChange_CreatedTime")
  @Type(value=Types.TIMESTAMP)
  private Timestamp datachangeCreatedtime;

  @Column(name="DataChange_LastTime", insertable=false, updatable=false)
  @Type(value=Types.TIMESTAMP)
  private Timestamp datachangeLasttime;

  public BigInteger getId() {
    return id;
  }

  public void setId(BigInteger id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Timestamp getDatachangeCreatedtime() {
    return datachangeCreatedtime;
  }

  public void setDatachangeCreatedtime(Timestamp datachangeCreatedtime) {
    this.datachangeCreatedtime = datachangeCreatedtime;
  }

  public Timestamp getDatachangeLasttime() {
    return datachangeLasttime;
  }

  public void setDatachangeLasttime(Timestamp datachangeLasttime) {
    this.datachangeLasttime = datachangeLasttime;
  }

}

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
@Table(name="integrationtestcaseresult")
public class IntegrationTestCaseResult implements DalPojo {

  @Id
  @Column(name="Id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Type(value=Types.BIGINT)
  private BigInteger id;

  @Column(name="Result")
  @Type(value=Types.VARCHAR)
  private String result;

  //0: failed, 1: successful
  @Column(name="Status")
  @Type(value=Types.TINYINT)
  private Integer status;

  @Column(name="DataChange_CreatedTime")
  @Type(value=Types.TIMESTAMP)
  private Timestamp datachangeCreatedtime;

  public BigInteger getId() {
    return id;
  }

  public void setId(BigInteger id) {
    this.id = id;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Timestamp getDatachangeCreatedtime() {
    return datachangeCreatedtime;
  }

  public void setDatachangeCreatedtime(Timestamp datachangeCreatedtime) {
    this.datachangeCreatedtime = datachangeCreatedtime;
  }

}

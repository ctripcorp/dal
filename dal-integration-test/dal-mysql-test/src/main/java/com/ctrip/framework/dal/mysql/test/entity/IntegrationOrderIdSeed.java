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
@Table(name="integrationorderidseed")
public class IntegrationOrderIdSeed implements DalPojo {

  @Id
  @Column(name="Id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Type(value=Types.BIGINT)
  private BigInteger id;

  @Column(name="DataChange_CreatedTime")
  @Type(value=Types.TIMESTAMP)
  private Timestamp datachangeCreatedtime;

  public BigInteger getId() {
    return id;
  }

  public void setId(BigInteger id) {
    this.id = id;
  }

  public Timestamp getDatachangeCreatedtime() {
    return datachangeCreatedtime;
  }

  public void setDatachangeCreatedtime(Timestamp datachangeCreatedtime) {
    this.datachangeCreatedtime = datachangeCreatedtime;
  }

}
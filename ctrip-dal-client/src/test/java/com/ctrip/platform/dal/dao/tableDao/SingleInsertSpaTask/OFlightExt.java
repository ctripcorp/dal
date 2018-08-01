package com.ctrip.platform.dal.dao.tableDao.SingleInsertSpaTask;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Types;
import java.sql.Timestamp;

@Entity
@Database(name = "TVP")
@Table(name = "O_FlightExt")
public class OFlightExt implements DalPojo {

    @Id
    @Column(name = "OrderID")
    @Type(value = Types.BIGINT)
    private Long orderID;

    @Id
    @Column(name = "Sequence")
    @Type(value = Types.SMALLINT)
    private Short sequence;

    @Column(name = "RealSubclass")
    @Type(value = Types.VARCHAR)
    private String realSubclass;

    @Column(name = "SubPrice")
    @Type(value = Types.VARCHAR)
    private String subPrice;

    @Column(name = "DataChange_LastTime")
    @Type(value = Types.TIMESTAMP)
    private Timestamp datachangeLasttime;

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public Short getSequence() {
        return sequence;
    }

    public void setSequence(Short sequence) {
        this.sequence = sequence;
    }

    public String getRealSubclass() {
        return realSubclass;
    }

    public void setRealSubclass(String realSubclass) {
        this.realSubclass = realSubclass;
    }

    public String getSubPrice() {
        return subPrice;
    }

    public void setSubPrice(String subPrice) {
        this.subPrice = subPrice;
    }

    public Timestamp getDatachangeLasttime() {
        return datachangeLasttime;
    }

    public void setDatachangeLasttime(Timestamp datachangeLasttime) {
        this.datachangeLasttime = datachangeLasttime;
    }

}

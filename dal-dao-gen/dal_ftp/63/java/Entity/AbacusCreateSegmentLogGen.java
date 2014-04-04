package com.ctrip.dal.test.test4;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class AbacusCreateSegmentLogGen implements DalPojo {
	private Integer logID;
	private String uID;
	private Integer orderID;
	private Timestamp beginTime;
	private Timestamp endTime;
	private Integer result;
	private String sessionID;
	private String flightSegmentStatus;
	private String errDesc;
	private String source;
	private Timestamp insertTime;
	private Timestamp datachangeLasttime;
	public Integer getLogID() {
		return logID;
	}

	public void setLogID(Integer logID) {
		this.logID = logID;
	}

	public String getUID() {
		return uID;
	}

	public void setUID(String uID) {
		this.uID = uID;
	}

	public Integer getOrderID() {
		return orderID;
	}

	public void setOrderID(Integer orderID) {
		this.orderID = orderID;
	}

	public Timestamp getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Timestamp beginTime) {
		this.beginTime = beginTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getFlightSegmentStatus() {
		return flightSegmentStatus;
	}

	public void setFlightSegmentStatus(String flightSegmentStatus) {
		this.flightSegmentStatus = flightSegmentStatus;
	}

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Timestamp getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Timestamp insertTime) {
		this.insertTime = insertTime;
	}

	public Timestamp getDatachangeLasttime() {
		return datachangeLasttime;
	}

	public void setDatachangeLasttime(Timestamp datachangeLasttime) {
		this.datachangeLasttime = datachangeLasttime;
	}

}
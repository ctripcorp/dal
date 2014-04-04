package com.ctrip.dal.test.test4;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class AccountBalanceLetter implements DalPojo {
	private Integer recordId;
	private Integer accBalanceId;
	private String fileName;
	private String uploadPerson;
	private String describeInfo;
	private Timestamp createTime;
	private Timestamp datachangeLasttime;
	public Integer getRecordId() {
		return recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public Integer getAccBalanceId() {
		return accBalanceId;
	}

	public void setAccBalanceId(Integer accBalanceId) {
		this.accBalanceId = accBalanceId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUploadPerson() {
		return uploadPerson;
	}

	public void setUploadPerson(String uploadPerson) {
		this.uploadPerson = uploadPerson;
	}

	public String getDescribeInfo() {
		return describeInfo;
	}

	public void setDescribeInfo(String describeInfo) {
		this.describeInfo = describeInfo;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getDatachangeLasttime() {
		return datachangeLasttime;
	}

	public void setDatachangeLasttime(Timestamp datachangeLasttime) {
		this.datachangeLasttime = datachangeLasttime;
	}

}
package com.ctrip.dal.test.test4;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class Accountcheck implements DalPojo {
	private Integer accCheckID;
	private String corpID;
	private String batchNo;
	private Integer accountID;
	private Integer subAccountID;
	private String batchStatus;
	private Integer accBalanceID;
	private String accountType;
	private Integer checkAccType;
	private String operator;
	private Timestamp modifyTime;
	private String startDate;
	private String endDate;
	private BigDecimal fltconMoney;
	private BigDecimal htlHconMoney;
	private BigDecimal htlXconMoney;
	private BigDecimal limited;
	private BigDecimal limitedTemp;
	private Timestamp datachangeLasttime;
	public Integer getAccCheckID() {
		return accCheckID;
	}

	public void setAccCheckID(Integer accCheckID) {
		this.accCheckID = accCheckID;
	}

	public String getCorpID() {
		return corpID;
	}

	public void setCorpID(String corpID) {
		this.corpID = corpID;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Integer getAccountID() {
		return accountID;
	}

	public void setAccountID(Integer accountID) {
		this.accountID = accountID;
	}

	public Integer getSubAccountID() {
		return subAccountID;
	}

	public void setSubAccountID(Integer subAccountID) {
		this.subAccountID = subAccountID;
	}

	public String getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(String batchStatus) {
		this.batchStatus = batchStatus;
	}

	public Integer getAccBalanceID() {
		return accBalanceID;
	}

	public void setAccBalanceID(Integer accBalanceID) {
		this.accBalanceID = accBalanceID;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Integer getCheckAccType() {
		return checkAccType;
	}

	public void setCheckAccType(Integer checkAccType) {
		this.checkAccType = checkAccType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Timestamp getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getFltconMoney() {
		return fltconMoney;
	}

	public void setFltconMoney(BigDecimal fltconMoney) {
		this.fltconMoney = fltconMoney;
	}

	public BigDecimal getHtlHconMoney() {
		return htlHconMoney;
	}

	public void setHtlHconMoney(BigDecimal htlHconMoney) {
		this.htlHconMoney = htlHconMoney;
	}

	public BigDecimal getHtlXconMoney() {
		return htlXconMoney;
	}

	public void setHtlXconMoney(BigDecimal htlXconMoney) {
		this.htlXconMoney = htlXconMoney;
	}

	public BigDecimal getLimited() {
		return limited;
	}

	public void setLimited(BigDecimal limited) {
		this.limited = limited;
	}

	public BigDecimal getLimitedTemp() {
		return limitedTemp;
	}

	public void setLimitedTemp(BigDecimal limitedTemp) {
		this.limitedTemp = limitedTemp;
	}

	public Timestamp getDatachangeLasttime() {
		return datachangeLasttime;
	}

	public void setDatachangeLasttime(Timestamp datachangeLasttime) {
		this.datachangeLasttime = datachangeLasttime;
	}

}
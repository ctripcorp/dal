package com.ctrip.dal.test.test4;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class FltOrdersTmp implements DalPojo {
	private Integer recordId;
	private Integer orderId;
	private String passengerName;
	private Integer sequence;
	private Integer accCheckId;
	private BigDecimal price;
	private BigDecimal tax;
	private BigDecimal oilFee;
	private BigDecimal sendticketfee;
	private BigDecimal insurancefee;
	private BigDecimal serviceFee;
	private BigDecimal refund;
	private BigDecimal delAdjustAmount;
	private BigDecimal adjustedAmount;
	private String orderStatus;
	private String remark;
	private Timestamp createTime;
	private Timestamp confirmTime;
	private String dailyConfirmFlag;
	private Integer dealID;
	private BigDecimal cost;
	private Timestamp datachangeLasttime;
	public Integer getRecordId() {
		return recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getAccCheckId() {
		return accCheckId;
	}

	public void setAccCheckId(Integer accCheckId) {
		this.accCheckId = accCheckId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getOilFee() {
		return oilFee;
	}

	public void setOilFee(BigDecimal oilFee) {
		this.oilFee = oilFee;
	}

	public BigDecimal getSendticketfee() {
		return sendticketfee;
	}

	public void setSendticketfee(BigDecimal sendticketfee) {
		this.sendticketfee = sendticketfee;
	}

	public BigDecimal getInsurancefee() {
		return insurancefee;
	}

	public void setInsurancefee(BigDecimal insurancefee) {
		this.insurancefee = insurancefee;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	public BigDecimal getRefund() {
		return refund;
	}

	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}

	public BigDecimal getDelAdjustAmount() {
		return delAdjustAmount;
	}

	public void setDelAdjustAmount(BigDecimal delAdjustAmount) {
		this.delAdjustAmount = delAdjustAmount;
	}

	public BigDecimal getAdjustedAmount() {
		return adjustedAmount;
	}

	public void setAdjustedAmount(BigDecimal adjustedAmount) {
		this.adjustedAmount = adjustedAmount;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Timestamp confirmTime) {
		this.confirmTime = confirmTime;
	}

	public String getDailyConfirmFlag() {
		return dailyConfirmFlag;
	}

	public void setDailyConfirmFlag(String dailyConfirmFlag) {
		this.dailyConfirmFlag = dailyConfirmFlag;
	}

	public Integer getDealID() {
		return dealID;
	}

	public void setDealID(Integer dealID) {
		this.dealID = dealID;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public Timestamp getDatachangeLasttime() {
		return datachangeLasttime;
	}

	public void setDatachangeLasttime(Timestamp datachangeLasttime) {
		this.datachangeLasttime = datachangeLasttime;
	}

}
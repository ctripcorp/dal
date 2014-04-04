package com.ctrip.dal.test.test4;


import com.ctrip.platform.dal.dao.DalPojo;

public class Test1 implements DalPojo {
	private Integer htlOrderDetailId;
	private Integer orderId;
	private String orderType;
	private BigDecimal amount;
	private BigDecimal serviceFee;
	private BigDecimal rebate;
	private String isInbatch;
	private Integer accCheckId;
	private Timestamp creatTime;
	private Timestamp inbatchTime;
	private Timestamp lastModifyTime;
	private Integer accountId;
	private Integer subAccountID;
	private Integer rid;
	private Timestamp rCTime;
	private Integer rCQuantity;
	public Integer getHtlOrderDetailId() {
		return htlOrderDetailId;
	}

	public void setHtlOrderDetailId(Integer htlOrderDetailId) {
		this.htlOrderDetailId = htlOrderDetailId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	public BigDecimal getRebate() {
		return rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public String getIsInbatch() {
		return isInbatch;
	}

	public void setIsInbatch(String isInbatch) {
		this.isInbatch = isInbatch;
	}

	public Integer getAccCheckId() {
		return accCheckId;
	}

	public void setAccCheckId(Integer accCheckId) {
		this.accCheckId = accCheckId;
	}

	public Timestamp getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Timestamp creatTime) {
		this.creatTime = creatTime;
	}

	public Timestamp getInbatchTime() {
		return inbatchTime;
	}

	public void setInbatchTime(Timestamp inbatchTime) {
		this.inbatchTime = inbatchTime;
	}

	public Timestamp getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Timestamp lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getSubAccountID() {
		return subAccountID;
	}

	public void setSubAccountID(Integer subAccountID) {
		this.subAccountID = subAccountID;
	}

	public Integer getRid() {
		return rid;
	}

	public void setRid(Integer rid) {
		this.rid = rid;
	}

	public Timestamp getRCTime() {
		return rCTime;
	}

	public void setRCTime(Timestamp rCTime) {
		this.rCTime = rCTime;
	}

	public Integer getRCQuantity() {
		return rCQuantity;
	}

	public void setRCQuantity(Integer rCQuantity) {
		this.rCQuantity = rCQuantity;
	}

}
package com.ctrip.dal.test.test4;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class SenderGen implements DalPojo {
	private String sender;
	private String senderName;
	private Integer senderCity;
	private Integer sendSite;
	private String contactTel;
	private String mobilePhone;
	private Timestamp birthDay;
	private String gender;
	private String contactAddr;
	private String iDCardNo;
	private String iDCardAddr;
	private String warrantor;
	private String warrantorAddr;
	private String warrantorIDCard;
	private String warrantorTel;
	private String atWork;
	private String dept;
	private Integer isDelete;
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Integer getSenderCity() {
		return senderCity;
	}

	public void setSenderCity(Integer senderCity) {
		this.senderCity = senderCity;
	}

	public Integer getSendSite() {
		return sendSite;
	}

	public void setSendSite(Integer sendSite) {
		this.sendSite = sendSite;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Timestamp getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Timestamp birthDay) {
		this.birthDay = birthDay;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getContactAddr() {
		return contactAddr;
	}

	public void setContactAddr(String contactAddr) {
		this.contactAddr = contactAddr;
	}

	public String getIDCardNo() {
		return iDCardNo;
	}

	public void setIDCardNo(String iDCardNo) {
		this.iDCardNo = iDCardNo;
	}

	public String getIDCardAddr() {
		return iDCardAddr;
	}

	public void setIDCardAddr(String iDCardAddr) {
		this.iDCardAddr = iDCardAddr;
	}

	public String getWarrantor() {
		return warrantor;
	}

	public void setWarrantor(String warrantor) {
		this.warrantor = warrantor;
	}

	public String getWarrantorAddr() {
		return warrantorAddr;
	}

	public void setWarrantorAddr(String warrantorAddr) {
		this.warrantorAddr = warrantorAddr;
	}

	public String getWarrantorIDCard() {
		return warrantorIDCard;
	}

	public void setWarrantorIDCard(String warrantorIDCard) {
		this.warrantorIDCard = warrantorIDCard;
	}

	public String getWarrantorTel() {
		return warrantorTel;
	}

	public void setWarrantorTel(String warrantorTel) {
		this.warrantorTel = warrantorTel;
	}

	public String getAtWork() {
		return atWork;
	}

	public void setAtWork(String atWork) {
		this.atWork = atWork;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

}
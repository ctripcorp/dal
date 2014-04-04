package com.ctrip.dal.test.test4;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class AbacusParaGen implements DalPojo {
	private Integer paraID;
	private Integer paraTypeID;
	private String paraName;
	private String paraValue;
	private String description;
	private Integer abacusWSID;
	private Timestamp datachangeLasttime;
	public Integer getParaID() {
		return paraID;
	}

	public void setParaID(Integer paraID) {
		this.paraID = paraID;
	}

	public Integer getParaTypeID() {
		return paraTypeID;
	}

	public void setParaTypeID(Integer paraTypeID) {
		this.paraTypeID = paraTypeID;
	}

	public String getParaName() {
		return paraName;
	}

	public void setParaName(String paraName) {
		this.paraName = paraName;
	}

	public String getParaValue() {
		return paraValue;
	}

	public void setParaValue(String paraValue) {
		this.paraValue = paraValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAbacusWSID() {
		return abacusWSID;
	}

	public void setAbacusWSID(Integer abacusWSID) {
		this.abacusWSID = abacusWSID;
	}

	public Timestamp getDatachangeLasttime() {
		return datachangeLasttime;
	}

	public void setDatachangeLasttime(Timestamp datachangeLasttime) {
		this.datachangeLasttime = datachangeLasttime;
	}

}
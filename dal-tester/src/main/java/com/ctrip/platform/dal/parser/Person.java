package com.ctrip.platform.dal.parser;

import java.sql.Timestamp;

import com.ctrip.fx.enteroctopus.common.jpa.DBColumn;
import com.ctrip.fx.enteroctopus.common.jpa.DBEntity;
import com.ctrip.fx.enteroctopus.common.jpa.DBId;
import com.ctrip.fx.enteroctopus.common.jpa.DataBase;
import com.ctrip.platform.dal.dao.DalPojo;

@DBEntity(tableName="Person", db = DataBase.dao_test)
public class Person implements DalPojo {
	
	@DBId
	@DBColumn
	private Integer iD;
	@DBColumn
	private String address;
	@DBColumn
	private String telephone;
	@DBColumn
	private String name;
	@DBColumn
	private Integer age;
	@DBColumn
	private Integer gender;
	@DBColumn
	private Timestamp birth;
	@DBColumn
	private Integer partmentID;
	
	public Integer getID() {
		return iD;
	}

	public void setID(Integer iD) {
		this.iD = iD;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Timestamp getBirth() {
		return birth;
	}

	public void setBirth(Timestamp birth) {
		this.birth = birth;
	}

	public Integer getPartmentID() {
		return partmentID;
	}

	public void setPartmentID(Integer partmentID) {
		this.partmentID = partmentID;
	}

}


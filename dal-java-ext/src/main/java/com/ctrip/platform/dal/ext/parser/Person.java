package com.ctrip.platform.dal.ext.parser;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity(name="Person")
public class Person {
	@Id
	private Integer iD;
	
	@Column
	private String address;
	
	@Column(nullable=false)
	private String telephone;
	
	@Basic
	private String name;
	
	@Column(nullable=false)
	private Integer age;
	
	@Basic
	private Integer gender;
	
	@Basic
	private Timestamp birth;
	
	@Basic(optional=false)
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

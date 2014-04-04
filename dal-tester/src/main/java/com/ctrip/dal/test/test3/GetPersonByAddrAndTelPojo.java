package com.ctrip.dal.test.test3;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class GetPersonByAddrAndTelPojo implements DalPojo {
	private String address;
	private Integer age;
	private String telephone;
	private Timestamp birth;
	private Integer gender;
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Timestamp getBirth() {
		return birth;
	}

	public void setBirth(Timestamp birth) {
		this.birth = birth;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

}
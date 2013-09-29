package com.ctrip.platform.dao.pojo;

import java.sql.Timestamp;

import com.ctrip.platform.dao.annotation.TableColumn;

public class Person {
	
	@TableColumn(columnName="ID")
	private int iD;
	
	@TableColumn(columnName="Address")
	private String address;
	
	@TableColumn(columnName="Name")
	private String name;
	
	@TableColumn(columnName="Telephone")
	private String telephone;
	
	@TableColumn(columnName="Age")
	private int age;
	
	@TableColumn(columnName="Gender")
	private int gender;
	
	@TableColumn(columnName="Birth")
	private Timestamp birth;

	public int getiD() {
		return iD;
	}

	public void setiD(int iD) {
		this.iD = iD;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public Timestamp getBirth() {
		return birth;
	}

	public void setBirth(Timestamp birth) {
		this.birth = birth;
	}
	
	
	
}

package com.ctrip.platform.dal.tester.person;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class Person implements DalPojo {

	private Integer ID;

	public Integer getID() {
		return ID;
	}

	// (表示(不会被Verlocity解析
	public void setID(Integer ID) {
		this.ID = ID;
	}

	private String Address;

	public String getAddress() {
		return Address;
	}

	// (表示(不会被Verlocity解析
	public void setAddress(String Address) {
		this.Address = Address;
	}

	private String Telephone;

	public String getTelephone() {
		return Telephone;
	}

	// (表示(不会被Verlocity解析
	public void setTelephone(String Telephone) {
		this.Telephone = Telephone;
	}

	private String Name;

	public String getName() {
		return Name;
	}

	// (表示(不会被Verlocity解析
	public void setName(String Name) {
		this.Name = Name;
	}

	private Integer Age;

	public Integer getAge() {
		return Age;
	}

	// (表示(不会被Verlocity解析
	public void setAge(Integer Age) {
		this.Age = Age;
	}

	private Integer Gender;

	public Integer getGender() {
		return Gender;
	}

	// (表示(不会被Verlocity解析
	public void setGender(Integer Gender) {
		this.Gender = Gender;
	}

	private Timestamp Birth;

	public Timestamp getBirth() {
		return Birth;
	}

	// (表示(不会被Verlocity解析
	public void setBirth(Timestamp Birth) {
		this.Birth = Birth;
	}

}

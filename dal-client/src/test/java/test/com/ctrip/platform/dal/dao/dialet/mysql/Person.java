package test.com.ctrip.platform.dal.dao.dialet.mysql;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class Person implements DalPojo {
	private Integer iD;
	private String address;
	private String telephone;
	private String name;
	private Integer age;
	private Integer gender;
	private Timestamp birth;
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


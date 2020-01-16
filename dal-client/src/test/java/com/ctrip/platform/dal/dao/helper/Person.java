package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name="MySqlSimpleShard")
@Table(name="person")
public class Person implements DalPojo {
	
	@Id
	@Column(name="PeopleID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value=Types.INTEGER)
	private Integer peopleID;
	
	@Column(name="Name")
	@Type(value=Types.VARCHAR)
	private String name;
	
	@Column(name="CityID")
	@Type(value=Types.INTEGER)
	private Integer cityID;
	
	@Column(name="ProvinceID")
	@Type(value=Types.INTEGER)
	private Integer provinceID;
	
	@Column(name="CountryID")
	@Type(value=Types.INTEGER)
	private Integer countryID;
	
	@Column(name="DataChange_LastTime")
	@Type(value=Types.TIMESTAMP)
	private Timestamp dataChange_LastTime;

	public Integer getPeopleID() {
		return peopleID;
	}

	public void setPeopleID(Integer peopleID) {
		this.peopleID = peopleID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCityID() {
		return cityID;
	}

	public void setCityID(Integer cityID) {
		this.cityID = cityID;
	}

	public Integer getProvinceID() {
		return provinceID;
	}

	public void setProvinceID(Integer provinceID) {
		this.provinceID = provinceID;
	}

	public Integer getCountryID() {
		return countryID;
	}

	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
	}

	public Timestamp getDataChange_LastTime() {
		return dataChange_LastTime;
	}

	public void setDataChange_LastTime(Timestamp dataChange_LastTime) {
		this.dataChange_LastTime = dataChange_LastTime;
	}

}

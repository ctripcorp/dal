package com.ctrip.platform.dal.dao;

import java.sql.Types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

@Entity
@Database(name="SimpleShard")
@Table(name="People")
public class PeopleOldVersion implements DalPojo {
	
	@Column(name="Name")
	@Type(value=Types.VARCHAR)
	private String name;

	//To simulate old style table that has a column before primary key
    @Id
    @Column(name="PeopleID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value=Types.INTEGER)
    private Integer peopleID;
    
	@Column(name="CityID")
	@Type(value=Types.INTEGER)
	private Integer cityID;
	
	@Column(name="ProvinceID")
	@Type(value=Types.INTEGER)
	private Integer provinceID;
	
	@Column(name="CountryID")
	@Type(value=Types.INTEGER)
	private Integer countryID;

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
}

package com.ctrip.platform.dal.dao;

public class People implements DalPojo {
	private Long peopleID;
	private String name;
	private Integer cityID;
	private Integer provinceID;
	private Integer countryID;
	public Long getPeopleID() {
		return peopleID;
	}

	public void setPeopleID(Long peopleID) {
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

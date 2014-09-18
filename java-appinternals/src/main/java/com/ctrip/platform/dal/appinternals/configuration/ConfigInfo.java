package com.ctrip.platform.dal.appinternals.configuration;

import java.util.Date;

public class ConfigInfo{
	
	private String name;
	private String fullName;
	private Date lastModifyTime;
     
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getUrl() {
		return "/appinternals/configurations/beans/" + 
				this.fullName + "?action=view";
	}
	public Date getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
}

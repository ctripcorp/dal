package com.ctrip.sysdev.das.console.domain;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class DbSetting {
	private String driver;
	private String jdbcUrl;
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	
}

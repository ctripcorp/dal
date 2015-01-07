package com.ctrip.platform.dal.console.common;

public class Status {
	private String code;
	
	private String info;

	public static final Status OK = new Status("OK");
	
	public static final Status ERROR = new Status("Error");;

	public Status() {
	}

	public Status(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
}
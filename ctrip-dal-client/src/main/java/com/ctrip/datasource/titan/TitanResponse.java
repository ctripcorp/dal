package com.ctrip.datasource.titan;

public class TitanResponse {
	private String status;
	private String message;
	private TitanData[] data;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public TitanData[] getData() {
		return data;
	}
	public void setData(TitanData[] data) {
		this.data = data;
	}
}

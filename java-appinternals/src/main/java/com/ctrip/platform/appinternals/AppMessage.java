package com.ctrip.platform.appinternals;

public class AppMessage {
	private boolean sueccess;
	private String message;
	
	public boolean isSueccess() {
		return sueccess;
	}
	public void setSueccess(boolean sueccess) {
		this.sueccess = sueccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toString(){
		return String.format("{\"Message\":\"%s\",\"IsSuccess\":\"%s\"}",
				this.message, this.sueccess ? "True" : "False");
	}
}

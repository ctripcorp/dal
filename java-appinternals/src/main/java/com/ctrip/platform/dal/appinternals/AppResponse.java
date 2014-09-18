package com.ctrip.platform.dal.appinternals;

public class AppResponse {
	private String contextType = "text/html";
	private String encode = "UTF-8";
	private StringBuffer message = new StringBuffer();
	
	public String getContextType() {
		return contextType;
	}
	public void setContextType(String contextType) {
		this.contextType = contextType;
	}
	public String getEncode() {
		return encode;
	}
	public void setEncode(String encode) {
		this.encode = encode;
	}

	public void append(String ms){
		this.message.append(ms);
	}
	
	public void append(String msp, Object... args){
		this.append(String.format(msp, args));
	}
	
	public void clear(){
		this.message = new StringBuffer();
	}
	
	public String getMessage(){
		return this.message.toString();
	}
	
}

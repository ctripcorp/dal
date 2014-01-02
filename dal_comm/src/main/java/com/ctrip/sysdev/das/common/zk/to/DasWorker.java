package com.ctrip.sysdev.das.common.zk.to;

public class DasWorker {
	private String ip;
	private int port;
	// Not used right now, should store worker's status
	private String status;
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String encode() {
		return ip + ":" + port;
	}
}

package com.ctrip.platform.dasconsole.domain;

public class Worker {
	private String ip;
	private Port ports;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Port getPorts() {
		return ports;
	}

	public void setPorts(Port ports) {
		this.ports = ports;
	}
}

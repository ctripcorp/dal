package com.ctrip.sysdev.das.common.zk.to;

public class DasWorker {
	private String id;
	private int port;
	// Not used right now, should store worker's status
	private String status;
	
	public DasWorker() {}
	
	public DasWorker(String id, int port) {
		this.id = id;
		this.port = port;
	}
	
	public DasWorker(String idPort) {
		String[] values = idPort.split(":");
		this.id = values[0];
		this.port = Integer.parseInt(values[1]);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String toString() {
		return id + ":" + port;
	}
}

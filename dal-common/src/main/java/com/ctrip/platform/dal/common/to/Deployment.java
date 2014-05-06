package com.ctrip.platform.dal.common.to;

import com.ctrip.platform.dal.common.zk.DasZkPathConstants;

public class Deployment {
	private int port;
	private boolean shared;
	private String value;
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	
	/**
	 * @return logic Db for dedicate deployment, or db groups for shared deployment
	 */
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String[] convertToDbGroups() {
		return value.split(DasZkPathConstants.DEPLOYMENT_VALUE_SEPARATOR);
	}
	
	public static Deployment create(int port, String rawValue) {
		Deployment deployment = new Deployment();
		deployment.setPort(port);
		String[] values = rawValue.split(DasZkPathConstants.DEPLOYMENT_SEPARATOR);
		deployment.setShared(values[0].equals(DasZkPathConstants.SHARED));
		deployment.setValue(values.length == 1 ? null: values[1]);
		return deployment; 
	}
}

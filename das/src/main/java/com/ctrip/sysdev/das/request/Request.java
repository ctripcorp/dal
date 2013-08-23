package com.ctrip.sysdev.das.request;

public interface Request {
	
	/**
	 * Each version of Request should implement this method
	 * @return
	 */
	public int getProtocolVersion();

}

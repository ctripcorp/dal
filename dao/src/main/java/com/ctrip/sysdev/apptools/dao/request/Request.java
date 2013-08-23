package com.ctrip.sysdev.apptools.dao.request;

public interface Request {
	
	/**
	 * Each version of Request should implement this method
	 * @return
	 */
	public int getProtocolVersion();

}

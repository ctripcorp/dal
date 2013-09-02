package com.ctrip.platform.international.daogen.dao.request;

public interface Request {
	
	/**
	 * Each version of Request should implement this method
	 * @return
	 */
	public int getProtocolVersion();

}

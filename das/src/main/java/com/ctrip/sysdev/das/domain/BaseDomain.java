package com.ctrip.sysdev.das.domain;

public class BaseDomain implements Domain {

	protected int protocolVersion;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4516618655812362676L;

	@Override
	public int getProtocolVersion() {
		return protocolVersion;
	}

}

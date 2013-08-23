package com.ctrip.sysdev.das.request;

public class AbstractRequest implements Request {
	
	protected int protocolVersion;

	@Override
	public int getProtocolVersion() {
		// TODO Auto-generated method stub
		return protocolVersion;
	}

}

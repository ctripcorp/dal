package com.ctrip.sysdev.apptools.dao.request;

public class AbstractRequest implements Request {
	
	protected int protocolVersion;

	@Override
	public int getProtocolVersion() {
		// TODO Auto-generated method stub
		return protocolVersion;
	}

}

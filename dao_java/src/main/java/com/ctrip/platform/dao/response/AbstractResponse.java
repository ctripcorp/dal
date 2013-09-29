package com.ctrip.platform.dao.response;

public class AbstractResponse implements Response {
	
	protected int protocolVersion;
	
	@Override
	public int getProtocolVersion() {
		// TODO Auto-generated method stub
		return protocolVersion;
	}

}

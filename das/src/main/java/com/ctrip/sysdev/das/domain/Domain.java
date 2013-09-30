package com.ctrip.sysdev.das.domain;

import java.io.Serializable;

public class Domain implements Serializable {
	private static final long serialVersionUID = -4516618655812362676L;

	protected int protocolVersion;

	public int getProtocolVersion() {
		return protocolVersion;
	}
}

package com.ctrip.platform.dal.service.jmx;

public class DasServerInfoMBean implements ServerInfoMXBean {

	@Override
	public String getName() {
		return "Dal-Server";
	}

	@Override
	public String getVersion() {
		return "V****";
	}

	@Override
	public String getReleaseInfo() {
		return "yyyy-mm-dd release";
	}

}
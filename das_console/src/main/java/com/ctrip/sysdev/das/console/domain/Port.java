package com.ctrip.sysdev.das.console.domain;

import java.util.Set;

public class Port {
	private Set<Integer> ports;

	public Set<Integer> getPorts() {
		return ports;
	}

	public void setPorts(Set<Integer> ports) {
		this.ports = ports;
	}
}

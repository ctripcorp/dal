package com.ctrip.sysdev.das.console.domain;

import java.util.ArrayList;
import java.util.List;

public class PerformanceHistory {
	private Integer port;
	
	private List<Performance> performanceHistory = new ArrayList<Performance>();

	public List<Performance> getPerformanceHistory() {
		return performanceHistory;
	}

	public void setPerformanceHistory(List<Performance> performanceHistory) {
		this.performanceHistory = performanceHistory;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void add(Performance p) {
//		performanceHistory.add(p);
		synchronized(performanceHistory) {
			if(performanceHistory.size() == 0)
				performanceHistory.add(p);
			else
				performanceHistory.set(0, p);
		}
	}
	
	public PerformanceHistory getSub(long start, long end) {
		return this;
	}
}

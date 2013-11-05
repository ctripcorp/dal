package com.ctrip.sysdev.das.console.domain;

import java.util.ArrayList;
import java.util.List;

public class PerformanceHistory {
	private List<Performance> performanceHistory = new ArrayList<Performance>();

	public List<Performance> getPerformanceHistory() {
		return performanceHistory;
	}

	public void setPerformanceHistory(List<Performance> performanceHistory) {
		this.performanceHistory = performanceHistory;
	}

	public void add(Performance p) {
		performanceHistory.add(p);
	}
}

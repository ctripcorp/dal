package com.ctrip.sysdev.das.console.domain;

import java.util.LinkedList;
import java.util.List;

public class PerformanceHistory {
	private List<Performance> performanceHistory = new LinkedList<Performance>();

	public List<Performance> getPerformanceHistory() {
		return performanceHistory;
	}

	public void add(Performance p) {
		performanceHistory.add(p);
	}
}

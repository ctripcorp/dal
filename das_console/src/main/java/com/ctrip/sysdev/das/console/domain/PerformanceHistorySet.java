package com.ctrip.sysdev.das.console.domain;

import java.util.concurrent.ConcurrentHashMap;

public class PerformanceHistorySet {
	private ConcurrentHashMap<String, PerformanceHistory> performanceHistoryMap = new ConcurrentHashMap<String, PerformanceHistory>();

	public ConcurrentHashMap<String, PerformanceHistory> getperformanceHistoryMap() {
		return performanceHistoryMap;
	}
	
	public PerformanceHistory getPerformanceHistory(String id) {
		return performanceHistoryMap.get(id);
	}
	
	public void add(String id, Performance p) {
		PerformanceHistory history = performanceHistoryMap.get(id);
		if(history == null){
			history = new PerformanceHistory();
			PerformanceHistory oldHistory =  performanceHistoryMap.putIfAbsent(id, history);
			if(oldHistory == null)
				history.add(p);
			else
				oldHistory.add(p);
		}else{
			history.add(p);
		}
	}
}

package com.ctrip.platform.dasconsole.domain;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PerformanceHistorySet {
	private String ip;
	
	private ConcurrentHashMap<String, PerformanceHistory> performanceHistoryMap = new ConcurrentHashMap<String, PerformanceHistory>();

	public PerformanceHistory[] getPerformanceHistoryList() {
		Collection<PerformanceHistory> v = performanceHistoryMap.values();
		return v.toArray(new PerformanceHistory[v.size()]);
	}

	public void setPerformanceHistoryList(PerformanceHistory[] v) {
		;
	}

	public PerformanceHistory getPerformanceHistory(String id) {
		return performanceHistoryMap.get(id);
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void add(String id, Performance p) {
		PerformanceHistory history = performanceHistoryMap.get(id);
		if(history == null){
			history = new PerformanceHistory();
			PerformanceHistory oldHistory =  performanceHistoryMap.putIfAbsent(id, history);
			if(oldHistory == null){
				history.setPort(new Integer(id));
				history.add(p);
			}else
				oldHistory.add(p);
		}else{
			history.add(p);
		}
	}
}
